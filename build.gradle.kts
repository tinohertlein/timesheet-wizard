plugins {
    id("com.github.jmongard.git-semver-plugin") version "0.10.1"
}

val ver = semver.version
allprojects {
    version = ver
}


repositories {
    mavenCentral()
}
