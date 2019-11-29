package controller

import java.io.{File, FileOutputStream, ObjectOutputStream, PrintWriter}

import exception.{DoubleCellException, DoubleEnemyException, DoubleMovementException, MissingCellException}
import javafx.animation.Animation.Status
import model.{Bottom, Cell, EnemyCell, Left, Player, PlayerRepresentation, PlayerWithCell, RectangleCell, RectangleWithCell, Right, Top}
import exception._
import scalafx.scene.control.{Button, Separator, ToolBar}
import scalafx.scene.input.KeyCode
import scalafx.scene.paint.Color
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.{Scene, SnapshotParameters}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.layout._
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle

import scala.collection.mutable.ListBuffer
import scala.util.Random
import javafx.scene.input.MouseEvent
import javafx.scene.paint.ImagePattern
import view.map

import scala.collection.mutable.ListBuffer
import scala.util.Random

class MapController (_list:ListBuffer[RectangleWithCell], startingDefined : Option[RectangleCell]) {

  var gameController :  GameController = _
  def setGameController(gameC : GameController) {
    gameController = gameC
  }

  def this() {this(MapController.setup(),Option.empty)}

  var _selected:Option[Cell] = Option.empty;
  def selected = _selected
  def selected_(selected : Option[Cell]) = { _selected = selected}


  def list = _list
  def addToList(rect: RectangleWithCell): Unit = {
    _list.append(rect)
    dashboard.setCells(_list)
  }
  var startingCell:RectangleCell = _
  if(!startingDefined.isDefined) startingCell= list.apply(0).rectCell
  else  startingCell=startingDefined.get

  val _player = new PlayerWithCell(startingCell, "bot.png");
  _player.setFill()
  def player = _player

  val dashboard = new Dashboard(list, _player);

  var _view : map  =null
  def view_ (view : map) = {
    _view = view
    println("VIEW: " + _view)
    MovementAnimation.setAnimationNode(_view.bpane)
    _view.setMenu()
  }


  def checkAnimationEnd(url: String):Boolean = {
    if(MovementAnimation.checkAnimationEnd()) {
      player.player.url_(url + ".png")
      player.setFill()
      true
    }
    else throw new DoubleMovementException
  }

  def afterMovement(newRectangle: RectangleCell ,stringUrl : String, isEnded: Boolean) = {
    if(isEnded) {
      player.player.position_(newRectangle, stringUrl);
      player.setFill();
      println("--------------------------------")
      println(player.player._position)
      if(player.player._position.enemy.isDefined) {
        //_view.changeScene(gameController.user, player.player._position.enemy.get)
        _view.changeScene()
      }
    } else {
      player.player.url_(stringUrl)
      player.setFill();
    }

  }


  def handleKey(keyCode : KeyCode): Unit = {
    keyCode.getName match {
      case "Up" => if(checkAnimationEnd("top")) {
        dashboard.move(Top, afterMovement) ;

      }
      case "Left" => if(checkAnimationEnd("left")){
        dashboard.move(Left, afterMovement) ;
    }
      case "Down" => if(checkAnimationEnd("bot")) {
        dashboard.move(Bottom, afterMovement)

      }
      case "Right" => if(checkAnimationEnd("right")) {
        dashboard.move(Right, afterMovement) ;

      }
      case _ => {}
    }
  }

  def getAllEnemies(): ListBuffer[PlayerRepresentation] = {
    val outList = new ListBuffer[PlayerRepresentation]
    for (el <- list) yield {
      if(el.rectCell.enemy.isDefined) outList.append(el.rectCell.enemy.get)
    }
    outList
  }

  def handleSave(): Unit = {
    val output = new ObjectOutputStream(new FileOutputStream("./src/main/saves/save2.txt"))

    val outList = new ListBuffer[RectangleCell]
    for(el <-list) {
      outList.append(el.rectCell)

    }
    output.writeObject(outList)
    output.writeObject(player.player)
    output.close()
  }


  def createBottomCard(): ListBuffer[Button] = {
    val tmpList = ListBuffer[Button]()
    val btn = new Button {
      val re = new RectangleCell(true, true, true, true, elementX= 0.0, elementY=0.0)
      onAction = () => _selected = Option(re)
      defaultButton = true
      graphic = new ImageView(RectangleCell.createImage(re.url, re.rotation).getImage)
    }
    tmpList.append(btn)

    for(i<-0 until 4) {
      //
      val btn_tmp = new Button {
        var re: Cell = null
        if(math.random() <= 0.8) {
          re = RectangleCell.generateRandomCard
          val tmpRect = re.asInstanceOf[RectangleCell]
          graphic = new ImageView((RectangleCell.createImage(tmpRect.url, tmpRect.rotation)).getImage)
        } else {

          re = new EnemyCell(gameController.spawnEnemy(0))
          graphic = new ImageView(re.image)
        }
        onAction = () => _selected = Option(re)
        defaultButton = true

      }
      tmpList.append(btn_tmp)

    }

    tmpList
  }

def postInsert(): Unit = {
  _view.setPaneChildren(list, Option.empty)
  _selected = Option.empty
  _view.setBPane()
}

  import model.Placeable._;

  def handleMouseClicked(e:MouseEvent) = {
    if(_selected.isDefined) {
      val cell = dashboard.searchPosition(e.x - dashboard.traslationX, e.y - dashboard.traslationY)

      if(_selected.get.isInstanceOf[RectangleCell]) {
        val tmpRect = _selected.get.asInstanceOf[RectangleCell]
        tmpRect.setX(e.x - dashboard.traslationX - e.x % 200)
        tmpRect.setY(e.y - dashboard.traslationY - e.y % 200)
        place(tmpRect,cell,this)

      } else {
        val tmpRect = _selected.get.asInstanceOf[EnemyCell]
        place(tmpRect,cell,this)
      }


    }
    //println(dashboard.searchPosition(e.x - dashboard.traslationX, e.y - dashboard.traslationY))

  }


}

object MapController {
  def setup(): ListBuffer[RectangleWithCell] = {
    val list = new ListBuffer[RectangleWithCell]()

    val tmp = Random.nextInt(10) + 2;

    var excludedValues: Map[Int,ListBuffer[Int]] = Map()
    val tmplist = new ListBuffer[Int]();

    for(i<-0 until tmp) {
      val rect = RectangleCell.generateRandom(excludedValues)

      list.append(new RectangleWithCell(rect.getWidth, rect.getHeight, rect.getX, rect.getY, rect) {
        fill = (RectangleCell.createImage(rect.url, rect.rotation))
      })
      if(!excludedValues.contains(rect.getX.toInt)) {
        val tmplist = new ListBuffer[Int]();
        tmplist.append(rect.getY.toInt);
        excludedValues += (rect.getX.toInt -> tmplist)
      } else {
        excludedValues.get(rect.getX.toInt).get.append(rect.getY.toInt)
      }
      //excludedValues += (rect.getX.toInt -> rect.getY.toInt)
      //println(excludedValues)
    }

    for(el <- list ) {
      println(el)
    }
    list
  }
}