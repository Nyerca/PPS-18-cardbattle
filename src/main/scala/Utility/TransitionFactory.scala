package Utility

import javafx.event.{ActionEvent, EventHandler}
import scalafx.animation.{FadeTransition, RotateTransition, TranslateTransition}
import scalafx.scene.Node
import scalafx.util.Duration

object TransitionFactory {

  val DEFAULT_ON_FINISHED: EventHandler[ActionEvent]  = null

  def fadeTransitionFactory(time: Duration, subject: Node, action: EventHandler[ActionEvent], byVal: Double = -1, cycles: Int = 1, autoReversible: Boolean = false): FadeTransition = new FadeTransition(time, subject) {
    byValue = byVal
    autoReverse = autoReversible
    cycleCount = cycles
    onFinished = action
  }

  def rotateTransitionFactory(time: Duration, subject: Node, action: EventHandler[ActionEvent], byVal: Double, cycles: Int, autoReversible: Boolean): RotateTransition = new RotateTransition(time, subject) {
    byAngle = byVal
    cycleCount = cycles
    autoReverse = autoReversible
  }

  def translateTransitionFactory(time: Duration, subject: Node, action: EventHandler[ActionEvent], x: Double, y: Double, cycles: Int, autoReversible: Boolean): TranslateTransition = new TranslateTransition(time, subject) {
    byX = x
    byY = y
    cycleCount = cycles
    autoReverse = autoReversible
    onFinished = action
  }

}
