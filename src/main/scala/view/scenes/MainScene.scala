package view.scenes

import utility.GUIObjectFactory
import controller.{GameController, MusicPlayer, OperationType}
import scalafx.Includes._
import scalafx.scene.control.Button
import scalafx.scene.media.MediaPlayer.Status
import scalafx.stage.Stage
import view.scenes.component.CustomAlert


class MainScene(override val parentStage: Stage) extends BaseScene {
  private val gameController: GameController = GameController()

  MusicPlayer.changeStatus(Status.Paused)

  stylesheets.add("style.css")

  val newGame: Button = GUIObjectFactory.buttonFactory(950, 400, mouseTransparency = false, handle {
    new CustomAlert().showAndWait() match {
      case (Some(name), Some(diff)) =>
        gameController.difficulty = diff
        gameController.setUserInformation(OperationType.NewGame, this, name)
      case _ => ;
    }
  }, GUIObjectFactory.DEFAULT_STYLE, "New Game")("mainPageButton")

  val loadGame: Button = GUIObjectFactory.buttonFactory(950, 600, mouseTransparency = false, handle {
    gameController.setUserInformation(OperationType.LoadGame, this)
  }, GUIObjectFactory.DEFAULT_STYLE, "Load Game")("mainPageButton")

  root = GUIObjectFactory.paneFactory(List(newGame, loadGame))("common","mainPane")(0,0)
}

object MainScene {
  def apply(parentStage: Stage) = new MainScene(parentStage)
}


