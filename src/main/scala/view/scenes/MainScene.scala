package view.scenes

import controller.{GameController, OperationType}
import scalafx.Includes._
import scalafx.scene.control.Button
import scalafx.scene.layout.BorderPane
import scalafx.stage.Stage


class MainScene(override val parentStage: Stage) extends BaseScene {
  private val gameController: GameController = GameController(this.parentStage)


  stylesheets.add("style.css")
  root = new BorderPane {
    styleClass.add("common")
    id = "mainPane"
    bottom = new Button {
      id = "playButton"
      translateX = 530
      translateY = -45
      onAction = handle {
        gameController.setUserInformation(OperationType.NewGame)
        gameController.setMapScene

      }
    }
  }

}

object MainScene {
  def apply(parentStage: Stage) = new MainScene(parentStage)
}
