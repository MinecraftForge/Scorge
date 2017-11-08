# Scorge
A Language adapter for [scala](https://www.scala-lang.org/)

## Usage
include `modLanguageAdapter = "net.minecraftforge.scorge.ForgeScalaAdapter"` in your @Mod annotation

## Maven repo

```
TBA
```

## Supported Scala version
Current Supported version is 2.11.1

## Scala Version Policy
Scorge will only support the latest major release of scala starting after MC 1.13. That is currently __2.11.1__ it will __not__ support the 
release candidates or milestone versions.

## Scorg library policy
The current libraries that are shipped with Scorge are meant to keep compatibility with the ones what are currently shipped with Forge excluding these libraries the compiler, xml, continuations, and swing. It was discussed in IRC to drop support for those libraries.

If you want to add a new library for Scorge to support.
1. It must be made for scala
2. You must have a good technical to why it would be better supported in Scorge than shipped with your mod

If you want to drop a library supported by Scorge
1. It must be done during a verion switch of minecraft to not break backwards compatibility within an MC version
2. You must have a good technical reason as to why it should be done

