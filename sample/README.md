# kotlin-pcsc sample

This is a simple example application that uses [kotlin-pcsc][].

This implements [the PC/SC sample application][pcsc-sample] developed by [Ludovic Rousseau][].

The main source file is [Sample.kt](./src/commonMain/kotlin/Sample.kt).

## Running

From the root of the `kotlin-pcsc` repository:

* JVM: `./gradlew :sample:shadowJar; java -jar sample/build/libs/sample-all.jar`
* Linux x86_64: `./gradlew :sample:runPcsc_sampleReleaseExecutableLinuxX64`
* macOS x86_64: `./gradlew :sample:runPcsc_sampleReleaseExecutableMacosX64`
* Windows x86_64: `.\gradlew :sample:runPcsc_sampleReleaseExecutableMingwX64`

[kotlin-pcsc]: https://github.com/micolous/kotlin-pcsc
[Ludovic Rousseau]: https://blog.apdu.fr/
[pcsc-sample]: https://blog.apdu.fr/posts/2010/04/pcsc-sample-in-different-languages/
