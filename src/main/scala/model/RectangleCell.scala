package model

import javafx.scene.paint.ImagePattern
import scalafx.Includes._
import scalafx.scene.SnapshotParameters
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle


import scala.collection.mutable.ListBuffer
import scala.util.Random

@SerialVersionUID(123L)
class RectangleCell (top: Boolean, right: Boolean, bottom: Boolean, left: Boolean, elementWidth: Double = 200, elementHeight: Double = 200,var elementX:Double, var elementY:Double) extends Rectangle with Cell with java.io.Serializable  {
  var _enemy: Option[Player] = Option.empty

  def enemy = _enemy
  def enemy_(enemy : Player) = {_enemy = Option(enemy)}

  def canEqual(other: Any) = other.isInstanceOf[RectangleCell]

  def getX = elementX;
  def getY = elementY;
  def getWidth = elementWidth;
  def getHeight = elementHeight;
  def setX(newX: Double) = {
    elementX = newX
  }
  def setY(newY: Double) = {
    elementY = newY
  }

  override def equals(other:Any) :Boolean = other match {
    case that : RectangleCell => {
      that.canEqual(this) && this.getX == that.getX && this.getY == that.getY
    }
    case _  => false
  }

  override def hashCode(): Int = {
    val state = Seq(elementX,elementY,elementWidth,elementHeight)
    state.map(_.hashCode()).foldLeft(0)((a,b)=>31 * a + b)
  }

  override def toString :String = {
    "Rectangle ("+elementX + ", " +elementY+") T: " + top + " R: " + right + " B: " + bottom + " L: " + left
  }


  def show(): Unit = {
    print("ciaoooo " + top);
  }

  super.width_=(elementWidth);
  super.height_=(elementHeight);
  super.x_=(elementX);
  super.y_=(elementY);

  private var _image : Image = null ;
  def image = _image


  var _url:String = _
  var _rotation:Int = 0
  def url = _url
  def rotation = _rotation




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
    case Top => {
      println("Its top, im:" + top)
      if(top) true
      else false
    } case Right => {
      println("Its right, im:" + right)
      if(right) true
      else false
    } case Bottom => {
      println("Its bottom, im:" + bottom)
      if(bottom) true
      else false
    } case Left  => {
      println("Its left, im:" + left)
      if(left) true
      else false
    } case _  => {
      false
    }
  }

  if(top == false && right == false && bottom == false && left == false) throw new NoMovementException();
  if(elementWidth == 0 || elementHeight == 0)  throw new IllegalSizeException();
}

object RectangleCell {
  def generateRandom(excludedValues : Map[Int,ListBuffer[Int]]) : RectangleCell = {
    var rngX = 800
    var rngY = 400
    while(excludedValues.contains(rngX) && excludedValues.get(rngX).get.contains(rngY)) {
      rngX = Random.nextInt(8) * 200;
      rngY = Random.nextInt(4) * 200
    }
    var top:Boolean = false
    var right:Boolean = false
    var bottom:Boolean = false
    var left:Boolean = false
    while(top == false && right == false && bottom == false && left == false) {
      top = math.random()>0.5
      right = math.random()>0.5
      bottom = math.random()>0.5
      left = math.random()>0.5
    }
    println("Rectangle ("+rngX + ", " +rngY+") T: " + top + " R: " + right + " B: " + bottom + " L: " + left)
    val rectcell=  new RectangleCell(top, right, bottom, left, elementX= rngX, elementY=rngY)
    var probEnemy = 0.1
    if(excludedValues.size == 1) probEnemy = 1
    if( math.random()<=probEnemy) {val enemy = new Player(rectcell, "vamp.png"); enemy.icon.fill_=(new ImagePattern(new Image(enemy.url))); rectcell.enemy_(enemy) }

    rectcell
  }
  def generateRandomCard() : RectangleCell = {
    var top:Boolean = false
    var right:Boolean = false
    var bottom:Boolean = false
    var left:Boolean = false
    while(top == false && right == false && bottom == false && left == false) {
      top = math.random()>0.5
      right = math.random()>0.5
      bottom = math.random()>0.5
      left = math.random()>0.5
    }
    new RectangleCell(top, right, bottom, left, elementX= 0, elementY=0)
  }

  def createImage(url: String, rotation: Double): ImagePattern = {
    val iv = new ImageView(new Image( url));
    iv.setRotate(rotation);
    var params = new SnapshotParameters();
    params.setFill(Color.Transparent);
    val image = iv.snapshot(params, null);
    new ImagePattern(image)
  }

}
