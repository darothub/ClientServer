package com.darothub.serverclient.utils

import java.math.BigInteger

fun String.decodeHex(): ByteArray {
    check(length % 2 == 0) { "Must have an even length" }

    return chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}

fun convertMsgToHex(message: String):String{
    val ba = message.toByteArray()
    println("byteArray $ba")
    val bn = BigInteger(1, ba)
    println("bigNumber $bn")
    val hexV = String.format("%x", bn)
    println("Hex $hexV")
    return  hexV
}