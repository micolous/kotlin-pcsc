/*
 * ContextTest.kt
 * Tests for Context operations
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
package au.id.micolous.kotlin.pcsc.test

import au.id.micolous.kotlin.pcsc.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ContextTest {
    @Test
    fun testUserContext() {
        val context = Context.establish(Scope.User)
        assertTrue(context.isValid())
        context.release()
    }

    @Test
    fun testSystemContext() {
        val context = Context.establish(Scope.System)
        assertTrue(context.isValid())
        context.release()
    }

    @Test
    fun testTerminalContext() {
        val context = Context.establish(Scope.Terminal)
        assertTrue(context.isValid())
        context.release()
    }

    @Test
    fun testListReaders() {
        val context = Context.establish()
        assertNotEquals(0, context.listReaders().size)
        context.release()
    }

    @Test
    fun testConnectToFirstReader() {
        val context = Context.establish()
        val firstReader = context.listReaders()[0]
        val card = context.connect(firstReader, ShareMode.Direct, Protocol.Any)
        assertNotNull(card.protocol)
        card.reconnect(ShareMode.Direct, initialization = Initialization.Leave)
        assertNotNull(card.protocol)
        card.disconnect()
        context.release()
    }
}