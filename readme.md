# Scorge
A Forge language loader the [Scala](https://www.scala-lang.org/) programming language

## Supported Scala version
Current Supported version is 3.2.1

## Shaded libraries
Scorge currently includes
* scala3-library_3
* scala-library

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
(You can find a complete example setup in the [example](example) directory.) 

To use Scorge in your project, simply add it to your gradle dependencies.
Scorge will automatically add the necessary scala libraries to your compile classpath.
```gradle
dependencies {
    implementation 'net.minecraftforge:Scorge:VERSION'
}
```

Next, modify your `mods.toml` to use the `scorge` loader.
Additionally, each declared mod also needs to have an `entryClass` attribute that points to the
mod's scala class.
```toml
modLoader="scorge"
loaderVersion="[4,)"

[[mods]]
    entryClass="com.example.mod.ExampleScalaMod"
```