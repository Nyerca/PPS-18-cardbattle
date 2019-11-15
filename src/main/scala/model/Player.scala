package model

import scalafx.scene.paint.Color
import scalafx.scene.shape.Circle

class Player {
  private var _position:RectangleCell = null;
  private var _circle:Circle = null;

  def circle = _circle;

  def position = _position;
  def position_ (value:RectangleCell) :Unit = {
    _position = value;
    println("New player position: (" + _position.getX + ", "+ _position.getY+ ")")
    _circle = new Circle {
      centerX = value.getX + value.getWidth/2;
      centerY = value.getY + value.getHeight/2;
      radius = 20
      fill = Color.Blue
    }
    println("New player position: (" + _circle.centerX.toDouble + ", "+ _circle.centerY.toDouble+ ")")
  }
}