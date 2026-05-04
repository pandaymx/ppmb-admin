plugins {
    id("buildlogic.java-common-conventions")
}
val libs = the<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation(platform(libs.findLibrary("spring-boot-dependencies").get()))
    implementation(platform(libs.findLibrary("testcontainers-bom").get()))

    implementation(project(":ppmb-common:ppmb-common-api"))

    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation(libs.findLibrary("spring-boot-starter-data-redis").get())
    implementation(libs.findLibrary("jacksonDatabind").get())
    implementation(libs.findLibrary("jackson3-databind").get())
    implementation(libs.findLibrary("jackson3-core").get())

    implementation(libs.findLibrary("jacksonDatatypeJsr310").get())
    implementation(libs.findLibrary("caffeine").get())

    compileOnly(libs.findLibrary("lombok").get())
    annotationProcessor(libs.findLibrary("lombok").get())

    testImplementation(libs.findLibrary("spring-boot-starter-test").get())
    testImplementation(libs.findLibrary("testcontainers").get())
    testImplementation(libs.findLibrary("testcontainers-junit-jupiter").get())
    testImplementation(libs.findLibrary("spring-boot-testcontainers").get())
    testImplementation(libs.findLibrary("mockito-core").get())

    testCompileOnly(libs.findLibrary("lombok").get())
    testAnnotationProcessor(libs.findLibrary("lombok").get())
}
