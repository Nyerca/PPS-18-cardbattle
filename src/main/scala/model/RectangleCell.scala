package model

import Utility.UrlFactory
import exception.{IllegalSizeException, NoMovementException}
import javafx.scene.paint.ImagePattern
import model.MapPosition.STARTING_Y
import scalafx.Includes._
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.shape.Rectangle

import scala.collection.mutable.ListBuffer

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
  def damage: Boolean

  def canEqual(other: Any): Boolean
  def url: String
  def rotation: Int
  def isMoveAllowed(movement : Move): Boolean
  def mapEvent:Option[MapEvent]
  def mapEvent_(cellEve: Option[MapEvent]): Unit
  def isRectangle(posX: Double, posY: Double): Boolean
}

object RectangleCell {
  val DAMAGE_PROBABILITY = 0.3

  implicit def RectangleCell2Rectangle(rect : RectangleCell): Rectangle =
    new Rectangle() {
      width = rect.elementWidth
      height = rect.elementHeight
      x = rect.x
      y = rect.y
      fill = new ImagePattern(Cell.createImage(rect.url, rect.rotation))
    }

  implicit def RectangleCell2ListRectangle(list : ListBuffer[RectangleCell]): ListBuffer[Rectangle] = {
    list.map(rect => new Rectangle() {
      width = rect.elementWidth
      height = rect.elementHeight
      x = rect.x
      y = rect.y
      fill = new ImagePattern(Cell.createImage(rect.url, rect.rotation))
    })
  }

  def generateRandomCard() : RectangleCell = {
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
    new RectangleCellImpl(top, right, bottom, left, x= 0, y=0, damage = math.random <= DAMAGE_PROBABILITY)
  }


  def createRectangle(rectangleCell: RectangleCell): Rectangle = {
    val rect:Rectangle = new Rectangle()
    rect.fill_= (new ImagePattern(Cell.createImage(rectangleCell.url, rectangleCell.rotation)))
    rect.width_=(rectangleCell.elementWidth)
    rect.height_=(rectangleCell.elementHeight)
    rect.x_=(rectangleCell.x)
    rect.y_=(rectangleCell.y)
    rect
  }
  def apply(top: Boolean, right: Boolean, bottom: Boolean, left: Boolean, width:Double, height: Double, x: Double, y: Double, damage: Boolean) : RectangleCell = new RectangleCellImpl(top, right, bottom, left, width, height, x, y, damage)
  def apply(top: Boolean, right: Boolean, bottom: Boolean, left: Boolean, x: Double, y: Double, damage: Boolean = false) : RectangleCell = new RectangleCellImpl(top, right, bottom, left, x = x, y = y, damage = damage)


  private class RectangleCellImpl (override val top: Boolean, override val right: Boolean, override val bottom: Boolean, override val left: Boolean, override val elementWidth: Double = 200, override val elementHeight: Double = 200, var x: Double, var y:Double, override val damage: Boolean = false) extends RectangleCell  {
    var _mapEvent:Option[MapEvent] = Option.empty
    override def mapEvent:Option[MapEvent] = _mapEvent
    override def mapEvent_(cellEve: Option[MapEvent]): Unit = _mapEvent = cellEve

    override def x_(newX: Double):Unit = x=newX
    override def y_(newY: Double):Unit = y=newY

    override def isRectangle(posX: Double, posY: Double): Boolean = this.x <= posX && this.y <= posY && this.x + this.elementWidth > posX && this.y + this.elementHeight > posY

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

    override def image: Image = Cell.createImage(url,rotation)

    val parameters:(String,Int) = UrlFactory.getParameters(top,right,bottom,left)
    var url:String = parameters._1
    var rotation:Int = parameters._2
    if(damage)url = url.substring(0, url.length - 4) + "Dmg.png"

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

}
