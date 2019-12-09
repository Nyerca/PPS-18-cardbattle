package view.scenes

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


class EquipmentScene(override val parentStage: Stage, gameController: GameController) extends BaseScene{

  stylesheets.add("mapStyle.css")
  stylesheets.add("style.css")

  private def createCardPane(card: Card): Pane = {
    new Pane {
      children = new ListBuffer[Node]

      maxHeight = 800
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
  }

  var index = 0

  private def addCard(gridPane: GridPane, card: Card): Unit = {
    gridPane.add(createCardPane(card), index % 4, index / 4)
    index=index+1
  }

  private val gridPane: GridPane = new GridPane() {id="grid"}
  gameController.user.allCards.foreach(c => addCard(gridPane, c))

  private val toolbar = new ToolBar()
  private val observableCards = new SimpleStringProperty(gameController.user.battleDeck.size+ " /")
  private var cards: Label = new Label{text <== observableCards}
  private def setCards(): Unit = observableCards.set(gameController.user.battleDeck.size+ " /")

  toolbar.getItems.add(new Text("CARDS:         "))
  toolbar.getItems.add(cards)
  toolbar.getItems.add(new Text("8   "))
  toolbar.getItems.add(new ImageView(new Image("cardSprite.png")));
  private def changeScene(): Unit = gameController.setScene(this)
  private val btn: Button = new Button("Back") {onAction = () =>
    if(gameController.user.battleDeck.size == 8) changeScene()
    else println("You have to take 8 cards in order to procede.")
  }
  toolbar.getItems.add(btn)

  private val cardsPane: BorderPane = new BorderPane() {
    top = toolbar
    center = gridPane
    id = "cardsPane"
  }

  root = new ScrollPane() {
    hbarPolicy = ScrollBarPolicy.Never
    vbarPolicy = ScrollBarPolicy.AsNeeded
    id= "scrollPane"
    content = cardsPane
  }
}

object EquipmentScene {
  def apply(parentStage: Stage, gameController: GameController): EquipmentScene = new EquipmentScene(parentStage, gameController)
}