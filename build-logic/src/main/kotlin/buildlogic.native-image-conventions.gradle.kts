plugins {
    id("org.graalvm.buildtools.native")
}

graalvmNative {
    binaries {
        named("main") {
            // Main options
            buildArgs.add("--enable-preview")

            // Allow override to fast build in local env via -PfastNative
            val fastNative = project.findProperty("fastNative") == "true"
            if (fastNative) {
                buildArgs.add("-Ob")
            } else {
                buildArgs.add("-O2")
                buildArgs.add("-march=native")
            }
        }
    }
}
