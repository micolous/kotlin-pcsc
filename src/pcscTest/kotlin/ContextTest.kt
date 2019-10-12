package au.id.micolous.kotlin.pcsc.test

import au.id.micolous.kotlin.pcsc.Context
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ContextTest {
    @Test
    fun testUserContext() {
        val context = Context.establish(Context.Scope.User)
        assertTrue(context.isValid())
        context.release()
    }

    @Test
    fun testSystemContext() {
        val context = Context.establish(Context.Scope.System)
        assertTrue(context.isValid())
        context.release()
    }

    @Test
    fun testTerminalContext() {
        val context = Context.establish(Context.Scope.Terminal)
        assertTrue(context.isValid())
        context.release()
    }

    @Test
    fun testListReaders() {
        val context = Context.establish(Context.Scope.User)
        assertEquals(emptyList(), context.listReaders())
        context.release()
    }
}