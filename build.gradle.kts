plugins {
    id("java")
    id("war")
}

group = "ru.clevertec"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("jakarta.servlet:jakarta.servlet-api:6.0.0")
    implementation("org.postgresql:postgresql:42.7.2")
    implementation ("com.google.code.gson:gson:2.8.9")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation ("com.h2database:h2:2.2.220")
    testImplementation("org.mockito:mockito-core:3.12.4")
}

tasks.test {
    useJUnitPlatform()
}

tasks.war {
    archiveFileName.set("clevertec-check.war")
}