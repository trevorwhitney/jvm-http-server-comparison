import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val mainClass = "com.github.trevorwhitney.webflux.SimpleServerKt"

plugins {
    id("com.github.johnrengelman.shadow") version "1.2.3"
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile(kotlin("reflect"))

    compile("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.9.7")
    compile("com.fasterxml.jackson.core:jackson-databind:2.9.7")
    compile("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.7")
    compile("org.apache.logging.log4j:log4j-core:2.11.1")
    compile("org.apache.logging.log4j:log4j-slf4j-impl:2.11.1")
}

task<JavaExec>("run") {
        classpath = java.sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).runtimeClasspath
        main = mainClass
}
