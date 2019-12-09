package view.scenes

import Utility.GUIObjectFactory
import controller.{Difficulty, GameController, OperationType}
import scalafx.Includes._
import scalafx.scene.control.{Button, ChoiceDialog}
import scalafx.stage.Stage

class MainScene(override val parentStage: Stage) extends BaseScene {
  private val gameController: GameController = GameController()

  stylesheets.add("style.css")

  val newGame: Button = GUIObjectFactory.buttonFactory(950, 400, mouseTransparency = false, handle{
    gameController.difficulty = setDifficulty.getOrElse(Difficulty.Medium)
    gameController.setUserInformation(OperationType.NewGame, parentStage)
    gameController.setScene(this)
  }, GUIObjectFactory.DEFAULT_STYLE, "New Game")("mainPageButton")

  val loadGame: Button = GUIObjectFactory.buttonFactory(950, 600, mouseTransparency = false, handle {
    gameController.setUserInformation(OperationType.LoadGame, parentStage)
    gameController.setScene(this)
  }, GUIObjectFactory.DEFAULT_STYLE, "Load Game")("mainPageButton")

  root = GUIObjectFactory.paneFactory(List(newGame, loadGame),"common","mainPane")

  private def setDifficulty: Option[Difficulty] = {
    new ChoiceDialog(Difficulty.Medium, List(Difficulty.Easy, Difficulty.Medium, Difficulty.Hard)) {
      title = "Select difficulty"
      headerText = "Select difficulty"
    }.showAndWait()
  }
}

object MainScene {
  def apply(parentStage: Stage) = new MainScene(parentStage)
}
