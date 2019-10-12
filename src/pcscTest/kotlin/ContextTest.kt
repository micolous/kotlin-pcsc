package au.id.micolous.kotlin.pcsc.test

import au.id.micolous.kotlin.pcsc.*
import kotlin.test.Test
import kotlin.test.assertEquals
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
        assertEquals(listOf("Yubico Yubikey 4 U2F+CCID"), context.listReaders())
        context.release()
    }

    @Test
    fun testConnectToFirstReader() {
        val context = Context.establish()
        val firstReader = context.listReaders()[0]
        val (card, protocol) = context.connect(firstReader, ShareMode.Direct, Protocol.Any)
        assertNotNull(protocol)
        val protocol2 = card.reconnect(ShareMode.Direct, initialization = Initialization.Leave)
        assertNotNull(protocol2)
        card.disconnect()
        context.release()
    }
}