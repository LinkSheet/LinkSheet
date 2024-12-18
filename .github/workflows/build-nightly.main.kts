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
import io.github.typesafegithub.workflows.dsl.expressions.Contexts
import io.github.typesafegithub.workflows.dsl.expressions.contexts.EnvContext
import io.github.typesafegithub.workflows.dsl.expressions.expr
import io.github.typesafegithub.workflows.dsl.workflow


val KEYSTORE_FILE by Contexts.secrets
val KEYSTORE_PASSWORD by Contexts.secrets
val KEY_ALIAS by Contexts.secrets
val KEY_PASSWORD by Contexts.secrets
val NIGHTLY_REPO_ACCESS_TOKEN by Contexts.secrets

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
    actionVersion = "0.0.9",
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
    actionVersion = "0.0.4",
    inputs = mapOf(
        "github-token" to expr(NIGHTLY_REPO_ACCESS_TOKEN),
        "repo" to expr("vars.NIGHTLY_PRO_REPO_URL"),
        "ref" to expr { github.ref },
        "event-type" to "rebuild-nightly"
    )
)


//val BUILD_FLAVOR by Contexts.env.propertyToExprPath

val BUILD_FLAVOR by Contexts.env.propertyToExprPath
val BUILD_TYPE by Contexts.env.propertyToExprPath
val BUILD_FLAVOR_TYPE by Contexts.env.propertyToExprPath


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

    fun exec(exec: ExecDsl.() -> Unit): String {
        val dsl = ExecDsl().apply(exec)
        return dsl.exec
    }
}

class ExecDsl(var exec: String="") {

    operator fun set(variable: Variable, value: String) {
        exec = "$variable=$value"
    }
}


fun bash(block: BashDsl.() -> String): String {
//    return BashScript()
    return block(BashDsl())
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
            tags = listOf("nightly/2[0-9][2-9][4-9][0-1][0-2][0-3][0-9][0-9][0-9]"),
            pathsIgnore = listOf("*.md")
        )
    ),
    sourceFile = __FILE__,
) {
    job(id = "build", runsOn = UbuntuLatest) {
        run(name = "Install JQ", command = "sudo apt-get install jq -y")
        uses(action = Checkout(submodules = true))

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

        run(
            command = "./gradlew assembleFossNightly",
            env = mapOf(
                "GITHUB_WORKFLOW_RUN_ID" to expr { github.run_id },
                "KEYSTORE_FILE_PATH" to expr { androidKeyStore.outputs["filePath"] },
                "KEYSTORE_PASSWORD" to expr { KEYSTORE_PASSWORD },
                "KEY_ALIAS" to expr { KEY_ALIAS },
                "KEY_PASSWORD" to expr { KEY_PASSWORD }
            )
        )

        val baseOutPathExpr = "app/build/outputs/apk/${expr("env.BUILD_FLAVOR")}/${expr("env.BUILD_TYPE")}"
        val jsonContentVar = Variable("json_content")

        val cmdGetJsonContent = bash {
            val cmdReadOutputMetaData = cat(outputMetaDataJsonVar())
            exec {
                this[jsonContentVar] = subshell(cmdReadOutputMetaData)
            }
        }

//        app/build/outputs/apk/foss/nightly/LinkSheet-2024-12-18T05_53_07-nightly/2024121802-foss-nightly.apk
//        app/build/outputs/mapping/fossNightly/*.txt

        val versionCodeVar = Variable("VERSION_CODE")
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

        uses(
            action = UploadArtifact(
                name = "linksheet-nightly",
                path = listOf(apkPathExpr, "app/build/outputs/mapping/${expr("env.BUILD_FLAVOR_TYPE")}/*.txt")
            )
        )

        val nightlyReleaseNotesStep = uses(action = nightlyReleaseNotes)

        run(
            command = """
                gh release create -R "${'$'}NIGHTLY_REPO" -t "${'$'}VERSION_CODE" "${'$'}NIGHTLY_TAG" "${'$'}APK_FILE" --latest --notes "${'$'}RELEASE_NOTE"
            """.trimIndent(),
            env = mapOf(
                "APK_FILE" to apkPathExpr,
                "NIGHTLY_TAG" to expr { github.ref },
                "VERSION_CODE" to versionCodeExpr,
                "GITHUB_TOKEN" to expr(NIGHTLY_REPO_ACCESS_TOKEN),
                "NIGHTLY_REPO" to expr("vars.NIGHTLY_REPO_URL"),
                "BUILD_FLAVOR" to expr("env.BUILD_FLAVOR"),
                "BUILD_TYPE" to expr("env.BUILD_TYPE"),
                "RELEASE_NOTE" to expr(nightlyReleaseNotesStep.outputs["releaseNote"])
            )
        )


//
//
//        uses(action = triggerRemoteWorkflow)

        /*
        - name: Trigger a remote workflow
        id: trigger_pro_build
        uses: 1fexd/gh-trigger-remote-action@0.0.4
        with:
          github-token: ${{ secrets.NIGHTLY_REPO_ACCESS_TOKEN }}
          repo: ${{ vars.NIGHTLY_PRO_REPO_URL }}
          ref: ${{ github.ref }}
          event-type: rebuild-nightly


         */

        /*
        - run: gh release create -R "$NIGHTLY_REPO" -t "$VERSION_CODE" "$NIGHTLY_TAG" "$APK_FILE" --latest --notes "$RELEASE_NOTE"
        env:
          APK_FILE: ${{ steps.get_output_file_path.OUTPUT_FILE }}
          NIGHTLY_TAG: ${{ github.ref }}
          VERSION_CODE: ${{ steps.get_output_file_path.VERSION_CODE }}
          GITHUB_TOKEN: ${{ secrets.NIGHTLY_REPO_ACCESS_TOKEN }}
          NIGHTLY_REPO: ${{ vars.NIGHTLY_REPO_URL }}
          RELEASE_NOTE: ${{ steps.release_note.outputs.releaseNote }}
          BUILD_FLAVOR: ${{ env.BUILD_FLAVOR }}
          BUILD_TYPE: ${{ env.BUILD_TYPE }}
         */

//        - uses: actions/upload-artifact@v4
//        with:
//        name: linksheet-nightly
//        path: |
//        app/build/outputs/apk/${{ env.BUILD_FLAVOR }}/${{ env.BUILD_TYPE }}/"$APK_FILE"
//        app/build/outputs/mapping/${{ env.BUILD_FLAVOR_TYPE }}/*.txt
//        env:
//          APK_FILE: ${{ steps.get_output_file_path.OUTPUT_FILE }}
//        uses(
//            action = Cache(
//                path = listOf("~/.gradle/caches", "~/.gradle/wrapper"),
//                key = expr { "${runner.os}-gradle-${hashFiles("**/*.gradle*", "**/gradle-wrapper.properties")}" },
//            )
//        )
//        key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
//        restore-keys: |
//        ${{ runner.os }}-gradle-
//        uses()


//        uses(name = "Set up JDK 21", action = SetupJava())

//        run(name = "Print greeting", command = "echo 'Hello world!'")
    }
}

