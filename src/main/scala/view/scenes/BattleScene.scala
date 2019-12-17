package view.scenes

import Utility.{GUIObjectFactory, TransitionFactory}
import controller.{BattleController, GameController}
import model._
import scalafx.Includes._
import scalafx.scene.control.Button
import scalafx.scene.layout.Pane
import scalafx.stage.Stage
import scalafx.util.Duration
import view.scenes.component.{BattleEnemyRepresentation, BattlePlayerRepresentation, BattleUserRepresentation, CardComponent}

import scala.language.postfixOps
import scala.util.Try

trait BattleScene extends BaseScene {

  def drawCard(playerType: Player)(card: Card): Unit

  def fadeSceneChanging(player: Player): Unit

  def userDeck: Button

  def cpuDeck: Button

  def cpuCardIndicator: Button

  def cpuHandCard: CardComponent

  def userCardIndicators: List[Button]

  def userHandCard: List[CardComponent]

  def userRepresentation: BattlePlayerRepresentation

  def enemyRepresentation: BattlePlayerRepresentation

  def battleField: Pane
}

class BattleSceneImpl(override val parentStage: Stage, user: User, enemy: Enemy, gameController: GameController) extends BattleScene {

  stylesheets.add("style.css")

  private val battleController: BattleController = BattleController(this)

  override val userDeck: Button = GUIObjectFactory.buttonFactory(35, 50, mouseTransparency = false, handle(battleController.drawCard(user)))("card", "deck")

  override val cpuDeck: Button = GUIObjectFactory.buttonFactory(995, 50, mouseTransparency = true)( "card", "deck")

  override val cpuCardIndicator: Button = GUIObjectFactory.buttonFactory(995, 450, mouseTransparency = true)("cardIndicator")

  override val cpuHandCard: CardComponent = CardComponent(995, 450, mouseTransparency = true, handle(cpuHandCard.fadeOutAll(handle(playFightAnimation(cpuHandCard.card.family, enemy)))))

  override val userCardIndicators: List[Button] = for (
    n <- 1 until 4 toList
  ) yield GUIObjectFactory.buttonFactory(35 + n * 240, 50,mouseTransparency = true)("cardIndicator")

  override val userHandCard: List[CardComponent] = for(
    n <- 1 until 4 toList
  ) yield CardComponent(35 + n * 240, 50, mouseTransparency = false, handle {
    cpuHandCard.clickableCard.fire()
    userDeck.mouseTransparent = true
    userHandCard foreach(x => x.clickableCard.mouseTransparent = true)
    userHandCard(n - 1).fadeOutAll(handle{
      battleController.fight(userHandCard(n - 1).card, cpuHandCard.card, user, enemy)
      playFightAnimation(userHandCard(n - 1).card.family, user)
    })
  })

  override val userRepresentation: BattlePlayerRepresentation = BattleUserRepresentation(10, 200, user)

  override val enemyRepresentation: BattlePlayerRepresentation = BattleEnemyRepresentation(500, 200, enemy)

  override val battleField: Pane = GUIObjectFactory.paneFactory(List(enemyRepresentation, userRepresentation))("battleField")(45, 280)

  override def drawCard(playerType: Player)(card: Card): Unit = playerType match {
    case _: Enemy => cpuHandCard.setCardInformation(card)
    case _ => Try(userHandCard.find(cc => cc.clickableCard.opacity.value == 0 || cc.cardName.text.value == "").get.setCardInformation(card))
  }

  override def fadeSceneChanging(player: Player): Unit = player match {
    case _: User => TransitionFactory.fadeTransitionFactory(Duration(2000), root.value, handle {
      gameController.user ++ enemy
      gameController.setScene(this, RewardScene(parentStage, gameController))
    }).play()
    case _ => TransitionFactory.fadeTransitionFactory(Duration(2000), root.value, handle(gameController.setScene(this, GameOverScene(parentStage, gameController)))).play()
  }

  private def playFightAnimation(family: (Category, Type), player: Player): Unit = {
      player match {
      case _: Enemy => enemyRepresentation.playAnimation(-90, family, handle {
        battleController.drawCard(enemy)
        enemyRepresentation.updateHP()
      })
      case _ => userRepresentation.playAnimation(90, family, handle {
        userRepresentation.updateHP()
        battleController.checkWinner(user, enemy)
      })
    }
  }

  root = GUIObjectFactory.paneFactory(userCardIndicators ++ userHandCard.map(x => x.clickableCard) ++ userHandCard.map(x => x.cardLevel) ++ userHandCard.map(x => x.cardName) ++ userHandCard.map(x => x.cardDamage) ++ List(cpuCardIndicator, userDeck, cpuDeck, cpuHandCard.clickableCard, cpuHandCard.cardName, cpuHandCard.cardDamage, cpuHandCard.cardLevel, battleField))( "common", "battleScene")(0,0)

  battleController drawCard enemy

  userHandCard foreach(_ => battleController drawCard user)
}

object BattleScene {
  def apply(parentStage: Stage, user: User, enemy: Enemy, gameController: GameController): BattleScene = new BattleSceneImpl(parentStage, user, enemy, gameController)
}
