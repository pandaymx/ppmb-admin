plugins {
    id("buildlogic.java-common-conventions")
    id("java-library")
}

val libs = the<VersionCatalogsExtension>().named("libs")

dependencies {
    api(platform(libs.findLibrary("spring-boot-dependencies").get()))
    api(platform(libs.findLibrary("spring-cloud-dependencies").get()))
    api(project(":ppmb-common:ppmb-common-api"))
    api(libs.findLibrary("spring-cloud-starter-openfeign").get())

    compileOnly(libs.findLibrary("lombok").get())
    annotationProcessor(libs.findLibrary("lombok").get())
}
