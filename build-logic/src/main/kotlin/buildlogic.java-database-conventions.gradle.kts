plugins {
    id("buildlogic.java-common-conventions")
}

val libs = the<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation(platform(libs.findLibrary("spring-boot-dependencies").get()))
    implementation(libs.findLibrary("spring-boot-starter-data-jpa").get())
    implementation(libs.findLibrary("liquibase-core").get())
}
