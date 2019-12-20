package view.scenes

import Utility.GUIObjectFactory
import controller.GameController
import javafx.beans.property.SimpleStringProperty
import model.Card
import scalafx.Includes._
import scalafx.scene.control.ScrollPane.ScrollBarPolicy
import scalafx.scene.control._
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout._
import scalafx.scene.text.Text
import scalafx.scene.Node
import scalafx.stage.Stage
import view.scenes.component.CardComponentImpl

import scala.collection.mutable.ListBuffer

trait EquipmentScene extends BaseScene

object EquipmentScene {
  def apply(parentStage: Stage, gameController: GameController): EquipmentScene = new EquipmentSceneImpl(parentStage, gameController)

  private case class EquipmentSceneImpl(override val parentStage: Stage, gameController: GameController) extends EquipmentScene{

    stylesheets.add("mapStyle.css")
    stylesheets.add("style.css")

    private def createCardPane(card: Card): Pane = new Pane {
      children = new ListBuffer[Node]
      val c = new CardComponentImpl(0,0, false,handle{
        println(gameController.user.battleDeck.map(el => el.name))

        if(gameController.user.battleDeck.contains(card)) {
          gameController.user.battleDeck = gameController.user.battleDeck.filter(c => c != card)
          btn.styleClass.remove("equipSelectedCard")
        } else {
          gameController.user.battleDeck = card :: gameController.user.battleDeck
          btn.styleClass.add("equipSelectedCard")
        }
        setCards()
      })
      c.setCardInformation(card)
      val btn: Button = c.clickableCard
      if(gameController.user.battleDeck.contains(card)) btn.styleClass.add("equipSelectedCard")

      children.append(btn)

      children.append(c.cardLevel)
      children.append(c.cardName)
      children.append(c.cardDamage)
    }

    private var index = 0

    private def addCard(gridPane: GridPane, card: Card): Unit = {
      gridPane.add(createCardPane(card), index % 4, index / 4)
      index=index+1
    }

    private val gridPane: GridPane = new GridPane() {id="grid"; minHeight = 798}
    gameController.user.allCards.foreach(c => addCard(gridPane, c))

    private val observableCards = new SimpleStringProperty("CARDS:         " + gameController.user.battleDeck.size+ " /8")
    private def setCards(): Unit = observableCards.set("CARDS:         " + gameController.user.battleDeck.size+ " /8")

    private def changeScene(): Unit = gameController.setScene(this)

    /*
    root = new ScrollPane() {
      hbarPolicy = ScrollBarPolicy.Never
      vbarPolicy = ScrollBarPolicy.AsNeeded
      id= "scrollPane"
      content = new BorderPane() {
        top = GUIObjectFactory.toolbarFactory(
          List(
            (new Label{text <== observableCards}, false),
            (new ImageView(new Image("cardSprite.png")), true),
            (new Button("Back") {onAction = () =>
              if(gameController.user.battleDeck.size == 8) changeScene()
              else println("You have to take 8 cards in order to procede.")
            }, false)
          )
        )
        center = gridPane
        id = "cardsPane"
        minHeight = 798
      }
    }
    */
    root = new BorderPane() {
      top = GUIObjectFactory.toolbarFactory(
        List(
          (new Label{text <== observableCards}, false),
          (new ImageView(new Image("cardSprite.png")), true),
          (new Button("Back") {onAction = () =>
            if(gameController.user.battleDeck.size == 8) changeScene()
            else println("You have to take 8 cards in order to procede.")
          }, false)
        )
      )
      center = new ScrollPane() {
        hbarPolicy = ScrollBarPolicy.Never
        vbarPolicy = ScrollBarPolicy.AsNeeded
        content = gridPane
        id = "scrollPane"
      }
      id = "cardsPane"
    }
  }
}