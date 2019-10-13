package au.id.micolous.kotlin.pcsc

data class PCSCError(val errorCode: Long) : Throwable("PCSCError: $errorCode")

