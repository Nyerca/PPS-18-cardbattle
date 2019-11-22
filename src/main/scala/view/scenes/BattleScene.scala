package view.scenes

import model.{Card, Category, Game, Player, Type}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, ProgressBar}
import scalafx.scene.layout.{BorderPane, Pane}
import scalafx.stage.Stage
import scalafx.Includes._
import scalafx.animation.FadeTransition
import scalafx.util.Duration

trait BattleScene extends Scene{
  def parentStage: Stage

}

class BattleSceneImpl(override val parentStage: Stage) extends BattleScene {
  stylesheets.add("style.css")


    /****************************CARD**************************/

  val userDeck: Button = new Button {
    styleClass.addAll("card", "deck")
    text = "USER DECK"
    translateX = 45
    translateY = 50
  }

  val cpuDeck: Button = new Button {
    styleClass.addAll("card", "deck")
    text = "CPU DECK"
    translateX = 1005
    translateY = 50
    mouseTransparent = true
  }

  val cpuCardIndicators: Button = new Button {
    styleClass.add("cardIndicator")
    translateX = 1005
    translateY = 450
    mouseTransparent = true
  }

  val cpuHandCard: Button = new Button {
    styleClass.add("card")
    translateX = 1005
    translateY = 450
    mouseTransparent = true
    onAction = handle {
      new FadeTransition(Duration(300), this) {
        byValue = -1
        cycleCount = 1
        onFinished = handle {
          userDeck.mouseTransparent = false
          node.value.mouseTransparent = true
        }
      }.play()
    }
  }

  val userCardIndicators: List[Button] = cardGenerator("cardIndicator")

  val userHandCard: List[Button] = cardGenerator("card")

  /*****************************************************/
    /******************BATTLE FIELD ******************/

  val userRepresentation = new BorderPane {
    translateX = 100
    translateY = 200
    top = new ProgressBar {
      progress = 1
      styleClass.add("life")
    }
    center = new Button {
      styleClass.add("image")
      mouseTransparent = true
    }
  }

  val enemyRepresentation = new BorderPane {
    translateX = 500
    translateY = 200
    top = new ProgressBar {
      progress = 1
      styleClass.add("life")
    }
    center = new Button {
      styleClass.add("image")
      mouseTransparent = true
    }
  }

  val battleField: Pane = new Pane {
    id = "battleField"
    translateX = 45
    translateY = 300
    children = List(userRepresentation,enemyRepresentation)
  }




  /*********************************************************/



  root = new Pane {
    styleClass.add("common")
    id = "battleScene"
    children = userCardIndicators ++ userHandCard :+ userDeck :+ cpuDeck :+ battleField :+ cpuCardIndicators :+ cpuHandCard
  }

  private def cardGenerator(name: String): List[Button] = for (
    n <- 1 until 4 toList
  ) yield new Button {
    styleClass.add(name)
    translateX = 45 + (n * 245)
    translateY = 50
    mouseTransparent = true
  }

}

object BattleScene {
  def apply(parentStage: Stage): BattleScene = new BattleSceneImpl(parentStage)
}
