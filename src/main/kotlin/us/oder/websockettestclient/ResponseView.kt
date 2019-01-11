package us.oder.websockettestclient

import javafx.beans.property.SimpleStringProperty
import tornadofx.View
import tornadofx.bind
import tornadofx.textarea
import tornadofx.vbox

class ResponseView: View() {

    private val responseMessageObservable = SimpleStringProperty()

    fun addResponseMessage(message: String?) {
        responseMessageObservable.value = message
    }

    override val root = vbox {
        textarea {
            prefHeight = 200.0
        }.bind(responseMessageObservable)
    }
}
