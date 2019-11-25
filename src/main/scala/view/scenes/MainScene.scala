package view.scenes

import scalafx.scene.control.Button
import scalafx.scene.layout.BorderPane
import scalafx.stage.Stage
import scalafx.Includes._
import view.map


class MainScene(override val parentStage: Stage) extends BaseScene {

  stylesheets.add("style.css")
  root = new BorderPane {
    styleClass.add("common")
    id = "mainPane"
    bottom = new Button {
      id = "playButton"
      translateX = 530
      translateY = -45
      onAction = handle {
        changeScene(map(parentStage).getScene())
      }
    }
  }

}

object MainScene {
  def apply(parentStage: Stage) = new MainScene(parentStage)
}
