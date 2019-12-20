package controller

import javafx.scene.paint.ImagePattern
import model.RectangleCell
import scalafx.Includes._
import scalafx.animation.{Interpolator, TranslateTransition}
import scalafx.scene.image.Image
import scalafx.scene.shape.Rectangle
import scalafx.util.Duration

object PlayerAnimation {
  val LEVELUP_PREFIX: String = "lev"
  val HEAL_PREFIX: String = "heal"
  private var currentPrefix: String = _

  private var animationImg = new Rectangle() {
    width = 80
    height = 100
    fill_=(null)
  }

  def setup(rectangle: RectangleCell): Rectangle = {
    animationImg.x_=(rectangle.x + rectangle.elementWidth/2 - 41)
    animationImg.y_=(rectangle.y + rectangle.elementHeight/2 - 75)
    animationImg
  }

  private val anim : TranslateTransition = new TranslateTransition {
    duration = Duration(200.0)
    interpolator = Interpolator.Linear
    node = animationImg
  }

  def play(prefix: String): Unit = {
    currentPrefix = prefix
    animationImg.fill_=(new ImagePattern(new Image(currentPrefix + "_1.png")))
    anim.setOnFinished(_ =>  {
      animationImg.fill_=(new ImagePattern(new Image(currentPrefix + "_2.png")))
      anim.setOnFinished(_ =>  {
        animationImg.fill_=(new ImagePattern(new Image(currentPrefix + "_3.png")))
        anim.setOnFinished(_ =>  {
          animationImg.fill_=(new ImagePattern(new Image(currentPrefix + "_2.png")))
          anim.setOnFinished(_ =>  {
            animationImg.fill_=(new ImagePattern(new Image(currentPrefix + "_1.png")))
            anim.setOnFinished(_ =>  {
              animationImg.fill_=(null)
            })
            anim.play()
          })
          anim.play()
        })
        anim.play()
      })
      anim.play()
    })
    anim.play()
  }
}
