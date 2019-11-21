package controller

import javafx.animation.Animation.Status
import javafx.scene.paint.ImagePattern
import model.{MissingCellException, Move, NoMovementException, PlayerWithCell, RectangleCell, RectangleWithCell, Top, Right, Left, Bottom}
import scalafx.Includes._
import scalafx.animation.{Interpolator, TranslateTransition}
import scalafx.scene.image.Image
import scalafx.scene.layout.BorderPane
import scalafx.util.Duration

import scala.collection.mutable.ListBuffer

class Dashboard (var cells: ListBuffer[RectangleWithCell], player: PlayerWithCell) extends Serializable {

  def addCell(cell : RectangleWithCell): Unit = {
    cells.append(cell)
  }

  def addPane(pane:BorderPane): Unit = {
    anim.node = pane.center.apply()
  }

  var anim = new TranslateTransition {
    duration = Duration(200.0)
    interpolator = Interpolator.Linear
    // autoReverse = true
    // cycleCount = Timeline.Indefinite
  }

  def setAnimationNode (bpane : BorderPane): Unit = {
    anim.node = bpane.center.apply()

  }

  def showMap(): Unit = {
    for (rectangle <- cells) yield {
      println("RECTANGLE: (" + rectangle.getX + ", " + rectangle.getY+ ") w: "+ rectangle.getWidth + " h: " + rectangle.getHeight)
    }
  }

  def searchPosition(newX : Double, newY : Double): Option[RectangleCell] = {
    //for(rectangle <- cells if(rectangle.getX == newX)) yield rectangle
    (for (rectangle <- cells if rectangle.getX <= newX && rectangle.getY <= newY && rectangle.getX + rectangle.getWidth > newX && rectangle.getY + rectangle.getHeight > newY) yield rectangle.rectCell).headOption
  }

  def searchPosition(newX : Double, newY : Double, movement: Move): Option[RectangleCell] = {
    //for(rectangle <- cells if(rectangle.getX == newX)) yield rectangle
    println("Searching: (" + newX + ", " + newY + ")")
    for (rectangle <- cells if  rectangle.getX <= newX && rectangle.getY <= newY && rectangle.getX + rectangle.getWidth > newX && rectangle.getY + rectangle.getHeight > newY) yield {println("RE: " + rectangle); if(rectangle.rectCell.isMoveAllowed(movement)) println("ALSO MOVE")}
    (for (rectangle <- cells if  rectangle.getX <= newX && rectangle.getY <= newY && rectangle.getX + rectangle.getWidth > newX && rectangle.getY + rectangle.getHeight > newY && rectangle.rectCell.isMoveAllowed(movement)) yield rectangle.rectCell).headOption
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

  var list = new ListBuffer[String]
  list.append("2.png")
  list.append(".png")
  list.append("1.png")
  list.append(".png")

  def setAnim(newRectangle:RectangleCell, incrementX : Double, incrementY: Double,stringUrl : String, fun:() => Unit): Unit = {
    player.player.position_(newRectangle, stringUrl);
    fun()
    anim.fromX = anim.toX.toDouble
    anim.fromY = anim.toY.toDouble
    anim.toX = anim.fromX.toDouble + incrementX;
    anim.toY = anim.fromY.toDouble + incrementY;
  }


  def setAnimationIncrement(newRectangle:RectangleCell, incrementX : Double, incrementY: Double, stringUrl : String, fun:() => Unit): Unit = {
    anim.toX = anim.fromX.toDouble + incrementX;
    anim.toY = anim.fromY.toDouble + incrementY;
    _traslationX += incrementX * 5;
    _traslationY += incrementY * 5
    anim.setOnFinished(e =>  {
      setAnim(newRectangle,incrementX,incrementY,stringUrl + "2.png",fun)
      anim.setOnFinished(e => {
        setAnim(newRectangle,incrementX,incrementY,stringUrl + ".png",fun)
        anim.setOnFinished(e => {
          setAnim(newRectangle,incrementX,incrementY,stringUrl + "1.png",() => fun)
          anim.setOnFinished(e => {
            setAnim(newRectangle,incrementX,incrementY,stringUrl + ".png",() => fun)
            anim.setOnFinished(e => {printInfos })
            anim.play();})
          anim.play();})
        anim.play();})
      anim.play();
    })
  }

  def printInfos(): Unit = {
    println("Player position: (" + player.player.position.getX + ", " + player.player.position.getY + ")")
    println("Animation From: (" + anim.fromX.toDouble + ", " + anim.fromY.toDouble + ") to: (" + anim.toX.toDouble + ", " + anim.toY.toDouble + ")")
    println("Translation: (" + _traslationX + ", " +_traslationY + ")")
  }


  def checkAnimationEnd():Boolean = {
    if(anim.status.getValue == Status.STOPPED) true
    else false
  }

  def move(url : String, movement:Move, incX : Double, incY : Double, fun:() => Unit): Unit = {
    setAnimation(traslationX, traslationX + incX.toInt * (-5), traslationY, traslationY + incY.toInt * (-5))
    //printInfos
    val newRectangle = this.searchPosition(player.player.position.getX + incX.toInt * (-5), player.player.position.getY + incY.toInt * (-5), movement.opposite)

    if(player.player.position.isMoveAllowed(movement)) {
      if(newRectangle.isDefined) {
        setAnimationIncrement(newRectangle.get, incX, incY, url, fun)
        anim.play();
      } else {
        throw new MissingCellException
      }
    } else {
      throw new NoMovementException
    }
  }


  def move(movement : Move, fun:() => Unit): Unit = movement match {
    case Top  => {
      move("top", movement, 0,+40, fun)
    }
    case Right => {
      move("right",movement, -40,0, fun)
    }
    case Bottom => {
      move("bot", movement, 0,-40, fun)
    }
    case Left => {
      move("left", movement, +40,0, fun)
    }
    case _  => {}
  }


}