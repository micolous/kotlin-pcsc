# Module kotlin-pcsc

This is documentation for [kotlin-pcsc][], a Kotlin Multiplatform library for using the PC/SC API.

[kotlin-pcsc]: https://github.com/micolous/kotlin-pcsc

# Package au.id.micolous.kotlin.pcsc

A PC/SC API implementation for Kotlin Multiplatform.

This takes some small liberties with the PC/SC API to make it object oriented, and easier to use.

### PC/SC C API

Alphabetical list of functions in the PC/SC C API, and their equivalent function in
`kotlin-pcsc`:

* `SCardBeginTransaction` ➡️ [Card.beginTransaction][]
* `SCardCancel` ➡️ [Context.cancel][]
* `SCardConnect` ➡️ [Context.connect][]
* `SCardControl` ➡️ [Card.control][]
* `SCardDisconnect` ➡️ [Card.disconnect][]
* `SCardEndTransaction` ➡️ [Card.endTransaction][]
* `SCardEstablishContext` ➡️ [Context.establish][]
* `SCardGetAttrib` ➡️ [Card.getAttrib][]
* `SCardGetStatusChange` ➡️ [Context.getStatusChange][]
* `SCardIsValidContext` ➡️ [Context.isValid][]
* `SCardListReaders` ➡️ [Context.listReaders][]
* `SCardReconnect` ➡️ [Card.reconnect][]
* `SCardReleaseContext` ➡️ [Context.release][]
* `SCardStatus` ➡️ [Card.status][]
* `SCardTransmit` ➡️ [Card.transmit][]

Unimplemented functions:

* `SCardCancelTransaction`
* `SCardListReaderGroups`
* `SCardSetAttrib`
* `SCardSetTimeout`

### Public API

Only the `commonMain` module is part of the public API.

Anything that **only** appears in `jnaMain`, `nativeMain`, `nativeMacosMain` or `nativeWindowsMain`
is an internal implementation detail, and is subject to change without warning.


