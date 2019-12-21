package model

import scalafx.scene.SnapshotParameters
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.paint.Color

trait Cell extends Serializable {
  def image:Image
}

object Cell {
  def createImage(url: String, rotation: Double): Image = {
    val iv = new ImageView(new Image( url))
    iv.setRotate(rotation)
    val params = new SnapshotParameters()
    params.setFill(Color.Transparent)
    iv.snapshot(params, null)
  }
}
