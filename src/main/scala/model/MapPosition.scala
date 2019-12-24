package model

import controller.GameController
import scala.util.Random

trait MapPosition {
  def create(gameC: GameController, excludedValues : Map[Int,List[Int]]): RectangleCell
}
object MapPosition {
  val STARTING_X = 400
  val STARTING_Y = 200

  def getRng(excludedValues : Map[Int,List[Int]]): (Int,Int) = {
    var outX = STARTING_X
    var outY = STARTING_Y

    while(excludedValues.contains(outX) && excludedValues(outX).contains(outY)) {
      outX = Random.nextInt(6) * 200
      outY = Random.nextInt(4) * 200
    }
    (outX, outY)
  }
  def getRngBooleans: (Boolean, Boolean, Boolean, Boolean) = {
    var top:Boolean = false
    var right:Boolean = false
    var bottom:Boolean = false
    var left:Boolean = false

    while(!top && !right && !bottom && !left) {
      top = math.random()>0.5
      right = math.random()>0.5
      bottom = math.random()>0.5
      left = math.random()>0.5
    }
    (top,right,bottom,left)
  }
  def createCell(excludedValues : Map[Int,List[Int]], damage: Boolean = false): RectangleCell = {
    val (x,y) = MapPosition.getRng(excludedValues)
    RectangleCell(top = true, right = true, bottom = true, left = true, x, y, damage = damage && math.random() <= 0.3)
  }
  def createRngCell(excludedValues : Map[Int,List[Int]], damage: Boolean = false): RectangleCell = {
    val (x,y) = MapPosition.getRng(excludedValues)
    val (top,right,bottom,left) = MapPosition.getRngBooleans
    RectangleCell(top, right, bottom, left, x, y, damage = damage && math.random() <= 0.3)
  }
}

case object PlayerPosition extends MapPosition {
  override def create(gameC: GameController, excludedValues : Map[Int,List[Int]]): RectangleCell = MapPosition.createCell(excludedValues)
}

case object EnemyPosition extends MapPosition {
  override def create(gameC: GameController, excludedValues : Map[Int,List[Int]]): RectangleCell = {
    val rectangle_cell: RectangleCell = MapPosition.createCell(excludedValues, damage = true)
    val enemy = gameC.spawnEnemy(Random.nextInt(5))
    rectangle_cell.mapEvent_(Option(MapEvent(enemy, PlayerRepresentation(rectangle_cell, enemy.image))))
    rectangle_cell
  }
}
case object StatuePosition extends MapPosition {
  override def create(gameC: GameController, excludedValues : Map[Int,List[Int]]): RectangleCell = {
    val rectangle_cell: RectangleCell = MapPosition.createRngCell(excludedValues)
    rectangle_cell.mapEvent_(Option(MapEvent(Statue(Random.nextInt(8) + 2), PlayerRepresentation(rectangle_cell, "statue.png"))))
    rectangle_cell
  }
}

case object PyramidPosition extends MapPosition {
  override def create(gameC: GameController, excludedValues : Map[Int,List[Int]]): RectangleCell = {
    val rectangle_cell: RectangleCell = MapPosition.createCell(excludedValues)
    rectangle_cell.mapEvent_(Option(MapEvent(Pyramid(), PlayerRepresentation(rectangle_cell, "pyramid.png"))) )
    rectangle_cell
  }
}

case object ChestPosition extends MapPosition {
  override def create(gameC: GameController, excludedValues : Map[Int,List[Int]]): RectangleCell = {
    val rectangle_cell: RectangleCell = MapPosition.createRngCell(excludedValues)
    rectangle_cell.mapEvent_(Option(MapEvent(Chest(Random.nextInt(4) + 1), PlayerRepresentation(rectangle_cell, "chest.png"))))
    rectangle_cell
  }
}

case object EmptyPosition extends MapPosition {
  override def create(gameC: GameController, excludedValues: Map[Int, List[Int]]): RectangleCell = MapPosition.createRngCell(excludedValues, damage = true)
}




