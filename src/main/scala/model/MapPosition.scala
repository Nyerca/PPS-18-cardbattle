package model

import controller.GameController


import scala.util.Random

trait MapPosition {
  def create(gameC: GameController, excludedValues : Map[Int,List[Int]]): RectangleCell
}
object MapPosition {
  val STARTING_X = 400
  val STARTING_Y = 200
  val rngX = STARTING_X
  val rngY = STARTING_Y


  def getRng(excludedValues : Map[Int,List[Int]]): (Int,Int) = {
    var outX = rngX
    var outY = rngY

    while(excludedValues.contains(outX) && excludedValues.get(outX).get.contains(outY)) {
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
    RectangleCell(true, true, true, true, x, y, damage = damage && math.random() <= 0.3)
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
    val rectcell: RectangleCell = MapPosition.createRngCell(excludedValues, true)
    val enem = gameC.spawnEnemy(Random.nextInt(5))
    val enemy = PlayerRepresentation(rectcell, enem.image)
    rectcell.mapEvent_(Option(MapEvent(enem, enemy)))
    rectcell
  }
}
case object StatuePosition extends MapPosition {
  override def create(gameC: GameController, excludedValues : Map[Int,List[Int]]): RectangleCell = {
    val rectcell: RectangleCell = MapPosition.createRngCell(excludedValues)
    val statue = Statue(Random.nextInt(8) + 2)
    rectcell.mapEvent_(Option(MapEvent(statue, PlayerRepresentation(rectcell, "statue.png"))))
    rectcell
  }
}

case object PyramidPosition extends MapPosition {
  override def create(gameC: GameController, excludedValues : Map[Int,List[Int]]): RectangleCell = {
    val rectcell: RectangleCell = MapPosition.createCell(excludedValues)
    rectcell.mapEvent_(Option(MapEvent(Pyramid(), PlayerRepresentation(rectcell, "pyramid.png"))) )
    rectcell
  }
}

case object ChestPosition extends MapPosition {
  override def create(gameC: GameController, excludedValues : Map[Int,List[Int]]): RectangleCell = {
    val rectcell: RectangleCell = MapPosition.createRngCell(excludedValues)
    val chest = Chest(Random.nextInt(4) + 1)
    rectcell.mapEvent_(Option(MapEvent(chest, PlayerRepresentation(rectcell, "chest.png"))))
    rectcell
  }
}

case object EmptyPosition extends MapPosition {
  override def create(gameC: GameController, excludedValues: Map[Int, List[Int]]): RectangleCell = MapPosition.createRngCell(excludedValues, true)
}




