plugins {
    id("buildlogic.java-common-conventions")
}

val libs = the<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation(platform(libs.findLibrary("spring-boot-dependencies").get()))
    implementation(platform(libs.findLibrary("spring-cloud-dependencies").get()))

    implementation(libs.findLibrary("spring-cloud-starter-consul-discovery").get())
    implementation(libs.findLibrary("spring-cloud-starter-consul-config").get())
    implementation(libs.findLibrary("spring-cloud-starter-loadbalancer").get())
    implementation(libs.findLibrary("spring-cloud-starter-openfeign").get())
    implementation(libs.findLibrary("spring-cloud-starter-circuitbreaker-resilience4j").get())
    implementation(libs.findLibrary("spring-boot-starter-actuator").get())
}
