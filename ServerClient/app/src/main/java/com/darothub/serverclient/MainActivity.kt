package com.darothub.serverclient

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.darothub.serverclient.databinding.ActivityMainBinding
import com.darothub.serverclient.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import viewBinding
import java.io.*
import java.math.BigInteger
import java.net.InetAddress
import java.net.Socket
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityMainBinding::inflate)
    private var clientConnection:ClientConnection?=null
    private var thread: Thread? = null
    private var handler: Handler? = null
    private var socket:Socket? = null
    private val senderConverter by lazy { SenderConverter() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)
        handler = Handler()

        binding.connectServer.setOnClickListener {
            binding.msgList.removeAllViews()
            CoroutineScope(IO).launch {
                val serverAddr: InetAddress = InetAddress.getByName(Constants.SERVER_IP)
                socket = Socket(serverAddr, Constants.SERVER_PORT)
            }
            showMessage("Connected to Server...", Color.RED)
        }
        binding.sendData.setOnClickListener {
            val clientMessage = binding.edMessage.text.toString().trim { it <= ' ' }
            socket?.let { sock -> InputFrame(sock, senderConverter, clientMessage).start() }
            clientConnection = socket?.let { it1 ->
                ClientConnection(it1) { readable->
                    showMessage("Server: $readable", Color.GREEN)
                }
            }
            thread = Thread(clientConnection)
            thread?.start()
            showMessage(clientMessage, Color.BLUE)
//            clientConnection?.sendMessage(clientMessage)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun textView(message: String?, color: Int): TextView? {
        var message = message
        if (null == message || message.trim { it <= ' ' }.isEmpty()) {
            message = "<Empty Message>"
        }
        val tv = TextView(this)
        tv.setTextColor(color)
        tv.text = message + " [" + getTime() + "]"
        tv.textSize = 20f
        tv.setPadding(0, 5, 0, 0)
        return tv
    }
    fun showMessage(message: String?, color: Int) {
        handler!!.post { binding.msgList.addView(textView(message, color)) }
    }
    private fun getTime(): String? {
        val sdf = SimpleDateFormat("HH:mm:ss")
        return sdf.format(Date())
    }

    override fun onDestroy() {
        super.onDestroy()
        thread?.interrupt()
    }
}


