package view.scenes

import controller.{BattleController, PlayerType}
import javafx.event.{ActionEvent, EventHandler}
import model._
import scalafx.Includes._
import scalafx.animation.{FadeTransition, Transition}
import scalafx.scene.control.Button
import scalafx.scene.layout.Pane
import scalafx.scene.{Node, Scene}
import scalafx.stage.Stage
import scalafx.util.Duration
import view.scenes.component.BattlePlayerRepresentation

trait BattleScene extends Scene{
  def parentStage: Stage
  def drawCard(playerType: PlayerType)(card: Card): Unit
  def updateHealthPoint(playerType: PlayerType, value: Double): Unit
  def isDrawingAllowed: Boolean
}

class BattleSceneImpl(override val parentStage: Stage) extends BattleScene {
  private val DEFAULT_ON_FINISHED = null
  stylesheets.add("style.css")

  /**************************************CONTROLLER********************************************/

  val deck = List(Card("Fireball", "fireball.png", (Category.Attack,Type.Magic)),
    Card("Iceball", "iceball.png", (Category.Attack,Type.Magic)),
    Card("Ariete", "ariete.png", (Category.Attack,Type.Physic)),
    Card("Magic shield", "magicShield.png", (Category.Defense,Type.Magic)),
    Card("Physic shield", "physicShield.png", (Category.Defense,Type.Physic)))
  val bc = BattleController(Game(Player.userFactory("player1", "images/user.png", deck, deck),Player.enemyFactory("enemy", "images/enemy.png", deck)), this)

  /**************************************************************************************************/


  /****************************CARD**************************/

  val userDeck: Button = cardFactory(45, 50, "USER DECK", false, handle {
    bc.drawCard(PlayerType.User)
  }, "card", "deck")

  val cpuDeck: Button = cardFactory(1005, 50, "CPU DECK", true, DEFAULT_ON_FINISHED, "card", "deck")

  val cpuCardIndicator: Button = cardFactory(1005, 450, "", true, DEFAULT_ON_FINISHED, "cardIndicator")

  val cpuHandCard: Button = cardFactory(1005, 450, "", true, handle {
    fadeTransitionFactory(Duration(300), cpuHandCard, -1, 1).play()
  }, "card")

  val userCardIndicators: List[Button] = multipleCardFactory("cardIndicator")

  val userHandCard: List[Button] = multipleCardFactory("card")

  /*****************************************************/
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
    children = userCardIndicators ++ userHandCard :+ userDeck :+ cpuDeck :+ battleField :+ cpuCardIndicator :+ cpuHandCard
  }
  initUserHandCard()
  bc.drawCard(PlayerType.EnemyType)

  override def drawCard(playerType: PlayerType)(card: Card): Unit = playerType match {
    case PlayerType.User =>
      val btn = userHandCard.find(card => card.opacity.value == 0 || card.text.value == "").get
      setCardInformation(btn)(card)
      btn.mouseTransparent = false
    case _ => setCardInformation(cpuHandCard)(card)
  }

  override def updateHealthPoint(playerType: PlayerType, hp: Double): Unit = playerType match {
    case PlayerType.User => userRepresentation.updateHP(hp)
    case _ => enemyRepresentation.updateHP(hp)
  }

  override def isDrawingAllowed: Boolean = userHandCard.exists(btn => btn.opacity.value == 0 || btn.text.value == "")


  private def setCardInformation(btn: Button)(card: Card):Unit = {
    btn.text = card.name
    btn.opacity = 1
  }

  private def cardFactory(marginX: Double, marginY: Double, description: String, mouseTransparency: Boolean, action: EventHandler[ActionEvent], classes: String*): Button = new Button {
    classes.foreach(c => styleClass.add(c))
    translateX = marginX
    translateY = marginY
    text = description
    mouseTransparent = mouseTransparency
    onAction = action
  }

  private def multipleCardFactory(name: String): List[Button] = for (
    n <- 1 until 4 toList
  ) yield cardFactory(45 + (n * 245), 50, "",true, handle {
    cpuHandCard.fire()
    fadeTransitionFactory(Duration(300), userHandCard(n - 1), -1, 1, handle {
      userHandCard(n - 1).mouseTransparent = true
      bc.fight(userHandCard(n - 1).text.value, cpuHandCard.text.value)
    }).play()

  }, name)

  private def fadeTransitionFactory(duration: Duration, node: Node, byVal: Double, cycles: Int, action: EventHandler[ActionEvent] = DEFAULT_ON_FINISHED): Transition = new FadeTransition(duration, node) {
    byValue = byVal
    cycleCount = cycles
    onFinished = action
  }

  private def initUserHandCard(): Unit = userHandCard foreach(_ => bc.drawCard(PlayerType.User))

}

object BattleScene {
  def apply(parentStage: Stage): BattleScene = new BattleSceneImpl(parentStage)
}
