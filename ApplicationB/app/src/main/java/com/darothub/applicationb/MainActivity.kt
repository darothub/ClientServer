package com.darothub.applicationb


import android.annotation.SuppressLint
import android.graphics.Color
import android.os.*
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.darothub.applicationb.databinding.ActivityMainBinding
import com.darothub.applicationb.utils.CommunicationFrame
import com.darothub.applicationb.utils.Constants
import com.darothub.applicationb.utils.convertHexStringToReadableMessage
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import viewBinding
import java.io.*
import java.math.BigInteger
import java.net.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityMainBinding::inflate)
    private var tempClientSocket: Socket? = null
    var serverThread: Thread? = null
    private var handler: Handler? = null
    private val senderConverter by lazy { SenderConverter() }
    private val queueThread by lazy { HandlerThread("QueueThread") }
    private val queueHandler by lazy { Handler(queueThread.looper) }
    private var connectionService:ConnectionService?=null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)
        title = "Application B"
        handler = Handler(Looper.getMainLooper())
        queueThread.start()
        lifecycleScope.launch {
            handleServiceState()
        }
        with(binding){
            startServer.setOnClickListener {
                with(binding.msgList) { removeAllViews() }
                showMessage("Application B started.", Color.BLACK)
                val serverSocket = ServerSocket(Constants.SERVER_PORT)
                connectionService = ConnectionService(serverSocket) {
                    Log.d("Main", "String")
                    CoroutineScope(Main).launch {
                        binding.startServer.visibility = View.GONE
                    }
                }
                Thread(connectionService).start()
            }
            msgInputLayout.setEndIconOnClickListener {
                val msg = msgInputEt.text.toString().trim { it <= ' ' }
                val hex = convertMsgToHex(msg)
                tempClientSocket?.let {
                    InputFrame(it, senderConverter, hex).start()
                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun handleServiceState() {
        ConnectionService.state.collect { state ->
            when (state) {
                is ServiceState.SocketData -> {
                    withContext(Main) {
                        binding.startServer.visibility = View.GONE
                    }
                    val commThread = CommunicationFrame(state.socket) { hex ->
                        val readable = convertHexStringToReadableMessage(hex)
                        showMessage("Message : $readable", Constants.green)
                        senderConverter(tempClientSocket, "$readable")
                    }
                    queueHandler.post(commThread)
                    tempClientSocket = state.socket
                    showMessage("Connected to Client!!", Constants.green)
                }
                is ServiceState.ErrorState ->
                    showMessage("Error Communicating to Client :" + state.error, Constants.red)
                else -> {}
            }
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
    private fun sendMessage(message: String) {
        try {
           tempClientSocket?.let {
               thread {
                   val out = PrintWriter(
                       BufferedWriter(
                           OutputStreamWriter(it?.getOutputStream())
                       ),
                       true
                   )
                   out.println(message)
               }.start()
           }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
        if (null != serverThread) {
            sendMessage("Disconnect")
            serverThread!!.interrupt()
            serverThread = null
        }
    }
}

fun main(){
    val hexV =  convertMsgToHex("hey")
    println("HexV $hexV")
    val ba1 = hexV.decodeHex()
    println("ba1 $ba1")
    val bn1 = BigInteger(1, ba1)
    println("bn1 $bn1")
    val str1 = String(bn1.toByteArray())
    println(str1)
}

fun convertMsgToHex(message: String):String{
    val ba = message.toByteArray()
    println("byteArray $ba")
    val bn = BigInteger(1, ba)
    println("bigNumber $bn")
    val hexV = String.format("%x", bn)
    println("Hex $hexV")
    return  hexV
}

fun String.decodeHex(): ByteArray {
    check(length % 2 == 0) { "Must have an even length" }

    return chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}