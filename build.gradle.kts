plugins {
    id("java")
    id("application")
}

group = "ru.clevertec"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}



dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation ("com.h2database:h2:2.2.220")
    implementation("org.postgresql:postgresql:42.7.2")
}

application {
    mainClass.set("ru.clevertec.CheckRunner")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "ru.clevertec.check.CheckRunner"
    }
    archiveBaseName.set("clevertec-check")
    archiveVersion.set("")
    from({
        configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
    })
}

tasks.test {
    useJUnitPlatform()
}