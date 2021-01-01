/*
 * Enums.kt
 * Enumerations used by PC/SC API
 *
 * Copyright 2019 Michael Farrell <micolous+git@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    /** T=0 protocol */
    T0,
    /** T=1 protocol */
    T1,
    T15,
    Raw,
    Undefined
}

/**
 * Action to take on the card when closing the connection.
 * @see [Card.disconnect]
 */
expect enum class DisconnectDisposition {
    /** Do not do anything special when disconnecting. */
    Leave,
    /** Reset the card when disconnecting. */
    Reset,
    /** Power down the card when disconnecting. */
    Unpower,
    /** Physically eject the card when disconnecting. This may not be possible on all readers. */
    Eject
}

expect enum class Initialization {
    Leave,
    Reset,
    Unpower
}
