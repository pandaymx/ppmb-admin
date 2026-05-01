plugins {
    checkstyle
    id("com.diffplug.spotless")
}

checkstyle {
    toolVersion = libs.versions.checkstyle.get()
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
