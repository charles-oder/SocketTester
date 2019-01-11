package us.oder.websockettestclient

import com.google.gson.Gson
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import tornadofx.*
import java.util.prefs.Preferences

interface RequestViewListener {
    fun sendMessage(message: String)
}

data class Request(var name: String, var body: String)
data class RequestEnvelope(var items: Array<Request>?)

class RequestView(val listener: RequestViewListener): View() {

    private val preferences = Preferences.userNodeForPackage(RequestView::class.java)
    private val savedRequestsKey = "saved-requests-key"

    private val requestMessageObservable = SimpleStringProperty()
    private val firstButton = Button("First")
    private val prevButton = Button("<")
    private val nextButton = Button(">")
    private val lastButton = Button("Last")
    private val saveButton = Button("Save")
    private val deleteButton = Button("Delete")
    private val savedItemSelector = ComboBox<String>()
    private var requestArray: Array<Request> = emptyArray()
    private var currentRequestIndex = 0
    private var currentRequestName = SimpleStringProperty()
    private var savedRequestArray: Array<Request>
        get() {
            val json = preferences.get(savedRequestsKey, "{}")
            val envelope = Gson().fromJson(json, RequestEnvelope::class.java)
            println("Fetching JSON: $json")
            return envelope.items ?: emptyArray()
        }
        set(value) {
            val envelope = RequestEnvelope(value)
            val json = Gson().toJson(envelope)
            println("Storing JSON: $json")
            preferences.put(savedRequestsKey, json)
        }

    override val root = vbox {
        textarea {
            prefHeight = 1000.0
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
            pane { prefWidth = 20.0 }
            textfield().bind(currentRequestName)
            this += saveButton
            this += deleteButton
            this += savedItemSelector
        }

    }

    fun addRequest(message: String) {
        val requestName = "Request ${requestArray.size + 1}"
        currentRequestName.value = requestName
        requestArray += Request(requestName, message)
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
        savedItemSelector.items.addAll(savedRequestArray.map { item -> item.name })
        updateButtons()
        savedItemSelector.setOnAction {
            val selectedItem = savedRequestArray.firstOrNull { item -> item.name == savedItemSelector.selectedItem }
            if (selectedItem == null) {
                return@setOnAction
            }
            currentRequestName.value = selectedItem.name
            requestMessageObservable.value = selectedItem.body
        }
        deleteButton.setOnAction {
            if (currentRequestName.value.isNullOrEmpty()) {
                return@setOnAction
            }
            var newRequestArray = savedRequestArray.filter { item -> item.name != currentRequestName.value }.toTypedArray()
            savedRequestArray = newRequestArray
            savedItemSelector.items.clear()
            savedItemSelector.items.addAll(savedRequestArray.map { item -> item.name })
        }
        saveButton.setOnAction {
            if (currentRequestName.value.isNullOrEmpty() || requestMessageObservable.value.isNullOrEmpty()) {
                return@setOnAction
            }
            val request = Request(currentRequestName.value, requestMessageObservable.value)
            var newRequestArray = savedRequestArray.filter { item -> item.name != currentRequestName.value }.toTypedArray()
            newRequestArray += request
            savedRequestArray = newRequestArray
            savedItemSelector.items.clear()
            savedItemSelector.items.addAll(savedRequestArray.map { item -> item.name })
        }
        firstButton.setOnAction {
            currentRequestIndex = 0
            requestMessageObservable.value = requestArray[currentRequestIndex].body
            updateButtons()
        }
        prevButton.setOnAction {
            currentRequestIndex -= 1
            requestMessageObservable.value = requestArray[currentRequestIndex].body
            updateButtons()
        }

        nextButton.setOnAction {
            currentRequestIndex += 1
            requestMessageObservable.value = requestArray[currentRequestIndex].body
            updateButtons()
        }
        lastButton.setOnAction {
            currentRequestIndex = requestArray.lastIndex
            requestMessageObservable.value = requestArray[currentRequestIndex].body
            updateButtons()
        }


    }
}

