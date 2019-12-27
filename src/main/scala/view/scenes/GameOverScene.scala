package view.scenes

import utility.{GUIObjectFactory, TransitionFactory}
import controller.{GameController, MusicPlayer, SoundType}
import scalafx.Includes._
import scalafx.animation.Transition
import scalafx.scene.control.Button
import scalafx.scene.layout.Pane
import scalafx.stage.Stage
import scalafx.util.Duration

trait GameOverScene extends BaseScene

object GameOverScene {

  private class GameOver(override val parentStage: Stage, val gameController: GameController) extends GameOverScene {

    val brokenHeart: Button = GUIObjectFactory.buttonFactory(550, 200, mouseTransparency = true)("brokenHeart")

    val anubisDefeated: Button = GUIObjectFactory.buttonFactory(470, 310, mouseTransparency = true)("anubiDefeated")

    val retryButton: Button = GUIObjectFactory.buttonFactory(500,550, mouseTransparency = false, handle(gameController.setScene(this, MainScene(parentStage))), GUIObjectFactory.DEFAULT_STYLE, "Retry")("mainPageButton")

    root = new Pane {
      styleClass.add("common")
      style = "-fx-background-color: black;"
      children = List(brokenHeart, retryButton, anubisDefeated)
    }

    MusicPlayer.play(SoundType.LoseSound)

    stylesheets.add("style.css")

    TransitionFactory.translateTransitionFactory(Duration(1000), brokenHeart, TransitionFactory.DEFAULT_ON_FINISHED, 0, -100, Transition.Indefinite, autoReversible = true).play()
  }

  def apply(parentStage: Stage, gameController: GameController): GameOverScene = new GameOver(parentStage, gameController)
}
