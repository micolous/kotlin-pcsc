/*
 * PCSCError.kt
 * Error class for PC/SC API
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

data class PCSCError private constructor(
    val code: Long,
    /** Canonical enumeration for this error condition */
    val error: PCSCErrorCode?) : Throwable() {
    internal constructor(error: PCSCErrorCode) : this(error.code, error)

    override val message: String
        get() = error?.message ?: "UNKNOWN (0x${code.toString(16)})"

    override fun toString() = "PCSCError: $message"

    companion object {
        /** Converts a numeric error code to a [PCSCError] */
        internal fun fromCode(errorCode: Long) : PCSCError {
            return errorCode.toPCSCErrorCode()?.let {
                PCSCError(it)
            } ?: PCSCError(errorCode, null)
        }
    }
}

enum class PCSCErrorCode(
    /** Numeric error code */
    val code: Long,
    /** Description of the error condition */
    val description: String) {
    /** `0x00000000`: No error was encountered */
    S_SUCCESS(0x00000000L, "No error was encountered"),

    /** `0x80100001`: An internal consistency check failed */
    F_INTERNAL_ERROR(0x80100001L, "An internal consistency check failed"),

    /** `0x80100002`: The action was cancelled by an SCardCancel request */
    E_CANCELLED(0x80100002L, "The action was cancelled by an SCardCancel request"),

    /** `0x80100003`: The supplied handle was invalid */
    E_INVALID_HANDLE(0x80100003L, "The supplied handle was invalid"),

    /** `0x80100004`: One or more of the supplied parameters could not be properly interpreted */
    E_INVALID_PARAMETER(0x80100004L, "One or more of the supplied parameters could not be properly interpreted"),

    /** `0x80100005`: Registry startup information is missing or invalid */
    E_INVALID_TARGET(0x80100005L, "Registry startup information is missing or invalid"),

    /** `0x80100006`: Not enough memory available to complete this command */
    E_NO_MEMORY(0x80100006L, "Not enough memory available to complete this command"),

    /** `0x80100007`: An internal consistency timer has expired */
    F_WAITED_TOO_LONG(0x80100007L, "An internal consistency timer has expired"),

    /** `0x80100008`: The data buffer to receive returned data is too small for the returned data */
    E_INSUFFICIENT_BUFFER(0x80100008L, "The data buffer to receive returned data is too small for the returned data"),

    /** `0x80100009`: The specified reader name is not recognized */
    E_UNKNOWN_READER(0x80100009L, "The specified reader name is not recognized"),

    /** `0x8010000A`: The user-specified timeout value has expired */
    E_TIMEOUT(0x8010000AL, "The user-specified timeout value has expired"),

    /** `0x8010000B`: The smart card cannot be accessed because of other connections outstanding */
    E_SHARING_VIOLATION(0x8010000BL, "The smart card cannot be accessed because of other connections outstanding"),

    /** `0x8010000C`: The operation requires a Smart Card, but no Smart Card is currently in the device */
    E_NO_SMARTCARD(0x8010000CL, "The operation requires a Smart Card, but no Smart Card is currently in the device"),

    /** `0x8010000D`: The specified smart card name is not recognized */
    E_UNKNOWN_CARD(0x8010000DL, "The specified smart card name is not recognized"),

    /** `0x8010000E`: The system could not dispose of the media in the requested manner */
    E_CANT_DISPOSE(0x8010000EL, "The system could not dispose of the media in the requested manner"),

    /** `0x8010000F`: The requested protocols are incompatible with the protocol currently in use with the smart card */
    E_PROTO_MISMATCH(0x8010000FL, "The requested protocols are incompatible with the protocol currently in use with the smart card"),

    /** `0x80100010`: The reader or smart card is not ready to accept commands */
    E_NOT_READY(0x80100010L, "The reader or smart card is not ready to accept commands"),

    /** `0x80100011`: One or more of the supplied parameters values could not be properly interpreted */
    E_INVALID_VALUE(0x80100011L, "One or more of the supplied parameters values could not be properly interpreted"),

    /** `0x80100012`: The action was cancelled by the system, presumably to log off or shut down */
    E_SYSTEM_CANCELLED(0x80100012L, "The action was cancelled by the system, presumably to log off or shut down"),

    /** `0x80100013`: An internal communications error has been detected */
    F_COMM_ERROR(0x80100013L, "An internal communications error has been detected"),

    /** `0x80100014`: An internal error has been detected, but the source is unknown */
    F_UNKNOWN_ERROR(0x80100014L, "An internal error has been detected, but the source is unknown"),

    /** `0x80100015`: An ATR obtained from the registry is not a valid ATR string */
    E_INVALID_ATR(0x80100015L, "An ATR obtained from the registry is not a valid ATR string"),

    /** `0x80100016`: An attempt was made to end a non-existent transaction */
    E_NOT_TRANSACTED(0x80100016L, "An attempt was made to end a non-existent transaction"),

    /** `0x80100017`: The specified reader is not currently available for use */
    E_READER_UNAVAILABLE(0x80100017L, "The specified reader is not currently available for use"),

    /** `0x80100018`: The operation has been aborted to allow the server application to exit */
    P_SHUTDOWN(0x80100018L, "The operation has been aborted to allow the server application to exit"),

    /** `0x80100019`: The PCI Receive buffer was too small */
    E_PCI_TOO_SMALL(0x80100019L, "The PCI Receive buffer was too small"),

    /** `0x8010001A`: The reader driver does not meet minimal requirements for support */
    E_READER_UNSUPPORTED(0x8010001AL, "The reader driver does not meet minimal requirements for support"),

    /** `0x8010001B`: The reader driver did not produce a unique reader name */
    E_DUPLICATE_READER(0x8010001BL, "The reader driver did not produce a unique reader name"),

    /** `0x8010001C`: The smart card does not meet minimal requirements for support */
    E_CARD_UNSUPPORTED(0x8010001CL, "The smart card does not meet minimal requirements for support"),

    /** `0x8010001D`: The Smart card resource manager is not running */
    E_NO_SERVICE(0x8010001DL, "The Smart card resource manager is not running"),

    /** `0x8010001E`: The Smart card resource manager has shut down */
    E_SERVICE_STOPPED(0x8010001EL, "The Smart card resource manager has shut down"),

    /** `0x8010001F`: An unexpected card error has occurred */
    E_UNEXPECTED(0x8010001FL, "An unexpected card error has occurred"),

    /** `0x8010001F`: This smart card does not support the requested feature */
    E_UNSUPPORTED_FEATURE(0x8010001FL, "This smart card does not support the requested feature"),

    /** `0x80100020`: No primary provider can be found for the smart card */
    E_ICC_INSTALLATION(0x80100020L, "No primary provider can be found for the smart card"),

    /** `0x80100021`: The requested order of object creation is not supported */
    E_ICC_CREATEORDER(0x80100021L, "The requested order of object creation is not supported"),

    /** `0x80100023`: The identified directory does not exist in the smart card */
    E_DIR_NOT_FOUND(0x80100023L, "The identified directory does not exist in the smart card"),

    /** `0x80100024`: The identified file does not exist in the smart card */
    E_FILE_NOT_FOUND(0x80100024L, "The identified file does not exist in the smart card"),

    /** `0x80100025`: The supplied path does not represent a smart card directory */
    E_NO_DIR(0x80100025L, "The supplied path does not represent a smart card directory"),

    /** `0x80100026`: The supplied path does not represent a smart card file */
    E_NO_FILE(0x80100026L, "The supplied path does not represent a smart card file"),

    /** `0x80100027`: Access is denied to this file */
    E_NO_ACCESS(0x80100027L, "Access is denied to this file"),

    /** `0x80100028`: The smart card does not have enough memory to store the information */
    E_WRITE_TOO_MANY(0x80100028L, "The smart card does not have enough memory to store the information"),

    /** `0x80100029`: There was an error trying to set the smart card file object pointer */
    E_BAD_SEEK(0x80100029L, "There was an error trying to set the smart card file object pointer"),

    /** `0x8010002A`: The supplied PIN is incorrect */
    E_INVALID_CHV(0x8010002AL, "The supplied PIN is incorrect"),

    /** `0x8010002B`: An unrecognized error code was returned from a layered component */
    E_UNKNOWN_RES_MNG(0x8010002BL, "An unrecognized error code was returned from a layered component"),

    /** `0x8010002C`: The requested certificate does not exist */
    E_NO_SUCH_CERTIFICATE(0x8010002CL, "The requested certificate does not exist"),

    /** `0x8010002D`: The requested certificate could not be obtained */
    E_CERTIFICATE_UNAVAILABLE(0x8010002DL, "The requested certificate could not be obtained"),

    /** `0x8010002E`: Cannot find a smart card reader */
    E_NO_READERS_AVAILABLE(0x8010002EL, "Cannot find a smart card reader"),

    /** `0x8010002F`: A communications error with the smart card has been detected. More.. */
    E_COMM_DATA_LOST(0x8010002FL, "A communications error with the smart card has been detected. More.."),

    /** `0x80100030`: The requested key container does not exist on the smart card */
    E_NO_KEY_CONTAINER(0x80100030L, "The requested key container does not exist on the smart card"),

    /** `0x80100031`: The Smart Card Resource Manager is too busy to complete this operation */
    E_SERVER_TOO_BUSY(0x80100031L, "The Smart Card Resource Manager is too busy to complete this operation"),

    /** `0x80100065`: The reader cannot communicate with the card, due to ATR string configuration conflicts */
    W_UNSUPPORTED_CARD(0x80100065L, "The reader cannot communicate with the card, due to ATR string configuration conflicts"),

    /** `0x80100066`: The smart card is not responding to a reset */
    W_UNRESPONSIVE_CARD(0x80100066L, "The smart card is not responding to a reset"),

    /** `0x80100067`: Power has been removed from the smart card, so that further communication is not possible */
    W_UNPOWERED_CARD(0x80100067L, "Power has been removed from the smart card, so that further communication is not possible"),

    /** `0x80100068`: The smart card has been reset, so any shared state information is invalid */
    W_RESET_CARD(0x80100068L, "The smart card has been reset, so any shared state information is invalid"),

    /** `0x80100069`: The smart card has been removed, so further communication is not possible */
    W_REMOVED_CARD(0x80100069L, "The smart card has been removed, so further communication is not possible"),

    /** `0x8010006A`: Access was denied because of a security violation */
    W_SECURITY_VIOLATION(0x8010006AL, "Access was denied because of a security violation"),

    /** `0x8010006B`: The card cannot be accessed because the wrong PIN was presented */
    W_WRONG_CHV(0x8010006BL, "The card cannot be accessed because the wrong PIN was presented"),

    /** `0x8010006C`: The card cannot be accessed because the maximum number of PIN entry attempts has been reached */
    W_CHV_BLOCKED(0x8010006CL, "The card cannot be accessed because the maximum number of PIN entry attempts has been reached"),

    /** `0x8010006D`: The end of the smart card file has been reached */
    W_EOF(0x8010006DL, "The end of the smart card file has been reached"),

    /** `0x8010006E`: The user pressed "Cancel" on a Smart Card Selection Dialog */
    W_CANCELLED_BY_USER(0x8010006EL, "The user pressed \"Cancel\" on a Smart Card Selection Dialog"),

    /** `0x8010006F`: No PIN was presented to the smart card */
    W_CARD_NOT_AUTHENTICATED(0x8010006FL, "No PIN was presented to the smart card");

    val message: String = "${toString()} (0x${code.toString(16)}): $description"
}

private fun Long.toPCSCErrorCode(): PCSCErrorCode? =
    PCSCErrorCode.values().find { it.code == this }
