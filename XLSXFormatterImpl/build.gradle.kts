plugins {
    kotlin("jvm")
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
    implementation("org.apache.poi:poi:5.2.3") // For .xls format
    implementation("org.apache.poi:poi-ooxml:5.2.3") // For .xlsx format
    implementation("org.apache.logging.log4j:log4j-core:2.20.0") // Najnovija verzija
    implementation("org.apache.logging.log4j:log4j-api:2.20.0")
    implementation(project(":SPI"))
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("com.itextpdf:itextpdf:5.5.13.2")

}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"]) // If you're using the 'java' or 'kotlin' plugin

            groupId = "org.example"
            artifactId = "XLSXFormatterImpl"
            version = "1.0.0"
        }
    }
}



kotlin {
    jvmToolchain(21)
}