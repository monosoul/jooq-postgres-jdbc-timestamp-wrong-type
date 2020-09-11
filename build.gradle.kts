import com.avast.gradle.dockercompose.ComposeExtension
import com.avast.gradle.dockercompose.tasks.ComposeUp
import nu.studer.gradle.jooq.JooqGenerate
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric
import org.flywaydb.gradle.task.FlywayMigrateTask
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED

plugins {
    kotlin("jvm") version "1.4.10"
    id("com.avast.gradle.docker-compose") version "0.13.2"
    id("nu.studer.jooq")
    id("org.flywaydb.flyway")
}

repositories {
    mavenCentral()
    jcenter()
}

buildscript {
    dependencies {
        classpath("org.apache.commons:commons-lang3")
    }
}

dependencies {
    jooqGenerator("org.postgresql:postgresql")

    implementation("javax.validation:validation-api:2.0.1.Final")
    implementation("org.postgresql:postgresql")
    implementation("org.jooq:jooq")
    implementation("org.flywaydb:flyway-core")
    implementation("ch.qos.logback:logback-classic:1.2.3")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.testcontainers:postgresql:1.14.3")
    testImplementation("org.apache.commons:commons-lang3")
    testImplementation("io.strikt:strikt-core:0.27.0")
}

val postgresDbName: String = randomAlphabetic(10)
val postgresUser: String = randomAlphabetic(10)
val postgresPassword: String = randomAlphanumeric(10)
val jooqConfiguration = "main"

jooq {
    configurations {
        create(jooqConfiguration) {
            jooqConfiguration.generator.apply {
                name = "org.jooq.codegen.DefaultGenerator"
                database.apply {
                    inputSchema = "public"
                    includes = "partitioned_table"
                    syntheticPrimaryKeys = ".*\\.partitioned_table\\.id"
                }
                generate.apply {
                    isIndexes = false
                    isGlobalTableReferences = false
                    isGlobalKeyReferences = false
                    isRecordsImplementingRecordN = false
                    isValidationAnnotations = true
                }
                target.apply {
                    directory = "src/main/java"
                    packageName = "com.github.monosoul.jooq"
                }
            }
        }
    }
}

dockerCompose {
    useComposeFiles = listOf("postgres-docker.yml")
    captureContainersOutput = true

    environment["POSTGRES_DB"] = postgresDbName
    environment["POSTGRES_USER"] = postgresUser
    environment["POSTGRES_PASSWORD"] = postgresPassword
}

tasks {

    withType<Test> {
        useJUnitPlatform()

        testLogging {
            events(PASSED, SKIPPED, FAILED)
            exceptionFormat = FULL
        }
    }

    withType<ComposeUp> {
        doLast {
            println("POSTGRES_DB: $postgresDbName")
            println("POSTGRES_USER: $postgresUser")
            println("POSTGRES_PASSWORD: $postgresPassword")

            val jdbcUrl = "jdbc:postgresql://localhost:${dockerCompose.getPostgresPort()}/$postgresDbName"
            flyway {
                url = jdbcUrl
                user = postgresUser
                password = postgresPassword
                configurations = arrayOf("compileClasspath", "jooqGenerator")
            }

            jooq.configurations.getByName(jooqConfiguration).apply {
                jooqConfiguration.jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = jdbcUrl
                    user = postgresUser
                    password = postgresPassword
                }
            }
        }
    }

    val migrations = withType<FlywayMigrateTask> {
        dependsOn(composeUp)
    }

    withType<JooqGenerate> {
        dependsOn(migrations)
        finalizedBy(composeDown)
    }
}

fun ComposeExtension.getPostgresPort(port: Int = 5432) = servicesInfos["postgres"]?.tcpPorts?.get(port) ?: port