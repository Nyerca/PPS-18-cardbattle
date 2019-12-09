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

  def x: Double
  def x_(newX: Double): Unit

  def y: Double
  def y_(newY: Double): Unit

  def setDamage():Unit
  def canEqual(other: Any): Boolean
  def url: String
  def rotation: Int
  def getWidth: Double
  def getHeight: Double

  def isMoveAllowed(movement : Move): Boolean
  def mapEvent:Option[MapEvent]
  def mapEvent_(cellEve: Option[MapEvent]): Unit
}


class RectangleCellImpl (override val top: Boolean, override val right: Boolean, override val bottom: Boolean, override val left: Boolean, override val elementWidth: Double = 200, override val elementHeight: Double = 200, var _x: Double, var _y:Double) extends RectangleCell  {
  var _mapEvent:Option[MapEvent] = Option.empty
  override def mapEvent:Option[MapEvent] = _mapEvent
  override def mapEvent_(cellEve: Option[MapEvent]): Unit = _mapEvent = cellEve

  override def canEqual(other: Any): Boolean = other.isInstanceOf[RectangleCell]

  override def x: Double = _x
  override def y: Double = _y
  override def getWidth: Double = elementWidth
  override def getHeight: Double = elementHeight
  override def x_(newX: Double): Unit = _x = newX
  override def y_(newY: Double): Unit = _y = newY

  override def equals(other:Any) :Boolean = other match {
    case that : RectangleCell => that.canEqual(this) && this.x == that.x && this.y == that.y
    case _  => false
  }

  override def hashCode(): Int = {
    val state = Seq(_x,_y,elementWidth,elementHeight)
    state.map(_.hashCode()).foldLeft(0)((a,b)=>31 * a + b)
  }

  override def toString :String = {
    "Rectangle ("+_x + ", " +_y+") T: " + top + " R: " + right + " B: " + bottom + " L: " + left + " enemy: " + _mapEvent
  }

  override def image: Image = {
    val iv = new ImageView(new Image( url))
    iv.setRotate(rotation)
    var params = new SnapshotParameters()
    params.setFill(Color.Transparent)
    iv.snapshot(params, null)
  }

  var _url:String = _
  var _rotation:Int = 0
  override def url: String = _url
  override def rotation: Int = _rotation

  override def setDamage(): Unit = _url = _url.substring(0, _url.length - 4) + "Dmg.png"

  if(top && left && right && bottom) {
    this._url="/roads/4road.png"
  } else if(top && right && left) {
    this._url="/roads/3road.png"
    this._rotation = 180
  } else if(top && right && bottom) {
    this._url="/roads/3road.png"
    this._rotation = 270
  } else if(top && left && bottom) {
    this._url="/roads/3road.png"
    this._rotation = 90
  } else if(left && right && bottom) {
    this._url="/roads/3road.png"
  } else if(top && bottom) {
    this._url="/roads/2roadLine.png"
    this._rotation = 90
  } else if(right && left) {
    this._url="/roads/2roadLine.png"
  } else if(top && right) {
    this._url="/roads/2road.png"
    this._rotation = 270
  } else if(top && left) {
    this._url="/roads/2road.png"
    this._rotation = 180
  } else if(bottom && right) {
    this._url="/roads/2road.png"
  } else if(bottom && left) {
    this._url="/roads/2road.png"
    this._rotation = 90
  } else if(bottom) {
    this._url="/roads/1road.png"
    this._rotation = 90
  } else if(top) {
    this._url="/roads/1road.png"
    this._rotation = 270
  } else if(right) {
    this._url="/roads/1road.png"
  } else if(left) {
    this._url="/roads/1road.png"
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


  /*
  def generateRandom(gameC: GameController, excludedValues : Map[Int,List[Int]], iteration: Int) : RectangleCell = {
    var rngX = STARTING_X
    var rngY = STARTING_Y
    while(excludedValues.contains(rngX) && excludedValues.get(rngX).get.contains(rngY)) {
      rngX = Random.nextInt(6) * 200
      rngY = Random.nextInt(4) * 200
    }
    var top:Boolean = false
    var right:Boolean = false
    var bottom:Boolean = false
    var left:Boolean = false

    var probEnemy = 0.1
    var probStatue = 0.1
    val probDmg = 0.3

    if(iteration==0 || iteration==3) {
      top=true
      right = true
      left=true
      bottom=true
      probEnemy = 0
      probStatue = 0
    } else {
      while(!top && !right && !bottom && !left) {
        top = math.random()>0.5
        right = math.random()>0.5
        bottom = math.random()>0.5
        left = math.random()>0.5
      }
    }

    val rectcell=  new RectangleCellImpl(top, right, bottom, left, _x= rngX, _y=rngY)

    if(iteration == 1) probEnemy = 1
    if(iteration == 2) {
      probEnemy=0
      probStatue=1
    }

    if(iteration ==3) {
      val pyramid = new Pyramid()
      rectcell.mapEvent_(Option(MapEvent.createMapEvent(pyramid, new PlayerRepresentation(rectcell, "pyramid.png"))) )
    } else if (math.random() <= probEnemy) {
      val enem = gameC.spawnEnemy(Random.nextInt(5))
      val enemy = new PlayerRepresentation(rectcell, enem.image)
      rectcell.mapEvent_(Option(MapEvent.createMapEvent(enem, enemy)))
    } else if (math.random() <= probStatue) {
      val statue = new Statue(Random.nextInt(8) + 2)
      rectcell.mapEvent_(Option(MapEvent.createMapEvent(statue, new PlayerRepresentation(rectcell, "statue.png"))))
    }

    if (iteration > 3 && math.random() <= probDmg) rectcell.setDamage()
    //println(rectcell)
    rectcell
  }*/


  def generateRandomCard() : RectangleCellImpl = {
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
    val re = new RectangleCellImpl(top, right, bottom, left, _x= 0, _y=0)
    if(math.random() <= 0.4) re.setDamage()
    re
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
    rect.y_=(rectangleCell.y)
    rect
  }
}
