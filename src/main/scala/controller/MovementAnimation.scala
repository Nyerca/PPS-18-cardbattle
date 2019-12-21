package controller

import model._
import scalafx.Includes._
import scalafx.animation.{Interpolator, TranslateTransition}
import scalafx.scene.layout.BorderPane
import scalafx.util.Duration

object MovementAnimation {

  val anim: TranslateTransition = new TranslateTransition {
    duration = Duration(200.0)
    interpolator = Interpolator.Linear
  }

  def setAnimationNode (pane : BorderPane): Unit = anim.node = pane.center.apply()

  def setAnimation(fromX: Double, toX: Double, fromY:Double, toY: Double): Unit   = {
    anim.fromX = fromX
    anim.fromY= fromY
    anim.toX = toX
    anim.toY= toY
  }

  def setAnim(newRectangle:RectangleCell, incrementX : Double, incrementY: Double,stringUrl : String, fun:(RectangleCell, String, Boolean) => Unit): Unit = {
    fun(newRectangle,stringUrl, false)
    anim.fromX = anim.toX.toDouble
    anim.fromY = anim.toY.toDouble
    anim.toX = anim.fromX.toDouble + incrementX
    anim.toY = anim.fromY.toDouble + incrementY
  }

  def setAnimationIncrement(newRectangle:RectangleCell, incrementX : Double, incrementY: Double, stringUrl : String, fun:(RectangleCell, String, Boolean) => Unit): Unit = {
    anim.toX = anim.fromX.toDouble + incrementX
    anim.toY = anim.fromY.toDouble + incrementY

    anim.setOnFinished(_ =>  {
      setAnim(newRectangle,incrementX,incrementY,stringUrl + "2.png",fun)
      anim.setOnFinished(_ => {
        setAnim(newRectangle,incrementX,incrementY,stringUrl + ".png",fun)
        anim.setOnFinished(_ => {
          setAnim(newRectangle,incrementX,incrementY,stringUrl + "1.png",fun)
          anim.setOnFinished(_ => {
            setAnim(newRectangle,incrementX,incrementY,stringUrl + ".png",fun)
            anim.setOnFinished(_ => {
              anim.stop()
                fun(newRectangle,stringUrl+ ".png", true)
            })
            anim.play()})
          anim.play()})
        anim.play()})
      anim.play()
    })
  }
}
