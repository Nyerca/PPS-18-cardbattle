package model

import javafx.scene.paint.ImagePattern
import scalafx.scene.SnapshotParameters
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Rectangle}
import scalafx.Includes._

import scala.collection.mutable.ListBuffer

class RectangleCell (top: Boolean, right: Boolean, bottom: Boolean, left: Boolean, elementWidth: Double = 200, elementHeight: Double = 200, elementX:Double, elementY:Double, paint:Color) extends Rectangle {
  private var _borders = new ListBuffer[Rectangle]();

  def canEqual(other: Any) = other.isInstanceOf[RectangleCell]

  def getX = elementX;
  def getY = elementY;
  def getWidth = elementWidth;
  def gethHeight = elementHeight;

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

  if(!top) {
    var border =  new Rectangle() {
      width=elementWidth;
      height=20;
      x = elementX;
      y = elementY;
      fill=Color.Red;
    };
    _borders.append(border)
  }

  if(!left) {
    var border =  new Rectangle() {
      width=20;
      height=elementHeight;
      x = elementX;
      y = elementY;
      fill=Color.Red;
    };
    _borders.append(border)
  }
  if(!right) {
    var border =  new Rectangle() {
      width=20;
      height=elementHeight;
      x = elementX + elementWidth - 20;
      y = elementY;
      fill=Color.Red;
    };
    _borders.append(border)
  }

  if(!bottom) {
    var border =  new Rectangle() {
      width=elementWidth;
      height=20;
      x = elementX;
      y = elementY + elementHeight - 20;
      fill=Color.Red;
    };
    _borders.append(border)
  }

  def borders = { List[Rectangle]() ++ this._borders };

}