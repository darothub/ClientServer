package com.darothub.applicationb.utils

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.Socket

class CommunicationFrame(clientSocket: Socket, val action:(String)->Unit) : Runnable {
    private var input: BufferedReader? = null
    override fun run() {
        try {
          while (true){
              var read: String = input?.readLine().toString()
              if (null == read || "Disconnect".contentEquals(read)) {
                  Thread.interrupted()
                  read = "Client Disconnected"
                  action(read)
                break
              }
              action(read)
          }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    init {
        try {
            input = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}