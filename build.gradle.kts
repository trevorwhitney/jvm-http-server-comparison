import org.gradle.plugins.ide.idea.model.IdeaModel
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.0-rc-198"
}

allprojects {
    apply {
        plugin("idea")
    }

    val nonKotlinProjects = emptyList<String>()

    if(!nonKotlinProjects.contains(project.name)) {
        apply {
            plugin("kotlin")
        }

        tasks.withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = "1.8"
                apiVersion = "1.3"
                languageVersion = "1.3"
            }
        }
    }

    repositories {
        jcenter()
    }

    configure<IdeaModel> {
        module {
            outputDir = File("$buildDir/classes/main")
            testOutputDir = File("$buildDir/classes/test")
        }
    }
}
