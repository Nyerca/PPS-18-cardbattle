package view

import controller.Dashboard
import javafx.scene.paint.ImagePattern
import model.{Bottom, Enemy, Left, Player, RectangleCell, Right, Top}
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.control.{Button, ToolBar}
import scalafx.scene.input.{KeyCode, KeyEvent, MouseEvent}
import scalafx.scene.{Group, Scene}
import scalafx.scene.layout._
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Rectangle}
import scalafx.Includes._
import scalafx.animation.{Interpolator, TranslateTransition}
import scalafx.scene.image.{Image, ImageView}
import scalafx.util.Duration

import scala.collection.mutable.ListBuffer
import scala.util.Random

object test extends JFXApp {
  var selected:Option[RectangleCell] = Option.empty;
  val enemies = new ListBuffer[Enemy];

  def generateRandom(excludedValues : Map[Int,ListBuffer[Int]]) : RectangleCell = {
    var rngX = 800
    var rngY = 400
    while(excludedValues.contains(rngX) && excludedValues.get(rngX).get.contains(rngY)) {
      rngX = Random.nextInt(8) * 200;
      rngY = Random.nextInt(4) * 200
    }
    var top:Boolean = false
    var right:Boolean = false
    var bottom:Boolean = false
    var left:Boolean = false
    while(top == false && right == false && bottom == false && left == false) {
      top = math.random()>0.5
      right = math.random()>0.5
      bottom = math.random()>0.5
      left = math.random()>0.5
    }
    println("Rectangle ("+rngX + ", " +rngY+") T: " + top + " R: " + right + " B: " + bottom + " L: " + left)
    val rectcell=  new RectangleCell(top, right, bottom, left, elementX= rngX, elementY=rngY, paint=Color.Grey)
    var probEnemy = 0.1
    if(excludedValues.size == 1) probEnemy = 1
    if( math.random()<=probEnemy) enemies.append(new Enemy(rectcell))
    rectcell
  }
  def generateRandomCard() : RectangleCell = {
    var top:Boolean = false
    var right:Boolean = false
    var bottom:Boolean = false
    var left:Boolean = false
    while(top == false && right == false && bottom == false && left == false) {
      top = math.random()>0.5
      right = math.random()>0.5
      bottom = math.random()>0.5
      left = math.random()>0.5
    }
    new RectangleCell(top, right, bottom, left, elementX= 0, elementY=0, paint=Color.Grey)
  }

  var startingCell:RectangleCell = null

  def setup(): ListBuffer[RectangleCell] = {
    val list = new ListBuffer[RectangleCell]()
    startingCell = new RectangleCell(math.random()>0.5, math.random()>0.5, math.random()>0.5, math.random()>0.5, elementX= 800.0, elementY=400.0, paint=Color.Grey)
    list.append(startingCell)
    val tmp = Random.nextInt(10) + 1;

    var excludedValues: Map[Int,ListBuffer[Int]] = Map()
    val tmplist = new ListBuffer[Int]();
    tmplist.append(startingCell.getY.toInt)
    excludedValues += (startingCell.getX.toInt ->  tmplist)

    if(excludedValues.contains(800) && excludedValues.get(800).get.contains(400)) {
      println("into")
    }
    if(excludedValues.contains(800)) {
      println("into2")
    }
    println("START")
    for(i<-0 until tmp) {
      val rect = generateRandom(excludedValues)

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

  val list = setup()

  import javafx.scene.input.MouseEvent


  def keyPressed (keyCode: KeyCode, dashboard : Dashboard): Unit = {
    keyCode.getName match {
      case "Up" => dashboard.move(Top);
      case "Left" => dashboard.move(Left);
      case "Down" => dashboard.move(Bottom);
      case "Right" => dashboard.move(Right);
      case _ => {}
    }
  }

  val pane = new Pane {
    children = list
    for(el <- enemies ) {
      children.append(el.icon)
    }
  }

  def createBottomCard(): ListBuffer[Button] = {
    val tmpList = ListBuffer[Button]()
    val btn = new Button {
      val re = new RectangleCell(true, true, true, true, elementX= 0.0, elementY=0.0, paint=Color.Grey)
      onAction = () => selected = Option(re)
      defaultButton = true
      graphic = new ImageView(re.image)
    }
    tmpList.append(btn)

    for(i<-0 until 4) {
      val btn_tmp = new Button {
        var re =generateRandomCard
        onAction = () => selected = Option(re)
        defaultButton = true
        graphic = new ImageView(re.image)
      }
      tmpList.append(btn_tmp)
    }
    tmpList
  }

  stage = new PrimaryStage {
    title = "Cardbattle"
    scene = new Scene(1200, 800) {



      fill = (new ImagePattern(new Image( "noroad.png"), 0, 0, 200, 200, false));
      val bpane = new BorderPane {
        center = pane
        bottom = new HBox() {

          layoutX = 10
          layoutY = 580
          id = "pane"
          children = List()



          val addList = createBottomCard
          children = addList
        }
      }
      val player = new Player(startingCell);
      val dashboard = new Dashboard(list, player, bpane);
      content = bpane
      content.add(player.icon);
      //content.add(hbox);


      onKeyPressed = (ke : KeyEvent) => {
        keyPressed(ke.code, dashboard);
      }


      onMouseClicked = (e: MouseEvent) => {

        if(selected.isDefined) {
          val tmpRect = selected.get;
          dashboard.showMap
          tmpRect.x_=(e.x - dashboard.traslationX - e.x % 200)
          tmpRect.y_=(e.y - dashboard.traslationY- e.y % 200)
          tmpRect.setX(e.x - dashboard.traslationX - e.x % 200)
          tmpRect.setY(e.y - dashboard.traslationY- e.y % 200)
          println("SELECTED: " + selected);


          val listTmp = new ListBuffer[Rectangle]()
          for(el <- list) yield { listTmp.append(el); }
          listTmp.append(tmpRect)
          for(el <- enemies ) {
            listTmp.append(el.icon)
          }
          pane.children =(listTmp)


          println("------")
          dashboard.addCell(tmpRect)
          dashboard.showMap
          selected = Option.empty
          bpane.bottom = new HBox() {

            layoutX = 10
            layoutY = 580
            id = "pane"
            children = List()



            var addList = createBottomCard
            children = addList
          }
        }
        println(dashboard.searchPosition(e.x - dashboard.traslationX, e.y - dashboard.traslationY))
      }

    }
  }
  stage.resizable = false
  stage.fullScreen = true




}