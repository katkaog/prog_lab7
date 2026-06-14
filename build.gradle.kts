plugins {
    kotlin("jvm") version "2.2.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Работа с JSON (для сервера)
    implementation("com.google.code.gson:gson:2.10.1")
    implementation(kotlin("stdlib"))

    // Тестирование (JUnit 5 + MockK)
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

// Настройка сборки КЛИЕНТСКОГО джарника
val clientJar = tasks.register<Jar>("clientJar") {
    archiveClassifier.set("client")
    manifest {
        attributes["Main-Class"] = "myapp.ClientMainKt" // Точка входа клиента
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get())
}

// Настройка сборки СЕРВЕРНОГО джарника
val serverJar = tasks.register<Jar>("serverJar") {
    archiveClassifier.set("server")
    manifest {
        attributes["Main-Class"] = "myapp.ServerMainKt" // Точка входа сервера
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get())
}

// Делаем так, чтобы при запуске обычной задачи jar собирались оба
tasks.jar {
    enabled = false // Отключаем стандартный пустой jar
    dependsOn(clientJar, serverJar)
}