package model

import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Rectangle}
import scala.collection.mutable.ListBuffer

class RectangleCell (top: Boolean, right: Boolean, bottom: Boolean, left: Boolean, elementWidth: Double = 200, elementHeight: Double = 200, elementX:Double, elementY:Double, paint:Color) extends Rectangle {
  private var _borders = new ListBuffer[Rectangle]();

  def canEqual(other: Any) = other.isInstanceOf[RectangleCell]

  def getX = elementX;
  def getY = elementY;
  def getWidth = elementWidth;
  def gethHeight = elementHeight;

  override def equals(other:Any) :Boolean = other match {
    case that : RectangleCell => {
      that.canEqual(this) && this.getX == that.getX && this.getY == that.getY
    }
    case _  => false
  }

  override def hashCode(): Int = {
    val state = Seq(elementX,elementY,elementWidth,elementHeight)
    state.map(_.hashCode()).foldLeft(0)((a,b)=>31 * a + b)
  }


  super.width_=(elementWidth);
  super.height_=(elementHeight);
  super.x_=(elementX);
  super.y_=(elementY);
  super.fill_=(paint);




}