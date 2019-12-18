package model

import scalafx.scene.image.{Image, ImageView}

trait EnemyCell extends Cell {
  def enemy: Enemy
}

object EnemyCell {
  private final case class EnemyCellImpl(private val _enemy: Enemy) extends EnemyCell{

    def enemy: Enemy = _enemy

    def image: Image = Cell.createImage(_enemy.image, 0)
  }

  def apply(enemy: Enemy): EnemyCell = new EnemyCellImpl(enemy)
}