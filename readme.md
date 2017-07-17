# Scorg
A Language adapter for [scala](https://www.scala-lang.org/)

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


## Supported Scala version
Current Supported version is 2.12.2

## Scala Version Policy
Scorg will only support the latest major release of scala. That is currently __2.12.2__ it will __not__ support the 
release candidates or milestone versions.

## Scorg library policy
The current libraries that are shipped with Scorg are meant to keep compatibilty with the ones what are currently shipped with Forge excluding these libraries the compiler, xml, continuations, and swing. It was discussed in IRC to drop support for those libraries.

If you want to add a new library for Scorg to support.
1. It must be made for scala
2. You must have a good technical to why it would be better supported in Scorg than shipped with your mod I want to keep this language adapter as small as possible

If you want to drop a library supported by Scorg 
1. It must be done during a verion switch of minecraft to not break backwards compatibility within an MC version
2. You must have a good technical reason as to why it should be done

## Contact
* __IRC__ irc.esper.net #Scorg
* [__MATRIX__](https://matrix.to/#/#scorg:matrix.org)
* [__CurseForge__](https://minecraft.curseforge.com/projects/scorg)

