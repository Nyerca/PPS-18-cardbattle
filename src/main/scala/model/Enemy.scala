package model

import javafx.scene.paint.ImagePattern
import scalafx.scene.image.Image
import scalafx.scene.shape.Rectangle
import scalafx.Includes._

class Enemy  (var _position : RectangleCell) {

  private var _icon:Rectangle = new Rectangle() {
    x=_position.getX+_position.getWidth/2 -30
    y=(_position.getY+_position.getHeight/2)-70
    width = 60
    height = 80
  }

  def icon = _icon;



  def image_(url: String) = {
    var im = new Image( url);
    _icon.fill_=(new ImagePattern(im))
  }
  image_("vamp.png")




  def position = _position;
  def position_ (value:RectangleCell, url: String) :Unit = {
    _position = value;
    //println("New player position: (" + _position.getX + ", "+ _position.getY+ ")")


    image_(url)
  }
}