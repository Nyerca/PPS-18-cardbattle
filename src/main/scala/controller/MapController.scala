package controller

import java.io.{FileOutputStream, ObjectOutputStream}

import exception.DoubleMovementException
import javafx.scene.input.MouseEvent
import model.{Bottom, Cell, Enemy, EnemyCell, Left, MapEvent, PlayerRepresentation, Pyramid, RectangleCell, RectangleCellImpl, RectangleWithCell, Right, Statue, Top}
import scalafx.Includes._
import scalafx.scene.control.Button
import scalafx.scene.image.ImageView
import scalafx.scene.input.KeyCode
import view.scenes.MapScene

import scala.collection.mutable.ListBuffer
import scala.util.Random
import model.Placeable._
import scalafx.animation.{Interpolator, TranslateTransition}
import scalafx.util.Duration

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

  def getAllEnemies(): ListBuffer[PlayerRepresentation]
  def getAllStatues(): ListBuffer[PlayerRepresentation]
  def getPyramid(): PlayerRepresentation
  def handleSave(): Unit
  def handleKey(keyCode : KeyCode): Unit
  def handleMouseClicked(e:MouseEvent): Unit
  def reset():Unit
}


class MapControllerImpl (override val gameC : GameController, var _list:ListBuffer[RectangleWithCell], var startingDefined : Option[RectangleCell], traslationX:Double, traslationY:Double) extends MapController {

  def this(gameC : GameController) {this(gameC,MapController.setup(gameC),Option.empty,0,0)}

  println("traslationX: " + traslationX)


  var selected:Option[Cell] = Option.empty
  override def selected_(element: Option[Cell]):Unit = selected = element

  override def list:ListBuffer[RectangleWithCell] = _list


  override def removeEnemyCell(): Unit = {
    println("POOOOOOOOOOOOOOOOS: " + _player.position)

    //var outElem: PlayerRepresentation = _

    //for { el <- list; element = el.rectCell.enemy._2; if element.isDefined} yield println("UGUALE a: " + element.get)

    val elem: Option[RectangleWithCell] = list.find(rc => rc.rectCell.mapEvent.isDefined && rc.rectCell == _player.position)
    println("FIND: " + elem)

    //list.remove(list.indexOf(list.find(rc => rc.rectCell == _player.position)))

  if(elem.isDefined && elem.get.rectCell.mapEvent.get.callEvent.isInstanceOf[Enemy]) {
    elem.get.rectCell.mapEvent_(Option.empty)
    postInsert()
  }

    view.updateParameters()

      if(getAllEnemies.size == 0) {
        println("No more enemies.......")

        for { el <- list; element = el.rectCell.mapEvent; if element.isDefined && element.get.callEvent.isInstanceOf[Pyramid]} yield{

          val pyramid: Pyramid = el.rectCell.mapEvent.get.callEvent.asInstanceOf[Pyramid]
          el.rectCell.mapEvent_(Option(MapEvent.createMapEvent(pyramid, new PlayerRepresentation(el.rectCell, "pyramidDoor.png"))))

        }
        postInsert()


      }

    //outList
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
    case Some(rect: RectangleCell) => {
      _player = new PlayerRepresentation(dashboard.searchPosition(rect.x, rect.getY).get, "bot.png")
    }
    case _ => _player = new PlayerRepresentation(list.head.rectCell, "bot.png")
  }
  override def player: PlayerRepresentation = _player


  dashboard.player_(_player)
  println("DASHBOARD: " + dashboard.toString)


  var view: MapScene = _
  override def view_ (newView : MapScene): Unit = {
    view = newView

    //println("VIEW: " + view)
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

  def afterMovement(newRectangle: RectangleCell ,stringUrl : String, isEnded: Boolean): Unit = isEnded match {
    case true =>
      println("TRASLATION: " +  view.bpane.translateX.toDouble + " " + view.bpane.translateY.toDouble)

      if(newRectangle.url.contains("Dmg")) {
        gameC.user.actualHealthPoint = gameC.user.actualHealthPoint - 1
        view.updateHP()
      }
      player.position_(newRectangle, stringUrl)
      view.playerImg_(player)
      //println("--------------------------------")
      //println(player._position)
      if(player._position.mapEvent.isDefined) {
        if(player._position.mapEvent.get.callEvent.isInstanceOf[Enemy]) view.changeScene(gameC.user, player._position.mapEvent.get.callEvent.asInstanceOf[Enemy])
        else if(player._position.mapEvent.get.callEvent.isInstanceOf[Statue]) view.showStatueAlert(5);
        else if(player._position.mapEvent.get.callEvent.isInstanceOf[Pyramid]) {
          if(player._position.mapEvent.get.playerRepresentation.url.contains("Door")) {
            reset()
        }
        }

        // _view.changeScene()
      }
    case _ =>
      player.url_(stringUrl)
      view.playerImg_(player)

  }


  override def handleKey(keyCode : KeyCode): Unit = {
    keyCode.getName match {
      case "W" => if(checkAnimationEnd("top")) dashboard.move(Top, afterMovement) ;
      case "A" => if(checkAnimationEnd("left")) dashboard.move(Left, afterMovement) ;
      case "S" => if(checkAnimationEnd("bot")) dashboard.move(Bottom, afterMovement)
      case "D" => if(checkAnimationEnd("right")) dashboard.move(Right, afterMovement) ;
      case _ => {}
    }
  }

  override def getAllEnemies(): ListBuffer[PlayerRepresentation] = {
    val outList = new ListBuffer[PlayerRepresentation]
    for { el <- list; element = el.rectCell.mapEvent; if element.isDefined && element.get.callEvent.isInstanceOf[Enemy]} yield outList.append(element.get.playerRepresentation)
    outList
  }
  override def getAllStatues(): ListBuffer[PlayerRepresentation] = {
    val outList = new ListBuffer[PlayerRepresentation]
    for { el <- list; element = el.rectCell.mapEvent; if element.isDefined && element.get.callEvent.isInstanceOf[Statue]} yield outList.append(element.get.playerRepresentation)
    println("STATUE LIST: " + outList)
    outList
  }
  override def getPyramid(): PlayerRepresentation = {
    list.find(p=> p.rectCell.mapEvent.isDefined && p.rectCell.mapEvent.get.callEvent.isInstanceOf[Pyramid]).get.rectCell.mapEvent.get.playerRepresentation
  }

  override def handleSave(): Unit = {
    val output = new ObjectOutputStream(new FileOutputStream("./src/main/saves/save2.txt"))

    val outList = new ListBuffer[RectangleCell]
    for(el <-list)  outList.append(el.rectCell)
    output.writeObject(outList)
    output.writeObject(player)
    output.writeObject(gameC.user)
    output.writeObject(gameC.difficulty)
    output.writeObject(dashboard.traslationX)
    output.writeObject(dashboard.traslationY)
    output.close()
  }

  override def postInsert(): Unit = {
    view.updateParameters()
    if(getAllEnemies.size > 0) {

      for { el <- list; element = el.rectCell.mapEvent; if element.isDefined && element.get.callEvent.isInstanceOf[Pyramid]} yield{

        val pyramid: Pyramid = el.rectCell.mapEvent.get.callEvent.asInstanceOf[Pyramid]
        el.rectCell.mapEvent_(Option(MapEvent.createMapEvent(pyramid, new PlayerRepresentation(el.rectCell, "pyramid.png"))))

      }

    }
    view.setPaneChildren(list, Option.empty)
    selected = Option.empty
    view.setBPane()
  }



  import model.Monoid._

  override def handleMouseClicked(e:MouseEvent): Unit = {
    val cell = dashboard.searchPosition(e.x - dashboard.traslationX, e.y - dashboard.traslationY)
    println("CLICKED : " + cell)
    selected match {
      case Some(rc:RectangleCell) =>
        rc.x_(sum(e.x, dashboard.traslationX))
        rc.setY(sum(e.y, dashboard.traslationY))
        //rc.x_(e.x - dashboard.traslationX - e.x % 200)
        //rc.setY(e.y - dashboard.traslationY - e.y % 200)
        place(rc,cell,this)
      case Some(ec:EnemyCell) => place(ec,cell,this)
      case _ => //some default action
    }
  }
}

object MapController {
  def setup(gameC: GameController): ListBuffer[RectangleWithCell] = {
    val list = new ListBuffer[RectangleWithCell]()

    val tmp = Random.nextInt(6) + 4

    var excludedValues: Map[Int,ListBuffer[Int]] = Map()
    val tmplist = new ListBuffer[Int]()

    for(i<-0 until tmp) {
      val rect = RectangleCell.generateRandom(gameC,excludedValues, i)

      list.append(new RectangleWithCell(rect.getWidth, rect.getHeight, rect.x, rect.getY, rect) {
        fill = RectangleCell.createImage(rect.url, rect.rotation)
      })
      if(!excludedValues.contains(rect.x.toInt)) {
        val tmplist = new ListBuffer[Int]()
        tmplist.append(rect.getY.toInt)
        excludedValues += (rect.x.toInt -> tmplist)
      } else {
        excludedValues.get(rect.x.toInt).get.append(rect.getY.toInt)
      }
      //excludedValues += (rect.getX.toInt -> rect.getY.toInt)
      //println(excludedValues)
    }

    for(el <- list ) println(el)
    list
  }
}