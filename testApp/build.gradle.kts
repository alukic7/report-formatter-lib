plugins {
    kotlin("jvm")
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(project(":SPI")) {
        exclude(module = "StyleConfig")
    }
    runtimeOnly(project(":CSVFormatterImpl"))
    runtimeOnly(project(":TXTFormatterImpl"))
    runtimeOnly(project(":PDFFormatterImpl"))
    runtimeOnly(project(":XLSXFormatterImpl"))
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("mysql:mysql-connector-java:8.0.23")
}


application {
    mainClass.set("testApp.TestKt")
}

tasks.shadowJar {
    archiveClassifier.set("all")
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
    mergeServiceFiles() // include meta-inf services files
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}