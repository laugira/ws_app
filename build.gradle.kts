plugins {
    kotlin("jvm") version "2.3.20"
    id("io.ktor.plugin") version "3.5.0"
}

group = "fr.dynalgo"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor Server
    implementation("io.ktor:ktor-server-core:3.5.0")
    implementation("io.ktor:ktor-server-netty:3.5.0")
    implementation("io.ktor:ktor-server-content-negotiation:3.5.0")
    implementation("io.ktor:ktor-serialization-jackson:3.5.0")

    implementation("io.ktor:ktor-server-status-pages:3.5.0")
    implementation("io.ktor:ktor-server-call-logging:3.5.0")

    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-test-host:3.5.0")

    // Exposed
    implementation("org.jetbrains.exposed:exposed-core:0.52.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.52.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.52.0")

    // PostgreSQL + HikariCP
    implementation("org.postgresql:postgresql:42.7.4")
    implementation("com.zaxxer:HikariCP:6.2.1")

    // H2 (embedded dev database)
    implementation("com.h2database:h2:2.3.232")

    // Jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.2")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.13")
}

ktor {
    fatJar {
        archiveFileName.set("ws-app.jar")
    }
}

application {
    mainClass.set("fr.dynalgo.MultiGraphLabKt")
}

tasks.test {
    useJUnitPlatform()
}

val tailwindVersion = "v4.3.0"
val tailwindOs = when {
    org.gradle.internal.os.OperatingSystem.current().isMacOsX -> "macos-arm64"
    org.gradle.internal.os.OperatingSystem.current().isWindows -> "windows-x64.exe"
    else -> "linux-x64"
}
val tailwindCli = layout.projectDirectory.file(".gradle/tailwindcss/tailwindcss-$tailwindOs")
val tailwindDownloadUrl =
    "https://github.com/tailwindlabs/tailwindcss/releases/download/$tailwindVersion/tailwindcss-$tailwindOs"

tasks.register<Exec>("downloadTailwindCli") {
    group = "frontend"
    description = "Download Tailwind CSS standalone CLI"
    val cliFile = tailwindCli.asFile
    onlyIf { !cliFile.exists() }
    doFirst {
        cliFile.parentFile.mkdirs()
    }
    commandLine(
        "curl", "-sL",
        "-o", cliFile.absolutePath,
        tailwindDownloadUrl
    )
    doLast {
        cliFile.setExecutable(true)
    }
    outputs.file(cliFile)
}

tasks.register<Exec>("buildCss") {
    group = "frontend"
    description = "Build Tailwind CSS for production"
    dependsOn("downloadTailwindCli")
    val inputCss = layout.projectDirectory.file("frontend/src/input.css")
    val outputCss = layout.projectDirectory.file("src/main/resources/static/css/app.css")
    commandLine(
        tailwindCli.asFile.absolutePath,
        "-i", inputCss.asFile.absolutePath,
        "-o", outputCss.asFile.absolutePath,
        "--minify"
    )
    inputs.file(inputCss)
    inputs.dir(layout.projectDirectory.dir("src/main/resources/static"))
    outputs.file(outputCss)
}

tasks.named("processResources") {
    dependsOn("buildCss")
}
