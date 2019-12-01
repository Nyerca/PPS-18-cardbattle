package view.scenes.component

import javafx.beans.property.SimpleDoubleProperty
import javafx.event.{ActionEvent, EventHandler}
import model.{Category, Player}
import scalafx.animation.{FadeTransition, RotateTransition, TranslateTransition}
import scalafx.scene.control.{Button, Label, ProgressBar}
import scalafx.scene.layout.{Pane, StackPane}
import scalafx.util.Duration
import scalafx.Includes._


trait BattlePlayerRepresentation extends Pane {
  def marginX: Double
  def marginY: Double
  def player: Player
  def playAnimation(byVal: Double = 0, category: Category, healthPoint: Double): Unit
}

class BattlePlayerRepresentationImpl(override val marginX: Double, override val marginY: Double, override val player: Player) extends BattlePlayerRepresentation {
  private val observableHealthPoint = new SimpleDoubleProperty(1)
  translateX = marginX
  translateY = marginY
  val life: StackPane = new StackPane {
    translateY = -10
    children = List(new ProgressBar {
      progress <== observableHealthPoint
      styleClass.add("life")
    }, new Label{
      styleClass.add("title")
      text = player.name
    })
  }

  val playerRepresentation: Button = new Button {
    styleClass.add("image")
    translateY = 20
    mouseTransparent = true
    style = "-fx-background-image: url(" + player.image + ")"
  }

  val shield: Button = new Button {
    translateX = playerRepresentation.translateX.value - 50
    translateY = playerRepresentation.translateY.value -20
    styleClass.add("shield")
  }

  children = List(life, playerRepresentation, shield)

  override def playAnimation(byVal: Double = 0, category: Category, healthPoint: Double): Unit = category match {
    case Category.Attack => attack(byVal, handle(updateHP(healthPoint)))
    case Category.Defense => defense(handle(updateHP(healthPoint)))
  }


  private def updateHP(hp: Double): Unit = {
    if(hp / player.healthPoint != observableHealthPoint.value) {
      damage()
      observableHealthPoint.set(if(hp / player.healthPoint > 0) hp / player.healthPoint else 0)
    }
  }

  private def attack(byVal: Double, action: EventHandler[ActionEvent]): Unit = new TranslateTransition(Duration(300), playerRepresentation) {
    byX = byVal
    cycleCount = 2
    autoReverse = true
    onFinished = action
  }.play()

  private def defense(action: EventHandler[ActionEvent]): Unit = new FadeTransition(Duration(300), shield) {
    byValue = 1
    cycleCount = 2
    autoReverse = true
    onFinished = action
  }.play()

  private def damage(): Unit = new RotateTransition(Duration(20), playerRepresentation) {
    byAngle = 5
    cycleCount = 20
    autoReverse = true
  }.play()
}

object BattlePlayerRepresentation {
  def apply(marginX: Double, marginY: Double, player:Player): BattlePlayerRepresentation = new BattlePlayerRepresentationImpl(marginX, marginY, player)
}
