package view.scenes.component

import Utility.TransitionFactory
import javafx.beans.property.{SimpleDoubleProperty, SimpleStringProperty}
import javafx.event.{ActionEvent, EventHandler}
import model.{Category, Player}
import scalafx.scene.control.{Button, Label, ProgressBar}
import scalafx.scene.layout.{Pane, StackPane}
import scalafx.util.Duration
import scalafx.Includes._


trait BattlePlayerRepresentation extends Pane {
  def marginX: Double

  def marginY: Double

  def player: Player

  def playAnimation(byVal: Double = 0, category: Category, action: EventHandler[ActionEvent]): Unit

  def updateHP(hp: Double, action: EventHandler[ActionEvent]): Unit
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
    }, new Label {
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
    translateY = playerRepresentation.translateY.value - 20
    styleClass.add("shield")
  }

  children = List(life, playerRepresentation, shield)

  override def playAnimation(byVal: Double = 0, category: Category, action: EventHandler[ActionEvent]): Unit = category match {
    case Category.Attack => attack(byVal, action)
    case Category.Defense => defense(action)
  }


  override def updateHP(hp: Double, action: EventHandler[ActionEvent]): Unit = {
    if ( hp / player.healthPoint != observableHealthPoint._1.value ) {
      damage()
      observableHealthPoint._1.set(if ( hp / player.healthPoint > 0 ) hp / player.healthPoint else 0)
      observableHealthPoint._2.set(if ( hp > 0 ) "Player: " + hp.toInt + "hp" else "Player: 0hp")
      defeat(hp, action)
    }
  }

  private def defeat(hp: Double, action: EventHandler[ActionEvent]): Unit = hp match {
    case n if n <= 0 => TransitionFactory.fadeTransitionFactory(Duration(500), this, action).play()
    case _ => ;
  }

  private def attack(byVal: Double, action: EventHandler[ActionEvent]): Unit = TransitionFactory.translateTransitionFactory(Duration(150), playerRepresentation, action, byVal, 0, 2, autoReversible = true).play()

  private def defense(action: EventHandler[ActionEvent]): Unit = TransitionFactory.fadeTransitionFactory(Duration(150), shield, action, 1, 2, autoReversible = true).play()

  private def damage(): Unit = TransitionFactory.rotateTransitionFactory(Duration(20), playerRepresentation, TransitionFactory.DEFAULT_ON_FINISHED, 5, 20, autoReversible = true).play()
}

object BattlePlayerRepresentation {
  def apply(marginX: Double, marginY: Double, player:Player): BattlePlayerRepresentation = new BattlePlayerRepresentationImpl(marginX, marginY, player)
}
