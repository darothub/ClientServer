package com.darothub.applicationa

import android.os.HandlerThread
import java.net.Socket

class InputFrame (
    val clientSocket: Socket,
    val senderConverter: SenderConverter,
    val msg: String
) : HandlerThread("InputFrame") {
    override fun run() {
        senderConverter(clientSocket, msg)
    }
}

