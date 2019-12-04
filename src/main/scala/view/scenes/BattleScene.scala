package view.scenes

import Utility.{GUIObjectFactory, TransitionFactory}
import controller.{BattleController, GameController}
import model._
import scalafx.Includes._
import scalafx.scene.control.Button
import scalafx.scene.layout.Pane
import scalafx.stage.Stage
import scalafx.util.Duration
import view.scenes.component.{BattlePlayerRepresentation, CardComponent}

trait BattleScene extends BaseScene {

  def drawCard(playerType: Player)(card: Card): Unit

  def playFightAnimation(family: (Category, Type), player: Player): Unit

  def fadeSceneChanging: Unit
}

class BattleSceneImpl(override val parentStage: Stage, user: User, enemy: Enemy, gameController: GameController) extends BattleScene {
  stylesheets.add("style.css")

  val battleController: BattleController = BattleController(user, enemy, this)

  val userDeck: Button = GUIObjectFactory.buttonFactory(35, 50, false, handle(battleController.drawCard(user)), GUIObjectFactory.DEFAULT_STYLE, "card", "deck")

  val cpuDeck: Button = GUIObjectFactory.buttonFactory(995, 50,true, GUIObjectFactory.DEFAULT_ON_ACTION, GUIObjectFactory.DEFAULT_STYLE, "card", "deck")

  val cpuCardIndicator: Button = GUIObjectFactory.buttonFactory(995, 450, true, GUIObjectFactory.DEFAULT_ON_ACTION, GUIObjectFactory.DEFAULT_STYLE, "cardIndicator")

  val cpuHandCard: CardComponent = CardComponent(995, 450, mouseTransparency = true, handle(cpuHandCard.fadeOutAll()))

  val userCardIndicators: List[Button] = for (
    n <- 1 until 4 toList
  ) yield GUIObjectFactory.buttonFactory(35 + n * 240, 50,true, GUIObjectFactory.DEFAULT_ON_ACTION, GUIObjectFactory.DEFAULT_STYLE,"cardIndicator")

  val userHandCard: List[CardComponent] = for(
    n <- 1 until 4 toList
  ) yield CardComponent(35 + n * 240, 50, mouseTransparency = false, handle {
    cpuHandCard.clickableCard.fire()
    userHandCard foreach(x => x.clickableCard.mouseTransparent = true)
    userHandCard(n - 1).fadeOutAll(handle {
      battleController.fight(userHandCard(n - 1).card, cpuHandCard.card)
    })
  })

  val userRepresentation: BattlePlayerRepresentation = BattlePlayerRepresentation(10, 200, battleController.user)

  val enemyRepresentation: BattlePlayerRepresentation = BattlePlayerRepresentation(500, 200, battleController.enemy)

  val battleField: Pane = new Pane {
    id = "battleField"
    translateX = 45
    translateY = 280
    children = List(enemyRepresentation, userRepresentation)
  }

  root = new Pane {
    styleClass.add("common")
    styleClass.add("battleScene")
    children = userCardIndicators  ++ userHandCard.map(x => x.clickableCard) ++ userHandCard.map(x => x.cardLevel) ++ userHandCard.map(x => x.cardName) ++ userHandCard.map(x => x.cardDamage) ++ List(cpuCardIndicator, userDeck, cpuDeck, cpuHandCard.clickableCard, cpuHandCard.cardName, cpuHandCard.cardDamage, cpuHandCard.cardLevel, battleField)
  }

  battleController.drawCard(enemy)

  userHandCard foreach(_ => battleController.drawCard(user))

  override def drawCard(playerType: Player)(card: Card): Unit = playerType match {
    case _:Enemy => cpuHandCard.setCardInformation(card)
    case _ => userHandCard.find(cc => cc.clickableCard.opacity.value == 0 || cc.cardName.text.value == "").map(cc => cc.setCardInformation(card))
  }

  override def playFightAnimation(family: (Category, Type), player: Player): Unit = player match {
    case _:Enemy =>
      enemyRepresentation.playAnimation(-90, family, handle(enemyRepresentation.updateHP(handle(battleController.checkWinner(player)))))
      battleController.drawCard(player)
    case _ => userRepresentation.playAnimation(90, family, handle {
        userRepresentation.updateHP(handle(battleController.checkWinner(player)))
        if(enemyRepresentation.player.actualHealthPoint > 0 && userRepresentation.player.actualHealthPoint > 0) userHandCard.filter(cc => cc.clickableCard.opacity.value == 1) foreach(cc => cc.clickableCard.mouseTransparent = false)
    })
  }

  override def fadeSceneChanging: Unit = TransitionFactory.fadeTransitionFactory(Duration(1000), root.value, handle(parentStage.scene = RewardScene(parentStage, gameController))).play()

}

object BattleScene {
  def apply(parentStage: Stage, user: User, enemy: Enemy, gameController: GameController): BattleScene = new BattleSceneImpl(parentStage, user, enemy, gameController)
}
