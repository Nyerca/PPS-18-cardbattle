package view.scenes

import controller.GameController
import javafx.scene.layout.Background
import javafx.scene.paint.ImagePattern
import scalafx.Includes._
import scalafx.geometry.Pos
import scalafx.scene.control.ScrollPane.ScrollBarPolicy
import scalafx.scene.control._
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout._
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.Text
import scalafx.scene.{Node, Scene}
import scalafx.stage.Stage
import view.scenes.component.{CardComponent, CardComponentImpl}

import scala.collection.mutable.ListBuffer


class EquipmentScene(override val parentStage: Stage, gameController: GameController) extends BaseScene{

  stylesheets.add("mapStyle.css")
  stylesheets.add("style.css")

  println("BATTLEDECK")
  println(gameController.user.battleDeck);
  println("ALL CARDS")
  println(gameController.user.allCards);


/*
  def createCardPane(valX:Double, valY:Double): Pane = {
    new Pane {
      children = new ListBuffer[Node]
      maxHeight = 800
      val btn = new Button() {
        graphic = new ImageView(new Image("card2.png"))
        onAction = () => println("Clicked")
      }
      children.append(btn)
      children.append(new Text(valX + 20,valY + 30,"Fireball"))
      children.append(new Text(valX + 90,valY + 184,"MAGIC"))
      children.append(new Text(valX + 60,valY + 240,"DMG:     3x"))
      val img = new ImageView(new Image("mdmg.png")) { x=valX + 120; y=valY + 210}
      children.append(img)
    }
  }
*/
  def createCardPane(valX:Double, valY:Double): Pane = {
    new Pane {
      children = new ListBuffer[Node]
      maxHeight = 800
      val c = new CardComponentImpl(0,0, false,handle{println("ciao")})
      c.setCardInformation(gameController.allCards.head)
      val btn: Button = c.clickableCard
      children.append(btn)
    }
  }

  def addCard(gridPane: GridPane, num : Integer): Unit = {
    for(i<- 0 to num - 1) gridPane.add(createCardPane(0,0), i % 4, i / 4);
  }

  var gridPane = new GridPane() {id="grid"}
  addCard(gridPane, 12)

  val toolbar = new ToolBar()
  val text2 = new Text(gameController.user.battleDeck.size + " /")
  val text3 = new Text("8   ")
  toolbar.getItems().add(new Text("CARDS:         "))
  toolbar.getItems().add(text2);
  toolbar.getItems().add(text3);
  toolbar.getItems().add(new ImageView(new Image("cardSprite.png")));
  private def changeScene(): Unit = gameController.setScene(this)
  val back = new Button("Back") {onAction = () => changeScene}
  toolbar.getItems().add(back);

  val cardsPane = new BorderPane() {
    top = toolbar
    center = gridPane
    id = "cardsPane"
  }

  //var image =  new Image("shop.jpg")
  //val bSize = new BackgroundSize(BackgroundSize.Auto, BackgroundSize.Auto, false, false, true, true);
  //val bimg = new BackgroundImage(image, BackgroundRepeat.NoRepeat, BackgroundRepeat.NoRepeat, BackgroundPosition.Center, bSize)
  //val background2 = new Background(bimg);
  //bpane.background_=(background2)


  root = new ScrollPane() {
    hbarPolicy = ScrollBarPolicy.Never
    vbarPolicy = ScrollBarPolicy.AsNeeded
    id= "scrollPane"
    content = cardsPane
    //pannable_=(true)
  }

}

object EquipmentScene {
  def apply(parentStage: Stage, gameController: GameController): EquipmentScene = new EquipmentScene(parentStage, gameController)
}