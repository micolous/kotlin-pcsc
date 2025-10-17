# Security Policy

Thank you for your interest in improving the security of this project!

## Supported versions

Only the latest released version is supported.

## Scope

This library allows direct access to smart cards on a number of platforms. It
aims to provide a [safe][] interface to the PC/SC API.

There are a number of factors which contribute to secure smart card usage. This
project only provides low-level access to those interfaces, and does not attempt
to implement any access control, authentication, encryption or other security
functions.

It is the responsibility of the _calling application_ to:

* Implement protocols used to communicate with the IFD or ICC (eg: ISO/IEC 7816)
* Validate and sanitise inputs used in commands to be sent to the IFD or ICC
* Validate and sanitise responses from the IFD or ICC

As a result, there is a fairly narrow scope for security-relevant issues with
_this_ library, eg: [memory safety issues][safe].

## Reporting

* Use GitHub's security tab to make a private report.

  Off-platform reports are not accepted.

* All reports must be written in English.

* All reports must be verified by a human before submission and include some
  sort of proof of concept exploiting the bug in _this_ project.

  Properly triaging and investigating issues (not just security issues) takes
  time, and this project is not funded.

* Ideally, a report should include a patch that fixes the issue. :)

There are no bounties or payments on offer.

If you wish, I may credit you with discovery of the issue. The form of this
credit is at my personal discretion.

[safe]: https://en.wikipedia.org/wiki/Memory_safety
