package view.scenes

import controller.{GameController, MusicPlayer, PlayerAnimation, SoundType}
import model.{Card, LevelUp}
import scalafx.Includes._
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.Label
import scalafx.stage.Stage
import utility.GUIObjectFactory
import view.scenes.component.CardComponent
import scala.language.postfixOps
import scala.util.Random

trait RewardScene extends BaseScene

object RewardScene {

  private class Reward(override val parentStage: Stage, gameController: GameController, levelUp: Option[LevelUp]) extends RewardScene {

    MusicPlayer.play(SoundType.WinningSound)

    stylesheets.add("style.css")

    private val shuffledCards: List[Card] = Random.shuffle(gameController.allCards)

    val rewards: List[CardComponent] = for (
      n <- 0 until 3 toList
    ) yield CardComponent(marginX = 115 + (n * 385), marginY = 300, mouseTransparency = false, action = handle {
      rewards(n).fadeOutAll()
      rewards.foreach(cc => cc.clickableCard.mouseTransparent = true)
      gameController.gainCard(rewards(n).card)
      GUIObjectFactory.alertFactory(AlertType.Information, parentStage, "Card gained","Congratulations, you gained a card").showAndWait()
      gameController.setScene(this)
      if(levelUp isDefined) PlayerAnimation.play(PlayerAnimation.LEVEL_UP_PREFIX)
    })

    val title: Label = GUIObjectFactory.labelFactory(120, 50, "Choose your reward", "rewardTitle")

    for (n <- 0 until 3) yield rewards(n) setCardInformation shuffledCards(n)

    root = GUIObjectFactory.paneFactory(rewards.map(x => x.cardDamage) ++ rewards.map(x => x.cardLevel) ++ rewards.map(x => x.clickableCard) ++ rewards.map(x => x.cardName) :+ title)( "common", "battleScene")(0,0)
  }

  def apply(parentStage: Stage, gameController: GameController, levelUp: Option[LevelUp]): RewardScene = new Reward(parentStage, gameController, levelUp)
}

