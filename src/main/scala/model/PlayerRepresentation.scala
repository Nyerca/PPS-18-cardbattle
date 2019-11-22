package model

import javafx.scene.paint.ImagePattern
import scalafx.Includes._
import scalafx.scene.image.Image
import scalafx.scene.shape.Rectangle
class PlayerRepresentation  (var _position : RectangleCell, var _url :  String) extends Serializable {

  def url = _url
  def url_(str : String) = _url = str

  def position = _position;
  def position_ (value:RectangleCell, url: String) :Unit = {
    _position = value;
    //println("New player position: (" + _position.getX + ", "+ _position.getY+ ")")

    _url = url
  }

  override def toString: String = {
    "Url: " + _url + " Position: " + _position
  }
}

object PlayerRepresentation {
  def createPlayerCell( position : RectangleCell, url : String): PlayerWithCell = {
    new PlayerWithCell(position, url)
  }
}
