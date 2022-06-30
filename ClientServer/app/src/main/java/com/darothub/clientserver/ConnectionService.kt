package com.darothub.clientserver

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.darothub.clientserver.utils.Constants
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

class ConnectionService  : Service() {
    private var serverSocket: ServerSocket? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            serverSocket = ServerSocket(Constants.SERVER_PORT)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (null != serverSocket) {
            Thread {
                while (true) {
                    Log.d("Service", "StartedUP")
                    try {
                        val socket = serverSocket?.accept()!!
                        Log.d("Service", "Started")
                        state.value = ServiceState.SocketData(socket)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Log.d("Service", "Cancelled")
                        state.value = ServiceState.ErrorState(e)
                    }
                }
            }.start()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? = null
    companion object {
        val state = MutableStateFlow<ServiceState>(ServiceState.Nothing)
    }
}
sealed class ServiceState {
    data class SocketData(val socket:Socket) : ServiceState()
    data class ErrorState(val error:Exception): ServiceState()
    object Nothing: ServiceState()
}
