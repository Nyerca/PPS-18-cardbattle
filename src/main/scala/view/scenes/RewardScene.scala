package view.scenes

import Utility.GUIObjectFactory
import controller.GameController
import model.Card
import scalafx.stage.Stage
import view.scenes.component.CardComponent
import scalafx.Includes._
import scalafx.scene.control.ButtonType
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.layout.Pane

import scala.util.Random


class RewardScene(override val parentStage: Stage, gameController: GameController) extends BaseScene {

  stylesheets.add("style.css")

  private val shuffledCards: List[Card] = List.concat(Random.shuffle(gameController.allCards))

  val rewards: List[CardComponent] = for (
    n <- 0 until 3 toList
  ) yield CardComponent(marginX = 115 + (n * 385), marginY = 300, mouseTransparency = false, action = handle {
    rewards(n).fadeOutAll()
    getInformationMessage(gameController.user.gainCard(rewards(n).card), rewards(n).card)
    gameController.setMapScene(this)
  })

  for (n <- 0 until 3) yield rewards(n) setCardInformation shuffledCards(n)

  root = new Pane {
    styleClass.add("common")
    styleClass.add("battleScene")
    children = rewards.map(x => x.cardDamage) ++ rewards.map(x => x.cardLevel) ++ rewards.map(x => x.clickableCard) ++ rewards.map(x => x.cardName)
  }

  private def getInformationMessage(level: Option[Int], card: Card): Option[ButtonType] = level match {
    case Some(n) => GUIObjectFactory.alertFactory(AlertType.Information, parentStage, "Card level up", "Congratulations, " + card.name + " raised level " + n).showAndWait()
    case _ => GUIObjectFactory.alertFactory(AlertType.Information, parentStage, "Card gained","Congratulations, you gained a card").showAndWait()
  }
}

object RewardScene {
  def apply(parentStage: Stage, gameController: GameController): RewardScene = new RewardScene(parentStage, gameController)
}

