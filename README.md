# [kotlin-pcsc][]

[Kotlin Multiplatform][multi] bindings for the [PC/SC][] API ([winscard][]),
which targets:

* **[JNA][]** on JVM (builds a single library for anything [JNA][] supports)
* **[Native][]** for [Kotlin/Native][native] apps

This was developed to support the PC version of [Metrodroid][] (a public transit
card reader).

## Supported platforms

**Note:** Cross-compiling **Native** targets is not supported.

Platform           | [PC/SC][] Implementation | [JNA][] (JRE) | [Native][]
------------------ | ------------------------ | ------------- | ----------
Linux x86_64       | [pcsclite][]             | :o:           | :o:
macOS 10.14 x86_64 | `PCSC.framework`         | :o:           | :o:
Windows 10 x86_64  | [WinSCard.dll][winscard] | :o:           | :o:

## API

[API documentation can be viewed online][api-docs], or built locally with:
`./gradlew dokkaHtml`

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

### JNA (all platforms)

```sh
./gradlew :jnaMainClasses :jnaTest
```

This builds for all platforms, as the prebuilt `net.java.dev.jna` package already includes
platform-specific JNI helpers.  You don't need any cross-compiling or special machine for that.

### Native targets

**Note:** Only `x86_64` targets are currently supported.

#### Linux

* Build dependencies: `libpcsclite1 libpcsclite-dev`
* Run-time dependencies: `libpcsclite1`

```sh
./gradlew :linuxX64MainKlibrary :linuxX64Test
```

#### macOS

* Build dependencies: Xcode 11 or later

```sh
./gradlew :macosX64MainKlibrary :macosX64Test
```

#### Windows

```powershell
.\gradlew :mingwX64MainKlibrary :mingwX64Test
```

## Runtime notes

### Linux (JNA and Native)

Install `libpcsclite1` and `pcscd` packages.

If you're using a reader with NXP PN53x series chipset (eg: ACS ARC122U), you
need to disable the `pn533` and `pn533_usb` modules:

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

## FAQ

### Is there sample code?

Yes!  See [the `sample` directory of this repository](./sample/).

This supports building on all target platforms, and includes a `jnaFatJar` task, which pulls in all
dependencies to a single JAR file.

### How does this relate or compare to...

#### `javax.smartcardio`?

This is _entirely different_, and does not support these APIs at all, even when
they are available (on Java 8 and earlier).

If you want to use that API, take a look at [jnasmartcardio][]. `kotlin-pcsc`'s
JNA implementation was inspired by it.

#### intarsys smartcard-io?

[intarsys smartcard-io][intarsys] is a Java/JRE library which provides a
Java-friendly PC/SC API (and a `javax.smartcardio` wrapper).

While it _can_ be used with Kotlin, it only targets the JRE (not Native).

### What about mobile (Android / iOS) support?

This is explicitly _not_ in scope for this project.

Most mobile devices do not offer a [PC/SC][]-compatible API. The few devices
that _do_ run a regular enough Linux userland that you should be able to build
using that.

### How do I use this to connect to FeliCa / MIFARE / etc?

You'll need to provide your own implementation of those protocols. PC/SC only provides a very low
level interface, and you'll be sending `ByteArray` to the ICC and getting `ByteArray` back.

We don't even parse the APDUs for you...

[api-docs]: https://micolous.github.io/kotlin-pcsc/index.html
[intarsys]: https://github.com/intarsys/smartcard-io
[JNA]: https://github.com/java-native-access/jna
[jnasmartcardio]: https://github.com/jnasmartcardio/jnasmartcardio
[kotlin-pcsc]: https://github.com/micolous/kotlin-pcsc
[Metrodroid]: https://github.com/metrodroid/metrodroid
[multi]: https://kotlinlang.org/docs/reference/multiplatform.html
[native]: https://kotlinlang.org/docs/reference/native-overview.html
[PC/SC]: https://www.pcscworkgroup.com/
[pcsclite]: https://pcsclite.apdu.fr/
[winscard]: https://docs.microsoft.com/en-us/windows/win32/api/winscard/
