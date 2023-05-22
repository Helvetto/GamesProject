plugins {
	java
}

group = "com.mygame"
version = "0.1"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
}

tasks.withType<Test> {
	useJUnitPlatform()
}
