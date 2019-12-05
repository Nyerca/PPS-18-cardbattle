package view.scenes

import Utility.GUIObjectFactory
import controller.{GameController, OperationType}
import scalafx.Includes._
import scalafx.scene.control.Button
import scalafx.scene.layout.BorderPane
import scalafx.stage.Stage

class MainScene(override val parentStage: Stage) extends BaseScene {
  private val gameController: GameController = GameController()


  stylesheets.add("style.css")

  val playButton: Button = GUIObjectFactory.buttonFactory(530, -45, mouseTransparency = false, handle{
    gameController.setUserInformation(OperationType.NewGame, parentStage)
    gameController.setScene(this)
  })("playButton")
  root = new BorderPane {
    styleClass.add("common")
    id = "mainPane"
    bottom = playButton
  }
}

object MainScene {
  def apply(parentStage: Stage) = new MainScene(parentStage)
}
