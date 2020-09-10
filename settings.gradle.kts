rootProject.name = "wrong-type-example"

gradle.allprojects {
    configurations.all {
        resolutionStrategy {
            eachDependency {
                when (requested.group) {
                    "org.junit.jupiter" -> useVersion("5.6.2")
                }
            }
        }
    }
}