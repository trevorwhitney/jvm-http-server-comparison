import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val mainClass = "com.github.trevorwhitney.vertx.SimpleServerKt"

plugins {
    id("com.github.johnrengelman.shadow") version "1.2.3"
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile(kotlin("reflect"))
    compile(project(":components:logging"))
    compile(project(":components:prometheus"))

    compile("io.vertx:vertx-core:3.5.4")
    compile("io.vertx:vertx-web:3.5.4")

}

tasks.withType<ShadowJar> {
    classifier = "fat"
    mergeServiceFiles {
        include("META-INF/services/io.vertx.core.spi.VerticleFactory")
    }
}

task<JavaExec>("run") {
        classpath = java.sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).runtimeClasspath
        main = mainClass
}
