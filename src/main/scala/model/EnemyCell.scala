package model

import scalafx.scene.SnapshotParameters
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.paint.Color

class EnemyCell(private val _enemy: Enemy) extends Cell{

  def enemy: Enemy = _enemy

  def image: Image = {
    println("IMAGE: " + _enemy.image)
    val iv = new ImageView(new Image( _enemy.image))
    iv.fitWidth_=(200)
    iv.fitHeight_=(200)
    var params = new SnapshotParameters()
    params.setFill(Color.Transparent)
    iv.snapshot(params, null)
  }
}
