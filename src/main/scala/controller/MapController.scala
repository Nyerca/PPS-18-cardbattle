package controller


import java.io.{File, FileOutputStream, ObjectOutputStream, PrintWriter}

import model.{Bottom, Cell, Enemy, EnemyCell, Left, Player, RectangleCell, Right, Top}
import scalafx.scene.control.Button
import scalafx.scene.image.ImageView
import scalafx.scene.input.KeyCode
import scalafx.scene.paint.Color
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.layout._
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle

import scala.collection.mutable.ListBuffer
import scala.util.Random
import javafx.scene.input.MouseEvent
import view.map

import scala.collection.mutable.ListBuffer
import scala.util.Random

class MapController {
  var _selected:Option[Cell] = Option.empty;

  def selected = _selected
  def selected_(selected : Option[Cell]) = { _selected = selected}

  def getAllEnemies(): ListBuffer[Enemy] = {
    val outList = new ListBuffer[Enemy]
    for (el <- list) yield {
      if(el.enemy.isDefined) outList.append(el.enemy.get)
    }
    outList
  }

  val list = setup()
  var startingCell:RectangleCell = list.apply(0)

  var _view : map  =null
  def view_ (view : map) = {
    _view = view
    println("VIEW: " + _view)
    dashboard.setAnimationNode(_view.bpane)
  }

  val _player = new Player(startingCell);
  def player = _player

  val dashboard = new Dashboard(list, _player);

  def setup(): ListBuffer[RectangleCell] = {
    val list = new ListBuffer[RectangleCell]()

    val tmp = Random.nextInt(10) + 2;

    var excludedValues: Map[Int,ListBuffer[Int]] = Map()
    val tmplist = new ListBuffer[Int]();

    for(i<-0 until tmp) {
      val rect = RectangleCell.generateRandom(excludedValues)

      list.append(rect)
      if(!excludedValues.contains(rect.getX.toInt)) {
        val tmplist = new ListBuffer[Int]();
        tmplist.append(rect.getY.toInt);
        excludedValues += (rect.getX.toInt -> tmplist)
      } else {
        excludedValues.get(rect.getX.toInt).get.append(rect.getY.toInt)
      }
      //excludedValues += (rect.getX.toInt -> rect.getY.toInt)
      println(excludedValues)
    }

    for(el <- list ) {
      println(el)
    }
    list
  }


  def handleKey(keyCode : KeyCode): Unit = {
    keyCode.getName match {
      case "Up" => dashboard.move(Top);
      case "Left" => dashboard.move(Left);
      case "Down" => dashboard.move(Bottom);
      case "Right" => dashboard.move(Right);
      case _ => {}
    }
  }


  def createBottomCard(): ListBuffer[Button] = {
    val tmpList = ListBuffer[Button]()
    val btn = new Button {
      val re = new RectangleCell(true, true, true, true, elementX= 0.0, elementY=0.0, paint=Color.Grey)
      onAction = () => _selected = Option(re)
      defaultButton = true
      graphic = new ImageView(re.image)
    }
    tmpList.append(btn)

    for(i<-0 until 4) {
      //
      val btn_tmp = new Button {
        var re: Cell = null
        if(math.random() <= 0.8) {
          re = RectangleCell.generateRandomCard
        } else {
          re = new EnemyCell()
        }
        onAction = () => _selected = Option(re)
        defaultButton = true
        graphic = new ImageView(re.image)
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
          tmpRect.x_=(e.x - dashboard.traslationX - e.x % 200)
          tmpRect.y_=(e.y - dashboard.traslationY - e.y % 200)
          tmpRect.setX(e.x - dashboard.traslationX - e.x % 200)
          tmpRect.setY(e.y - dashboard.traslationY - e.y % 200)
          println("SELECTED: " + _selected);


          val listTmp = new ListBuffer[Rectangle]()
          for (el <- list) yield {
            listTmp.append(el);
            if(el.enemy.isDefined) listTmp.append(el.enemy.get.icon)
          }
          listTmp.append(tmpRect)

          _view.setPaneChildren(listTmp)


          println("------")
          dashboard.addCell(tmpRect)
          dashboard.showMap
          _selected = Option.empty
          _view.setBPane()
        }
      } else {
        if(dashboard.searchPosition(e.x - dashboard.traslationX, e.y - dashboard.traslationY).isDefined) {
          val tmpRect = _selected.get.asInstanceOf[EnemyCell]
          dashboard.searchPosition(e.x - dashboard.traslationX, e.y - dashboard.traslationY).get.enemy_(new Enemy(dashboard.searchPosition(e.x - dashboard.traslationX, e.y - dashboard.traslationY).get))
          val listTmp = new ListBuffer[Rectangle]()
          for (el <- list) yield {
            listTmp.append(el);
            if(el.enemy.isDefined) listTmp.append(el.enemy.get.icon)
          }


          _view.setPaneChildren(listTmp)
          _selected = Option.empty
          _view.setBPane()
        }
      }
    }
    println(dashboard.searchPosition(e.x - dashboard.traslationX, e.y - dashboard.traslationY))

  }

  @SerialVersionUID(123L)
  class Stock(var symbol: String, var price: BigDecimal)
    extends java.io.Serializable {
    override def toString = f"$symbol%s is ${price.toDouble}%.2f"
  }

  @SerialVersionUID(1000L)
  class Foo extends Serializable {
    // class code here
  }
  def save(): Unit = {
    val rectangleCell = RectangleCell.generateRandomCard()

    val output = new ObjectOutputStream(new FileOutputStream("./src/main/saves/save2.txt"))
    output.writeObject(new Stock("a",3))
    output.close()


    val file_Object = new File("./src/main/saves/save.txt" )

    // Passing reference of file to the printwriter
    val print_Writer = new PrintWriter(file_Object)
    print_Writer.print(rectangleCell)
    // Closing printwriter
    print_Writer.close()
  }

}
