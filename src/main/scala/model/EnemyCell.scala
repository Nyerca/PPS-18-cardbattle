package model

import scalafx.scene.SnapshotParameters
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.paint.Color

class EnemyCell(var _enemy: Enemy) extends Cell{

  val enemy = _enemy


  def image(): Image = {
    println("IMAGE: " + enemy.image)
    val iv = new ImageView(new Image( enemy.image));
    var params = new SnapshotParameters();
    params.setFill(Color.Transparent);
    iv.snapshot(params, null);
  }
}
