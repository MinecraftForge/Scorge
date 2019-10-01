# Scorge
A Forge language loader the [Scala](https://www.scala-lang.org/) programming language


## Supported Scala version
Current Supported version is 2.13.1

## Shaded libraries
Scorge currently includes
* scala-library
* scala-java8-compat

## Scala Version Policy
Scorge will only support the latest major release of Scala starting once Forge is stabilized for a release. It will lock Scala version till its next stable release. Scorge will __not__ support release candidates or milestone versions of Scala. 

## Scorge library policy


If you want to add a new library for Scorge to support.
1. It must be made for Scala
2. You must have a good technical reason as to why it would be better supported in Scorge instead of being shipped with your mod.

If you want to drop a library supported by Scorge
1. It must be done during a version switch of minecraft to not break backwards compatibility within an MC version
2. You must have a good technical reason as to why it should be dropped

## Usage

Follow the exmaple project add Scorge as a maven dependency  and place it in the mods folder in the the run directory or
manually add it to the classpath.
This is due to how language providers are loaded by fml and there's nothing that can be done about this.