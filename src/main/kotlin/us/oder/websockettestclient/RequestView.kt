package us.oder.websockettestclient

import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Button
import tornadofx.*

interface RequestViewListener {
    fun sendMessage(message: String)
}

class RequestView(val listener: RequestViewListener): View() {

    private val requestMessageObservable = SimpleStringProperty()
    private val firstButton = Button("First")
    private val prevButton = Button("<")
    private val nextButton = Button(">")
    private val lastButton = Button("Last")
    private var requestArray: Array<String> = emptyArray()
    private var currentRequestIndex = 0

    override val root = vbox {
        textarea {
            prefHeight = 200.0
        }.bind(requestMessageObservable)
        hbox {
            button("Send").apply {
                action {
                    addRequest(requestMessageObservable.value)
                    listener.sendMessage(requestMessageObservable.value)
                }
            }
            this += firstButton
            this += prevButton
            this += nextButton
            this += lastButton
        }

    }

    fun addRequest(message: String) {
        requestArray += message
        currentRequestIndex = requestArray.lastIndex
        updateButtons()
    }

    fun updateButtons() {
        firstButton.isDisable = requestArray.size < 2
        prevButton.isDisable = requestArray.size < 2 || currentRequestIndex <= 0
        nextButton.isDisable = requestArray.size < 2 || currentRequestIndex >= requestArray.lastIndex
        lastButton.isDisable = requestArray.size < 2
    }

    init {
        updateButtons()
        firstButton.setOnAction {
            currentRequestIndex = 0
            requestMessageObservable.value = requestArray[currentRequestIndex]
            updateButtons()
        }
        prevButton.setOnAction {
            currentRequestIndex -= 1
            requestMessageObservable.value = requestArray[currentRequestIndex]
            updateButtons()
        }

        nextButton.setOnAction {
            currentRequestIndex += 1
            requestMessageObservable.value = requestArray[currentRequestIndex]
            updateButtons()
        }
        lastButton.setOnAction {
            currentRequestIndex = requestArray.lastIndex
            requestMessageObservable.value = requestArray[currentRequestIndex]
            updateButtons()
        }


    }
}

