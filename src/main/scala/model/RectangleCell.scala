package model

import Utility.UrlFactory
import controller.GameController
import exception.{IllegalSizeException, NoMovementException}
import javafx.scene.paint.ImagePattern
import scalafx.Includes._
import scalafx.scene.SnapshotParameters
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle

import scala.collection.mutable.ListBuffer
import scala.util.Random

trait RectangleCell extends Serializable with Cell {
  def top: Boolean
  def right: Boolean
  def bottom: Boolean
  def left: Boolean
  def elementWidth: Double
  def elementHeight: Double

  def x: Double
  def x_(newX: Double): Unit

  def y: Double
  def y_(newY: Double): Unit

  def setDamage():Unit
  def canEqual(other: Any): Boolean
  def url: String
  def rotation: Int
  def getWidth: Double
  def getHeight: Double

  def isMoveAllowed(movement : Move): Boolean
  def mapEvent:Option[MapEvent]
  def mapEvent_(cellEve: Option[MapEvent]): Unit
}


class RectangleCellImpl (override val top: Boolean, override val right: Boolean, override val bottom: Boolean, override val left: Boolean, override val elementWidth: Double = 200, override val elementHeight: Double = 200, var _x: Double, var _y:Double) extends RectangleCell  {
  var _mapEvent:Option[MapEvent] = Option.empty
  override def mapEvent:Option[MapEvent] = _mapEvent
  override def mapEvent_(cellEve: Option[MapEvent]): Unit = _mapEvent = cellEve

  override def x: Double = _x
  override def y: Double = _y
  override def x_(newX: Double): Unit = _x = newX
  override def y_(newY: Double): Unit = _y = newY
  override def getWidth: Double = elementWidth
  override def getHeight: Double = elementHeight

  override def canEqual(other: Any): Boolean = other.isInstanceOf[RectangleCell]

  override def equals(other:Any) :Boolean = other match {
    case that : RectangleCell => that.canEqual(this) && this.x == that.x && this.y == that.y
    case _  => false
  }

  override def hashCode(): Int = {
    val state = Seq(_x,_y,elementWidth,elementHeight)
    state.map(_.hashCode()).foldLeft(0)((a,b)=>31 * a + b)
  }

  override def toString :String = {
    "Rectangle ("+_x + ", " +_y+") T: " + top + " R: " + right + " B: " + bottom + " L: " + left + " enemy: " + _mapEvent
  }

  override def image: Image = {
    val iv = new ImageView(new Image( url))
    iv.setRotate(rotation)
    var params = new SnapshotParameters()
    params.setFill(Color.Transparent)
    iv.snapshot(params, null)
  }

  val parameters:(String,Int) = UrlFactory.getParameters(top,right,bottom,left)
  var _url:String = parameters._1
  var _rotation:Int = parameters._2
  override def url: String = _url
  override def rotation: Int = _rotation

  override def setDamage(): Unit = _url = _url.substring(0, _url.length - 4) + "Dmg.png"

  def isMoveAllowed(movement : Move): Boolean =movement match {
    case Top => top
    case Right => right
    case Bottom => bottom
    case Left  => left
    case _  => false
  }

  if(!top && !right && !bottom && !left) throw new NoMovementException()
  if(elementWidth == 0 || elementHeight == 0)  throw new IllegalSizeException()
}

object RectangleCell {
  def generateRandomCard() : RectangleCellImpl = {
    var top:Boolean = false
    var right:Boolean = false
    var bottom:Boolean = false
    var left:Boolean = false
    while(!top && !right && !bottom && !left) {
      top = math.random()>0.5
      right = math.random()>0.5
      bottom = math.random()>0.5
      left = math.random()>0.5
    }
    val re = new RectangleCellImpl(top, right, bottom, left, _x= 0, _y=0)
    if(math.random() <= 0.4) re.setDamage()
    re
  }

  def createImage(url: String, rotation: Double): ImagePattern = {
    val iv = new ImageView(new Image( url))
    iv.setRotate(rotation)
    var params = new SnapshotParameters()
    params.setFill(Color.Transparent)
    val image = iv.snapshot(params, null)
    new ImagePattern(image)
  }

  def createRectangle(rectangleCell: RectangleCell): Rectangle = {
    val rect:Rectangle = new Rectangle()
    rect.fill_=(RectangleCell.createImage(rectangleCell.url, rectangleCell.rotation))
    rect.width_=(rectangleCell.getWidth)
    rect.height_=(rectangleCell.getHeight)
    rect.x_=(rectangleCell.x)
    rect.y_=(rectangleCell.y)
    rect
  }
}
