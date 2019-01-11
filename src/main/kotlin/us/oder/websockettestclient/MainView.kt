package us.oder.websockettestclient

import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import us.oder.websockettestclient.websocket.NeoWebSocketService
import us.oder.websockettestclient.websocket.WebsocketService
import us.oder.websockettestclient.websocket.WebsocketServiceListener
import java.io.IOException

class MainView : View() {

    val urlObservable = SimpleStringProperty()
    var connectedStatus = false
    val connectionStatusObservable = SimpleStringProperty("Disconnected")
    val responseMessageObservable = SimpleStringProperty()
    val requestMessageObservable = SimpleStringProperty()

    var service: WebsocketService? = null

    override val root = vbox {
        prefHeight = 600.0
        prefWidth = 800.0
        hbox {
            label("Connection Status: ")
            label("Disconnected").bind(connectionStatusObservable)
        }
        hbox {
            label("URL: ")
            textfield {
                prefWidth = 600.0
            }.bind(urlObservable)
        }
        button("Connect").apply {
            action {
                println("Connecting to ${urlObservable.value}")
                connectedStatus = !connectedStatus
                onConnectButtonClick()
                this.text = buttonText(connectedStatus)
            }
        }
        pane {
            prefHeight = 20.0
        }
        textarea {
            prefHeight = 200.0
        }.bind(requestMessageObservable)
        button("Send").apply {
            action {
                sendMessage()
            }
        }
        pane {
            prefHeight = 20.0
        }
        textarea().bind(responseMessageObservable)
    }

    fun sendMessage() {
        service?.sendMessage(requestMessageObservable.value)
    }

    fun updateConnectionStatus(connected: Boolean) {
        if (connected) {
            connectionStatusObservable.value = "Connected"
        } else {
            connectionStatusObservable.value = "Disconnected"
        }
    }

    fun onConnectButtonClick() {
        if (connectedStatus) {
            service?.disconnect()
            service = createWebsocketService(urlObservable.value)
            service?.connect()
        } else {
            service?.disconnect()
        }
    }

    fun createWebsocketService(url: String): WebsocketService {
        var ws = NeoWebSocketService(url)
        ws.addListener(object : WebsocketServiceListener {
            override fun handleMessage(message: String): Boolean {
                Platform.runLater {
                    responseMessageObservable.value = message
                }
                return true
            }

            override fun onConnected(websocketService: WebsocketService) {
                Platform.runLater {
                    connectedStatus = true
                    updateConnectionStatus(true)
                }
            }

            override fun onDisconnected(websocketService: WebsocketService,
                                        code: Int,
                                        reason: String,
                                        closedByServer: Boolean
            ) {
                Platform.runLater {
                    connectedStatus = false
                    updateConnectionStatus(false)
                }
            }

            override fun onSocketError(websocketService: WebsocketService, exception: IOException) {
                Platform.runLater {
                    connectedStatus = false
                    updateConnectionStatus(false)
                }
            }

            override fun onErrorMessage(message: String) {
                Platform.runLater {
                    connectedStatus = false
                    updateConnectionStatus(false)
                }
            }

        })
        return ws
    }

    fun buttonText(status: Boolean): String {
        if (connectedStatus) {
            return "Disconnect"
        } else {
            return "Connect"
        }
    }

    init {
        title = "Websocket Test Client"
    }


}
