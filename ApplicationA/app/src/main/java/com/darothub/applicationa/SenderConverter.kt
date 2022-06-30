package com.darothub.applicationa

import com.darothub.applicationa.utils.convertMsgToHex
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.Socket

class SenderConverter {

    operator fun invoke(socket:Socket?, message: String){
        sendMessage(socket, convertMsgToHex(message))
    }
    private fun sendMessage(socket:Socket?, message: String) {
        Thread {
            try {
                if (null != socket) {
                    val out = PrintWriter(
                        BufferedWriter(
                            OutputStreamWriter(socket!!.getOutputStream())
                        ),
                        true
                    )
                    message
                    out.println(message)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }
}