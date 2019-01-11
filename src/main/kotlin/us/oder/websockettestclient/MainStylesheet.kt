package us.oder.websockettestclient

import javafx.scene.text.FontWeight
import tornadofx.Stylesheet
import tornadofx.px

class MainStylesheet : Stylesheet() {
    init {
        label {
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
        }
    }
}
