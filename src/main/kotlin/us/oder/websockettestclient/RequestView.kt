package us.oder.websockettestclient

import javafx.beans.property.SimpleStringProperty
import tornadofx.*

interface RequestViewListener {
    fun sendMessage(message: String)
}

class RequestView(val listener: RequestViewListener): View() {

    val requestMessageObservable = SimpleStringProperty()

    override val root = vbox {
        textarea {
            prefHeight = 200.0
        }.bind(requestMessageObservable)
        button("Send").apply {
            action {
                listener.sendMessage(requestMessageObservable.value)
            }
        }

    }
}

