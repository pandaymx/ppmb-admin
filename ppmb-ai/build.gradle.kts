plugins {
    id("buildlogic.spring-cloud-conventions")
    alias(libs.plugins.spring.boot)
}

dependencies {
    implementation(project(":ppmb-common:ppmb-common-web"))
    implementation(project(":ppmb-common:ppmb-common-api"))
    implementation(project(":ppmb-common:ppmb-common-mq"))

    // Spring AI
    implementation(platform(libs.spring.ai.bom))
    implementation(libs.spring.ai.openai.spring.boot.starter)
    implementation(libs.spring.ai.ollama.spring.boot.starter)

    implementation(libs.spring.boot.starter.web)

    // For reactive Flux (needed by Spring AI streaming)
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // For RabbitMQ Listener
    implementation(libs.spring.boot.starter.amqp)

    developmentOnly(libs.spring.boot.devtools)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.mockito.core)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveFileName.set("app.jar")
}
