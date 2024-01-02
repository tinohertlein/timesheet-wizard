plugins {
    id("com.github.jmongard.git-semver-plugin") version "0.11.0"
}

val ver = semver.version
allprojects {
    version = ver
}


repositories {
    mavenCentral()
}
