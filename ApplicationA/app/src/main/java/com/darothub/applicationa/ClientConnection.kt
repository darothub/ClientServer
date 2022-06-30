package com.darothub.applicationa

import java.io.*
import java.net.Socket
import java.net.UnknownHostException

class ClientConnection(val socket: Socket, val action:(String)->Unit) : Runnable {
    private var input: BufferedReader? = null
    override fun run() {
        try {
            while (!Thread.currentThread().isInterrupted) {
                input = BufferedReader(InputStreamReader(socket!!.getInputStream()))
                var message = input?.readLine()
                if ("Disconnect".contentEquals(message)) {
                    Thread.interrupted()
                    message = "Server Disconnected."
                    action(message)
                    break
                }
                message?.let {
                    action(it)
                }

            }
        } catch (e1: UnknownHostException) {
            e1.printStackTrace()
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
    }
}
