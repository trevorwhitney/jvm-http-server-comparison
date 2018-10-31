import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val mainClass = "com.github.trevorwhitney.builtin.SimpleServerKt"

plugins {
    id("com.github.johnrengelman.shadow") version "1.2.3"
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile(kotlin("reflect"))
    compile(project(":components:logging"))

    compile("io.micrometer:micrometer-core:1.0.7")
    compile("io.micrometer:micrometer-registry-prometheus:1.0.7")
    compile("io.github.mweirauch:micrometer-jvm-extras:0.1.2")
}

task<JavaExec>("run") {
    environment = mapOf(
        "LOG_LEVEL" to "debug"
    ).plus(System.getenv())

    classpath = java.sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).runtimeClasspath
    main = mainClass
}
