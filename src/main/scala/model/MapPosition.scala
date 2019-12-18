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
}

case object PlayerPosition extends MapPosition {
  override def create(gameC: GameController, excludedValues : Map[Int,List[Int]]): RectangleCell = {
    val (x,y) = MapPosition.getRng(excludedValues)
    RectangleCell(true, true, true, true, x, y)
  }
}

case object EnemyPosition extends MapPosition {
  override def create(gameC: GameController, excludedValues : Map[Int,List[Int]]): RectangleCell = {
    val (x,y) = MapPosition.getRng(excludedValues)
    val (top,right,bottom,left) = MapPosition.getRngBooleans

    val rectcell=  RectangleCell(top, right, bottom, left, x, y)

    val enem = gameC.spawnEnemy(Random.nextInt(5))
    val enemy = PlayerRepresentation(rectcell, enem.image)
    rectcell.mapEvent_(Option(MapEvent(enem, enemy)))

    if (math.random() <= 0.3) rectcell.setDamage()
    //println(rectcell)
    rectcell
  }
}
case object StatuePosition extends MapPosition {
  override def create(gameC: GameController, excludedValues : Map[Int,List[Int]]): RectangleCell = {
    val (x,y) = MapPosition.getRng(excludedValues)
    val (top,right,bottom,left) = MapPosition.getRngBooleans

    val rectcell=  RectangleCell(top, right, bottom, left, x, y)

    val statue = Statue(Random.nextInt(8) + 2)
    rectcell.mapEvent_(Option(MapEvent(statue, PlayerRepresentation(rectcell, "statue.png"))))

    //println(rectcell)
    rectcell
  }
}

case object PyramidPosition extends MapPosition {
  override def create(gameC: GameController, excludedValues : Map[Int,List[Int]]): RectangleCell = {

    val (x,y) = MapPosition.getRng(excludedValues)

    val rectcell=  RectangleCell(true, true, true, true, x, y)
    rectcell.mapEvent_(Option(MapEvent(Pyramid(), PlayerRepresentation(rectcell, "pyramid.png"))) )
    rectcell
  }
}

case object ChestPosition extends MapPosition {
  override def create(gameC: GameController, excludedValues : Map[Int,List[Int]]): RectangleCell = {
    val (x,y) = MapPosition.getRng(excludedValues)
    val (top,right,bottom,left) = MapPosition.getRngBooleans

    val rectcell=  RectangleCell(top, right, bottom, left, x, y)

    val chest = Chest(Random.nextInt(4) + 1)
    rectcell.mapEvent_(Option(MapEvent(chest, PlayerRepresentation(rectcell, "chest.png"))))

    //println(rectcell)
    rectcell
  }
}

case object EmptyPosition extends MapPosition {
  override def create(gameC: GameController, excludedValues: Map[Int, List[Int]]): RectangleCell = {

    val (x,y) = MapPosition.getRng(excludedValues)
    val (top,right,bottom,left) = MapPosition.getRngBooleans

    val rectcell=   RectangleCell(top, right, bottom, left, x, y)
    if (math.random() <= 0.3) rectcell.setDamage()
    //println(rectcell)
    rectcell
  }
}




