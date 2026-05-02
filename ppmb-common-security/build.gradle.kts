plugins {
    id("buildlogic.java-common-conventions")
    id("java-library")
}

val libs = the<VersionCatalogsExtension>().named("libs")

dependencies {
    api(platform(libs.findLibrary("spring-boot-dependencies").get()))
    api(project(":ppmb-common-api"))

    api(libs.findLibrary("spring-boot-starter-security").get())
    api(libs.findLibrary("spring-boot-starter-web").get())

    implementation(libs.findLibrary("jacksonDatabind").get())
    implementation(libs.findLibrary("jjwt-api").get())
    runtimeOnly(libs.findLibrary("jjwt-impl").get())
    runtimeOnly(libs.findLibrary("jjwt-jackson").get())

    compileOnly(libs.findLibrary("lombok").get())
    annotationProcessor(libs.findLibrary("lombok").get())

    testImplementation(libs.findLibrary("spring-boot-starter-test").get())
    testCompileOnly(libs.findLibrary("lombok").get())
    testAnnotationProcessor(libs.findLibrary("lombok").get())
}
