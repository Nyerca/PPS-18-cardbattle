package view.scenes

import scalafx.scene.Scene
import scalafx.scene.control.{Button}
import scalafx.scene.layout.BorderPane
import scalafx.stage.Stage
import scalafx.Includes._
import scalafx.scene.Scene

class MainScene(val parentStage: Stage) extends Scene{

  stylesheets.add("style.css")

  root = new BorderPane {
    styleClass.add("common")
    id = "mainPane"
    bottom = new Button {
      id = "playButton"
      translateX = 530
      translateY = -45
      onAction = handle {
        
      }
    }
  }
}

object MainScene {
  def apply(parentStage: Stage) = new MainScene(parentStage)
}
