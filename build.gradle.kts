plugins {
    id("fabric-loom") apply false
    id("net.neoforged.moddev") apply false
    id("com.kneelawk.submodule.unobf") apply false
    id("com.kneelawk.versioning") apply false
    id("com.kneelawk.kpublish") apply false
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}

allprojects {
    repositories {
        maven("https://maven.nucleoid.xyz/releases") {
            name = "Nucleoid"
        }
    }

    // make builds reproducible
    tasks.withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
}
