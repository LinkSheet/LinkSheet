#!/usr/bin/env kotlin

@file:Repository("https://repo.maven.apache.org/maven2/")
@file:DependsOn("io.github.typesafegithub:github-workflows-kt:3.0.1")
@file:Repository("https://bindings.krzeminski.it")
@file:DependsOn("actions:checkout:v4")
@file:DependsOn("actions:setup-java:v4")
@file:DependsOn("actions:cache:v4")
@file:DependsOn("actions:upload-artifact:v4")
@file:DependsOn("gradle:actions__setup-gradle:v3")

import io.github.typesafegithub.workflows.actions.actions.Checkout
import io.github.typesafegithub.workflows.actions.actions.SetupJava
import io.github.typesafegithub.workflows.actions.actions.UploadArtifact
import io.github.typesafegithub.workflows.actions.gradle.ActionsSetupGradle
import io.github.typesafegithub.workflows.domain.RunnerType.UbuntuLatest
import io.github.typesafegithub.workflows.domain.Shell
import io.github.typesafegithub.workflows.domain.actions.CustomAction
import io.github.typesafegithub.workflows.domain.triggers.Push
import io.github.typesafegithub.workflows.domain.triggers.WorkflowDispatch
import io.github.typesafegithub.workflows.dsl.JobBuilder
import io.github.typesafegithub.workflows.dsl.expressions.Contexts
import io.github.typesafegithub.workflows.dsl.expressions.ExpressionContext
import io.github.typesafegithub.workflows.dsl.expressions.expr
import io.github.typesafegithub.workflows.dsl.workflow
import io.github.typesafegithub.workflows.yaml.ConsistencyCheckJobConfig


val KEYSTORE_FILE by Contexts.secrets
val KEYSTORE_PASSWORD by Contexts.secrets
val KEY_ALIAS by Contexts.secrets
val KEY_PASSWORD by Contexts.secrets
val NIGHTLY_REPO_ACCESS_TOKEN by Contexts.secrets
val NIGHTLY_PRO_REPO_ACCESS_TOKEN by Contexts.secrets
val PRO_FLAVOR_CONFIG by Contexts.secrets

object VariablesContext : ExpressionContext("vars")
object ActionEnvironmentContext : ExpressionContext("env")

val Contexts.vars: VariablesContext
    get() = VariablesContext

val Contexts.actionEnv: ActionEnvironmentContext
    get() = ActionEnvironmentContext

val ENABLE_RELEASES by Contexts.vars
val ENABLE_PRO_BUILDS by Contexts.vars
val NIGHTLY_REPO_URL by Contexts.vars
val NIGHTLY_PRO_REPO_URL by Contexts.vars

val BUILD_FLAVOR by Contexts.actionEnv
val BUILD_TYPE by Contexts.actionEnv
val BUILD_FLAVOR_TYPE by Contexts.actionEnv

val setupAndroid = CustomAction(
    actionOwner = "android-actions",
    actionName = "setup-android",
    actionVersion = "v3",
)

val base64ToFile = CustomAction(
    actionOwner = "timheuer",
    actionName = "base64-to-file",
    actionVersion = "v1",
    inputs = mapOf(
        "fileName" to "keystore.jks",
        "encodedString" to expr(KEYSTORE_FILE)
    )
)

val nightlyReleaseNotes = CustomAction(
    actionOwner = "1fexd",
    actionName = "gh-create-release-notes",
    actionVersion = "0.0.11",
    inputs = mapOf(
        "github-token" to expr { secrets.GITHUB_TOKEN },
        "stable-repo" to expr { github.repository },
        "nightly-repo" to expr("vars.NIGHTLY_REPO_URL"),
        "last-commit-sha" to expr("github.event.before"),
        "commit-sha" to expr { github.sha }
    )
)

val triggerRemoteWorkflow = CustomAction(
    actionOwner = "1fexd",
    actionName = "gh-trigger-remote-action",
    actionVersion = "0.0.5",
    inputs = mapOf(
        "github-token" to expr(NIGHTLY_REPO_ACCESS_TOKEN),
        "repo" to expr("vars.NIGHTLY_PRO_REPO_URL"),
        "ref" to expr { github.ref },
        "event-type" to "rebuild-nightly"
    )
)


fun cmdQuote(name: String): String {
    return """"$name""""
}


fun subshell(cmd: String): String {
    return "\$($cmd)"
}

class Variable(val name: String) {
    private val quoted = cmdQuote("\$$name")

    operator fun invoke(): String {
        return quoted
    }

    override fun toString(): String {
        return name
    }
}

var outputMetaDataJsonVar = Variable("OUTPUT_METADATA_JSON")


@DslMarker
annotation class BashDslMarker

@BashDslMarker
class BashDsl {
    fun cat(what: String): String {
        return "cat $what"
    }

    fun echo(what: String): String {
        return "echo $what"
    }

    fun jq(what: String): String {
        return "jq $what"
    }

    infix fun String.pipe(to: String): String {
        return "$this | $to"
    }

    fun exec(cmd: String): String {
        return cmd
    }

    fun exec(exec: ExecDsl.() -> Unit): String {
        val dsl = ExecDsl().apply(exec)
        return dsl.exec
    }
}

class ExecDsl(var exec: String = "") {

    operator fun set(variable: Variable, value: String) {
        exec = "$variable=$value"
    }
}

fun bash(block: BashDsl.() -> String): String {
    return block(BashDsl())
}

val versionCodeVar = Variable("VERSION_CODE")
val nightlyRepoVar = Variable("NIGHTLY_REPO")
val nightlyTagVar = Variable("NIGHTLY_TAG")
val apkFileVar = Variable("APK_FILE")
val releaseNoteVar = Variable("RELEASE_NOTE")


fun JobBuilder<*>.createRelease(path: String, version: String, token: String, nightlyRepoUrl: String, releaseNote: String) {
    run(
        command = bash {
            exec("gh release create -R ${nightlyRepoVar()} -t ${versionCodeVar()} ${nightlyTagVar()} ${apkFileVar()} --latest --notes ${releaseNoteVar()}")
        },
        env = mapOf(
            apkFileVar.name to path,
            nightlyTagVar.name to expr { github.ref },
            versionCodeVar.name to version,
            "GITHUB_TOKEN" to expr(token),
            nightlyRepoVar.name to expr(nightlyRepoUrl),
            "BUILD_FLAVOR" to expr(BUILD_FLAVOR),
            "BUILD_TYPE" to expr(BUILD_TYPE),
            releaseNoteVar.name to expr(releaseNote)
        ),
        `if` = expr { contains(ENABLE_RELEASES, "true") }
    )
}

fun JobBuilder<*>.parseOutput(baseOutPathExpr: String): Pair<String, String> {
    val jsonContentVar = Variable("json_content")

    val cmdGetJsonContent = bash {
        val cmdReadOutputMetaData = cat(outputMetaDataJsonVar())
        exec {
            this[jsonContentVar] = subshell(cmdReadOutputMetaData)
        }
    }

    val outputFileVar = Variable("OUTPUT_FILE")

    val cmdGetVersionCode = bash {
        val cmdReadVersionCode = echo(jsonContentVar()) pipe jq("-r '.elements[0].versionCode'")
        exec {
            this[versionCodeVar] = subshell(cmdReadVersionCode)
        }
    }

    val cmdGetOutputFile = bash {
        val cmdReadOutputFile = echo(jsonContentVar()) pipe jq("-r '.elements[0].outputFile'")
        exec {
            this[outputFileVar] = subshell(cmdReadOutputFile)
        }
    }

    val githubOutputVar = Variable("GITHUB_OUTPUT")

    val outputFilePathStep = run(
        name = "Get output file path",
        shell = Shell.Bash,
        command = """ 
                $cmdGetJsonContent
                echo "$cmdGetVersionCode" >> ${githubOutputVar()}
                echo "$cmdGetOutputFile" >> ${githubOutputVar()}
            """.trimIndent(),
        env = mapOf(
            "OUTPUT_METADATA_JSON" to "$baseOutPathExpr/output-metadata.json"
        )
    )

    val versionCodeExpr = expr(outputFilePathStep.outputs[versionCodeVar.name])
    val apkPathExpr = "$baseOutPathExpr/${expr(outputFilePathStep.outputs[outputFileVar.name])}"

    return versionCodeExpr to apkPathExpr
}

fun JobBuilder<*>.buildFlavor(keyStoreFilePath: String): Pair<Pair<String, String>, Pair<String, String>> {
    run(
        command = "./gradlew assembleFossNightly",
        env = mapOf(
            "GITHUB_WORKFLOW_RUN_ID" to expr { github.run_id },
            "KEYSTORE_FILE_PATH" to expr { keyStoreFilePath },
            "KEYSTORE_PASSWORD" to expr { KEYSTORE_PASSWORD },
            "KEY_ALIAS" to expr { KEY_ALIAS },
            "KEY_PASSWORD" to expr { KEY_PASSWORD },
            "FLAVOR_CONFIG" to "",
        )
    )

    run(
        command = "./gradlew assembleProNightly",
        env = mapOf(
            "GITHUB_WORKFLOW_RUN_ID" to expr { github.run_id },
            "KEYSTORE_FILE_PATH" to expr { keyStoreFilePath },
            "KEYSTORE_PASSWORD" to expr { KEYSTORE_PASSWORD },
            "KEY_ALIAS" to expr { KEY_ALIAS },
            "KEY_PASSWORD" to expr { KEY_PASSWORD },
            "FLAVOR_CONFIG" to expr(PRO_FLAVOR_CONFIG)
        )
    )

    val fossBaseOutPathExpr = "app/build/outputs/apk/foss/nightly"
    val proBaseOutPathExpr = "app/build/outputs/apk/pro/nightly"

    val foss = parseOutput(fossBaseOutPathExpr)
    val pro = parseOutput(proBaseOutPathExpr)

    return foss to pro
}

workflow(
    name = "Build nightly APK",
    env = mapOf(
        "BUILD_FLAVOR" to "foss",
        "BUILD_TYPE" to "nightly",
        "BUILD_FLAVOR_TYPE" to "fossNightly"
    ),
    on = listOf(
        WorkflowDispatch(), Push(
            tags = listOf("nightly/(2[0-9][2-9][4-9])((0[1-9])|11|12)([0-3][0-9])([0-9][0-9])"),
            pathsIgnore = listOf("*.md")
        )
    ),
    consistencyCheckJobConfig = ConsistencyCheckJobConfig.Disabled,
    sourceFile = __FILE__,
    targetFileName = __FILE__.toPath().fileName?.let {
        it.toString().substringBeforeLast(".main.kts") + ".yml"
    }
) {
    job(id = "build", runsOn = UbuntuLatest) {
        run(name = "Install JQ", command = "sudo apt-get install jq -y")
        uses(
            action = Checkout(
                submodules = true,
                fetchDepth = Checkout.FetchDepth.Infinite,
                fetchTags = true
            )
        )

        uses(
            action = SetupJava(
                javaVersion = "21",
                distribution = SetupJava.Distribution.Zulu,
                cache = SetupJava.BuildPlatform.Gradle
            )
        )

        uses(action = setupAndroid)
        val androidKeyStore = uses(action = base64ToFile)
        uses(action = ActionsSetupGradle())

        run(command = "./gradlew test")

        val (foss, pro) = buildFlavor(androidKeyStore.outputs["filePath"])
        val (fossVersionCodeExpr, fossApkPathExpr) = foss
        val (proVersionCodeExpr, proApkPathExpr) = pro

        uses(
            action = UploadArtifact(
                name = "linksheet-nightly",
                path = listOf(fossApkPathExpr, "app/build/outputs/mapping/${expr(BUILD_FLAVOR_TYPE)}/*.txt")
            )
        )

        val nightlyReleaseNotesStep = uses(action = nightlyReleaseNotes)
        val releaseNote = nightlyReleaseNotesStep.outputs["releaseNote"]

        createRelease(fossApkPathExpr, fossVersionCodeExpr, NIGHTLY_REPO_ACCESS_TOKEN, NIGHTLY_REPO_URL, releaseNote)
        createRelease(proApkPathExpr, proVersionCodeExpr, NIGHTLY_PRO_REPO_ACCESS_TOKEN, NIGHTLY_PRO_REPO_URL, releaseNote)
    }
}

