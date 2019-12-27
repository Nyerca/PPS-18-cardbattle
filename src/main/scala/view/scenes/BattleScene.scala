package view.scenes

import utility.{GUIObjectFactory, TransitionFactory}
import controller.{BattleController, GameController, MusicPlayer, SoundType}
import model._
import scalafx.Includes._
import scalafx.scene.control.Button
import scalafx.scene.layout.Pane
import scalafx.stage.Stage
import scalafx.util.Duration
import view.scenes.component.{BattleEnemyRepresentation, BattlePlayerRepresentation, BattleUserRepresentation, CardComponent}
import scala.language.postfixOps
import scala.util.{Success, Try}

trait BattleScene extends BaseScene with ObserverScene {
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

object BattleScene {

  private class BattleSceneImpl(override val parentStage: Stage, enemy: Enemy, gameController: GameController) extends BattleScene {

    private val battleController: BattleController = BattleController(this, Battle(gameController.user, enemy))

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

    override val userRepresentation: BattlePlayerRepresentation = BattleUserRepresentation(10, 200, gameController.user)

    override val enemyRepresentation: BattlePlayerRepresentation = BattleEnemyRepresentation(500, 200, enemy)

    override val battleField: Pane = GUIObjectFactory.paneFactory(List(enemyRepresentation, userRepresentation))("battleField")(45, 280)

    override def update[A](model: A): Unit = model match {
      case (card: Card, player: Player) => drawCard(card, player)
      case (player: Player, card: Card) => playFightAnimation(card.family, player)
      case (optionPlayer: Option[Player], optionLevelUp: Option[LevelUp]) => checkWinner(optionPlayer, optionLevelUp)
    }

    /**
     * Handles fight animation and at the end checks the winner and updates HP.
     * @param family of card played
     * @param player has to be animated
     */
    private def playFightAnimation(family: (Category, Type), player: Player): Unit = player match {
        case _: Enemy => enemyRepresentation.playAnimation(-90, family, handle (enemyRepresentation.updateHP(player.actualHealthPoint)))
        case _ => userRepresentation.playAnimation(90, family, handle {
          userRepresentation.updateHP(player.actualHealthPoint)
          battleController.checkWinner()
        })
      }

    /**
     * Handles draw card operation checking if the operation is allowed.
     * @param card to draw
     * @param player has to draw
     */

    private def drawCard(card: Card, player: Player): Unit = player match {
      case _: User => Try(userHandCard.find(cc => cc.clickableCard.opacity.value == 0 || cc.cardName.text.value == "").get.setCardInformation(card))
      case _ => cpuHandCard.setCardInformation(card)
    }

    /**
     * Handles the end of the fight
     * @param player winner.
     * @param levelUp optional params determines if user has to be levelled up.
     */
    private def checkWinner(player: Option[Player], levelUp: Option[LevelUp]): Unit = Try(player.get) match {
      case Success(value) => value match {
        case _: User => TransitionFactory.fadeTransitionFactory(Duration(2000), root.value, handle(gameController.setScene(this, RewardScene(parentStage, gameController, levelUp)))).play()
        case _: Enemy => TransitionFactory.fadeTransitionFactory(Duration(2000), root.value, handle(gameController.setScene(this, GameOverScene(parentStage, gameController)))).play()
      }
      case _ =>
        battleController.drawCard(enemy)
        userHandCard.filter(cc => cc.clickableCard.opacity.value == 1) foreach (cc => cc.clickableCard.mouseTransparent = false)
        userDeck.mouseTransparent = false
    }

    MusicPlayer.play(SoundType.BattleSound)

    stylesheets.add("style.css")

    root = GUIObjectFactory.paneFactory(userCardIndicators ++ userHandCard.map(x => x.clickableCard) ++ userHandCard.map(x => x.cardLevel) ++ userHandCard.map(x => x.cardName) ++ userHandCard.map(x => x.cardDamage) ++ List(cpuCardIndicator, userDeck, cpuDeck, cpuHandCard.clickableCard, cpuHandCard.cardName, cpuHandCard.cardDamage, cpuHandCard.cardLevel, battleField))("common", "battleScene")(0, 0)

    for (_ <- 0 until 3) yield battleController.drawCard(gameController.user)

    battleController.drawCard(enemy)
  }

  def apply(parentStage: Stage, enemy: Enemy, gameController: GameController): BattleScene = new BattleSceneImpl(parentStage, enemy, gameController)
}
