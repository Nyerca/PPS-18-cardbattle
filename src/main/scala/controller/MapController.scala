package controller

import java.io.{FileOutputStream, ObjectOutputStream}

import exception.DoubleMovementException
import javafx.scene.input.MouseEvent
import model.{Bottom, Cell, Chest, ChestPosition, EmptyPosition, Enemy, EnemyCell, EnemyPosition, Left, MapEvent, MapPosition, PlayerPosition, PlayerRepresentation, Pyramid, PyramidPosition, RectangleCell, Right, Statue, StatuePosition, Top}
import scalafx.Includes._
import scalafx.scene.input.KeyCode
import view.scenes.MapScene

import scala.util.Random
import model.Placeable._


trait MapController {
  def gameC: GameController
  def selected_(element: Option[Cell]):Unit
  def _list: List[RectangleCell]
  def removeEnemyCell(): Unit
  def startingDefined: Option[RectangleCell]
  def view_ (newView : MapScene): Unit
  def player: PlayerRepresentation
  def postInsert(): Unit
  def list:List[RectangleCell]
  def addToList(rect: RectangleCell): Unit

  def getAllEnemies: Int

  def handleSave(): Unit
  def handleKey(keyCode : KeyCode): Unit
  def handleMouseClicked(e:MouseEvent): Unit
  def reset():Unit
}


class MapControllerImpl (override val gameC : GameController, var _list:List[RectangleCell], var startingDefined : Option[RectangleCell], traslationX:Double, traslationY:Double) extends MapController {

  def this(gameC : GameController) {this(gameC,MapController.setup(gameC),Option.empty,0,0)}

  var selected:Option[Cell] = Option.empty
  override def selected_(element: Option[Cell]):Unit = selected = element

  override def list:List[RectangleCell] = _list

  override def removeEnemyCell(): Unit = list.collect { case f if f.mapEvent.isDefined && f == _player.position && f.mapEvent.get.cellEvent.isInstanceOf[Enemy] => f.mapEvent_(Option.empty); postInsert() }
    //list.filter(f => f.mapEvent.isDefined && f == _player.position && f.mapEvent.get.cellEvent.isInstanceOf[Enemy]).map(m => {m.mapEvent_(Option.empty); postInsert()} )
  private def removeChestCell(): Unit = list.collect { case f if f.mapEvent.isDefined && f == _player.position && f.mapEvent.get.cellEvent.isInstanceOf[Chest] => f.mapEvent_(Option.empty); postInsert() }


  override def addToList(rect: RectangleCell): Unit = {
    _list = _list :+ rect
    dashboard.cells = _list
  }

  var dashboard = Dashboard(_list, traslationX, traslationY)

  var _player : PlayerRepresentation = _
  startingDefined match {
    case Some(rect: RectangleCell) => _player = PlayerRepresentation((dashboard ? (rect.x, rect.y)).get, "/player/bot.png")
    case _ => _player = PlayerRepresentation(list.head, "/player/bot.png")
  }
  override def player: PlayerRepresentation = _player

  var view: MapScene = _
  override def view_ (newView : MapScene): Unit = {
    view = newView
    MovementAnimation.setAnimationNode(view.mapWindow)
    view.setMenu()
  }

  override def reset(): Unit = {
    val newMap =  MapScene(view.parentStage, gameC)
    gameC.gameMap = newMap
    gameC.setScene(view, newMap)
  }

  private def checkAnimationEnd(url: String):Boolean = {
    if(MovementAnimation.checkAnimationEnd()) {
      resetPlayer(_player.position, url + ".png")
      true
    }
    else throw new DoubleMovementException
  }

  private def afterMovement(newRectangle: RectangleCell ,stringUrl : String, isEnded: Boolean): Unit ={
    if(isEnded) {
      if(newRectangle.url.contains("Dmg")) {
        gameC.user.actualHealthPoint = gameC.user.actualHealthPoint - 1
        view.updateHP()
      }
      resetPlayer(newRectangle, _player.url)

      val event = player.position.mapEvent
      if(event.isDefined) {
        event.get.cellEvent match {
          case enemy:Enemy => view.changeScene(gameC.user, player.position.mapEvent.get.cellEvent.asInstanceOf[Enemy])
          case statue:Statue => view.showStatueAlert(player.position.mapEvent.get.cellEvent.asInstanceOf[Statue].moneyRequired)
          case pyramid: Pyramid => if(player.position.mapEvent.get.playerRepresentation.url.contains("Door")) reset()
          case chest: Chest => {view.showChestAlert(player.position.mapEvent.get.cellEvent.asInstanceOf[Chest].money); removeChestCell}
        }
      }
    } else resetPlayer(_player.position, stringUrl)
  }

  private def resetPlayer(newPosition: RectangleCell, newUrl: String): Unit = {
    _player = PlayerRepresentation(newPosition, newUrl)
    view.playerImg_(player)
  }

  override def handleKey(keyCode : KeyCode): Unit = {
    keyCode.getName match {
      case "W" => if(checkAnimationEnd(Top.url())) dashboard -> (Top, player, afterMovement)
      case "A" => if(checkAnimationEnd(Left.url())) dashboard -> (Left, player, afterMovement)
      case "S" => if(checkAnimationEnd(Bottom.url())) dashboard -> (Bottom, player, afterMovement)
      case "D" => if(checkAnimationEnd(Right.url())) dashboard -> (Right, player, afterMovement)
      case _ =>
    }
  }

  override def getAllEnemies: Int = list.map(m=> m.mapEvent).count(f => f.isDefined && f.get.cellEvent.isInstanceOf[Enemy])

  private def pyramidDoor(url: String): Unit = {
    val pyramid = list.find(f => f.mapEvent.isDefined && f.mapEvent.get.cellEvent.isInstanceOf[Pyramid]).get
    pyramid.mapEvent_(Option(MapEvent(pyramid.mapEvent.get.cellEvent, PlayerRepresentation(pyramid, url))))
  }

  override def handleSave(): Unit = {
    import FileManager._
    output = new ObjectOutputStream(new FileOutputStream("./src/main/saves/save.txt"))
    save(gameC.user)
    save(gameC.difficulty)
    save(list.map(el => el))
    save(player)
    save(dashboard.traslationX)
    save(dashboard.traslationY)
    output.close()
  }

  override def postInsert(): Unit = {
    view.updateParameters()
    if(getAllEnemies > 0) pyramidDoor("pyramid.png")
    else pyramidDoor("pyramidDoor.png")
    view.setPaneChildren(_list)
    selected = Option.empty
    view.setBPane()
  }

  override def handleMouseClicked(e:MouseEvent): Unit = {
    val cell = dashboard ? (e.x - dashboard.traslationX, e.y - dashboard.traslationY)
    selected match {
      case Some(rc:RectangleCell) =>
        rc.x_(e.x - dashboard.traslationX - e.x % 200)
        rc.y_(e.y - dashboard.traslationY - e.y % 200)
        place(rc,cell,this)
      case Some(ec:EnemyCell) => place(ec,cell,this)
      case _ =>
    }
  }
}

object MapController {
  def setup(gameC: GameController): List[RectangleCell] = {
    var list :List[RectangleCell] = List()

    var newList: List[MapPosition] =List(PlayerPosition, EnemyPosition, StatuePosition, PyramidPosition, ChestPosition)
    for(i<-0 until Random.nextInt(5)) {
      val rnd = math.random()
      if(rnd < 0.8) newList = newList:+EmptyPosition
      else if(rnd <0.9) newList = newList:+StatuePosition
      else newList = newList:+EnemyPosition
    }

    var excludedValues: Map[Int,List[Int]] = Map()
    newList.foreach(el => {
      val rect =  el.create(gameC,excludedValues)
      list = list:+rect
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