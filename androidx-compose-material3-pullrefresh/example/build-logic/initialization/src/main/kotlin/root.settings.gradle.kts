import me.omico.gradm.addDeclaredRepositories

addDeclaredRepositories()

plugins {
    id("example.gradm")
    id("example.gradle-enterprise")
}

includeBuild("example/build-logic/project")
