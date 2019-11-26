package view

import controller.{GameController, MapController}
import javafx.scene.control.Alert.AlertType
import javafx.scene.paint.ImagePattern
import model._
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.{Node, Scene}
import scalafx.scene.control._
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.layout._
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle

import scala.collection.mutable.ListBuffer
import scala.util.Random
import javafx.scene.input.MouseEvent
import scalafx.scene.text.Text
import scalafx.stage.Stage
import view.scenes.{BaseScene, BattleScene}


/** Main class for the "Hello World" style example. */
class map (override val parentStage: Stage, var _controller : MapController) extends BaseScene{

  def setController(controller : MapController): Unit = {
    _controller = controller
    _controller.view_(
      this)
  }

  val _pane = new Pane {
    children = _controller.list
    maxHeight = 800
    for(el <- _controller.getAllEnemies ) {
      val cell = PlayerRepresentation.createPlayerCell(el.position, el.url)
      cell.setFill()
      children.append(cell.icon)
    }


  }


  var menu = new VBox {
    val toolbar = new ToolBar()
    val card = new Button("Cards"){
      onAction = () => println("cards clicked")
      layoutX = 110
    }
    val shop = new Button("Shop"){
      onAction = () => println("shop clicked")
    }
    val save = new Button("Save"){
      onAction = () => _controller.handleSave()
    }
    val quit = new Button("Quit"){
      onAction = () => {  val alert = new Alert(AlertType.INFORMATION)
        alert.setTitle("Item Obtained")
        alert.setGraphic(new ImageView(new Image("vamp.png")))
        alert.setHeaderText("You obtained a new item!")
        alert.setContentText("ITEM")
        alert.showAndWait();}
    }
    toolbar.getItems().add(card);
    toolbar.getItems().add(new Separator());
    toolbar.getItems().add(shop);
    toolbar.getItems().add(new Separator());
    toolbar.getItems().add(save);
    toolbar.getItems().add(new Separator());
    toolbar.getItems().add(quit);
    children = toolbar
    minWidth = 800
  }


  def setMenu(): Unit = {
    bottomPane.toBack()
    _pane.toBack()
    /*
_bpane.top = new VBox {
  val toolbar = new ToolBar()
  val card = new Button("Cards"){
    onAction = () => println("cards clicked")
  }
  val shop = new Button("Shop"){
    onAction = () => println("shop clicked")
  }
  val save = new Button("Save"){
    onAction = () => println("save clicked")
  }
  val quit = new Button("Quit"){
    onAction = () => println("quit clicked")
  }
  toolbar.getItems().add(card);
  toolbar.getItems().add(new Separator());
  toolbar.getItems().add(shop);
  toolbar.getItems().add(new Separator());
  toolbar.getItems().add(save);
  toolbar.getItems().add(new Separator());
  toolbar.getItems().add(quit);
  children = toolbar
  minWidth = 200
}
*/
  }

  val bottomPane = new HBox() {
    layoutX = 10
    layoutY = 580
    children = List()

    var addList = _controller.createBottomCard
    children = addList
  }

  val _bpane = new BorderPane {
    top = menu
    center = _pane
    bottom = bottomPane
  }
  def pane = _pane
  def bpane = _bpane

  _controller.view_(this)

  def setPaneChildren(list :ListBuffer[RectangleWithCell], tmpRect : Option[RectangleCell]): Unit = {
    val listTmp = new ListBuffer[Rectangle]()
    for (el <- list) yield {
      listTmp.append(el);
      if(el.rectCell.enemy.isDefined) { val tmp = el.rectCell.enemy.get; var cell = PlayerRepresentation.createPlayerCell(tmp.position, tmp.url); cell.icon.fill_=(new ImagePattern(new Image(tmp.url))); listTmp.append(cell.icon); }
    }
    if(tmpRect.isDefined) listTmp.append(new RectangleWithCell(tmpRect.get.getWidth, tmpRect.get.getHeight, tmpRect.get.getX, tmpRect.get.getY,tmpRect.get) {
      fill = (RectangleCell.createImage(tmpRect.get.url, tmpRect.get.rotation))
    })

    pane.children =listTmp
  }
  def setBPane(): Unit = {
    var addList = _controller.createBottomCard
    bottomPane.children = addList
  }

  val scene = new Scene(1200, 800) {

    fill = (new ImagePattern(new Image( "noroad.png"), 0, 0, 200, 200, false));
    content = _bpane
    content.add(_controller.player.icon);

    onKeyPressed = (ke : KeyEvent) =>  {
      try {
        _controller.handleKey(ke.code)
      } catch {
        case e : DoubleMovementException => {
          println("You can't move while the previus movement is still executing.")
        }case e : MissingCellException => {
          println("You can't move on a missing cell.")
        }case e : NoMovementException => {
          println("You can't move in that direction because your cell doesn't allow that.")
        }
      }

    }
    onMouseClicked = (e: MouseEvent) => {
      try {
        _controller.handleMouseClicked(e)
      } catch {
        case e : DoubleCellException => {
          println("You can't place a cell on top of another cell.")
        }case e : MissingCellException => {
          println("You can't place an enemy on a missing cell.")
        }case e : DoubleEnemyException => {
          println("You can't place an enemy on top of another enemy.")
        }
      }
    }
  }

  def getScene(): Scene = {
    scene
  }

  //stage.resizable = false
  //stage.fullScreen = true

  def changeScene(): Unit = {
    parentStage.scene_=(BattleScene(parentStage))
  }


}

object map {
  def apply(parentStage: Stage): map = new map(parentStage, new MapController())
}
