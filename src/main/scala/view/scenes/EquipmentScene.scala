package view.scenes

import javafx.scene.layout.Background
import javafx.scene.paint.ImagePattern
import scalafx.Includes._
import scalafx.geometry.Pos
import scalafx.scene.control.ScrollPane.ScrollBarPolicy
import scalafx.scene.control._
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout._
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.Text
import scalafx.scene.{Node, Scene}
import scalafx.stage.Stage

import scala.collection.mutable.ListBuffer


class EquipmentScene(override val parentStage: Stage) extends BaseScene{


  def createCardPane(valX:Double, valY:Double): Pane = {
    val re = new Rectangle() {
      x=valX
      y=valY
      width = 220
      height = 300
      fill = new ImagePattern(new Image("card2.png"))
    }
    val btn = new Button() {
      graphic = new ImageView(new Image("card2.png"))
      onAction = () => println("Clicked")
      styleClass.add("cardIndicator");
    }
    val t = new Text(valX + 20,valY + 30,"Fireball")
    val t2 = new Text(valX + 90,valY + 184,"MAGIC")
    val t3 = new Text(valX + 60,valY + 240,"DMG:     3x")
    val t4 = new ImageView(new Image("mdmg.png")) {
      x=valX + 120
      y=valY + 210
    }


    val pane = new Pane {
      children = new ListBuffer[Node]
      maxHeight = 800
      children.append(btn)
      children.append(t)
      children.append(t2)
      children.append(t3)
      children.append(t4)

    }
    pane
  }

  var gridPane = new GridPane() {
    layoutX = 400
    layoutY = 50
    hgap = 80
    vgap = 200
    alignment = Pos.Center
  }

  def addCard(gridPane: GridPane, num : Integer): Unit = {
    for(i<- 0 to num - 1) {
      gridPane.add(createCardPane(0,0), i % 4, i / 4);
    }
  }

  //gridPane.setHgap(80);
  //gridPane.setVgap(200);
  //gridPane.setAlignment(Pos.Center)


  addCard(gridPane, 12)

  val toolbar = new ToolBar()
  val imagep = new ImageView(new Image("cardSprite.png"))
  val text = new Text("CARDS:         ")
  val text2 = new Text("0 /")
  val text3 = new Text("8   ")
  toolbar.getItems().add(text);
  toolbar.getItems().add(text2);
  toolbar.getItems().add(text3);
  toolbar.getItems().add(imagep);
  val back = new Button() {
     //onAction = () => parentStage.scene_=(MapScene(parentStage).getScene())
  }
  toolbar.getItems().add(back);

  val bpane = new BorderPane() {
    top = toolbar
    center = gridPane
  }

  var image =  new Image("shop.jpg")
  val bSize = new BackgroundSize(BackgroundSize.Auto, BackgroundSize.Auto, false, false, true, true);
  val bimg = new BackgroundImage(image, BackgroundRepeat.NoRepeat, BackgroundRepeat.NoRepeat, BackgroundPosition.Center, bSize)
  val background2 = new Background(bimg);
  bpane.background_=(background2)

  val scrollPane = new ScrollPane() {
    hbarPolicy = ScrollBarPolicy.Never
    vbarPolicy = ScrollBarPolicy.AsNeeded
    maxWidth = 1200
    minWidth = 1200
    maxHeight = 800
  }
  scrollPane.setContent(bpane);
  scrollPane.pannableProperty().set(true);

  root = scrollPane

}
object EquipmentScene {
  def apply(parentStage: Stage): EquipmentScene = new EquipmentScene(parentStage)
}