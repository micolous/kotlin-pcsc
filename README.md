# [kotlin-pcsc][]

Work-in-progress bindings for the PC/SC API ([winscard][]) in Kotlin.

This was developed to enable a cleaner interface for the PC version of [Metrodroid][] (a public
transit card reader).

This is a [Kotlin Multiplatform][multi] project, which supports building using:

* **[JNA][]** on JVM (builds a single library for anything [JNA][] supports)
* **Native** for Kotlin/Native apps

Platform           | PC/SC Implementation     | [JNA][] (JRE) | Native
------------------ | ------------------------ | ------------- | ------
Linux x86_64       | [pcsclite][]             | :o:           | :o:
macOS 10.14 x86_64 | `PCSC.framework`         | :o:           | :o:
Windows 10 x86_64  | [WinSCard.dll][winscard] | :question:    | :x:

> :warning: Cross-compiling **Native** targets is not supported.

## API:

API documentation can be built with: `./gradlew dokka`

This takes some small liberties with the PC/SC API to make it object oriented.

This library has a few tools to make working with PC/SC's data structures easier.

The `commonMain` module contains the "public" API. Anything that **only** appears in `jnaMain`,
`nativeMain`, `nativeMacosMain` or `nativeWindowsMain` is an internal implementation detail, and
subject to change without warning.

### Implemented:

#### Context management

* SCardEstablishContext -> Context.establish
* SCardIsValidContext -> Context.isValid
* SCardReleaseContext -> Context.release
* SCardListReaders -> Context.listReaders
* SCardCancel -> Context.cancel

#### Cards / ICC

* SCardConnect -> Context.connect
* SCardDisconnect -> Card.disconnect
* SCardReconnect -> Card.reconnect
* SCardTransmit -> Card.transmit
* SCardBeginTransaction -> Card.beginTransaction
* SCardEndTransaction -> Card.endTransaction
* SCardStatus -> Card.status
* SCardControl -> Card.control
* SCardGetAttrib -> Card.getAttrib

### TODO:

* SCardCancelTransaction
* SCardGetStatusChange
* SCardListReaderGroups
* SCardSetAttrib
* SCardSetTimeout

## Build and test

**Note:** Running tests requires a connected PC/SC reader, and a card connected to it.

All targets require JDK 9 or later to be installed (for Gradle).

### JNA (all platforms)

```
./gradlew :jnaMainClasses :jnaTest
```

This builds for all platforms, as the prebuilt `net.java.dev.jna` package already includes
platform-specific JNI helpers.  You don't need any cross-compiling or special machine for that.

### Native targets
#### Linux

Required packages: `libpcsclite1 libpcsclite-dev`

```
./gradlew :linuxX64MainKlibrary :linuxX64Test
```

#### macOS

Required packages: Xcode 11 or later

```
./gradlew :macosX64MainKlibrary :macosX64Test
```

#### Windows

**Note:** Not working yet.

...

## Runtime notes

### Linux (JNA and Native)

Install `libpcsclite1` and `pcscd` packages.

If you're using a reader with NXP PN53x series chipset, (eg: ACS ARC122U), recent Linux kernels
include an `pn533_usb` kernel module, which implements a new Linux-kernel-specific NFC subsystem
that is **incompatible all existing software**. You'll need to block it from loading to allow
`libacsccid1` (its PC/SC IFD handler) to work properly:

```
echo "blacklist pn533_usb" | sudo tee -a /etc/modprobe.d/blacklist.conf
sudo rmmod pn533_usb
```

Then unplug and replug the device.

## FAQ

#### Is there an example of use?

Yes!  See [the `sample` directory of this repository](./sample/).

This supports building on all target platforms, and includes a `jnaFatJar` task, which pulls in all
dependencies to a single JAR file.

#### How does this relate to javax.smartcardio?

This is _entirely different_ to the `javax.smartcardio` which was included in Java 8 and earlier.
It does not support these APIs at all, even when they are available.

If you want to use that API, take a look at [jnasmartcardio][]. This project's JNA implementation
was inspired by it.

#### What about mobile (Android / iOS) support?

This is explicitly _not_ in scope for this project.

Most mobile devices do not offer a PC/SC-compatible API. The few devices that _do_ run a regular
enough Linux userland that you should be able to build using that.

#### How do I use this to connect to FeliCa / MIFARE / etc?

You'll need to provide your own implementation of those protocols. PC/SC only provides a very low
level interface, and you'll be sending `ByteArray` to the ICC and getting `ByteArray` back.

We don't even parse the APDUs for you...

[JNA]: https://github.com/java-native-access/jna
[jnasmartcardio]: https://github.com/jnasmartcardio/jnasmartcardio
[kotlin-pcsc]: https://github.com/micolous/kotlin-pcsc
[Metrodroid]: https://github.com/metrodroid/metrodroid
[multi]: https://kotlinlang.org/docs/reference/multiplatform.html
[pcsclite]: https://pcsclite.apdu.fr/
[winscard]: https://docs.microsoft.com/en-us/windows/win32/api/winscard/
