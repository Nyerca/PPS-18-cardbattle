package view.scenes

import Utility.GUIObjectFactory
import controller.{Difficulty, GameController, OperationType}
import scalafx.Includes._
import scalafx.scene.control.Button
import scalafx.stage.Stage
import view.scenes.component.CustomAlert



class MainScene(override val parentStage: Stage) extends BaseScene {
  private val gameController: GameController = GameController()

  private lazy val settings: (Option[String],Option[Difficulty]) = new CustomAlert().showAndWait()

  stylesheets.add("style.css")

  val newGame: Button = GUIObjectFactory.buttonFactory(950, 400, mouseTransparency = false, handle{
    gameController.difficulty = settings._2.getOrElse(Difficulty.Medium)
    gameController.setUserInformation(OperationType.NewGame, this, settings._1.getOrElse("PLayer 1"))
  }, GUIObjectFactory.DEFAULT_STYLE, "New Game")("mainPageButton")

  val loadGame: Button = GUIObjectFactory.buttonFactory(950, 600, mouseTransparency = false, handle {
    gameController.setUserInformation(OperationType.LoadGame, this)
  }, GUIObjectFactory.DEFAULT_STYLE, "Load Game")("mainPageButton")

  root = GUIObjectFactory.paneFactory(List(newGame, loadGame))("common","mainPane")(0,0)
}

object MainScene {
  def apply(parentStage: Stage) = new MainScene(parentStage)
}
