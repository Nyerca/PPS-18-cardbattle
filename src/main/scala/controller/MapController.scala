package controller

import java.io.{File, FileOutputStream, ObjectOutputStream, PrintWriter}

import javafx.animation.Animation.Status
import model.{Bottom, Cell, DoubleCellException, DoubleEnemyException, DoubleMovementException, EnemyCell, Left, MissingCellException, NoMovementException, Player, PlayerRepresentation, PlayerWithCell, RectangleCell, RectangleWithCell, Right, Top}
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
  var _selected:Option[Cell] = Option.empty;
  def selected = _selected
  def selected_(selected : Option[Cell]) = { _selected = selected}

  def getAllEnemies(): ListBuffer[PlayerRepresentation] = {
    val outList = new ListBuffer[PlayerRepresentation]
    for (el <- list) yield {
      if(el.rectCell.enemy.isDefined) outList.append(el.rectCell.enemy.get)
    }
    outList
  }

  def this()
  {
    this(MapController.setup(),Option.empty)
  }
  def list = _list
  var startingCell:RectangleCell = _
  if(!startingDefined.isDefined) startingCell= list.apply(0).rectCell
  else  startingCell=startingDefined.get
  var _view : map  =null
  def view_ (view : map) = {
    _view = view
    println("VIEW: " + _view)
    dashboard.setAnimationNode(_view.bpane)
    _view.setMenu()
  }

  val _player = new PlayerWithCell(startingCell, "bot.png");
  _player.setFill()
  def player = _player

  val dashboard = new Dashboard(list, _player);


  def checkAnimationEnd(url: String):Boolean = {
    if(dashboard.checkAnimationEnd()) {
      player.player.url_(url + ".png")
      player.setFill()
      true
    }
    else throw new DoubleMovementException
  }


  def handleKey(keyCode : KeyCode): Unit = {
    keyCode.getName match {
      case "Up" => if(checkAnimationEnd("top")) {
        dashboard.move(Top, () => {player.setFill();
        println("--------------------------------")
          println(player.player._position)
        if(player.player._position.enemy.isDefined) {
          _view.changeScene()
        }

      }) ;

      }
      case "Left" => if(checkAnimationEnd("left")){ dashboard.move(Left,() => {player.setFill();
        println("--------------------------------")
        println(player.player._position)
        if(player.player._position.enemy.isDefined) {
          _view.changeScene()
        }
      }) ;

    }
      case "Down" => if(checkAnimationEnd("bot")) { dashboard.move(Bottom,() => {player.setFill();
        println("--------------------------------")
        println(player.player._position)
        if(player.player._position.enemy.isDefined) {
          _view.changeScene()
        }

      }) ;

      }
      case "Right" => if(checkAnimationEnd("right")) { dashboard.move(Right,() => {player.setFill();
        println("--------------------------------")
        println(player.player._position)
        if(player.player._position.enemy.isDefined) {
          _view.changeScene()
        }
      });

      }
      case _ => {}
    }
  }

  def handleSave(): Unit = {
    val output = new ObjectOutputStream(new FileOutputStream("./src/main/saves/save2.txt"))

    val outList = new ListBuffer[RectangleCell]
    for(el <- list) {
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
          re = new EnemyCell()
          graphic = new ImageView(re.image)
        }
        onAction = () => _selected = Option(re)
        defaultButton = true

      }
      tmpList.append(btn_tmp)

    }

    tmpList
  }

  def handleMouseClicked(e:MouseEvent) = {
    if(_selected.isDefined) {
      if(_selected.get.isInstanceOf[RectangleCell]) {
        if(!dashboard.searchPosition(e.x - dashboard.traslationX, e.y - dashboard.traslationY).isDefined) {
          val tmpRect = _selected.get.asInstanceOf[RectangleCell]
          dashboard.showMap
          tmpRect.setX(e.x - dashboard.traslationX - e.x % 200)
          tmpRect.setY(e.y - dashboard.traslationY - e.y % 200)
          //println("SELECTED: " + _selected);


          _view.setPaneChildren(list, Option(tmpRect))


          dashboard.addCell(new RectangleWithCell(tmpRect.getWidth, tmpRect.getHeight, tmpRect.getX, tmpRect.getY,tmpRect) {
            fill = (RectangleCell.createImage(tmpRect.url, tmpRect.rotation))
          })
          dashboard.showMap
          _selected = Option.empty
          _view.setBPane()
        } else {
          throw new DoubleCellException
        }
      } else {
        if(dashboard.searchPosition(e.x - dashboard.traslationX, e.y - dashboard.traslationY).isDefined) {

          val rect = dashboard.searchPosition(e.x - dashboard.traslationX, e.y - dashboard.traslationY).get
          if(!rect.enemy.isDefined) {
            rect.enemy_(new PlayerRepresentation(dashboard.searchPosition(e.x - dashboard.traslationX, e.y - dashboard.traslationY).get, "vamp.png"))

            _view.setPaneChildren(list, Option.empty)
            _selected = Option.empty
            _view.setBPane()
          } else {
            throw new DoubleEnemyException
          }

        } else {
          throw new MissingCellException
        }
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