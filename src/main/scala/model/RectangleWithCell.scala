package model

import scalafx.scene.shape.Rectangle


class RectangleWithCell(elementWidth: Double = 200, elementHeight: Double = 200,var elementX:Double, var elementY:Double,var _rectCell : RectangleCell) extends Rectangle{
  super.width_=(elementWidth)
  super.height_=(elementHeight)
  super.x_=(elementX)
  super.y_=(elementY)

  def rectCell: RectangleCell = _rectCell
  def rectCell_ (newRectCell : RectangleCell) :Unit = {_rectCell = newRectCell}

}

