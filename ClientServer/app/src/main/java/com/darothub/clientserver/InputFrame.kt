package com.darothub.clientserver

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import kotlinx.coroutines.Runnable
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.Socket

class InputFrame(
    val clientSocket: Socket,
    val senderConverter: SenderConverter,
    val msg: String
) : HandlerThread("InputFrame") {
    override fun run() {
        senderConverter(clientSocket, msg)
    }
}

