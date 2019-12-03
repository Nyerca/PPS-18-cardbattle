package view.scenes

import controller.{BattleController, GameController, PlayerType}
import javafx.event.{ActionEvent, EventHandler}
import model._
import scalafx.Includes._
import scalafx.animation.FadeTransition
import scalafx.scene.control.Button
import scalafx.scene.layout.Pane
import scalafx.stage.Stage
import scalafx.util.Duration
import view.scenes.component.{BattlePlayerRepresentation, CardComponent}

trait BattleScene extends BaseScene {

  def drawCard(playerType: PlayerType)(card: Card): Unit

  def playFightAnimation(family: (Category, Type), player: PlayerType, healthPoint: Double): Unit

  def fadeSceneChanging(): Unit
}

class BattleSceneImpl(override val parentStage: Stage, user: User, enemy: Enemy, gameController: GameController) extends BattleScene {
  private val DEFAULT_ON_FINISHED = null

  stylesheets.add("style.css")

  val battleController: BattleController = BattleController(Game(user, enemy), this)

  val userDeck: Button = singleButtonFactory(35, 50, "USER DECK", false, handle(battleController.drawCard(PlayerType.User)), "card", "deck")

  val cpuDeck: Button = singleButtonFactory(995, 50, "CPU DECK", true, DEFAULT_ON_FINISHED, "card", "deck")

  val cpuCardIndicator: Button = singleButtonFactory(995, 450, "", true, DEFAULT_ON_FINISHED, "cardIndicator")

  val cpuHandCard: CardComponent = CardComponent(995, 450, mouseTransparency = true, handle(cpuHandCard.fadeOutAll()))

  val userCardIndicators: List[Button] = for (
    n <- 1 until 4 toList
  ) yield singleButtonFactory(35 + n * 240, 50, "", true, DEFAULT_ON_FINISHED, "cardIndicator")

  val userHandCard: List[CardComponent] = for(
    n <- 1 until 4 toList
  ) yield CardComponent(35 + n * 240, 50, mouseTransparency = false, handle {
    cpuHandCard.clickableCard.fire()
    userHandCard foreach(x => x.clickableCard.mouseTransparent = true)
    userHandCard(n - 1).fadeOutAll(handle {
      battleController.fight(userHandCard(n - 1).card, cpuHandCard.card)
    })
  })

  val userRepresentation: BattlePlayerRepresentation = BattlePlayerRepresentation(10,200, battleController.game.user)

  val enemyRepresentation: BattlePlayerRepresentation = BattlePlayerRepresentation(500,200, battleController.game.enemy)

  val battleField: Pane = new Pane {
    id = "battleField"
    translateX = 45
    translateY = 280
    children = List(userRepresentation,enemyRepresentation)
  }

  root = new Pane {
    styleClass.add("common")
    styleClass.add("battleScene")
    children = userCardIndicators  ++ userHandCard.map(x => x.clickableCard) ++ userHandCard.map(x => x.cardLevel) ++ userHandCard.map(x => x.cardName) ++ userHandCard.map(x => x.cardDamage) ++ List(cpuCardIndicator, userDeck, cpuDeck, cpuHandCard.clickableCard, cpuHandCard.cardName, cpuHandCard.cardDamage, cpuHandCard.cardLevel, battleField)
  }

  battleController.drawCard(PlayerType.Enemy)

  userHandCard foreach(_ => battleController.drawCard(PlayerType.User))

  override def drawCard(playerType: PlayerType)(card: Card): Unit = playerType match {
    case PlayerType.Enemy => cpuHandCard.setCardInformation(card)
    case _ => userHandCard.find(cc => cc.clickableCard.opacity.value == 0 || cc.cardName.text.value == "").map(cc => cc.setCardInformation(card))
  }

  override def playFightAnimation(family: (Category, Type), player: PlayerType, healthPoint: Double): Unit = player match {
    case PlayerType.Enemy =>
      enemyRepresentation.playAnimation(-90, family, handle(enemyRepresentation.updateHP(healthPoint)))
      battleController.drawCard(PlayerType.Enemy)
    case _ => userRepresentation.playAnimation(90, family, handle {
        userRepresentation.updateHP(healthPoint)
        userHandCard.filter(cc => cc.clickableCard.opacity.value == 1) foreach(cc => cc.clickableCard.mouseTransparent = false)
    })
  }

  override def fadeSceneChanging(): Unit = new FadeTransition(Duration(300), root.value) {
    byValue = -1
    onFinished = handle {
      parentStage.scene = RewardScene(parentStage, gameController)
    }
  }.play()

  private def singleButtonFactory(marginX: Double, marginY: Double, description: String, mouseTransparency: Boolean, action: EventHandler[ActionEvent], classes: String*): Button = new Button {
    classes.foreach(c => styleClass.add(c))
    translateX = marginX
    translateY = marginY
    text = description
    mouseTransparent = mouseTransparency
    onAction = action
  }
}

object BattleScene {
  def apply(parentStage: Stage, user: User, enemy: Enemy, gameController: GameController): BattleScene = new BattleSceneImpl(parentStage, user, enemy, gameController)
}
