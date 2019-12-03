package controller

import java.io.{FileOutputStream, ObjectOutputStream}
import exception.DoubleMovementException
import javafx.scene.input.MouseEvent
import model.{Bottom, Cell, EnemyCell, Left, PlayerRepresentation, PlayerWithCell, RectangleCell, RectangleCellImpl, RectangleWithCell, Right, Top}
import scalafx.Includes._
import scalafx.scene.control.Button
import scalafx.scene.image.ImageView
import scalafx.scene.input.KeyCode
import view.scenes.MapScene
import scala.collection.mutable.ListBuffer
import scala.util.Random
import model.Placeable._

trait MapController {
  def gameC: GameController
  def selected_(element: Option[Cell]):Unit
  def _list: ListBuffer[RectangleWithCell]
  def startingDefined: Option[RectangleCell]
  def view_ (newView : MapScene): Unit
  def player: PlayerWithCell

  def postInsert(): Unit

  def list:ListBuffer[RectangleWithCell]
  def addToList(rect: RectangleWithCell): Unit

  def getAllEnemies(): ListBuffer[PlayerRepresentation]
  def handleSave(): Unit
  def handleKey(keyCode : KeyCode): Unit
  def handleMouseClicked(e:MouseEvent): Unit
}


class MapControllerImpl (override val gameC : GameController, var _list:ListBuffer[RectangleWithCell], var startingDefined : Option[RectangleCell]) extends MapController {

  def this(gameC : GameController) {this(gameC,MapController.setup(gameC),Option.empty)}

  var selected:Option[Cell] = Option.empty
  override def selected_(element: Option[Cell]):Unit = selected = element

  override def list:ListBuffer[RectangleWithCell] = _list
  override def addToList(rect: RectangleWithCell): Unit = {
    _list.append(rect)
    dashboard.setCells(_list)
  }

  var _player : PlayerWithCell = _
  startingDefined match {
    case Some(rect: RectangleCell) => _player = new PlayerWithCell(rect, "bot.png")
    case _ => _player = new PlayerWithCell(list.head.rectCell, "bot.png")
  }
  _player.setFill()
  override def player: PlayerWithCell = _player


  val dashboard = new DashboardImpl(list, _player)

  var view: MapScene = _
  override def view_ (newView : MapScene): Unit = {
    view = newView
    println("VIEW: " + view)
    MovementAnimation.setAnimationNode(view.bpane)
    view.setMenu()
  }


  def checkAnimationEnd(url: String):Boolean = {
    if(MovementAnimation.checkAnimationEnd()) {
      player.player.url_(url + ".png")
      player.setFill()
      true
    }
    else throw new DoubleMovementException
  }

  def afterMovement(newRectangle: RectangleCell ,stringUrl : String, isEnded: Boolean): Unit = isEnded match {
    case true =>
      player.player.position_(newRectangle, stringUrl)
      player.setFill()
      println("--------------------------------")
      println(player.player._position)
      if(player.player._position.enemy._2.isDefined) {
        view.changeScene(gameC.user, player.player._position.enemy._1.get)
       // _view.changeScene()
      }
    case _ =>
      player.player.url_(stringUrl)
      player.setFill()

  }


  override def handleKey(keyCode : KeyCode): Unit = {
    keyCode.getName match {
      case "Up" => if(checkAnimationEnd("top")) dashboard.move(Top, afterMovement) ;
      case "Left" => if(checkAnimationEnd("left")) dashboard.move(Left, afterMovement) ;
      case "Down" => if(checkAnimationEnd("bot")) dashboard.move(Bottom, afterMovement)
      case "Right" => if(checkAnimationEnd("right")) dashboard.move(Right, afterMovement) ;
      case _ => {}
    }
  }

  override def getAllEnemies(): ListBuffer[PlayerRepresentation] = {
    val outList = new ListBuffer[PlayerRepresentation]
    for { el <- list; element = el.rectCell.enemy._2; if element.isDefined} yield outList.append(element.get)
    outList
  }

  override def handleSave(): Unit = {
    val output = new ObjectOutputStream(new FileOutputStream("./src/main/saves/save2.txt"))

    val outList = new ListBuffer[RectangleCell]
    for(el <-list)  outList.append(el.rectCell)
    output.writeObject(outList)
    output.writeObject(player.player)
    output.close()
  }

/*
  override def createBottomCard(): ListBuffer[Button] = {
    val tmpList = ListBuffer[Button]()
    val btn: Button = new Button {
      val re = new RectangleCellImpl(true, true, true, true, _x= 0.0, elementY=0.0)
      onAction = () => selected = Option(re)
      defaultButton = true
      graphic = new ImageView(RectangleCell.createImage(re.url, re.rotation).getImage)
    }
    tmpList.append(btn)

    for(i<-0 until 4) {
      val btn_tmp: Button = new Button {
        var re: Cell = _
        if(math.random() <= 0.8) {
          re = RectangleCell.generateRandomCard()
          graphic = new ImageView(re.image)
        } else {
          re = new EnemyCell(gameC.spawnEnemy(Random.nextInt(4)))
          graphic = new ImageView(re.image)
        }
        onAction = () => selected = Option(re)
        defaultButton = true
      }
      tmpList.append(btn_tmp)
    }
    tmpList
  }
  */


  override def postInsert(): Unit = {
    view.setPaneChildren(list, Option.empty)
    selected = Option.empty
    view.setBPane()
  }


  import model.Monoid._

  override def handleMouseClicked(e:MouseEvent): Unit = {
    val cell = dashboard.searchPosition(e.x - dashboard.traslationX, e.y - dashboard.traslationY)
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

    val tmp = Random.nextInt(10) + 2

    var excludedValues: Map[Int,ListBuffer[Int]] = Map()
    val tmplist = new ListBuffer[Int]()

    for(_<-0 until tmp) {
      val rect = RectangleCell.generateRandom(gameC,excludedValues)

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