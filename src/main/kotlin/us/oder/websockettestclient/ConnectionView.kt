package us.oder.websockettestclient

import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TextField
import tornadofx.*

interface ConnectionViewListener {
    fun onConnectClick(url: String)
    fun onDisconnectClick()
}

class ConnectionView(val listener: ConnectionViewListener): View() {

    private val connectionStatusObservable = SimpleStringProperty("Disconnected")
    private val buttonDisplayString = SimpleStringProperty("Connect")
    private val urlObservable = SimpleStringProperty()
    private var connected: Boolean = false
    private var urlField: TextField? = null

    var url: String
        get() = urlObservable.value
        set(value) {
            urlObservable.value = value
        }

    override val root = vbox {
        hbox {
            label("Connection Status: ")
            label("Disconnected").bind(connectionStatusObservable)
        }
        hbox {
            label("URL: ")
            textfield {
                urlField = this
                prefWidth = 600.0
            }.bind(urlObservable)
        }
        button("Connect").apply {
            action {
                println("Connected: ${connected}")
                if (connected) {
                    println("Disconnecting")
                    listener.onDisconnectClick()
                } else {
                    println("Connecting to ${url}")
                    listener.onConnectClick(url)
                }
            }
        }.bind(buttonDisplayString)
    }

    fun setConnectionStatus(connected: Boolean, message: String?) {
        println("setConnectionStatus: ${connected}")
        urlField?.isDisable = connected
        this.connected = connected
        var messageString = ""
        var buttonText = ""
        if (connected) {
            buttonText += "Disconnect"
            messageString += "Connected: ${urlObservable.value}"
        } else {
            buttonText += "Connect"
            messageString += "Disconnected"
        }
        if (message != null) {
            messageString += ": ${message}"
        }
        connectionStatusObservable.value = messageString
        buttonDisplayString.value = buttonText
    }

}

