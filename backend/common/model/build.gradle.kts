plugins {
	java
	kotlin("jvm") version "1.8.21"
}

group = "com.mygame.common.model"
version = "0.1"
java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.0.6")
	implementation(project(mapOf("path" to ":common:repository")))

	testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.3")
	testImplementation("org.mockito:mockito-core:5.3.1")

}

tasks.test {
	useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
	kotlinOptions {
		jvmTarget = "17"
	}
}