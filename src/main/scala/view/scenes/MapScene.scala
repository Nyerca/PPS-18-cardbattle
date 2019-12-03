package view.scenes

import controller.{GameController, MapController, MapControllerImpl}
import exception._
import javafx.scene.control.Alert.AlertType
import javafx.scene.input.MouseEvent
import javafx.scene.paint.ImagePattern
import model._
import scalafx.Includes._
import scalafx.scene.{Node, Parent, Scene}
import scalafx.scene.control._
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.KeyEvent
import scalafx.scene.layout._
import scalafx.scene.shape.Rectangle
import scalafx.stage.Stage

import scala.collection.mutable.ListBuffer
import scala.util.Random
import model.Cell
import scalafx.scene.control.ScrollPane.ScrollBarPolicy

class MapScene (override val parentStage: Stage, var _controller : MapController, var gameC :GameController) extends BaseScene{
  stylesheets.add("mapStyle.css")

  val _pane = new Pane {
    children = _controller.list
    maxHeight = 800
    for(el <- _controller.getAllEnemies() ) {
      val cell = new PlayerRepresentation(el.position, el.url)

      children.append(icon(cell, 100, 90))
    }
  }

  def addToToolbar(toolbar: ToolBar, btn: Button, isLast:Boolean): Unit = {
    toolbar.getItems.add(btn)
    if(!isLast) toolbar.getItems.add(new Separator())
  }

  var menu: VBox = new VBox {
    val toolbar = new ToolBar()
    addToToolbar(toolbar, new Button("Cards"){
      onAction = () => parentStage.scene_=(EquipmentScene(parentStage, gameC))
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

    _pane.toBack()
  }


  def createBottomCard(): ListBuffer[Button] = {
    val tmpList = ListBuffer[Button]()
    val btn: Button = new Button {
      val re = new RectangleCellImpl(true, true, true, true, _x= 0.0, elementY=0.0)
      onAction = () => _controller.selected_(Option(re))
      defaultButton = true
      graphic = new ImageView(RectangleCell.createImage(re.url, re.rotation).getImage) {
        fitWidth_=(100)
        fitHeight_=(100)
      }
    }
    btn.getStyleClass().add("bottomButton");
    tmpList.append(btn)

    for(i<-0 until 4) {
      val btn_tmp: Button = new Button {
        var re: Cell = _
        if(math.random() <= 0.8) {
          re = RectangleCell.generateRandomCard()
        } else {
          re = new EnemyCell(gameC.spawnEnemy(Random.nextInt(4)))
        }
        graphic = new ImageView(re.image) {
          fitWidth_=(100)
          fitHeight_=(100)
        }
        onAction = () => _controller.selected_(Option(re))
        defaultButton = true

      }

      btn_tmp.getStyleClass().add("bottomButton");
      tmpList.append(btn_tmp)
    }
    tmpList
  }

  val bottomPane: HBox = new HBox() {
    id ="bottomRngPane"
    translateX = 300
    translateY = 630
    maxWidth = 500
    maxHeight = 120
    children = createBottomCard()
  }


  val _bpane: BorderPane = new BorderPane {
    top = menu
    center = _pane
    bottom = bottomPane
  }

  def bpane: BorderPane = _bpane

  _controller.view_(this)

  def setPaneChildren(list :ListBuffer[RectangleWithCell], tmpRect : Option[RectangleCell]): Unit = {
    val listTmp = new ListBuffer[Node]()
    for (el <- list) yield {
      listTmp.append(el)
      if(el.rectCell.enemy._2.isDefined) {
        val tmp = el.rectCell.enemy._2.get;

        val cell = new PlayerRepresentation(tmp.position, tmp.url)
        listTmp.append(icon(cell, 100, 90))
      }
    }
    if(tmpRect.isDefined) listTmp.append(new RectangleWithCell(tmpRect.get.getWidth, tmpRect.get.getHeight, tmpRect.get.x, tmpRect.get.getY,tmpRect.get) {
      fill = RectangleCell.createImage(tmpRect.get.url, tmpRect.get.rotation)
    })

    _pane.children =listTmp
  }
  def setBPane(): Unit = {
    var addList = createBottomCard()
    bottomPane.children = addList
  }

  def icon(player: PlayerRepresentation, elemWidth: Double = 60, elemHeight: Double = 80 ): Rectangle = {
    new Rectangle() {
      x=player.position.x+player.position.getWidth/2 - elemWidth/2
      y=(player.position.getY+player.position.getHeight/2)-(elemHeight-10)
      width = elemWidth
      height = elemHeight
      fill_=(new ImagePattern(new Image(player.url)))
    }
  }

  var playerImg = icon(_controller.player)
  def playerImg_(player: PlayerRepresentation):Unit = {println("CALL: " + player.url); playerImg.fill_=(new ImagePattern(new Image(player.url)))}

  val paneWithPlayer  = new Pane {
    children = _controller.list
    children.append(_bpane)
    children.append(playerImg)
    id = "rootPane"

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

  root = paneWithPlayer


  def changeScene(user:User, enemy:Enemy): Unit = {
    parentStage.scene_=(BattleScene(parentStage, user,enemy))
  }

}

object MapScene {
  def apply(parentStage: Stage, gameC : GameController): MapScene = new MapScene(parentStage, new MapControllerImpl(gameC),gameC)
}
