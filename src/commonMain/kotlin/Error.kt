package au.id.micolous.kotlin.pcsc

data class Error(val errorCode: Int) : Throwable("PCSCError: $errorCode")

