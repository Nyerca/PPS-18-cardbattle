package view.scenes

import utility.GUIObjectFactory
import controller.GameController
import javafx.beans.property.SimpleStringProperty
import model.Card
import scalafx.Includes._
import scalafx.scene.control.ScrollPane.ScrollBarPolicy
import scalafx.scene.control._
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout._
import scalafx.scene.Node
import scalafx.stage.Stage
import view.scenes.component.CardComponent
import scala.collection.mutable.ListBuffer

trait EquipmentScene extends BaseScene

object EquipmentScene {

  def apply(parentStage: Stage, gameController: GameController): EquipmentScene = EquipmentSceneImpl(parentStage, gameController)

  private case class EquipmentSceneImpl(override val parentStage: Stage, gameController: GameController) extends EquipmentScene{

    private var index = 0

    private val gridPane: GridPane = new GridPane() {id="grid"; minHeight = 798}

    private val observableCards = new SimpleStringProperty("CARDS:         " +gameController.user.battleDeck.size+ " /8")

    private def createCardPane(card: Card): Pane = new Pane {
      children = new ListBuffer[Node]
      val c = CardComponent(0,0, false,handle{
        println(gameController.user.battleDeck.map(el => el.name))

        gameController.setDeck(card)
        if(btn.styleClass.contains("equipSelectedCard")) btn.styleClass.remove("equipSelectedCard")
        else btn.styleClass.add("equipSelectedCard")

        observableCards.set("CARDS:         " + gameController.user.battleDeck.size+ " /8")
      })
      c.setCardInformation(card)
      val btn: Button = c.clickableCard
      if(gameController.user.battleDeck.contains(card)) btn.styleClass.add("equipSelectedCard")

      children.append(btn)

      children.append(c.cardLevel)
      children.append(c.cardName)
      children.append(c.cardDamage)
    }

    private def addCard(gridPane: GridPane, card: Card): Unit = {
      gridPane.add(createCardPane(card), index % 4, index / 4)
      index=index+1
    }

    private def changeScene(): Unit = gameController.setScene(this)

    stylesheets.add("mapStyle.css")

    stylesheets.add("style.css")

    gameController.user.allCards.foreach(c => addCard(gridPane, c))

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