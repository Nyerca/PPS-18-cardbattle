package controller

import java.io.{FileOutputStream, ObjectOutputStream}

import exception.DoubleMovementException
import javafx.scene.input.MouseEvent
import model.{Bottom, Cell, EmptyPosition, Enemy, EnemyCell, EnemyPosition, Left, MapEvent, MapPosition, PlayerPosition, PlayerRepresentation, Pyramid, PyramidPosition, RectangleCell, RectangleCellImpl, RectangleWithCell, Right, Statue, StatuePosition, Top}
import scalafx.Includes._
import scalafx.scene.input.KeyCode
import view.scenes.MapScene

import scala.collection.mutable.ListBuffer
import scala.util.Random
import model.Placeable._


trait MapController {
  def gameC: GameController
  def selected_(element: Option[Cell]):Unit
  def _list: ListBuffer[RectangleWithCell]
  def removeEnemyCell(): Unit
  def startingDefined: Option[RectangleCell]
  def view_ (newView : MapScene): Unit
  def player: PlayerRepresentation
  def postInsert(): Unit
  def list:ListBuffer[RectangleWithCell]
  def addToList(rect: RectangleWithCell): Unit

  def getAllEnemies: ListBuffer[PlayerRepresentation]

  def handleSave(): Unit
  def handleKey(keyCode : KeyCode): Unit
  def handleMouseClicked(e:MouseEvent): Unit
  def reset():Unit
}


class MapControllerImpl (override val gameC : GameController, var _list:ListBuffer[RectangleWithCell], var startingDefined : Option[RectangleCell], traslationX:Double, traslationY:Double) extends MapController {

  def this(gameC : GameController) {this(gameC,MapController.setup(gameC),Option.empty,0,0)}

  var selected:Option[Cell] = Option.empty
  override def selected_(element: Option[Cell]):Unit = selected = element

  override def list:ListBuffer[RectangleWithCell] = _list

  override def removeEnemyCell(): Unit = {
    val elem: Option[RectangleWithCell] = list.find(rc => rc.rectCell.mapEvent.isDefined && rc.rectCell == _player.position)

    if(elem.isDefined && elem.get.rectCell.mapEvent.get.callEvent.isInstanceOf[Enemy]) {
      elem.get.rectCell.mapEvent_(Option.empty)
      postInsert()
    }

    view.updateParameters()

    if(getAllEnemies.isEmpty) {
      println("No more enemies.......")
      pyramidDoor("pyramidDoor.png")
      postInsert()
    }
  }

  override def addToList(rect: RectangleWithCell): Unit = {
    _list.append(rect)
    dashboard.setCells(_list)
  }

  var dashboard = new DashboardImpl(list)
  dashboard.translationX_(traslationX)
  dashboard.translationY_(traslationY)

  var _player : PlayerRepresentation = _
  startingDefined match {
    case Some(rect: RectangleCell) => _player = new PlayerRepresentation(dashboard.searchPosition(rect.x, rect.y).get, "/player/bot.png")
    case _ => _player = new PlayerRepresentation(list.head.rectCell, "/player/bot.png")
  }
  override def player: PlayerRepresentation = _player

  dashboard.player_(_player)

  var view: MapScene = _
  override def view_ (newView : MapScene): Unit = {
    view = newView
    MovementAnimation.setAnimationNode(view.bpane)
    view.setMenu()
  }

  override def reset(): Unit = {
    val newMap =  MapScene(view.parentStage, gameC)
    gameC.gameMap = newMap
    gameC.setScene(view, newMap)
  }

  def checkAnimationEnd(url: String):Boolean = {
    if(MovementAnimation.checkAnimationEnd()) {
      player.url_(url + ".png")
      view.playerImg_(player)
      true
    }
    else throw new DoubleMovementException
  }

  def afterMovement(newRectangle: RectangleCell ,stringUrl : String, isEnded: Boolean): Unit ={
    if(isEnded) {
      if(newRectangle.url.contains("Dmg")) {
        gameC.user.actualHealthPoint = gameC.user.actualHealthPoint - 1
        view.updateHP()
      }
      player.position_(newRectangle, stringUrl)
      view.playerImg_(player)

      val event = player._position.mapEvent
      if(event.isDefined) {
        event.get.callEvent match {
          case enemy:Enemy => view.changeScene(gameC.user, player._position.mapEvent.get.callEvent.asInstanceOf[Enemy])
          case statue:Statue => view.showStatueAlert(player._position.mapEvent.get.callEvent.asInstanceOf[Statue].moneyRequired)
          case pyramid: Pyramid => if(player._position.mapEvent.get.playerRepresentation.url.contains("Door")) reset()
        }
      }
    } else {
      player.url_(stringUrl)
      view.playerImg_(player)
    }
  }

  override def handleKey(keyCode : KeyCode): Unit = {
    keyCode.getName match {
      case "W" => if(checkAnimationEnd(Top.url())) dashboard.move(Top, afterMovement) ;
      case "A" => if(checkAnimationEnd(Left.url())) dashboard.move(Left, afterMovement) ;
      case "S" => if(checkAnimationEnd(Bottom.url())) dashboard.move(Bottom, afterMovement)
      case "D" => if(checkAnimationEnd(Right.url())) dashboard.move(Right, afterMovement) ;
      case _ =>
    }
  }

  override def getAllEnemies: ListBuffer[PlayerRepresentation] = {
    list.map(m=> m.rectCell.mapEvent).filter(f => f.isDefined && f.get.callEvent.isInstanceOf[Enemy]).map(mm => mm.get.playerRepresentation)
  }

  private def pyramidDoor(url: String): Unit = {
    val pyramid = list.find(f => f.rectCell.mapEvent.isDefined && f.rectCell.mapEvent.get.callEvent.isInstanceOf[Pyramid]).get
    pyramid.rectCell.mapEvent_(Option(MapEvent.createMapEvent(pyramid.rectCell.mapEvent.get.callEvent, new PlayerRepresentation(pyramid.rectCell, url))))
  }

  override def handleSave(): Unit = {
    val output = new ObjectOutputStream(new FileOutputStream("./src/main/saves/save.txt"))
    output.writeObject(list.map(m => m.rectCell))
    output.writeObject(player)
    output.writeObject(gameC.user)
    output.writeObject(gameC.difficulty)
    output.writeObject(dashboard.traslationX)
    output.writeObject(dashboard.traslationY)
    output.close()
  }

  override def postInsert(): Unit = {
    view.updateParameters()
    if(getAllEnemies.nonEmpty) pyramidDoor("pyramid.png")
    view.setPaneChildren(list, Option.empty)
    selected = Option.empty
    view.setBPane()
  }

  import model.Monoid._

  override def handleMouseClicked(e:MouseEvent): Unit = {
    val cell = dashboard.searchPosition(e.x - dashboard.traslationX, e.y - dashboard.traslationY)
    //println("CLICKED : " + cell)
    selected match {
      case Some(rc:RectangleCell) =>
        rc.x_(sum(e.x, dashboard.traslationX))
        rc.y_(sum(e.y, dashboard.traslationY))
        place(rc,cell,this)
      case Some(ec:EnemyCell) => place(ec,cell,this)
      case _ =>
    }
  }
}

object MapController {
  def setup(gameC: GameController): ListBuffer[RectangleWithCell] = {
    val list = new ListBuffer[RectangleWithCell]()

    val rngCells = Random.nextInt(6)

    var newList: List[MapPosition] =List(PlayerPosition, EnemyPosition, StatuePosition, PyramidPosition)
    for(i<-0 until rngCells) {
      val rnd = math.random()
      if(rnd < 0.8) newList = newList:+EmptyPosition
      else if(rnd <0.9) newList = newList:+StatuePosition
      else newList = newList:+EnemyPosition
    }

    var excludedValues: Map[Int,List[Int]] = Map()
    newList.foreach(el => {
      val rect =  el.create(gameC,excludedValues)
      list.append(new RectangleWithCell(rect.getWidth, rect.getHeight, rect.x, rect.y, rect) {
        fill = RectangleCell.createImage(rect.url, rect.rotation)
      })
      if(!excludedValues.contains(rect.x.toInt)) {
        excludedValues += (rect.x.toInt -> List[Int](rect.y.toInt))
      } else {
        val tmplist: List[Int] = excludedValues.get(rect.x.toInt).get :+ rect.y.toInt
        excludedValues += (rect.x.toInt -> tmplist)
      }
    })
    list
  }

}