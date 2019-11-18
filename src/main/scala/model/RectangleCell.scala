package model

import javafx.scene.paint.ImagePattern
import scalafx.scene.SnapshotParameters
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Rectangle}
import scalafx.Includes._

import scala.collection.mutable.ListBuffer

class RectangleCell (top: Boolean, right: Boolean, bottom: Boolean, left: Boolean, elementWidth: Double = 200, elementHeight: Double = 200,var elementX:Double, var elementY:Double, paint:Color) extends Rectangle {

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


  def show(): Unit = {
    print("ciaoooo " + top);
  }

  super.width_=(elementWidth);
  super.height_=(elementHeight);
  super.x_=(elementX);
  super.y_=(elementY);
  super.fill_=(paint);

  def createImage(url: String, rotation: Double): Unit = {
    val iv = new ImageView(new Image( url));
    iv.setRotate(rotation);
    var params = new SnapshotParameters();
    params.setFill(Color.Transparent);
    val img = iv.snapshot(params, null);
    this.fill_=(new ImagePattern(img))
  }


  if(top && left && right && bottom) {
    this.createImage("4road.png",0)
  } else if(top && right && left) {
    this.createImage("3road.png",180)
  } else if(top && right && bottom) {
    this.createImage("3road.png",270)
  } else if(top && left && bottom) {
    this.createImage("3road.png",90)
  } else if(left && right && bottom) {
    this.createImage("3road.png",0)
  } else if(top && bottom) {
    this.createImage("2roadLine.png",90)
  } else if(right && left) {
    this.createImage("2roadLine.png",0)
  } else if(top && right) {
    this.createImage("2road.png",270)
  } else if(top && left) {
    this.createImage("2road.png",180)
  } else if(bottom && right) {
    this.createImage("2road.png",0)
  } else if(bottom && left) {
    this.createImage("2road.png",90)
  } else if(bottom) {
    this.createImage("1road.png",90)
  } else if(top) {
    this.createImage("1road.png",270)
  } else if(right) {
    this.createImage("1road.png",0)
  } else if(left) {
    this.createImage("1road.png",180)
  }

  def isMoveAllowed(movement : Move): Boolean = movement match {
    case Top => {
      if(top) true
      else false
    } case Right => {
      if(right) true
      else false
    } case Bottom => {
      if(bottom) true
      else false
    } case Left  => {
      if(left) true
      else false
    } case _  => {
      false
    }
  }

}