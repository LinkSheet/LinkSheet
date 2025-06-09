#!/usr/bin/env kotlin

@file:Repository("https://repo.maven.apache.org/maven2/")
@file:DependsOn("io.github.typesafegithub:github-workflows-kt:3.4.0")
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
import io.github.typesafegithub.workflows.domain.JobOutputs
import io.github.typesafegithub.workflows.domain.RunnerType.UbuntuLatest
import io.github.typesafegithub.workflows.domain.Shell
import io.github.typesafegithub.workflows.domain.actions.CustomAction
import io.github.typesafegithub.workflows.domain.actions.CustomLocalAction
import io.github.typesafegithub.workflows.domain.triggers.Push
import io.github.typesafegithub.workflows.domain.triggers.WorkflowDispatch
import io.github.typesafegithub.workflows.dsl.JobBuilder
import io.github.typesafegithub.workflows.dsl.WorkflowBuilder
import io.github.typesafegithub.workflows.dsl.expressions.Contexts
import io.github.typesafegithub.workflows.dsl.expressions.ExpressionContext
import io.github.typesafegithub.workflows.dsl.expressions.expr
import io.github.typesafegithub.workflows.dsl.workflow
import io.github.typesafegithub.workflows.yaml.ConsistencyCheckJobConfig
import kotlin.collections.mapOf


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
val API_HOST by Contexts.vars
val BUILD_FLAVOR by Contexts.actionEnv
val BUILD_TYPE by Contexts.actionEnv
val BUILD_FLAVOR_TYPE by Contexts.actionEnv

val fossBaseOutPathExpr = "app/build/outputs/apk/foss/nightly"
val proBaseOutPathExpr = "app/build/outputs/apk/pro/nightly"
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
    return "$($cmd)"
}

class Variable(val name: String) {
    private val quoted = cmdQuote("$$name")

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


fun JobBuilder<*>.createRelease(
    path: String,
    version: String,
    token: String,
    nightlyRepoUrl: String,
    releaseNote: String,
) {
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
            releaseNoteVar.name to expr(releaseNote),
        ),
        `if` = expr { contains(ENABLE_RELEASES, "true") }
    )
}

fun JobBuilder<*>.parseOutput(baseOutPathExpr: String): BuildResult {
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

    return BuildResult(
        outputFilePathStep.outputs[versionCodeVar.name],
        outputFilePathStep.outputs[outputFileVar.name]
    )
}

class BuildResult(val versionCode: String, val apkName: String)

fun JobBuilder<*>.buildFlavor(keyStoreFilePath: String): Pair<BuildResult, BuildResult> {
    run(
        command = "./gradlew assembleFossNightly",
        env = mapOf(
            "GITHUB_WORKFLOW_RUN_ID" to expr { github.run_id },
            "KEYSTORE_FILE_PATH" to expr { keyStoreFilePath },
            "KEYSTORE_PASSWORD" to expr { KEYSTORE_PASSWORD },
            "KEY_ALIAS" to expr { KEY_ALIAS },
            "KEY_PASSWORD" to expr { KEY_PASSWORD },
            "FLAVOR_CONFIG" to "",
            "API_HOST" to expr(API_HOST)
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
            "FLAVOR_CONFIG" to expr(PRO_FLAVOR_CONFIG),
            "API_HOST" to expr(API_HOST)
        )
    )


    val foss = parseOutput(fossBaseOutPathExpr)
    val pro = parseOutput(proBaseOutPathExpr)

    return foss to pro
}

fun JobBuilder<*>.setupAndroid() {
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
    uses(
        action = CustomAction(
            actionOwner = "android-actions",
            actionName = "setup-android",
            actionVersion = "v3",
        )
    )
    uses(action = ActionsSetupGradle())
}

fun WorkflowBuilder.setupWorkflow(release: Boolean) {
    val unitTestJob = job(
        id = "unit-tests",
        name = "Unit tests",
        runsOn = UbuntuLatest
    ) {
        setupAndroid()
        run(command = "./gradlew testFossReleaseUnitTest")
    }
    val integrationTestJob = job(
        id = "integration-tests",
        name = "Integration tests",
        runsOn = UbuntuLatest
    ) {
        run(
            name = "Enable KVM",
            shell = Shell.Bash,
            command = """echo 'KERNEL=="kvm", GROUP="kvm", MODE="0666", OPTIONS+="static_node=kvm"' | sudo tee /etc/udev/rules.d/99-kvm4all.rules
sudo udevadm control --reload-rules
sudo udevadm trigger --name-match=kvm"""
        )

        setupAndroid()

        val apiLevel = 35
        val avdInfoStep = uses(
            action = CustomLocalAction(
                actionPath = "./.github/actions/get-avd-info",
                inputs = mapOf(
                    "api-level" to "$apiLevel",
                    "target" to "google_apis"
                )
            ),
        )

        uses(
            action = CustomAction(
                actionOwner = "reactivecircus",
                actionName = "android-emulator-runner",
                actionVersion = "v2",
                inputs = mapOf(
                    "api-level" to "$apiLevel",
                    "arch" to expr(avdInfoStep.outputs["arch"]),
                    "target" to expr(avdInfoStep.outputs["target"]),
                    "script" to "./gradlew connectedAndroidTest"
                )
            ),
        )
    }

    job(
        id = "build-release",
        name = "Build release",
        runsOn = UbuntuLatest,
        needs = listOf(unitTestJob, integrationTestJob),
    ) {
        run(name = "Install JQ", command = "sudo apt-get install jq -y")
        setupAndroid()
        val androidKeyStore = uses(action = base64ToFile)

        val (foss, pro) = buildFlavor(androidKeyStore.outputs["filePath"])

        uses(
            action = UploadArtifact(
                name = "linksheet-nightly",
                path = listOf(
                    fossBaseOutPathExpr + "/" + expr(foss.apkName),
                    "app/build/outputs/mapping/${expr(BUILD_FLAVOR_TYPE)}/*.txt"
                )
            )
        )

        if (release) {
            val nightlyReleaseNotesStep = uses(action = nightlyReleaseNotes)
            val releaseNote = nightlyReleaseNotesStep.outputs["releaseNote"]

            createRelease(
                fossBaseOutPathExpr + "/" + expr(foss.apkName),
                expr(foss.versionCode),
                NIGHTLY_REPO_ACCESS_TOKEN,
                NIGHTLY_REPO_URL,
                releaseNote
            )
            createRelease(
                proBaseOutPathExpr + "/" + expr(pro.apkName),
                expr(pro.versionCode),
                NIGHTLY_PRO_REPO_ACCESS_TOKEN,
                NIGHTLY_PRO_REPO_URL,
                releaseNote
            )
        }
    }
}

val env = mapOf(
    "BUILD_FLAVOR" to "foss",
    "BUILD_TYPE" to "nightly",
    "BUILD_FLAVOR_TYPE" to "fossNightly"
)
workflow(
    name = "Build nightly APK",
    env = env,
    on = listOf(
        WorkflowDispatch(),
        Push(
            tags = listOf("nightly-*"),
            pathsIgnore = listOf("*.md")
        )
    ),
    consistencyCheckJobConfig = ConsistencyCheckJobConfig.Disabled,
    sourceFile = __FILE__,
    targetFileName = __FILE__.toPath().fileName?.let {
        it.toString().substringBeforeLast(".main.kts") + ".yml"
    },
    block = { setupWorkflow(true) }
)

workflow(
    name = "Build nightly APK (nopublish)",
    env = env,
    on = listOf(WorkflowDispatch()),
    consistencyCheckJobConfig = ConsistencyCheckJobConfig.Disabled,
    sourceFile = __FILE__,
    targetFileName = __FILE__.toPath().fileName?.let {
        it.toString().substringBeforeLast(".main.kts") + "-nopublish.yml"
    },
    block = { setupWorkflow(false) }
)
