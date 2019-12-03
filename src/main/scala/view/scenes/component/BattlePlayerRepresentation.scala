package view.scenes.component

import javafx.beans.property.{SimpleDoubleProperty, SimpleStringProperty}
import javafx.event.{ActionEvent, EventHandler}
import model.{Category, Player, Type}
import scalafx.animation.{FadeTransition, RotateTransition, TranslateTransition}
import scalafx.scene.control.{Button, Label, ProgressBar}
import scalafx.scene.layout.{Pane, StackPane}
import scalafx.util.Duration
import scalafx.Includes._


trait BattlePlayerRepresentation extends Pane {
  def marginX: Double

  def marginY: Double

  def player: Player

  def playAnimation(byVal: Double = 0, family: (Category, Type), action: EventHandler[ActionEvent]): Unit

  def updateHP(hp: Double): Unit
}

class BattlePlayerRepresentationImpl(override val marginX: Double, override val marginY: Double, override val player: Player) extends BattlePlayerRepresentation {
  private val observableHealthPoint = (new SimpleDoubleProperty(1), new SimpleStringProperty("Player: " + player.healthPoint + "hp"))
  translateX = marginX
  translateY = marginY
  val life: StackPane = new StackPane {
    translateY = -10
    children = List(new ProgressBar {
      progress <== observableHealthPoint._1
      styleClass.add("life")
    }, new Label{
      styleClass.add("title")
      text <== observableHealthPoint._2
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

  override def playAnimation(byVal: Double = 0, family: (Category, Type), action: EventHandler[ActionEvent]): Unit = family._1 match {
    case Category.Attack => attack(byVal, action)
    case Category.Defense => defense(action)
  }


  override def updateHP(hp: Double): Unit = {
    if(hp / player.healthPoint != observableHealthPoint._1.value) {
      damage()
      observableHealthPoint._1.set(if(hp / player.healthPoint > 0) hp / player.healthPoint else 0)
      observableHealthPoint._2.set(if(hp > 0) "Player: "  +  hp.toInt + "hp" else "Player: 0hp")
    }
  }

  private def attack(byVal: Double, action: EventHandler[ActionEvent]): Unit = new TranslateTransition(Duration(150), playerRepresentation) {
    byX = byVal
    cycleCount = 2
    autoReverse = true
    onFinished = action
  }.play()

  private def defense(action: EventHandler[ActionEvent]): Unit = new FadeTransition(Duration(150), shield) {
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
