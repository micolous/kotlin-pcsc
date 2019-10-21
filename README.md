# kotlin-pcsc

work-in-progress bindings for the PC/SC API in Kotlin.

This takes some small liberties with the PC/SC API to make it object oriented.

## Test / support matrix

Platform           | PC/SC Implementation | JNA (JRE)  | Native
------------------ | -------------------- | ---------- | ------
Linux x86_64       | pcsclite             | :o:        | :o:
macOS 10.14 x86_64 | `PCSC.framework`     | :o:        | :o:
Windows 10 x86_64  | `WinSCard.dll`       | :question: | :x:

:warning: Cross-compiling Native targets is not supported.

## Implemented:

* SCardEstablishContext -> Context.establish
* SCardIsValidContext -> Context.isValid
* SCardReleaseContext -> Context.release
* SCardListReaders -> Context.listReaders
* SCardConnect -> Context.connect
* SCardDisconnect -> Card.disconnect
* SCardReconnect -> Card.reconnect
* SCardTransmit -> Card.transmit

## TODO:

### Transactions and transmission

* SCardBeginTransaction
* SCardEndTransaction
* SCardCancel

### Status inquiry

* SCardStatus
* SCardGetStatusChange

### Control and attributes

* SCardControl
* SCardGetAttrib
* SCardSetAttrib

### Informational

* SCardListReaderGroups

## Building

* Linux requires: `libpcsclite1 libpcsclite-dev`

* macOS 10.14 should just work, as long as you have Xcode. Kotlin/Native typically requires the
  latest version of Xcode.

* Windows: Not working yet

## Tests

`ContextTest` requires a connected PC/SC compatible reader, and a card connected to it.

TODO: Make this work even if there is no card attached.

## Running

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
