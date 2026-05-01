plugins {
    id("buildlogic.java-common-conventions")
}

val libs = the<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation(platform(libs.findLibrary("spring-boot-dependencies").get()))
    implementation(project(":ppmb-common-api"))
    implementation(libs.findLibrary("spring-boot-starter-web").get())
    testImplementation(libs.findLibrary("spring-boot-starter-test").get())
    compileOnly(libs.findLibrary("lombok").get())
    annotationProcessor(libs.findLibrary("lombok").get())
}
