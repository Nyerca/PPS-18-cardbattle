package view.scenes

import controller.{BattleController, PlayerType}
import javafx.event.{ActionEvent, EventHandler}
import model.{Card, Category, Game, Player, Type}
import scalafx.scene.{Node, Scene}
import scalafx.scene.control.{Button, Label, ProgressBar}
import scalafx.scene.layout.{BorderPane, Pane, StackPane}
import scalafx.stage.Stage
import scalafx.Includes._
import scalafx.animation.{FadeTransition, Transition}
import scalafx.util.Duration

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

  val userDeck: Button = new Button {
    styleClass.addAll("card", "deck")
    text = "USER DECK"
    translateX = 45
    translateY = 50
    onAction = handle {
      bc.drawCard(PlayerType.User)
    }
  }

  val cpuDeck: Button = new Button {
    styleClass.addAll("card", "deck")
    text = "CPU DECK"
    translateX = 1005
    translateY = 50
    mouseTransparent = true
  }

  val cpuCardIndicators: Button = new Button {
    styleClass.add("cardIndicator")
    translateX = 1005
    translateY = 450
    mouseTransparent = true
  }

  val cpuHandCard: Button = new Button {
    styleClass.add("card")
    translateX = 1005
    translateY = 450
    mouseTransparent = true
    onAction = handle {
      fadeTransitionFactory(Duration(300), this, -1, 1).play()
    }
  }

  val userCardIndicators: List[Button] = cardGenerator("cardIndicator")

  val userHandCard: List[Button] = cardGenerator("card")

  /*****************************************************/
  /******************BATTLE FIELD ******************/

  val userRepresentation: BorderPane = new BorderPane {
    translateX = 10
    translateY = 200
    top = new StackPane {
      children = List(new ProgressBar {
        progress = 1
        styleClass.add("life")
      }, new Label{
        styleClass.add("title")
        text = bc.game.user.name
      })
    }
    center = new Button {
      styleClass.add("image")
      mouseTransparent = true
      style = "-fx-background-image: url(" + bc.game.user.image + ")"
    }
  }


  val enemyRepresentation: BorderPane = new BorderPane {
    translateX = 500
    translateY = 200
    top = new StackPane {
      children = List(new ProgressBar {
        progress = 1
        styleClass.add("life")
      }, new Label{
        styleClass.add("title")
        text = bc.game.enemy.name
      })
    }
    center = new Button {
      styleClass.add("image")
      mouseTransparent = true
      style = "-fx-background-image: url(" + bc.game.enemy.image + ")"
    }
  }

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
    children = userCardIndicators ++ userHandCard :+ userDeck :+ cpuDeck :+ battleField :+ cpuCardIndicators :+ cpuHandCard
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

  override def updateHealthPoint(playerType: PlayerType, hp: Double): Unit = {
    type ProgressBar = javafx.scene.control.ProgressBar
    type StackPane = javafx.scene.layout.StackPane
    playerType match {
      case PlayerType.User => userRepresentation.top.value.asInstanceOf[StackPane].getChildren.find(n => n.isInstanceOf[ProgressBar]).get.asInstanceOf[ProgressBar].progress = hp
      case _ => enemyRepresentation.top.value.asInstanceOf[StackPane].getChildren.find(n => n.isInstanceOf[ProgressBar]).get.asInstanceOf[ProgressBar].progress = hp
    }
  }

  override def isDrawingAllowed: Boolean = userHandCard.find(btn => btn.opacity.value == 0 || btn.text.value == "").isDefined


  private def setCardInformation(btn: Button)(card: Card):Unit = {
    btn.text = card.name
    btn.opacity = 1
  }


  private def cardGenerator(name: String): List[Button] = for (
    n <- 1 until 4 toList
  ) yield new Button {
    styleClass.add(name)
    translateX = 45 + (n * 245)
    translateY = 50
    mouseTransparent = true
    onAction = handle {
      cpuHandCard.fire()
      fadeTransitionFactory(Duration(300), this, -1, 1, handle {
        mouseTransparent = true
        bc.fight(text.value, cpuHandCard.text.value)
      }).play()
    }
  }

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
