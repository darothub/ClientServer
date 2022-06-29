package com.darothub.clientserver

import android.util.Log
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.math.BigInteger
import java.net.Socket
import kotlin.concurrent.thread

class SenderConverter {

    operator fun invoke(socket:Socket?, message: String){
        sendMessage(socket, message)
    }

    private fun sendMessage(socket:Socket?, message: String) {
        try {
            socket?.let {
                val out = PrintWriter(
                    BufferedWriter(
                        OutputStreamWriter(it?.getOutputStream())
                    ),
                    true
                )
                out.println(message)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}