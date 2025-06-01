import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

val kotlinVersion: String by project
val javaVersion: String by project
val ktLintVersion: String by project

val currentJavaVersion = JavaVersion.toVersion(javaVersion)
val currentJvmVersion = JvmTarget.fromTarget(javaVersion)
val splitKotlinVersion = kotlinVersion.split(".", limit = 3)
val currentKotlinVersion = KotlinVersion.fromVersion("${splitKotlinVersion[0]}.${splitKotlinVersion[1]}")

plugins {
	id("org.springframework.boot")
	id("io.spring.dependency-management")
	id("org.jlleitschuh.gradle.ktlint")
	id("io.gitlab.arturbosch.detekt")
	kotlin("jvm")
	kotlin("kapt")
	kotlin("plugin.spring")
	kotlin("plugin.jpa")
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = currentJavaVersion
	targetCompatibility = currentJavaVersion
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-webflux")

	// for mac
	if (OperatingSystem.current().isMacOsX && System.getProperty("os.arch") == "aarch64") {
		runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.94.Final:osx-aarch_64")
	}

	// kotlin
	implementation("org.jetbrains.kotlin:kotlin-stdlib")
	implementation(kotlin("stdlib-jdk8"))
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// security
	implementation("org.springframework.boot:spring-boot-starter-security")

	// batch
	implementation("org.springframework.boot:spring-boot-starter-batch")

	// devtools
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	// jpa
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

	// flyway
	implementation("org.flywaydb:flyway-core:11.3.4")

	// querydsl
	implementation("com.querydsl:querydsl-jpa:5.1.0:jakarta")
	implementation("com.querydsl:querydsl-apt:5.1.0:jakarta")
	kapt("com.querydsl:querydsl-apt:5.1.0:jakarta")
	kapt("jakarta.annotation:jakarta.annotation-api")
	kapt("jakarta.persistence:jakarta.persistence-api")

	// validation
	implementation("org.springframework.boot:spring-boot-starter-validation")

	// postgresql
	runtimeOnly("org.postgresql:postgresql")

	// jwt
	implementation("io.jsonwebtoken:jjwt:0.9.1")
	implementation("com.sun.xml.bind:jaxb-impl:4.0.1")
	implementation("com.sun.xml.bind:jaxb-core:4.0.1")
	implementation("javax.xml.bind:jaxb-api:2.4.0-b180830.0359")

	// redis
	implementation("org.springframework.boot:spring-boot-starter-data-redis")

	// kafka
	implementation("org.springframework.kafka:spring-kafka")

	// mail
	implementation("org.springframework.boot:spring-boot-starter-mail")

	// swagger
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")

	// sentry
	implementation("io.sentry:sentry-spring-boot-starter-jakarta:7.9.0")
	implementation("io.sentry:sentry-logback:7.9.0")

	// h2
	runtimeOnly("com.h2database:h2")

	// logger
	implementation("io.github.oshai:kotlin-logging-jvm:7.0.0")

	// jackson
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	// slack
	implementation("com.slack.api:slack-api-client:1.45.3")

	// test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.springframework.batch:spring-batch-test")
	testImplementation("org.instancio:instancio-junit:5.0.0")

	// test - mockito
	testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
	testImplementation("org.mockito:mockito-inline:5.2.0")

	// test - kotest & mockk
	testImplementation("io.kotest.extensions:kotest-extensions-spring:1.3.0")
	testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
	testImplementation("io.kotest:kotest-assertions-core:5.9.1")
	testImplementation("io.mockk:mockk:1.13.13")
	testImplementation("com.ninja-squad:springmockk:4.0.2")
}

tasks.withType<KotlinCompile> {
	kotlin {
		compilerOptions {
			jvmTarget.set(currentJvmVersion)
			freeCompilerArgs.add("-Xjsr305=strict")
			languageVersion.set(currentKotlinVersion)
			apiVersion.set(currentKotlinVersion)
		}
		jvmToolchain(currentJvmVersion.target.toInt())
	}
}

tasks.withType<Test>().configureEach {
	useJUnitPlatform()
}

ktlint {
	version.set(ktLintVersion)

	reporters {
		reporter(ReporterType.JSON)
	}
}

detekt {
	config.setFrom("$rootDir/detekt.yml")
	allRules = true
	buildUponDefaultConfig = true
	ignoreFailures = false
}

tasks.withType<Detekt>().configureEach {
	reports {
		html.required.set(true)
		xml.required.set(true)
		sarif.required.set(false)
		md.required.set(true)
	}
}

tasks.withType<Detekt>().configureEach {
	jvmTarget = javaVersion
}

tasks.withType<DetektCreateBaselineTask>().configureEach {
	jvmTarget = javaVersion
}
