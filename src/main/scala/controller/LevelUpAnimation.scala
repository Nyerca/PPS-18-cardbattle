package controller

import javafx.scene.paint.ImagePattern
import model.RectangleCell
import scalafx.animation.{Interpolator, TranslateTransition}
import scalafx.scene.image.Image
import scalafx.scene.shape.Rectangle
import scalafx.util.Duration
import scalafx.Includes._

object LevelUpAnimation {
  val LEVELUP_PREFIX: String = "lev"
  private var currentPrefix: String = _

  private val animationImg = new Rectangle() {
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
    onFinished_=(_ => {
      animationImg.fill_=(new ImagePattern(new Image(currentPrefix + "_2.png")))
      onFinished_=(_ => {
        animationImg.fill_=(new ImagePattern(new Image(currentPrefix + "_3.png")))
        onFinished_=(_ => {
          animationImg.fill_=(new ImagePattern(new Image(currentPrefix + "_2.png")))
          onFinished_=(_ => {
            animationImg.fill_=(new ImagePattern(new Image(currentPrefix + "_1.png")))
            onFinished_=(_ => animationImg.fill_=(null))
            anim.play()
          })
          anim.play()
        })
        anim.play()
      })
      anim.play()
    })
  }

  def play(prefix: String): Unit = {
    currentPrefix = prefix
    animationImg.fill_=(new ImagePattern(new Image(currentPrefix + "_1.png")))
    anim.play()
  }
}
