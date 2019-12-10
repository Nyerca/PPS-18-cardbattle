package model

import scalafx.scene.Node
import scalafx.scene.shape.Rectangle

import scala.collection.mutable.ListBuffer


class RectangleWithCell(val elementWidth: Double = 200, val elementHeight: Double = 200, var elementX:Double, var elementY:Double,var _rectCell : RectangleCell) extends Rectangle{
  super.width_=(elementWidth)
  super.height_=(elementHeight)
  super.x_=(elementX)
  super.y_=(elementY)

  def rectCell: RectangleCell = _rectCell
  def rectCell_ (newRectCell : RectangleCell) :Unit = {_rectCell = newRectCell}

  def isRectangle(posX: Double, posY: Double): Boolean = {
    if(this.getX <= posX && this.getY <= posY && this.getX + this.getWidth > posX && this.getY + this.getHeight > posY) return true
    false
  }
}

object RectangleWithCell {
  implicit def RectangleWithCell2RectangleCell(rect : RectangleWithCell): RectangleCell = rect.rectCell

}
