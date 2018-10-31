import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val mainClass = "com.github.trevorwhitney.jetty.SimpleServerKt"

plugins {
    id("com.github.johnrengelman.shadow") version "1.2.3"
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile(kotlin("reflect"))
    compile("org.eclipse.jetty:jetty-server:9.4.12.v20180830")

}

task<JavaExec>("run") {
        classpath = java.sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).runtimeClasspath
        main = mainClass
}
