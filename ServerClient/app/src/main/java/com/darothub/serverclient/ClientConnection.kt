package com.darothub.serverclient

import android.graphics.Color
import android.util.Log
import com.darothub.serverclient.utils.Constants
import com.darothub.serverclient.utils.decodeHex
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.*
import java.math.BigInteger
import java.net.InetAddress
import java.net.Socket
import java.net.UnknownHostException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

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
