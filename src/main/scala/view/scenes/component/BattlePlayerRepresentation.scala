package view.scenes.component

import javafx.beans.property.SimpleDoubleProperty
import javafx.event.{ActionEvent, EventHandler}
import model.Player
import scalafx.animation.{RotateTransition, TranslateTransition}
import scalafx.scene.control.{Button, Label, ProgressBar}
import scalafx.scene.layout.{BorderPane, StackPane}
import scalafx.util.Duration
import scalafx.Includes._


trait BattlePlayerRepresentation extends BorderPane{
  def marginX: Double
  def marginY: Double
  def player: Player
  def updateHP(hp: Double): Unit
  def attack(byVal: Double, action: EventHandler[ActionEvent]): Unit
}

class BattlePlayerRepresentationImpl(override val marginX: Double, override val marginY: Double, override val player: Player) extends BattlePlayerRepresentation {
  private val observableHealthPoint = new SimpleDoubleProperty(1)
  translateX = marginX
  translateY = marginY
  top = new StackPane {
    children = List(new ProgressBar {
      progress <== observableHealthPoint
      styleClass.add("life")
    }, new Label{
      styleClass.add("title")
      text = player.name
    })
  }

  center = new Button {
    styleClass.add("image")
    mouseTransparent = true
    style = "-fx-background-image: url(" + player.image + ")"
  }

  override def updateHP(hp: Double): Unit = {
    if(hp / player.healthPoint != observableHealthPoint.value) {
      damage()
      observableHealthPoint.set(hp / player.healthPoint)
    }
  }

  override def attack(byVal: Double, action: EventHandler[ActionEvent]): Unit = new TranslateTransition(Duration(100), center.value) {
    byX = byVal
    cycleCount = 2
    autoReverse = true
    onFinished = action
  }.play()


  private def damage(): Unit = new RotateTransition(Duration(20), center.value) {
    byAngle = 5
    cycleCount = 20
    autoReverse = true
  }.play()
}

object BattlePlayerRepresentation {
  def apply(marginX: Double, marginY: Double, player:Player): BattlePlayerRepresentation = new BattlePlayerRepresentationImpl(marginX, marginY, player)
}
