# [kotlin-pcsc][]

[Kotlin Multiplatform][multi] bindings for the [PC/SC][] API ([winscard][]),
which targets:

* **[JNA][]** on JVM (builds a single library for anything [JNA][] supports)
* **[Native][]** for [Kotlin/Native][native] apps

This was developed to support the PC version of [Metrodroid][] (a public transit
card reader).

## Supported platforms

> [!NOTE]
> Due to functional limitations in Kotlin/Native, cross-compiling **Native**
> targets is not supported, except from a macOS host to a macOS target.

Platform             | [PC/SC][] Implementation | JVM ([JNA][]) | [Native][]
-------------------- | ------------------------ | ------------- | ----------
Linux `x86_64`       | [pcsclite][]             | ✅            | ✅
macOS 15.7 `aarch64` | `PCSC.framework`         | ✅            | ✅
macOS 15.7 `x86_64`  | `PCSC.framework`         | ✅            | ✅
Windows 11 `aarch64` | [WinSCard.dll][winscard] | ✅            | [❌](#windows)
Windows 10 `x86_64`  | [WinSCard.dll][winscard] | ✅            | ✅

## API

[API documentation can be viewed online][api-docs], or built locally with:

```sh
./gradlew dokkaHtml
```

This library _mostly_ follows the PC/SC API, but takes some liberties to make it
easier to use in Kotlin, such as using object orientation, providing helper
methods for common patterns, parsing bitfields into properties, and abstracting
away the small platform-specific API differences.

The result is that the same "common" API can be used on _all_ platforms: see
[the `sample` directory](./sample/) for an example.

The online version of the documentation can be updated with `./update_online_docs.sh`.

## Build and test

All targets, even native ones, require JDK 9 or later to be installed (for Gradle).

To run the tests, you need:

* a working [PC/SC][]-compatible smart card reader
* a card inserted into the reader

### JVM/JNA (all platforms)

To build the library to run on a regular JVM, and run tests:

```sh
./gradlew :jvmMainClasses :jvmTest
```

This builds for all platforms, as the prebuilt `net.java.dev.jna` package already includes
platform-specific JNI helpers. You don't need any cross-compiling or special machine for that.

### Native targets

#### Linux

> [!NOTE]
> Only `x86_64` Linux targets are currently supported for native builds.

* Build dependencies: `libpcsclite1 libpcsclite-dev`
* Run-time dependencies: `libpcsclite1`

To build the native library and run tests:

```sh
./gradlew :linuxX64MainKlibrary :linuxX64Test
```

#### macOS

* Build dependencies: Xcode 11 or later

To build the native library and run tests:

```sh
# For Apple Silicon (aarch64):
./gradlew :macosArm64MainKlibrary :macosArm64Test

# For Intel (x86_64):
./gradlew :macosX64MainKlibrary :macosX64Test
```

#### Windows

> [!NOTE]
> Only `x86_64` Windows hosts and targets are supported for native builds.
>
> Kotlin/Native
> [does not support building on Windows `aarch64` hosts][kotlin-win-aarch64-host]
> (even when targeting `x86_64`) and
> [does not support building for `mingwArm64` targets][kotlin-win-aarch64-target].
>
> It is possible to build the library for Windows `x86_64` on a Windows `x86_64`
> host, and run it on a Windows _11_ ARM system [through emulation][win-emu].
> Windows _10_ on ARM
> [_does not_ support running `x86_64` binaries in emulation][win-emu].

To build the native library and run tests:

```powershell
.\gradlew :mingwX64MainKlibrary :mingwX64Test
```

[kotlin-win-aarch64-host]: https://youtrack.jetbrains.com/issue/KT-48420/
[kotlin-win-aarch64-target]: https://youtrack.jetbrains.com/issue/KT-68504/
[win-emu]: https://learn.microsoft.com/en-us/windows/arm/apps-on-arm-x86-emulation

## Runtime notes

### Linux (JVM and Native)

Install `libpcsclite1` and `pcscd` packages.

If you're using a reader with NXP PN53x series chipset (eg: ACS ARC122U), you
need to _disable_ the `pn533` and `pn533_usb` kernel modules:

```sh
# First, unplug the card reader.

# On Linux 3.1 - 4.6:
echo "blacklist pn533" | sudo tee -a /etc/modprobe.d/blacklist.conf
sudo rmmod pn533

# On Linux 4.7 and later:
echo "blacklist pn533_usb" | sudo tee -a /etc/modprobe.d/blacklist.conf
sudo rmmod pn533_usb

# Finally, plug the card reader in again.
```

The `pn533`/`pn533_usb` module is a driver for a new Linux-kernel-specific NFC
subsystem, which is **incompatible with all existing software**, including
`libacsccid1` (its PC/SC IFD handler).

## Alternatives to this library

### JSR 268 / `javax.smartcardio`

JSR 268 defines a standard API for smart cards for Java applications. It will
only work on JVM targets.

The API design is entirely different, and is only available out-of-the-box on
Java 8 and earlier.

[jnasmartcardio][] provides a JNA-based implementation of these APIs with
numerous bug fixes. This inspired `kotlin-pcsc`'s JVM implementation.

### Kotlin/Native `platform` bindings

#### ...on macOS

Kotlin/Native on macOS has a disabled and partially-implemented
[`platform.PCSC` / `PCSC.def`][mac-pcsc.def].

This isn't actually usable, but because it's disabled, it doesn't cause any
troubles.

#### ...on Windows

Kotlin/Native on Windows includes bindings for the entire Win32 API
via [`platform.windows` / `windows.def`][windows.def], including `winscard.h`
(via `windows.h`).

However, [these bindings _don't actually work_][windows-kotlin-broken]:

* `windows.def` doesn't link binaries to `WinSCard.dll`
* `windows.def` doesn't handle multi-string types correctly (needed for
  `SCardListReaders`).

To make matters worse, `windows.def` doesn't use `headerFilter`, which makes it
harder to provide _working_ bindings on Windows.

### intarsys smartcard-io

[intarsys smartcard-io][intarsys] is a Java/JVM library which provides a
Java-friendly PC/SC API (and a `javax.smartcardio` wrapper).

While it _can_ be used with Kotlin, it only targets the JVM (not Native).

## FAQ

### Is there sample code?

Yes!  See [the `sample` directory of this repository](./sample/).

This supports building on all target platforms, and includes a `shadowJar` task,
which pulls in all dependencies to a single JAR file.

### What about mobile (Android / iOS) support?

This is explicitly _not_ in scope for this project.

Most mobile devices do not offer a [PC/SC][]-compatible API. The few devices
that _do_ run a regular enough Linux userland that you should be able to build
using that.

### How do I use this to connect to FeliCa / JCOP / MIFARE / etc.?

You'll need to provide your own implementation of those protocols.

PC/SC only provides a very low level interface. Your application will be sending
a `ByteArray` to the ICC and getting a `ByteArray` back.

It doesn't even parse the APDU fields (`CLA`, `INS`, etc.) for you...

[api-docs]: https://micolous.github.io/kotlin-pcsc/index.html
[intarsys]: https://github.com/intarsys/smartcard-io
[JNA]: https://github.com/java-native-access/jna
[jnasmartcardio]: https://github.com/jnasmartcardio/jnasmartcardio
[kotlin-pcsc]: https://github.com/micolous/kotlin-pcsc
[mac-pcsc.def]: https://github.com/JetBrains/kotlin/blob/master/kotlin-native/platformLibs/src/platform/osx/PCSC.def.disabled
[Metrodroid]: https://github.com/metrodroid/metrodroid
[multi]: https://kotlinlang.org/docs/reference/multiplatform.html
[native]: https://kotlinlang.org/docs/reference/native-overview.html
[PC/SC]: https://www.pcscworkgroup.com/
[pcsclite]: https://pcsclite.apdu.fr/
[windows.def]: https://github.com/JetBrains/kotlin/blob/master/kotlin-native/platformLibs/src/platform/mingw/windows.def
[windows-kotlin-broken]: https://github.com/JetBrains/kotlin-native/issues/3483
[winscard]: https://docs.microsoft.com/en-us/windows/win32/api/winscard/