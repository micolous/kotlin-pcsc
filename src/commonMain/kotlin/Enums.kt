package au.id.micolous.kotlin.pcsc

expect enum class Scope {
    User,
    Terminal,
    System
}

expect enum class ShareMode {
    Shared,
    Exclusive,
    Direct
}

expect enum class Protocol {
    Unset,
    Any,
    T0,
    T1,
    T15,
    Raw,
    Undefined
}

expect enum class DisconnectDisposition {
    Leave,
    Reset,
    Unpower,
    Eject
}

expect enum class Initialization {
    Leave,
    Reset,
    Unpower
}
