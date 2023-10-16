import me.omico.gradm.addDeclaredRepositories

addDeclaredRepositories()

plugins {
    id("example.gradm")
}

includeBuild("build-logic/project")
