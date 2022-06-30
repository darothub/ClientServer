package com.darothub.applicationb

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

class ConnectionService(val serverSocket: ServerSocket, val action:(String)->Unit)  : Runnable {

    companion object {
        val state = MutableSharedFlow<ServiceState>()
    }

    override fun run() {
        Thread {
            while (true) {
                Log.d("Service", "StartedUP")
                try {
                    val socket = serverSocket?.accept()!!
                    Log.d("Service", "Started")
                    CoroutineScope(IO).launch {
                        state.emit(ServiceState.SocketData(socket))
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("Service", "Cancelled")
                    CoroutineScope(IO).launch {
                        state.emit(ServiceState.ErrorState(e))
                    }
                }
            }
        }.start()
    }
}
sealed class ServiceState {
    data class SocketData(val socket:Socket) : ServiceState()
    data class ErrorState(val error:Exception): ServiceState()
    object Nothing: ServiceState()
}
