plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("com.apollographql.apollo").version("2.4.4")
    id("com.diffplug.spotless") version "5.8.1"
    id("com.adarshr.test-logger") version "2.1.1"
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.apollographql.apollo:apollo-runtime:2.4.4")
    implementation("com.github.boscojared.disparse:disparse-smalld:master-SNAPSHOT")
    implementation("com.github.princesslana:smalld:0.2.6")
    implementation("com.github.princesslana:somedb:0.1.0")
    implementation("com.google.guava:guava:28.0-jre")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.squareup.okhttp3:okhttp:3.12.11")
    implementation("io.github.cdimascio:dotenv-java:2.2.0")
    implementation("org.slf4j:slf4j-api:2.0.0-alpha1")
    runtimeOnly("org.slf4j:slf4j-simple:2.0.0-alpha1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.1")
    testImplementation("org.assertj:assertj-core:3.18.1")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

application {
    mainClassName = "com.github.princesslana.profilinator.App"
}

val test by tasks.getting(Test::class) {
    useJUnitPlatform()
}

spotless {
    java {
        googleJavaFormat("1.9")
        target("src/*/java/**/*.java")
    }
}
