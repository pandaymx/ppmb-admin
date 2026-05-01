plugins {
    checkstyle
    jacoco
    id("com.diffplug.spotless")
}

val libs = the<VersionCatalogsExtension>().named("libs")

checkstyle {
    toolVersion = libs.findVersion("checkstyle").get().requiredVersion
    configFile = rootProject.file("gradle/config/checkstyle/checkstyle.xml")
    isIgnoreFailures = false
    maxWarnings = 0
}

spotless {
    java {
        googleJavaFormat()
        removeUnusedImports()
        formatAnnotations()
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint()
    }
}

tasks.withType<Checkstyle>().configureEach {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.withType<JacocoReport> {
    dependsOn(tasks.withType<Test>())
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.withType<JacocoCoverageVerification> {
    dependsOn(tasks.withType<Test>())
    violationRules {
        rule {
            limit {
                minimum = "0.80".toBigDecimal()
            }
        }
    }
}

tasks.named("check") {
    dependsOn(tasks.withType<JacocoCoverageVerification>())
}
