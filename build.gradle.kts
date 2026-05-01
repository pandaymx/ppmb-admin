plugins {
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spotless)
    alias(libs.plugins.sonar)
}

// 提取公共属性，但不使用 subprojects 注入
allprojects {
    group = "top.ppmblszdp" // 建议换成你自己的域名
    version = "0.1.0"

    repositories {
        mavenCentral()
        // 建议加上阿里云镜像加速
        maven { url = uri("https://maven.aliyun.com/repository/public") }
    }
}

spotless {
    java {
        target("**/src/*/java/**/*.java")
        targetExclude("postgres/data/**", "consul/data/**", "**/build/**")
        googleJavaFormat()
        removeUnusedImports()
        formatAnnotations()
    }
    kotlinGradle {
        target("*.gradle.kts", "build-logic/**/*.gradle.kts")
        targetExclude("postgres/data/**", "consul/data/**", "**/build/**")
        ktlint()
    }
    format("misc") {
        target("*.md", ".gitignore", "gradle/**/*.toml")
        targetExclude("postgres/data/**", "consul/data/**", "**/build/**")
        trimTrailingWhitespace()
        leadingTabsToSpaces()
        endWithNewline()
    }
}

sonar {
    properties {
        property("sonar.organization", "pandaymx")
        property("sonar.projectKey", "pandaymx_ppmb-admin")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.language", "java")
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.coverage.jacoco.xmlReportPaths", "**/build/reports/jacoco/test/jacocoTestReport.xml")
    }
}
