rootProject.name = "wrong-type-example"

val junitVersion = "5.6.2"
val jooqVersion = "3.13.4"
val jooqPluginVersion = "5.0.2"
val flywayVersion = "6.5.5"
val postgresVersion = "42.2.14"
val apacheCommonsLangVersion = "3.11"

gradle.settingsEvaluated {
    pluginManagement {
        resolutionStrategy {
            eachPlugin {
                when (requested.id.id) {
                    "org.flywaydb.flyway" -> useVersion(flywayVersion)
                    "nu.studer.jooq" -> useVersion(jooqPluginVersion)
                }
            }
        }
    }
}

gradle.allprojects {
    buildscript {
        configurations.all {
            resolutionStrategy {
                eachDependency {
                    when (requested.group) {
                        "org.jooq" -> useVersion(jooqVersion)
                        "org.postgresql" -> useVersion(postgresVersion)
                    }
                }
            }
        }
    }

    configurations.all {
        resolutionStrategy {
            eachDependency {
                when (requested.group) {
                    "org.junit.jupiter" -> useVersion(junitVersion)
                    "org.jooq" -> useVersion(jooqVersion)
                    "org.flywaydb" -> useVersion(flywayVersion)
                    "org.postgresql" -> useVersion(postgresVersion)
                }
            }
        }
    }
}