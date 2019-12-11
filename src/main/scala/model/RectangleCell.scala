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
  def y: Double
  def x_(newX: Double):Unit
  def y_(newY: Double):Unit

  def setDamage():Unit
  def canEqual(other: Any): Boolean
  def url: String
  def rotation: Int
  def isMoveAllowed(movement : Move): Boolean
  def mapEvent:Option[MapEvent]
  def mapEvent_(cellEve: Option[MapEvent]): Unit
  def isRectangle(posX: Double, posY: Double): Boolean
}


class RectangleCellImpl (override val top: Boolean, override val right: Boolean, override val bottom: Boolean, override val left: Boolean, override val elementWidth: Double = 200, override val elementHeight: Double = 200, var x: Double, var y:Double) extends RectangleCell  {
  var _mapEvent:Option[MapEvent] = Option.empty
  override def mapEvent:Option[MapEvent] = _mapEvent
  override def mapEvent_(cellEve: Option[MapEvent]): Unit = _mapEvent = cellEve

  override def x_(newX: Double):Unit = x=newX
  override def y_(newY: Double):Unit = y=newY

  override def isRectangle(posX: Double, posY: Double): Boolean = {
    if(this.x <= posX && this.y <= posY && this.x + this.elementWidth > posX && this.y + this.elementHeight > posY) return true
    false
  }


  override def canEqual(other: Any): Boolean = other.isInstanceOf[RectangleCell]

  override def equals(other:Any) :Boolean = other match {
    case that : RectangleCell => that.canEqual(this) && this.x == that.x && this.y == that.y
    case _  => false
  }

  override def hashCode(): Int = {
    val state = Seq(x,y,elementWidth,elementHeight)
    state.map(_.hashCode()).foldLeft(0)((a,b)=>31 * a + b)
  }

  override def toString :String = "Rectangle ("+x + ", " +y+") T: " + top + " R: " + right + " B: " + bottom + " L: " + left + " enemy: " + _mapEvent

  override def image: Image = {
    val iv = new ImageView(new Image( url))
    iv.setRotate(rotation)
    var params = new SnapshotParameters()
    params.setFill(Color.Transparent)
    iv.snapshot(params, null)
  }

  val parameters:(String,Int) = UrlFactory.getParameters(top,right,bottom,left)
  var url:String = parameters._1
  var rotation:Int = parameters._2

  override def setDamage(): Unit = url = url.substring(0, url.length - 4) + "Dmg.png"

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
  implicit def RectangleCell2Rectangle(rect : RectangleCell): Rectangle =
    new Rectangle() {
      width = rect.elementWidth
      height = rect.elementHeight
      x = rect.x
      y = rect.y
      fill = Cell.createImage(rect.url, rect.rotation)
    }

  implicit def RectangleCell2ListRectangle(list : ListBuffer[RectangleCell]): ListBuffer[Rectangle] = {
    list.map(rect => new Rectangle() {
      width = rect.elementWidth
      height = rect.elementHeight
      x = rect.x
      y = rect.y
      fill = Cell.createImage(rect.url, rect.rotation)
    })
  }

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
    val re = new RectangleCellImpl(top, right, bottom, left, x= 0, y=0)
    if(math.random() <= 0.4) re.setDamage()
    re
  }


  def createRectangle(rectangleCell: RectangleCell): Rectangle = {
    val rect:Rectangle = new Rectangle()
    rect.fill_=(Cell.createImage(rectangleCell.url, rectangleCell.rotation))
    rect.width_=(rectangleCell.elementWidth)
    rect.height_=(rectangleCell.elementHeight)
    rect.x_=(rectangleCell.x)
    rect.y_=(rectangleCell.y)
    rect
  }
}
