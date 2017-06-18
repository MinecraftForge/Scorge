# Scorg
A Language adapter for [scala](https://www.scala-lang.org/)

## Why
I personally wanted an updated version of scala but I also don't believe Forge should providing the LanguageAdapter

## Usage
include `modLanguageAdapter = "io.github.illyohs.scorg.ScalaAdapter"` in your @Mod annotation

## Maven repo

```
repositories {
    mavenCentral()
    maven { url "https://jitpack.io" }

}

dependencies {
    compile 'com.github.illyohs:Scorg:@VERSION@'
}
```

[![](https://jitpack.io/v/illyohs/Scorg.svg)](https://jitpack.io/#illyohs/Scorg)
