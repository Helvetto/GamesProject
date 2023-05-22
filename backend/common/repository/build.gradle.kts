plugins {
	java
}

group = "com.mygame.common.repository"
version = "0.1"
java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.0.6")
	implementation(project(mapOf("path" to ":common:exception")))
}

tasks.withType<Test> {
	useJUnitPlatform()
}
