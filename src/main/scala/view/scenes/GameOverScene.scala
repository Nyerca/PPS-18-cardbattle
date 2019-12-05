package view.scenes

import Utility.{GUIObjectFactory, TransitionFactory}
import controller.GameController
import scalafx.animation.Transition
import scalafx.scene.control.Button
import scalafx.scene.layout.Pane
import scalafx.stage.Stage
import scalafx.Includes._
import scalafx.util.Duration


class GameOverScene(override val parentStage: Stage, val gameController: GameController) extends BaseScene {

  stylesheets.add("style.css")


  val brokenHeart: Button = GUIObjectFactory.buttonFactory(450, 200, mouseTransparency = true)("brokenHeart")
  val retryButton: Button = GUIObjectFactory.buttonFactory(500,550, mouseTransparency = false, handle(gameController.setScene(this, MainScene(parentStage))), GUIObjectFactory.DEFAULT_STYLE, "Retry")("retry")
  root = new Pane {
    styleClass.add("common")
    style = "-fx-background-color: black;"
    children = List(brokenHeart, retryButton)
  }
  TransitionFactory.translateTransitionFactory(Duration(1000), brokenHeart, TransitionFactory.DEFAULT_ON_FINISHED, 0, -100, Transition.Indefinite, autoReversible = true).play()
}

object GameOverScene {
  def apply(parentStage: Stage, gameController: GameController): GameOverScene = new GameOverScene(parentStage, gameController)
}
