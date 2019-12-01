package model

import controller.GameController
import exception.{IllegalSizeException, NoMovementException}
import javafx.scene.paint.ImagePattern
import scalafx.Includes._
import scalafx.scene.SnapshotParameters
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle

import scala.collection.mutable.ListBuffer
import scala.util.Random

trait RectangleCell extends Serializable with Cell {
  def top: Boolean
  def right: Boolean
  def bottom: Boolean
  def left: Boolean
  def elementWidth: Double
  def elementHeight: Double
  def _x: Double
  def x: Double
  def elementY: Double

  def canEqual(other: Any): Boolean
  def url: String
  def rotation: Int

  def getY: Double
  def getWidth: Double
  def getHeight: Double

  def x_(newX: Double): Unit
  def setY(newY: Double): Unit

  def enemy:(Option[Enemy], Option[PlayerRepresentation])
  def enemy_(enemy: Enemy, representation : PlayerRepresentation): Unit
  def isMoveAllowed(movement : Move): Boolean
}


class RectangleCellImpl (override val top: Boolean, override val right: Boolean, override val bottom: Boolean, override val left: Boolean, override val elementWidth: Double = 200, override val elementHeight: Double = 200, var _x: Double, var elementY:Double) extends RectangleCell  {
  var _enemy: (Option[Enemy],Option[PlayerRepresentation]) = (Option.empty, Option.empty)

  override def enemy:(Option[Enemy], Option[PlayerRepresentation]) = _enemy
  override def enemy_(enemy: Enemy, representation : PlayerRepresentation): Unit = {_enemy = (Option(enemy), Option(representation)) }

  override def canEqual(other: Any): Boolean = other.isInstanceOf[RectangleCell]

  override def x: Double = _x
  override def getY: Double = elementY
  override def getWidth: Double = elementWidth
  override def getHeight: Double = elementHeight
  override def x_(newX: Double): Unit = _x = newX
  override def setY(newY: Double): Unit = elementY = newY

  override def equals(other:Any) :Boolean = other match {
    case that : RectangleCell => that.canEqual(this) && this.x == that.x && this.getY == that.getY
    case _  => false
  }

  override def hashCode(): Int = {
    val state = Seq(_x,elementY,elementWidth,elementHeight)
    state.map(_.hashCode()).foldLeft(0)((a,b)=>31 * a + b)
  }

  override def toString :String = {
    "Rectangle ("+_x + ", " +elementY+") T: " + top + " R: " + right + " B: " + bottom + " L: " + left + " enemy: " + enemy._2.isDefined
  }


  private var _image : Image = _ ;
  def image: Image = _image


  var _url:String = _
  var _rotation:Int = 0
  def url: String = _url
  def rotation: Int = _rotation




  if(top && left && right && bottom) {
    this._url="4road.png"
  } else if(top && right && left) {
    this._url="3road.png"
    this._rotation = 180
  } else if(top && right && bottom) {
    this._url="3road.png"
    this._rotation = 270
  } else if(top && left && bottom) {
    this._url="3road.png"
    this._rotation = 90
  } else if(left && right && bottom) {
    this._url="3road.png"
  } else if(top && bottom) {
    this._url="2roadLine.png"
    this._rotation = 90
  } else if(right && left) {
    this._url="2roadLine.png"
  } else if(top && right) {
    this._url="2road.png"
    this._rotation = 270
  } else if(top && left) {
    this._url="2road.png"
    this._rotation = 180
  } else if(bottom && right) {
    this._url="2road.png"
  } else if(bottom && left) {
    this._url="2road.png"
    this._rotation = 90
  } else if(bottom) {
    this._url="1road.png"
    this._rotation = 90
  } else if(top) {
    this._url="1road.png"
    this._rotation = 270
  } else if(right) {
    this._url="1road.png"
  } else if(left) {
    this._url="1road.png"
    this._rotation = 180
  }

  def isMoveAllowed(movement : Move): Boolean =movement match {
    case Top => top
    case Right => right
    case Bottom => bottom
    case Left  => left
    case _  => false
  }

  if(!top && !right && !bottom && !left) throw new NoMovementException()
  if(elementWidth == 0 || elementHeight == 0)  throw new IllegalSizeException()
}

object RectangleCell {
  def generateRandom(gameC: GameController, excludedValues : Map[Int,ListBuffer[Int]]) : RectangleCell = {
    var rngX = 400
    var rngY = 200
    while(excludedValues.contains(rngX) && excludedValues.get(rngX).get.contains(rngY)) {
      rngX = Random.nextInt(8) * 200
      rngY = Random.nextInt(4) * 200
    }
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
    println("Rectangle ("+rngX + ", " +rngY+") T: " + top + " R: " + right + " B: " + bottom + " L: " + left)
    val rectcell=  new RectangleCellImpl(top, right, bottom, left, _x= rngX, elementY=rngY)
    var probEnemy = 0.1
    if(excludedValues.size == 1) probEnemy = 1
    if( math.random()<=probEnemy) {val enem = gameC.spawnEnemy(4); val enemy = new PlayerRepresentation(rectcell, enem.image);  rectcell.enemy_(enem,enemy) }

    rectcell
  }
  def generateRandomCard() : RectangleCell = {
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
    new RectangleCellImpl(top, right, bottom, left, _x= 0, elementY=0)
  }

  def createImage(url: String, rotation: Double): ImagePattern = {
    val iv = new ImageView(new Image( url))
    iv.setRotate(rotation)
    var params = new SnapshotParameters()
    params.setFill(Color.Transparent)
    val image = iv.snapshot(params, null)
    new ImagePattern(image)
  }

  def createRectangle(rectangleCell: RectangleCell): Rectangle = {
    val rect:Rectangle = new Rectangle()
    rect.fill_=(RectangleCell.createImage(rectangleCell.url, rectangleCell.rotation))
    rect.width_=(rectangleCell.getWidth)
    rect.height_=(rectangleCell.getHeight)
    rect.x_=(rectangleCell.x)
    rect.y_=(rectangleCell.getY)
    rect
  }
}
