package model
import controller.MapController
import exception.{DoubleCellException, DoubleEnemyException, MissingCellException}

trait Placeable[A <: Cell] {
  def place(a: A, cell:Option[RectangleCell],dashboard:Dashboard ): Unit
}

object Placeable {

  def apply[A <: Cell](implicit pleaceable: Placeable[A ]): Placeable[A] = pleaceable
  def place[A <: Cell :Placeable](a: A, cell:Option[RectangleCell], dashboard:Dashboard): Unit = Placeable[A].place(a, cell, dashboard)


  def instance[A <: Cell](func: (A, Option[RectangleCell], Dashboard) => Unit): Placeable[A] =
    new Placeable[A] {
      def place(a: A, cell:Option[RectangleCell] , dashboard:Dashboard): Unit = func(a, cell, dashboard)
    }

  implicit val rectanglePlaceable: Placeable[RectangleCell] =
    instance((selected, cell, dashboard) => {
      if(cell.isEmpty) {
        dashboard.list = dashboard.list :+ selected
        dashboard.postInsert()
      } else {
        throw new DoubleCellException
      }
    })

  implicit val enemyPlaceable: Placeable[EnemyCell] =
    instance((selected, cell, dashboard) => {
      if(cell.isDefined) {
        val rect = cell.get
        if(rect.mapEvent.isEmpty) {
          rect.mapEvent_(Option(MapEvent(selected.enemy, PlayerRepresentation(rect, selected.enemy.image))) )
          dashboard.postInsert()
        } else {
          throw new DoubleEnemyException
        }
      } else {
        throw new MissingCellException
      }
    })
}

