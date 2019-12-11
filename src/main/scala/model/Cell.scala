package model

import javafx.scene.paint.ImagePattern
import scalafx.scene.SnapshotParameters
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.paint.Color

trait Cell extends Serializable {
  def image:Image
}

object Cell {
  def createImage(url: String, rotation: Double): ImagePattern = {
    val iv = new ImageView(new Image( url))
    iv.setRotate(rotation)
    var params = new SnapshotParameters()
    params.setFill(Color.Transparent)
    val image = iv.snapshot(params, null)
    new ImagePattern(image)
  }
}
