package view.scenes

import Utility.GUIObjectFactory
import controller.GameController
import model.Card
import scalafx.stage.Stage
import view.scenes.component.CardComponent
import scalafx.Includes._
import scalafx.scene.control.{ButtonType, Label}
import scalafx.scene.control.Alert.AlertType
import scala.util.Random
import scala.language.postfixOps


class RewardScene(override val parentStage: Stage, gameController: GameController) extends BaseScene {

  stylesheets.add("style.css")

  private val shuffledCards: List[Card] = List.concat(Random.shuffle(gameController.allCards))

  val rewards: List[CardComponent] = for (
    n <- 0 until 3 toList
  ) yield CardComponent(marginX = 115 + (n * 385), marginY = 300, mouseTransparency = false, action = handle {
    rewards(n).fadeOutAll()
    rewards.foreach(cc => cc.clickableCard.mouseTransparent = true)
    getInformationMessage(gameController.user <-- rewards(n).card)
    gameController.setScene(this)
  })

  val title: Label = GUIObjectFactory.labelFactory(120, 50, "Choose your reward", "rewardTitle")

  for (n <- 0 until 3) yield rewards(n) setCardInformation shuffledCards(n)

  root = GUIObjectFactory.paneFactory(rewards.map(x => x.cardDamage) ++ rewards.map(x => x.cardLevel) ++ rewards.map(x => x.clickableCard) ++ rewards.map(x => x.cardName) :+ title)( "common", "battleScene")(0,0)

  private def getInformationMessage(c: Option[Card]): Option[ButtonType] = c match {
    case Some(card) => GUIObjectFactory.alertFactory(AlertType.Information, parentStage, "Card level up", "Congratulations, " + card.name + " raised level " + card.level).showAndWait()
    case _ => GUIObjectFactory.alertFactory(AlertType.Information, parentStage, "Card gained","Congratulations, you gained a card").showAndWait()
  }
}

object RewardScene {
  def apply(parentStage: Stage, gameController: GameController): RewardScene = new RewardScene(parentStage, gameController)
}

