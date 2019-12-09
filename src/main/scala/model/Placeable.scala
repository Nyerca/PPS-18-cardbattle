package model
import controller.MapController
import exception.{DoubleCellException, DoubleEnemyException, DoubleMovementException, MissingCellException}
import scalafx.Includes._

trait Placeable[A <: Cell] {
  def place(a: A, cell:Option[RectangleCell],controller:MapController ): Unit
}

object Placeable {
  //Apply
  def apply[A <: Cell](implicit pleaceable: Placeable[A ]): Placeable[A] = pleaceable
  def place[A <: Cell :Placeable](a: A, cell:Option[RectangleCell], controller:MapController): Unit = Placeable[A].place(a, cell, controller)


  def instance[A <: Cell](func: (A, Option[RectangleCell], MapController) => Unit): Placeable[A] =
    new Placeable[A] {
      def place(a: A, cell:Option[RectangleCell] , controller: MapController): Unit = func(a, cell, controller)
    }

  implicit val rectanglePlaceable: Placeable[RectangleCell] =
    instance((selected, cell, controller) => {

      if(cell.isEmpty) {

        val rect = new RectangleWithCell(selected.getWidth, selected.getHeight, selected.x, selected.getY,selected) {
          fill = RectangleCell.createImage(selected.url, selected.rotation)
        }
        controller.addToList(rect)
        controller.postInsert()

      } else {
        throw new DoubleCellException
      }
    })

  implicit val enemyPlaceable: Placeable[EnemyCell] =
    instance((selected, cell, controller) => {
      if(cell.isDefined) {

        val rect = cell.get

        if(rect.mapEvent.isEmpty) {
          rect.mapEvent_(Option(MapEvent.createMapEvent(selected.enemy, new PlayerRepresentation(rect, selected.enemy.image))) )
          controller.postInsert()
        } else {
          throw new DoubleEnemyException
        }

      } else {
        throw new MissingCellException
      }
    })
}

