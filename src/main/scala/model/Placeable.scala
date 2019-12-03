package model
import controller.MapController
import exception.{DoubleCellException, DoubleEnemyException, DoubleMovementException, MissingCellException}
import javafx.animation.Animation.Status
import model.{Bottom, Cell, EnemyCell, Left, Player, PlayerRepresentation, RectangleCell, RectangleWithCell, Right, Top}
import exception._
import scalafx.scene.control.{Button, Separator, ToolBar}
import scalafx.scene.input.KeyCode
import scalafx.scene.paint.Color
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.{Scene, SnapshotParameters}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.layout._
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle

import scala.collection.mutable.ListBuffer
import scala.util.Random
import javafx.scene.input.MouseEvent
import javafx.scene.paint.ImagePattern


import scala.collection.mutable.ListBuffer
import scala.util.Random

/*
trait Placeable[A] {
  def place(a: A): Unit
}

object Placeable {


  //Apply
  def apply[A](implicit pleaceable: Placeable[A]): Placeable[A] = pleaceable
  def place[A: Placeable](a: A) = Placeable[A].place(a)


  //notazione .show
  implicit class ShowOps[A: Placeable](a: A) {
    def place = Placeable[A].place(a)
  }

  def instance[A](func: A => Unit, s: String): Placeable[A] =
    new Placeable[A] {
      def place(a: A): Unit = func(a)
    }

  implicit val rectanglePlaceable: Placeable[RectangleCell] =
    instance(rectangle => {
      print("R: " + rectangle)
    }, "ciao")



}

*/

trait Placeable[A <: Cell] {
  def place(a: A, cell:Option[RectangleCell],controller:MapController ): Unit
}



object Placeable {



  //Apply
  def apply[A <: Cell](implicit pleaceable: Placeable[A ]): Placeable[A] = pleaceable
  def place[A <: Cell :Placeable](a: A, cell:Option[RectangleCell], controller:MapController) = Placeable[A].place(a, cell, controller)


  def instance[A <: Cell](func: (A, Option[RectangleCell], MapController) => Unit): Placeable[A] =
    new Placeable[A] {
      def place(a: A, cell:Option[RectangleCell] , controller: MapController): Unit = func(a, cell, controller)
    }

  implicit def cellToEnemyCell(cell: Cell): EnemyCell = cell.asInstanceOf[EnemyCell];
  implicit def cellToRectangleCell(cell: Cell): RectangleCell = cell.asInstanceOf[RectangleCell];


  implicit val rectanglePlaceable: Placeable[RectangleCell] =
    instance((selected, cell, controller) => {
      //val selectedElement = selected.asInstanceOf[RectangleCell]

      if(!cell.isDefined) {

        val rect = new RectangleWithCell(selected.getWidth, selected.getHeight, selected.x, selected.getY,selected) {
          fill = (RectangleCell.createImage(selected.url, selected.rotation))
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
        if(!rect.enemy._2.isDefined) {
          rect.enemy_(selected.enemy,new PlayerRepresentation(rect, selected.enemy.image))
          controller.postInsert()
        } else {
          throw new DoubleEnemyException
        }

      } else {
        throw new MissingCellException
      }
    })



}

