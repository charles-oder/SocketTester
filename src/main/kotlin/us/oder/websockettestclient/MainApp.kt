package us.oder.websockettestclient

import javafx.stage.Stage
import tornadofx.App

class MainApp : App(MainView::class, MainStylesheet::class) {

    override fun start(stage: Stage) {
        super.start(stage)
        stage.width = 800.0
        stage.height = 600.0
    }
}
