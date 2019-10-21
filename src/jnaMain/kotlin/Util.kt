/*
 * Util.kt
 * JNA utility functions for native interop
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

import java.nio.ByteBuffer

internal fun ByteBuffer.getMultiString(length: Int): Sequence<String> {
    return getByteArray(length).toMultiString()
}

internal fun ByteBuffer.getByteArray(length: Int): ByteArray {
    val out = ByteArray(length)
    get(out)
    return out
}
