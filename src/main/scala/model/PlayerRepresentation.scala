package model

import javafx.scene.paint.ImagePattern
import scalafx.Includes._
import scalafx.scene.image.Image
import scalafx.scene.shape.Rectangle

class PlayerRepresentation  (var _position : RectangleCell, var _url :  String) extends Serializable {

  def url: String = _url
  def url_(str : String): Unit = _url = str

  def position: RectangleCell = _position
  def position_ (value:RectangleCell, url: String) :Unit = {
    _position = value
    //println("New player position: (" + _position.getX + ", "+ _position.getY+ ")")

    _url = url
  }

  override def toString: String = {
    "Url: " + _url + " Position: " + _position
  }
}
/*
object PlayerRepresentation {
  def createPlayerCell( position : RectangleCell, url : String, elemWidth: Double = 60, elemHeight: Double = 80): PlayerWithCell = {
    new PlayerWithCell(position, url, elemWidth, elemHeight)
  }
}
*/