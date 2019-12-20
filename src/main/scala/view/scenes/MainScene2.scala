package view.scenes

import Utility.GUIObjectFactory
import controller.{GameController, GameController2, OperationType2}
import scalafx.Includes._
import scalafx.scene.control.Button
import scalafx.stage.Stage
import view.scenes.component.CustomAlert2


class MainScene2(override val parentStage: Stage) extends BaseScene2 {
  private val gameController: GameController2 = GameController2()

  stylesheets.add("style.css")

  val newGame: Button = GUIObjectFactory.buttonFactory(950, 400, mouseTransparency = false, handle {
    new CustomAlert2().showAndWait() match {
      case (Some(name), Some(diff)) =>
        gameController.difficulty = diff
        gameController.setUserInformation(OperationType2.NewGame, this, name)
      case _ => ;
    }
  }, GUIObjectFactory.DEFAULT_STYLE, "New Game")("mainPageButton")

  val loadGame: Button = GUIObjectFactory.buttonFactory(950, 600, mouseTransparency = false, handle {
    //gameController.setUserInformation(OperationType.LoadGame, this)
  }, GUIObjectFactory.DEFAULT_STYLE, "Load Game")("mainPageButton")

  root = GUIObjectFactory.paneFactory(List(newGame, loadGame))("common","mainPane")(0,0)
}

object MainScene2 {
  def apply(parentStage: Stage) = new MainScene2(parentStage)
}


