package com.darothub.applicationb.utils

import android.util.Log
import java.math.BigInteger

fun convertHexStringToReadableMessage(message: String): String {
    Log.d("ConversionMessage", "$message")
    val ba = message.decodeHex()
    Log.d("ConversionBA", "$ba")
    val bn = BigInteger(1, ba)
    Log.d("ConversionBN", "$bn")
    val str = String(bn.toByteArray())
    Log.d("ConversionStr", "$str")
    return str
}

fun String.decodeHex(): ByteArray {
    check(length % 2 == 0) { "Must have an even length" }

    return chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}