plugins {
    java
    application
}

group = "io.github.annupuri"
version = "1.1.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
    // Swing client entry point. The client picks local vs. RMI in its connection dialog.
    mainClass = "dvddemo.gui.ApplicationRunner"
}

// Forward `-Ddvddemo.hostname=<host>` from the Gradle invocation to the client JVM.
tasks.named<JavaExec>("run") {
    System.getProperty("dvddemo.hostname")?.let { systemProperty("dvddemo.hostname", it) }
}

// `./gradlew runServer` — starts the RMI registry (port 1099) and binds the DVD database service.
// Run this before choosing "Network Connection" in the client.
tasks.register<JavaExec>("runServer") {
    group = "application"
    description = "Starts the RMI registry and binds the DVD database service."
    classpath = sourceSets["main"].runtimeClasspath
    mainClass = "dvddemo.remote.RegDVDDatabase"
}

// `./gradlew runDosClient --console=plain -q` — text-menu admin console (catalogue management, seeding).
tasks.register<JavaExec>("runDosClient") {
    group = "application"
    description = "Runs the text-menu admin console for the DVD database."
    classpath = sourceSets["main"].runtimeClasspath
    mainClass = "dvddemo.db.DOSClient"
    standardInput = System.`in`
}

// `./gradlew seed` — writes a handful of sample DVDs into the database directory (default ./dvd_db).
tasks.register<JavaExec>("seed") {
    group = "application"
    description = "Seeds the DVD database with sample records."
    classpath = sourceSets["main"].runtimeClasspath
    mainClass = "dvddemo.db.SampleData"
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    // Surface every javac lint category in the build output (not fatal).
    options.compilerArgs.add("-Xlint:all")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    // Tame the very chatty java.util.logging output from dvddemo.* during the test run.
    systemProperty(
        "java.util.logging.config.file",
        layout.projectDirectory.file("src/test/resources/logging.properties").asFile.absolutePath,
    )
    testLogging {
        events("passed", "skipped", "failed")
    }
}
