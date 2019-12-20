package view.scenes

import Utility.{GUIObjectFactory, TransitionFactory}
import controller.{BattleController2, GameController2}
import model._
import scalafx.Includes._
import scalafx.scene.control.Button
import scalafx.scene.layout.Pane
import scalafx.stage.Stage
import scalafx.util.Duration
import view.scenes.component.{BattleEnemyRepresentation2, BattlePlayerRepresentation2, BattleUserRepresentation2, CardComponent}
import scala.language.postfixOps
import scala.util.{Success, Try}

trait BattleScene2 extends BaseScene2 with ObservableScene {

  def userDeck: Button

  def cpuDeck: Button

  def cpuCardIndicator: Button

  def cpuHandCard: CardComponent

  def userCardIndicators: List[Button]

  def userHandCard: List[CardComponent]

  def userRepresentation: BattlePlayerRepresentation2

  def enemyRepresentation: BattlePlayerRepresentation2

  def battleField: Pane
}

object BattleScene2 {

  private class BattleSceneImpl2(override val parentStage: Stage, enemy: Enemy2, gameController: GameController2) extends BattleScene2 {

    stylesheets.add("style.css")

    private val battleController: BattleController2 = BattleController2(this, Battle(gameController.user, enemy), gameController)

    override val userDeck: Button = GUIObjectFactory.buttonFactory(35, 50, mouseTransparency = false, handle(battleController.drawCard(gameController.user)))("card", "deck")

    override val cpuDeck: Button = GUIObjectFactory.buttonFactory(995, 50, mouseTransparency = true)("card", "deck")

    override val cpuCardIndicator: Button = GUIObjectFactory.buttonFactory(995, 450, mouseTransparency = true)("cardIndicator")

    override val cpuHandCard: CardComponent = CardComponent(995, 450, mouseTransparency = true, handle(cpuHandCard.fadeOutAll()))

    override val userCardIndicators: List[Button] = for (
      n <- 1 until 4 toList
    ) yield GUIObjectFactory.buttonFactory(35 + n * 240, 50, mouseTransparency = true)("cardIndicator")

    override val userHandCard: List[CardComponent] = for (
      n <- 1 until 4 toList
    ) yield CardComponent(35 + n * 240, 50, mouseTransparency = false, handle {
      cpuHandCard.clickableCard.fire()
      userDeck.mouseTransparent = true
      userHandCard foreach (x => x.clickableCard.mouseTransparent = true)
      userHandCard(n - 1).fadeOutAll(handle {
        battleController.fight(userHandCard(n - 1).card, cpuHandCard.card)
      })
    })

    override val userRepresentation: BattlePlayerRepresentation2 = BattleUserRepresentation2(10, 200, gameController.user)

    override val enemyRepresentation: BattlePlayerRepresentation2 = BattleEnemyRepresentation2(500, 200, enemy)

    override val battleField: Pane = GUIObjectFactory.paneFactory(List(enemyRepresentation, userRepresentation))("battleField")(45, 280)

    override def update[A](model: A): Unit = model match {
      case (card: Card, player: Player2) => drawCard(card, player)
      case (player: Player2, card: Card) => playFightAnimation(card.family, player)
      case optionPlayer: Option[Player2] => checkWinner(optionPlayer)
    }

    root = GUIObjectFactory.paneFactory(userCardIndicators ++ userHandCard.map(x => x.clickableCard) ++ userHandCard.map(x => x.cardLevel) ++ userHandCard.map(x => x.cardName) ++ userHandCard.map(x => x.cardDamage) ++ List(cpuCardIndicator, userDeck, cpuDeck, cpuHandCard.clickableCard, cpuHandCard.cardName, cpuHandCard.cardDamage, cpuHandCard.cardLevel, battleField))("common", "battleScene")(0, 0)

    for (_ <- 0 until 3) yield battleController.drawCard(gameController.user)

    battleController.drawCard(enemy)

    private def playFightAnimation(family: (Category, Type), player: Player2): Unit = player match {
        case _: Enemy2 => enemyRepresentation.playAnimation(-90, family, handle {
          battleController.drawCard(enemy)
          enemyRepresentation.updateHP(player.actualHealthPoint)
        })
        case _ => userRepresentation.playAnimation(90, family, handle {
          userRepresentation.updateHP(player.actualHealthPoint)
          battleController.checkWinner()
        })
      }

    private def drawCard(card: Card, player: Player2): Unit = player match {
      case _: User2 => Try(userHandCard.find(cc => cc.clickableCard.opacity.value == 0 || cc.cardName.text.value == "").get.setCardInformation(card))
      case _ => cpuHandCard.setCardInformation(card)
    }

    private def checkWinner(player: Option[Player2]): Unit = Try(player.get) match {
      case Success(value) => value match {
        case _: User2 => TransitionFactory.fadeTransitionFactory(Duration(2000), root.value, handle(gameController.setScene(this, RewardScene2(parentStage, gameController, enemy)))).play()
        case _ => TransitionFactory.fadeTransitionFactory(Duration(2000), root.value, handle(gameController.setScene(this, GameOverScene2(parentStage, gameController)))).play()
      }
      case _ =>
        userHandCard.filter(cc => cc.clickableCard.opacity.value == 1) foreach (cc => cc.clickableCard.mouseTransparent = false)
        userDeck.mouseTransparent = false
    }
  }

  def apply(parentStage: Stage, enemy: Enemy2, gameController: GameController2): BattleScene2 = new BattleSceneImpl2(parentStage, enemy, gameController)
}
