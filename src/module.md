# Module kotlin-pcsc

This is documentation for [kotlin-pcsc][], a Kotlin Multiplatform library for using the PC/SC API.

[kotlin-pcsc]: https://github.com/micolous/kotlin-pcsc

# Package au.id.micolous.kotlin.pcsc

A PC/SC API implementation for Kotlin Multiplatform.

This takes some small liberties with the PC/SC API to make it object oriented, and easier to use:

PC/SC function name     | Object      | Equivalent function         | Additional helper functions
----------------------- | ----------- | --------------------------- | ---------------------------
`SCardBeginTransaction` | [Card][]    | [Card.beginTransaction][]
`SCardCancel`           | [Context][] | [Context.cancel][]
`SCardConnect`          | [Context][] | [Context.connect][]
`SCardControl`          | [Card][]    | [Card.control][]
`SCardDisconnect`       | [Card][]    | [Card.disconnect][]
`SCardEndTransaction`   | [Card][]    | [Card.endTransaction][]
`SCardEstablishContext` | [Context][] | [Context.establish][]
`SCardGetAttrib`        | [Card][]    | [Card.getAttrib][]          | [Card.getIfdSerial][], [Card.getIfdType][], [Card.getIfdVersion][], [Card.getMechanicalCharacteristics][], [Card.getVendorName][]
`SCardGetStatusChange`  | [Context][] | [Context.getStatusChange][] | [Context.getAllReaderStatus][], [Context.getStatus][]
`SCardIsValidContext`   | [Context][] | [Context.isValid][]
`SCardListReaders`      | [Context][] | [Context.listReaders][]
`SCardReconnect`        | [Card][]    | [Card.reconnect][]
`SCardReleaseContext`   | [Context][] | [Context.release][]
`SCardStatus`           | [Card][]    | [Card.status][]
`SCardTransmit`         | [Card][]    | [Card.transmit][]

### Unimplemented functions

* `SCardCancelTransaction` (on Windows, this is "reserved for future use")
* `SCardListReaderGroups`
* `SCardSetAttrib`
* `SCardSetTimeout`

### Public API

Only the `commonMain` module is part of the public API.

Anything that **only** appears in `jnaMain`, `nativeMain`, `nativeMacosMain` or `nativeWindowsMain`
is an internal implementation detail, and is subject to change without warning.
