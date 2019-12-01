package view.scenes

import controller.{GameController, MapController, MapControllerImpl}
import exception._
import javafx.scene.control.Alert.AlertType
import javafx.scene.input.MouseEvent
import javafx.scene.paint.ImagePattern
import model._
import scalafx.Includes._
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.KeyEvent
import scalafx.scene.layout._
import scalafx.scene.shape.Rectangle
import scalafx.stage.Stage

import scala.collection.mutable.ListBuffer


class MapScene (override val parentStage: Stage, var _controller : MapController, var gameC :GameController) extends BaseScene{

  val _pane = new Pane {
    children = _controller.list
    maxHeight = 800
    for(el <- _controller.getAllEnemies() ) {
      val cell = PlayerRepresentation.createPlayerCell(el.position, el.url)
      cell.setFill()
      children.append(cell.icon)
    }
  }

  def addToToolbar(toolbar: ToolBar, btn: Button, isLast:Boolean): Unit = {
    toolbar.getItems.add(btn)
    if(!isLast) toolbar.getItems.add(new Separator())
  }

  var menu: VBox = new VBox {
    val toolbar = new ToolBar()
    addToToolbar(toolbar, new Button("Cards"){
      onAction = () => parentStage.scene_=(EquipmentScene(parentStage))
      layoutX = 110
    }, false)
    addToToolbar(toolbar, new Button("Shop"){onAction = () => parentStage.scene_=(ShopScene(parentStage))}, false)
    addToToolbar(toolbar, new Button("Save"){onAction = () => _controller.handleSave()}, false)
    addToToolbar(toolbar, new Button("Quit"){
      onAction = () => {  val alert = new Alert(AlertType.INFORMATION)
        alert.setTitle("Item Obtained")
        alert.setGraphic(new ImageView(new Image("vamp.png")))
        alert.setHeaderText("You obtained a new item!")
        alert.setContentText("ITEM")
        alert.showAndWait();}
    }, true)

    children = toolbar
    minWidth = 1200
  }


  def setMenu(): Unit = {
    bottomPane.toBack()
    _pane.toBack()
  }

  val bottomPane: HBox = new HBox() {
    layoutX = 10
    layoutY = 580
    children = List()

    var addList = _controller.createBottomCard()
    children = addList
  }

  val _bpane: BorderPane = new BorderPane {
    top = menu
    center = _pane
    bottom = bottomPane
  }
  def pane: Pane = _pane
  def bpane: BorderPane = _bpane

  _controller.view_(this)

  def setPaneChildren(list :ListBuffer[RectangleWithCell], tmpRect : Option[RectangleCell]): Unit = {
    val listTmp = new ListBuffer[Rectangle]()
    for (el <- list) yield {
      listTmp.append(el)
      if(el.rectCell.enemy._2.isDefined) { val tmp = el.rectCell.enemy._2.get; var cell = PlayerRepresentation.createPlayerCell(tmp.position, tmp.url); cell.icon.fill_=(new ImagePattern(new Image(tmp.url))); listTmp.append(cell.icon); }
    }
    if(tmpRect.isDefined) listTmp.append(new RectangleWithCell(tmpRect.get.getWidth, tmpRect.get.getHeight, tmpRect.get.x, tmpRect.get.getY,tmpRect.get) {
      fill = RectangleCell.createImage(tmpRect.get.url, tmpRect.get.rotation)
    })

    pane.children =listTmp
  }
  def setBPane(): Unit = {
    var addList = _controller.createBottomCard()
    bottomPane.children = addList
  }




  val scene: Scene = new Scene(1200, 800) {

    fill = new ImagePattern(new Image( "noroad.png"), 0, 0, 200, 200, false)
    content = _bpane
    content.add(_controller.player.icon)

    onKeyPressed = (ke : KeyEvent) =>  {
      try {
        _controller.handleKey(ke.code)
      } catch {
        case _ : DoubleMovementException => println("You can't move while the previus movement is still executing.")
        case _ : MissingCellException => println("You can't move on a missing cell.")
        case _ : NoMovementException => println("You can't move in that direction because your cell doesn't allow that.")
      }

    }
    onMouseClicked = (e: MouseEvent) => {
      try {
        _controller.handleMouseClicked(e)
      } catch {
        case _ : DoubleCellException => println("You can't place a cell on top of another cell.")
        case _ : MissingCellException => println("You can't place an enemy on a missing cell.")
        case _ : DoubleEnemyException => println("You can't place an enemy on top of another enemy.")
      }
    }
  }

  def getScene: Scene = scene

  def changeScene(user:User, enemy:Enemy): Unit = {
    parentStage.scene_=(BattleScene(parentStage, user,enemy))
  }

}

object MapScene {
  def apply(parentStage: Stage, gameC : GameController): MapScene = new MapScene(parentStage, new MapControllerImpl(gameC),gameC)
}
