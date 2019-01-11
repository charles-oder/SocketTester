package us.oder.websockettestclient

import javafx.application.Platform
import tornadofx.View
import tornadofx.pane
import tornadofx.plusAssign
import tornadofx.vbox
import us.oder.websockettestclient.websocket.NeoWebSocketService
import us.oder.websockettestclient.websocket.WebsocketService
import us.oder.websockettestclient.websocket.WebsocketServiceListener
import java.io.IOException


class MainView : View() {


    var service: WebsocketService? = null
    val connectionView = ConnectionView(object : ConnectionViewListener {
        override fun onConnectClick(url: String) {
            service?.disconnect()
            service = createWebsocketService(url)
            service?.connect()
        }

        override fun onDisconnectClick() {
            service?.disconnect()
        }

    })

    val requestView = RequestView(object : RequestViewListener {
        override fun sendMessage(message: String) {
            service?.sendMessage(message)
        }

    })

    val responseView = ResponseView()

    override val root = vbox {
        prefHeight = 600.0
        prefWidth = 800.0
        this += connectionView
        pane {
            prefHeight = 20.0
        }
        this += requestView
        pane {
            prefHeight = 20.0
        }
        this += responseView
    }

    fun updateConnectionStatus(connected: Boolean, message: String?) {
        connectionView.setConnectionStatus(connected, null)
    }

    fun createWebsocketService(url: String): WebsocketService {
        var ws = NeoWebSocketService(url)
        ws.addListener(object : WebsocketServiceListener {
            override fun handleMessage(message: String): Boolean {
                Platform.runLater {
                    responseView.addResponseMessage(message)
                }
                return true
            }

            override fun onConnected(websocketService: WebsocketService) {
                Platform.runLater {
                    updateConnectionStatus(true, null)
                }
            }

            override fun onDisconnected(websocketService: WebsocketService,
                                        code: Int,
                                        reason: String,
                                        closedByServer: Boolean
            ) {
                Platform.runLater {
                    updateConnectionStatus(false, reason)
                }
            }

            override fun onSocketError(websocketService: WebsocketService, exception: IOException) {
                Platform.runLater {
                    updateConnectionStatus(false, exception.message)
                }
            }

            override fun onErrorMessage(message: String) {
                Platform.runLater {
                    updateConnectionStatus(false, message)
                }
            }

        })
        return ws
    }

    init {
        title = "Websocket Test Client"
    }


}
