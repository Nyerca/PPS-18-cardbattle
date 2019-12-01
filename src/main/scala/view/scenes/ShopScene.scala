package view.scenes

import javafx.scene.layout.Background
import javafx.scene.paint.ImagePattern
import scalafx.Includes._
import scalafx.geometry.Pos
import scalafx.scene.control._
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout._
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.Text
import scalafx.scene.{Node, Scene}
import scalafx.stage.Stage

import scala.collection.mutable.ListBuffer


class ShopScene(override val parentStage: Stage) extends BaseScene{

  /*
  def createCard(valX:Double, valY:Double): ListBuffer[Node] = {
    val list = new ListBuffer[Node]
    val re = new Rectangle() {
      x=valX
      y=valY
      width = 220
      height = 300
      fill = new ImagePattern(new Image("card2.png"))
    }
    val t = new Text(valX + 20,valY + 30,"Fireball")
    val t2 = new Text(valX + 90,valY + 184,"MAGIC")
    val t3 = new Text(valX + 60,valY + 240,"DMG:     3x")
    val t4 = new ImageView(new Image("mdmg.png")) {
      x=valX + 120
      y=valY + 210
    }
    list.append(re)
    list.append(t)
    list.append(t2)
    list.append(t3)
    list.append(t4)
    list
  }
*/

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
    hgap = 200
    vgap = 200
    alignment = Pos.Center
  }

  //gridPane.setHgap(200);
  //gridPane.setVgap(200);
  //gridPane.setAlignment(Pos.Center)

  gridPane.add(createCardPane(0,0), 0, 0);
  gridPane.add(createCardPane(0,0), 1, 0);
  gridPane.add(createCardPane(0,0), 2, 0);
  gridPane.add(createCardPane(0,0), 0, 1);
  gridPane.add(createCardPane(0,0), 1, 1);
  gridPane.add(createCardPane(0,0), 2, 1);

  val toolbar = new ToolBar()
  val imagep = new ImageView(new Image("coin.png"))
  val text = new Text("GOLD:         50x  ")
  toolbar.getItems().add(text);
  toolbar.getItems().add(imagep);


  val bpane = new BorderPane() {
    top = toolbar
    center = gridPane
  }
  bpane.minWidth_=(1200)
  bpane.minHeight_=(800)
  bpane.maxHeight_=(800)
  var image =  new Image("shop.jpg")
  val bSize = new BackgroundSize(BackgroundSize.Auto, BackgroundSize.Auto, false, false, true, true);
  val bimg = new BackgroundImage(image, BackgroundRepeat.NoRepeat, BackgroundRepeat.NoRepeat, BackgroundPosition.Center, bSize)
  val background2 = new Background(bimg);
  bpane.background_=(background2)


  root = bpane
}

object ShopScene {
  def apply(parentStage: Stage): ShopScene = new ShopScene(parentStage)
}