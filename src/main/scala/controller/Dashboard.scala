package controller

import javafx.animation.Animation.Status
import model.{Move, Player, RectangleCell, Top, Right, Left, Bottom}
import scalafx.Includes._
import scalafx.animation.{Interpolator, TranslateTransition}
import scalafx.scene.layout.BorderPane
import scalafx.util.Duration

import scala.collection.mutable.ListBuffer

class Dashboard (var cells: ListBuffer[RectangleCell], player: Player, bpane:BorderPane) {

  def addCell(cell : RectangleCell): Unit = {
    cells.append(cell)
  }

  def addPane(pane:BorderPane): Unit = {
    anim.node = pane.center.apply()
  }

  var anim = new TranslateTransition {
    duration = Duration(200.0)
    node = bpane.center.apply()
    interpolator = Interpolator.Linear
    // autoReverse = true
    // cycleCount = Timeline.Indefinite
  }


  /*
    def searchPosition(newX : Double, newY : Double): Option[RectangleCell] = {
      //for(rectangle <- cells if(rectangle.getX == newX)) yield rectangle
      (for (rectangle <- cells if rectangle.getX == newX && rectangle.getY == newY) yield rectangle).headOption
    }
  */
  def showMap(): Unit = {
    for (rectangle <- cells) yield {
      println("RECTANGLE: (" + rectangle.getX + ", " + rectangle.getY+ ") w: "+ rectangle.getWidth + " h: " + rectangle.getHeight)
    }
  }

  def searchPosition(newX : Double, newY : Double): Option[RectangleCell] = {
    //for(rectangle <- cells if(rectangle.getX == newX)) yield rectangle
    (for (rectangle <- cells if rectangle.getX <= newX && rectangle.getY <= newY && rectangle.getX + rectangle.getWidth > newX && rectangle.getY + rectangle.getHeight > newY) yield rectangle).headOption
  }

  def searchPosition(newX : Double, newY : Double, movement: Move): Option[RectangleCell] = {
    //for(rectangle <- cells if(rectangle.getX == newX)) yield rectangle
    println("Searching: (" + newX + ", " + newY + ")")
    for (rectangle <- cells if  rectangle.getX <= newX && rectangle.getY <= newY && rectangle.getX + rectangle.getWidth > newX && rectangle.getY + rectangle.getHeight > newY) yield {println("RE: " + rectangle); if(rectangle.isMoveAllowed(movement)) println("ALSO MOVE")}
    (for (rectangle <- cells if  rectangle.getX <= newX && rectangle.getY <= newY && rectangle.getX + rectangle.getWidth > newX && rectangle.getY + rectangle.getHeight > newY && rectangle.isMoveAllowed(movement)) yield rectangle).headOption
  }

  private var _traslationX = 0.0
  private var _traslationY = 0.0
  def traslationX = _traslationX
  def traslationY = _traslationY


  def setAnimation(fromX: Double, toX: Double, fromY:Double, toY: Double)   = {
    anim.fromX = fromX;
    anim.fromY= fromY;
    anim.toX = toX
    anim.toY= toY
  }



  def setAnimationIncrement(newRectangle:RectangleCell, incrementX : Double, incrementY: Double, stringUrl : String): Unit = {
    anim.toX = anim.fromX.toDouble + incrementX;
    anim.toY = anim.fromY.toDouble + incrementY;
    _traslationX += incrementX * 5;
    _traslationY += incrementY * 5
    anim.setOnFinished(e =>  {
      player.position_(newRectangle, stringUrl + "2.png");
      anim.fromX = anim.toX.toDouble
      anim.fromY = anim.toY.toDouble
      anim.toX = anim.fromX.toDouble + incrementX;
      anim.toY = anim.fromY.toDouble + incrementY;
      anim.setOnFinished(e => {          player.position_(newRectangle, stringUrl + ".png");
        anim.fromX = anim.toX.toDouble
        anim.fromY = anim.toY.toDouble
        anim.toX = anim.fromX.toDouble + incrementX;
        anim.toY = anim.fromY.toDouble + incrementY;
        anim.setOnFinished(e => {          player.position_(newRectangle, stringUrl + "1.png");
          anim.fromX = anim.toX.toDouble
          anim.fromY = anim.toY.toDouble
          anim.toX = anim.fromX.toDouble + incrementX;
          anim.toY = anim.fromY.toDouble + incrementY;
          anim.setOnFinished(e => {  player.position_(newRectangle, stringUrl + ".png");
            anim.fromX = anim.toX.toDouble
            anim.fromY = anim.toY.toDouble
            anim.toX = anim.fromX.toDouble + incrementX;
            anim.toY = anim.fromY.toDouble + incrementY;
            anim.setOnFinished(e => {printInfos })
            anim.play();})
          anim.play();})
        anim.play();})
      anim.play();
    })
  }

  def printInfos(): Unit = {
    println("Player position: (" + player.position.getX + ", " + player.position.getY + ")")
    println("Animation From: (" + anim.fromX.toDouble + ", " + anim.fromY.toDouble + ") to: (" + anim.toX.toDouble + ", " + anim.toY.toDouble + ")")
    println("Translation: (" + _traslationX + ", " +_traslationY + ")")
  }

  def movementTo(incX: Integer, incY: Integer, movement: Move): Option[RectangleCell] = {
    setAnimation(traslationX, traslationX + incX, traslationY, traslationY + incY)
    //printInfos
    val newRectangle = this.searchPosition(player.position.getX + incX, player.position.getY + incY, movement.opposite)
    if(newRectangle.isDefined ) println("DEFINED")
    if(player.position.isMoveAllowed(movement) ) println("ALLOWED")
    if(anim.status.getValue == Status.STOPPED ) println("STOPPED")
    if (newRectangle.isDefined && player.position.isMoveAllowed(movement) && anim.status.getValue == Status.STOPPED) {
      newRectangle
    } else {
      Option.empty
    }

  }

  def checkAnimationEnd():Boolean = {
    if(anim.status.getValue == Status.STOPPED) true
    else false
  }


  def move(movement : Move): Unit = movement match {
    case Top  => {
      if(checkAnimationEnd) {
        player.image_("top.png")
        val newRectangle = movementTo(0, -200, movement)
        if (newRectangle.isDefined) {

          //println("From: (" + player.position.getX + ", " + player.position.getY + ") to: (" + anim.toX.toDouble + ", " + anim.toY.toDouble + ")")
          setAnimationIncrement(newRectangle.get, 0, +40, "top")
          anim.play();
        }
      }
    }
    case Right => {
      if(checkAnimationEnd) {
        player.image_("right.png")
        val newRectangle = movementTo(200,0,movement)
        if(newRectangle.isDefined ) {
          setAnimationIncrement(newRectangle.get, -40, 0, "right")
          anim.play();
        }
      }
    }
    case Bottom => {
      if(checkAnimationEnd) {
        player.image_("bot.png")
        val newRectangle = movementTo(0,200,movement)
        if(newRectangle.isDefined && player.position.isMoveAllowed(movement)) {
          setAnimationIncrement(newRectangle.get, 0, -40, "bot")
          anim.play();
        }
      }
    }
    case Left => {
      if(checkAnimationEnd) {
        player.image_("left.png")
        val newRectangle = movementTo(-200,0,movement)
        if(newRectangle.isDefined && player.position.isMoveAllowed(movement)) {
          setAnimationIncrement(newRectangle.get, +40, 0, "left")
          anim.play();
        }
      }
    }
    case _  => {}
  }
}