# Module kotlin-pcsc

[kotlin-pcsc][] is a Kotlin Multiplatform library for accessing smart cards via
the PC/SC API on the Java JVM and native applications (on Linux, macOS and
Windows).

[kotlin-pcsc]: https://github.com/micolous/kotlin-pcsc

# Package au.id.micolous.kotlin.pcsc

[kotlin-pcsc][] is a Kotlin Multiplatform library for accessing smart cards via
the PC/SC API on the Java JVM and native applications (on Linux, macOS and
Windows).

[kotlin-pcsc]: https://github.com/micolous/kotlin-pcsc

It takes some small liberties with the PC/SC API to make it object oriented, and
easier to use:

PC/SC function          | `kotlin-pcsc` function(s)
----------------------- | -------------------------
`SCardBeginTransaction` | [Card.beginTransaction][]
`SCardCancel`           | [Context.cancel][]
`SCardConnect`          | [Context.connect][]
`SCardControl`          | [Card.control][]
`SCardDisconnect`       | [Card.disconnect][]
`SCardEndTransaction`   | [Card.endTransaction][]
`SCardEstablishContext` | [Context.establish][]
`SCardGetAttrib`        | [Card.getAttrib][]; plus [Card.getIfdSerial][], [Card.getIfdType][], [Card.getIfdVersion][], [Card.getMechanicalCharacteristics][], [Card.getVendorName][]
`SCardGetStatusChange`  | [Context.getStatusChange][]; plus [Context.getAllReaderStatus][], [Context.getStatus][]
`SCardIsValidContext`   | [Context.isValid][]
`SCardListReaders`      | [Context.listReaders][]
`SCardReconnect`        | [Card.reconnect][]
`SCardReleaseContext`   | [Context.release][]
`SCardStatus`           | [Card.status][]
`SCardTransmit`         | [Card.transmit][]

### Unimplemented functions

These functions aren't available in `kotlin-pcsc`:

* `SCardCancelTransaction` (on Windows, this is "reserved for future use")
* `SCardListReaderGroups`
* `SCardSetAttrib`
* `SCardSetTimeout`

Pull requests are welcome!

Windows defines a number of extra APIs which do not have equivalents on other
platforms. Supporting these is not a priority.

### Source sets

Only the `commonMain` source set is part of the public API.

Other source sets are an internal implementation detail, and subject to change
without warning:

* `jvmMain`: JVM / JNA-based bindings

* `nativeAnyMain`: Native bindings.

  Because `cinterop` can't use the usual Kotlin `actual`/`expect` for its definitions, and
  restrictions about how Gradle evaluates source sets at build time, we need to symlink to this
  from:

  * `linuxMain`: Linux bindings, symlink to `nativeAnyMain`

  * `macosMain`: macOS bindings, symlink to `nativeAnyMain`

  * `mingwMain`: Windows bindings.

    This has symlinks to `nativeAnyMain` as well, but contains a work-around for
    [Kotlin/Native's `windows.def` including the **entire** Win32 API without a `headerFilter`][1].
    
    That wouldn't be a problem if it wasn't _broken_.

[1]: https://youtrack.jetbrains.com/issue/KT-45071
