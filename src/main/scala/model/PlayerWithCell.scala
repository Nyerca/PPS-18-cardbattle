package model

import javafx.scene.paint.ImagePattern
import scalafx.Includes._
import scalafx.scene.image.Image
import scalafx.scene.shape.Rectangle

class PlayerWithCell (var _position : RectangleCell, var _url :  String) {

  private var _player = new PlayerRepresentation(_position, _url)
  def player: PlayerRepresentation = _player

  private var _icon:Rectangle = new Rectangle() {
    x=player.position.x+player.position.getWidth/2 -30
    y=(player.position.getY+player.position.getHeight/2)-70
    width = 60
    height = 80
  }

  def icon: Rectangle = _icon

  def setFill(): Unit = {
    icon.fill_=(new ImagePattern(new Image(_player.url)))
  }

}