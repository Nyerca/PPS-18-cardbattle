package model

import scalafx.scene.SnapshotParameters
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.paint.Color

class EnemyCell extends Cell{

  def image(): Image = {
    val iv = new ImageView(new Image( "vamp.png"));
    var params = new SnapshotParameters();
    params.setFill(Color.Transparent);
    iv.snapshot(params, null);
  }
}
