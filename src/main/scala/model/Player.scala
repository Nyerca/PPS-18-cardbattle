package model

import javafx.scene.paint.ImagePattern
import scalafx.Includes._
import scalafx.scene.image.Image
import scalafx.scene.shape.Rectangle

class Player (var _position : RectangleCell, var _url :  String) {

  private var _icon:Rectangle = new Rectangle() {
    x=_position.getX+_position.getWidth/2 -30
    y=(_position.getY+_position.getHeight/2)-70
    width = 60
    height = 80
  }

  def icon = _icon;

  def setFill(): Unit = {
    icon.fill_=(new ImagePattern(new Image(url)))
  }


  def url = _url
  def url_(str : String) = _url = str




  def position = _position;
  def position_ (value:RectangleCell, url: String) :Unit = {
    _position = value;
    //println("New player position: (" + _position.getX + ", "+ _position.getY+ ")")

    _url = url
  }
}