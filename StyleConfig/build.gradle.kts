plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka") version "1.8.10"
    `java-library`
    `maven-publish`
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("com.google.code.gson:gson:2.8.8")
}

tasks.test {
    useJUnitPlatform()
}

tasks.javadoc {
    dependsOn(tasks.dokkaJavadoc)
    doLast {
        println("Javadoc task completed with Dokka output.")
    }
}

tasks.dokkaJavadoc {
    outputDirectory.set(file("build/dokka/javadoc"))
}


publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            groupId = "org.example"
            artifactId = "StyleConfig"
            version = "1.0.0"
        }
    }
}



kotlin {
    jvmToolchain(21)
}