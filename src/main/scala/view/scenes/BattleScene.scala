package view.scenes

import controller.{BattleController, PlayerType}
import javafx.event.{ActionEvent, EventHandler}
import model._
import scalafx.Includes._
import scalafx.scene.control.Button
import scalafx.scene.layout.Pane
import scalafx.stage.Stage
import view.scenes.component.{BattlePlayerRepresentation, CardComponent}

trait BattleScene extends BaseScene {
  def drawCard(playerType: PlayerType)(card: Card): Unit
  //def updateHealthPoint(playerType: PlayerType, value: Double): Unit
  def playFightAnimation(category: Category, player: PlayerType, healthPoint: Double): Unit
}

class BattleSceneImpl(override val parentStage: Stage, user:User, enemy:Enemy) extends BattleScene {
  private val DEFAULT_ON_FINISHED = null
  stylesheets.add("style.css")

  /**************************************CONTROLLER********************************************/
/*
  val deck = List(Card("Fireball", "images/attack.png", (Category.Attack,Type.Magic)),
    Card("Iceball", "images/attack.png", (Category.Attack,Type.Magic)),
    Card("Ariete", "images/attack.png", (Category.Attack,Type.Physic)),
    Card("Magic shield", "images/defense.png", (Category.Defense,Type.Magic)),
    Card("Physic shield", "images/defense.png", (Category.Defense,Type.Physic)))

  val user: Player = Player.userFactory("player1", "images/user.png", deck)
  val enemy: Player = Player.enemyFactory("enemy", "images/sphinx.png", deck, 1, 30)

  */
val bc = BattleController(Game(user, enemy), this)

  /**************************************************************************************************/


  /****************************CARD**************************/

  val userDeck: Button = singleButtonFactory(35, 50, "USER DECK", false, handle(bc.drawCard(PlayerType.User)), "card", "deck")

  val cpuDeck: Button = singleButtonFactory(995, 50, "CPU DECK", true, DEFAULT_ON_FINISHED, "card", "deck")

  val cpuCardIndicator: Button = singleButtonFactory(995, 450, "", true, DEFAULT_ON_FINISHED, "cardIndicator")

  val cpuHandCard: CardComponent = CardComponent(bc, 995, 450, true, handle(cpuHandCard.fadeOutAll()))

  val userCardIndicators: List[Button] = for (
    n <- 1 until 4 toList
  ) yield singleButtonFactory(35 + n * 240, 50, "", true, DEFAULT_ON_FINISHED, "cardIndicator")

  val userHandCard: List[CardComponent] = for(
    n <- 1 until 4 toList
  ) yield CardComponent(bc, 35 + n * 240, 50, false, handle {
    cpuHandCard.clickableCard.fire()
    userHandCard(n - 1).fadeOutAll(handle {
      userHandCard(n - 1).clickableCard.mouseTransparent = true
      bc.fight(userHandCard(n - 1).card, cpuHandCard.card)
      //bc.drawCard(PlayerType.EnemyType)
    })
  })

  /**************************************************************/
  /******************BATTLE FIELD ******************/

  val userRepresentation: BattlePlayerRepresentation = BattlePlayerRepresentation(10,200, bc.game.user)

  val enemyRepresentation: BattlePlayerRepresentation = BattlePlayerRepresentation(500,200, bc.game.enemy)

  val battleField: Pane = new Pane {
    id = "battleField"
    translateX = 45
    translateY = 280
    children = List(userRepresentation,enemyRepresentation)
  }

  /*********************************************************/

  root = new Pane {
    styleClass.add("common")
    id = "battleScene"
    children = userCardIndicators  ++ userHandCard.map(x => x.clickableCard) ++ userHandCard.map(x => x.cardLevel)++ userHandCard.map(x => x.cardName) ++ userHandCard.map(x => x.cardDamage) ++ List(cpuCardIndicator, userDeck, cpuDeck, cpuHandCard.clickableCard, cpuHandCard.cardName, cpuHandCard.cardDamage, cpuHandCard.cardLevel, battleField)
  }
  bc.drawCard(PlayerType.Enemy)
  userHandCard foreach(_ => bc.drawCard(PlayerType.User))

  override def drawCard(playerType: PlayerType)(card: Card): Unit = playerType match {
    case PlayerType.Enemy => cpuHandCard.setCardInformation(card)
    case _ => userHandCard.find(cc => cc.clickableCard.opacity.value == 0 || cc.cardName.text.value == "").map(cc => cc.setCardInformation(card))
  }

  override def playFightAnimation(category: Category, player: PlayerType, healthPoint: Double): Unit = player match {
    case PlayerType.Enemy =>
      enemyRepresentation.playAnimation(-90, category, healthPoint)
      bc.drawCard(PlayerType.Enemy)
    case _ =>
      userRepresentation.playAnimation(90, category, healthPoint)
      bc.drawCard(PlayerType.User)

  }

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
  def apply(parentStage: Stage, user:User, enemy:Enemy): BattleScene = new BattleSceneImpl(parentStage, user, enemy)
}
