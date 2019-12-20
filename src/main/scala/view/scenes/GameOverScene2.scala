package view.scenes

import Utility.{GUIObjectFactory, TransitionFactory}
import controller.GameController2
import scalafx.Includes._
import scalafx.animation.Transition
import scalafx.scene.control.Button
import scalafx.scene.layout.Pane
import scalafx.stage.Stage
import scalafx.util.Duration


trait GameOverScene2 extends BaseScene2

object GameOverScene2 {

  private class GameOver2(override val parentStage: Stage, val gameController: GameController2) extends GameOverScene2 {

    stylesheets.add("style.css")

    val brokenHeart: Button = GUIObjectFactory.buttonFactory(550, 200, mouseTransparency = true)("brokenHeart")

    val anubiDefeated: Button = GUIObjectFactory.buttonFactory(470, 310, mouseTransparency = true)("anubiDefeated")

    val retryButton: Button = GUIObjectFactory.buttonFactory(500,550, mouseTransparency = false, handle(gameController.setScene(this, MainScene2(parentStage))), GUIObjectFactory.DEFAULT_STYLE, "Retry")("mainPageButton")
    root = new Pane {
      styleClass.add("common")
      style = "-fx-background-color: black;"
      children = List(brokenHeart, retryButton, anubiDefeated)
    }
    TransitionFactory.translateTransitionFactory(Duration(1000), brokenHeart, TransitionFactory.DEFAULT_ON_FINISHED, 0, -100, Transition.Indefinite, autoReversible = true).play()
  }

  def apply(parentStage: Stage, gameController: GameController2): GameOverScene2 = new GameOver2(parentStage, gameController)
}
