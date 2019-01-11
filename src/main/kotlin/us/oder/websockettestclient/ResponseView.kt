package us.oder.websockettestclient

import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Button
import tornadofx.*

class ResponseView: View() {

    private val responseMessageObservable = SimpleStringProperty()
    private val firstButton = Button("First")
    private val prevButton = Button("<")
    private val nextButton = Button(">")
    private val lastButton = Button("Last")
    private var responseArray: Array<String> = emptyArray()
    private var currentResponseIndex = 0

    fun addResponseMessage(message: String?) {
        if (message == null) {
            return
        }
        var isViewingLatest = currentResponseIndex >= responseArray.lastIndex
        responseArray += message

        if (isViewingLatest) {
            currentResponseIndex = responseArray.lastIndex
            responseMessageObservable.value = message
        }

        updateButtons()
    }

    override val root = vbox {
        textarea {
            prefHeight = 200.0
        }.bind(responseMessageObservable)
        hbox {
            this += firstButton
            this += prevButton
            this += nextButton
            this += lastButton
        }
    }

    fun updateButtons() {
        firstButton.isDisable = responseArray.size < 2
        prevButton.isDisable = responseArray.size < 2 || currentResponseIndex <= 0
        nextButton.isDisable = responseArray.size < 2 || currentResponseIndex >= responseArray.lastIndex
        lastButton.isDisable = responseArray.size < 2
    }

    init {
        updateButtons()
        firstButton.setOnAction {
            currentResponseIndex = 0
            responseMessageObservable.value = responseArray[currentResponseIndex]
            updateButtons()
        }
        prevButton.setOnAction {
            currentResponseIndex -= 1
            responseMessageObservable.value = responseArray[currentResponseIndex]
            updateButtons()
        }

        nextButton.setOnAction {
            currentResponseIndex += 1
            responseMessageObservable.value = responseArray[currentResponseIndex]
            updateButtons()
        }
        lastButton.setOnAction {
            currentResponseIndex = responseArray.lastIndex
            responseMessageObservable.value = responseArray[currentResponseIndex]
            updateButtons()
        }


    }

}
