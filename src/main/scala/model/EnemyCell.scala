package model

import scalafx.scene.image.Image

trait EnemyCell extends Cell {
  def enemy: Enemy
}

object EnemyCell {
  private final case class EnemyCellImpl(override val enemy: Enemy) extends EnemyCell{
    override def image: Image = Cell.createImage(enemy.image, 0)
  }

  def apply(enemy: Enemy): EnemyCell = EnemyCellImpl(enemy)
}