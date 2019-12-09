package controller

import javafx.animation.Animation.Status
import javafx.scene.paint.ImagePattern
import model._
import scalafx.Includes._
import scalafx.animation.{Interpolator, TranslateTransition}
import scalafx.scene.image.Image
import scalafx.scene.layout.BorderPane
import scalafx.util.Duration

import scala.collection.mutable.ListBuffer

object MovementAnimation {

  var anim = new TranslateTransition {
    duration = Duration(200.0)
    interpolator = Interpolator.Linear
  }


  def setAnimationNode (pane : BorderPane): Unit = anim.node = pane.center.apply()


  def setAnimation(fromX: Double, toX: Double, fromY:Double, toY: Double)   = {
    anim.fromX = fromX;
    anim.fromY= fromY;
    anim.toX = toX
    anim.toY= toY
  }

  var list = new ListBuffer[String]
  list.append("2.png")
  list.append(".png")
  list.append("1.png")
  list.append(".png")

  def setAnim(newRectangle:RectangleCell, incrementX : Double, incrementY: Double,stringUrl : String, fun:(RectangleCell, String, Boolean) => Unit): Unit = {
    fun(newRectangle,stringUrl, false)
    anim.fromX = anim.toX.toDouble
    anim.fromY = anim.toY.toDouble
    anim.toX = anim.fromX.toDouble + incrementX;
    anim.toY = anim.fromY.toDouble + incrementY;
  }


  def setAnimationIncrement(newRectangle:RectangleCell, incrementX : Double, incrementY: Double, stringUrl : String, fun:(RectangleCell, String, Boolean) => Unit): Unit = {
    anim.toX = anim.fromX.toDouble + incrementX;
    anim.toY = anim.fromY.toDouble + incrementY;

    anim.setOnFinished(e =>  {
      setAnim(newRectangle,incrementX,incrementY,stringUrl + "2.png",fun)
      anim.setOnFinished(e => {
        setAnim(newRectangle,incrementX,incrementY,stringUrl + ".png",fun)
        anim.setOnFinished(e => {
          setAnim(newRectangle,incrementX,incrementY,stringUrl + "1.png",fun)
          anim.setOnFinished(e => {
            setAnim(newRectangle,incrementX,incrementY,stringUrl + ".png",fun)
            anim.setOnFinished(e => {
              anim.stop();
                fun(newRectangle,stringUrl+ ".png", true)
            })
            anim.play();})
          anim.play();})
        anim.play();})
      anim.play();
    })
  }


  def checkAnimationEnd():Boolean = anim.status.getValue match {
    case Status.STOPPED => true
    case _ => false
  }


}
