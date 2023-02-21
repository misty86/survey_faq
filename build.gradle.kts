plugins {
    application
    kotlin("jvm") version "1.5.21"
    id("com.justai.jaicf.jaicp-build-plugin") version "0.1.1"
    kotlin("plugin.serialization") version "1.5.21"
}

group = "com.survey_faq"
version = "1.0.0"

val jaicf = "1.2.5"
val logback = "1.3.0-alpha12"
val hoplite = "1.4.16"

application {
    mainClassName = "com.survey_faq.connections.ConsoleKt"
}

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
    maven("https://jitpack.io")
    maven {
        url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
        name = "Ktor EAP"
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("ch.qos.logback:logback-classic:$logback")

    implementation("com.just-ai.jaicf:jaicp:$jaicf")
    implementation("com.just-ai.jaicf:core:$jaicf")
    implementation("com.just-ai.jaicf:caila:$jaicf")
    implementation("com.just-ai.jaicf:telegram:$jaicf")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation ("io.github.rburgst:okhttp-digest:2.6")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
    implementation("com.just-ai.jaicf:mongo:$jaicf")
    implementation("com.sksamuel.hoplite:hoplite-core:$hoplite")
    implementation("com.sksamuel.hoplite:hoplite-yaml:$hoplite")
    implementation("com.just-ai.jaicf:rasa:$jaicf")
    implementation("io.ktor:ktor-server-core:1.6.7")
    implementation("io.ktor:ktor-server-netty:1.6.7")
    implementation("io.ktor:ktor-client-core:1.6.7")
    implementation("io.ktor:ktor-client-cio:1.6.7")
//    implementation("ch.qos.logback:logback-classic:1.2.5")

}

tasks {
    shadowJar {
        archiveFileName.set("app.jar")
    }
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

tasks.create("stage") {
    dependsOn("shadowJar")
}

tasks.withType<com.justai.jaicf.plugins.jaicp.build.JaicpBuild> {
    mainClassName.set(application.mainClassName)
}
