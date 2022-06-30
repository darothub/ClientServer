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
                if (message != null) {
                    val readable = convertHexStringToReadableMessage(message)
                    action(readable)
                }
            }
        } catch (e1: UnknownHostException) {
            e1.printStackTrace()
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
    }

    fun sendMessage(message: String?) {
        Thread {
            try {
                if (null != socket) {
                    val out = PrintWriter(
                        BufferedWriter(
                            OutputStreamWriter(socket?.getOutputStream())
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

   private fun convertHexStringToReadableMessage(message: String): String {
        Log.d("ConversionMessage", "$message")
        val ba = message.decodeHex()
        Log.d("ConversionBA", "$ba")
        val bn = BigInteger(1, ba)
        Log.d("ConversionBN", "$bn")
        val str = String(bn.toByteArray())
        Log.d("ConversionStr", "$str")
        return str
    }

    companion object {
        val state = MutableStateFlow<ServiceState>(ServiceState.Nothing)
    }
}

sealed class ServiceState {
    data class SocketData(val socket:Socket) : ServiceState()
    data class ErrorState(val error:Exception): ServiceState()
    object Nothing: ServiceState()
}

@OptIn(ExperimentalContracts::class)
fun <T>assertNotNull(t:T?){
    contract {
        returns() implies (t != null)
    }
}