package view.scenes.component

import Utility.TransitionFactory
import javafx.beans.property.{SimpleDoubleProperty, SimpleStringProperty}
import javafx.event.{ActionEvent, EventHandler}
import model.{Category, Player, Type, User}
import scalafx.scene.control.{Button, Label, ProgressBar}
import scalafx.scene.layout.{Pane, StackPane}
import scalafx.util.Duration
import scalafx.Includes._


trait BattlePlayerRepresentation extends Pane {
  def marginX: Double

  def marginY: Double

  def player: Player

  def playAnimation(byVal: Double = 0, family: (Category, Type), action: EventHandler[ActionEvent]): Unit

  def updateHP(action: EventHandler[ActionEvent]): Unit
}

class BattlePlayerRepresentationImpl(override val marginX: Double, override val marginY: Double, override val player: Player) extends BattlePlayerRepresentation {
  private val observableHealthPoint = (new SimpleDoubleProperty(player.actualHealthPoint.toDouble / player.totalHealthPoint.toDouble), new SimpleStringProperty("Player: " + player.actualHealthPoint + "hp"))
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

  val magicShield: Button = new Button {
    translateX = playerRepresentation.translateX.value - 50
    translateY = playerRepresentation.translateY.value - 20
    styleClass.add("magicShield")
    style="-fx-background-image: url('images/shield.png');"
    mouseTransparent = true
  }

  val physicShield: Button = new Button {
    translateX = if(player.isInstanceOf[User]) playerRepresentation.translateX.value  + 200 else playerRepresentation.translateX.value  - 100
    translateY = playerRepresentation.translateY.value + 60
    styleClass.add("physicShield")
    style = if(player.isInstanceOf[User]) "-fx-background-image: url('images/fshield.png');" else "-fx-background-image: url('images/fshield2.png');"
    mouseTransparent = true
  }

  val magicAttack: Button = new Button {
    styleClass.add("magicAttack")
    translateX = if(player.isInstanceOf[User]) playerRepresentation.translateX.value + 130 else playerRepresentation.translateX.value - 90
    translateY = playerRepresentation.translateY.value
    mouseTransparent = true
  }

  children = List(life,playerRepresentation, physicShield, magicShield, magicAttack)

  override def playAnimation(byVal: Double = 0, family: (Category, Type), action: EventHandler[ActionEvent]): Unit = family._1 match {
    case Category.Attack => attack(byVal, action, family._2)
    case Category.Defense => defense(action, family._2)
  }

  override def updateHP(action: EventHandler[ActionEvent]): Unit = {
    val ratio: Double = player.actualHealthPoint.toDouble / player.totalHealthPoint.toDouble
    if ( ratio != observableHealthPoint._1.value ) {
      damage()
      observableHealthPoint._1.set(if ( ratio > 0 ) ratio else 0)
      observableHealthPoint._2.set(if ( ratio > 0 ) "Player: " + player.actualHealthPoint + "hp" else "Player: 0hp")
      defeat(player.actualHealthPoint, action)
    }
  }

  private def defeat(hp: Double, action: EventHandler[ActionEvent]): Unit = hp match {
    case n if n <= 0 => TransitionFactory.fadeTransitionFactory(Duration(1000), this, action).play()
    case _ => ;
  }

  private def attack(byVal: Double, action: EventHandler[ActionEvent], cardType: Type): Unit = cardType match {
    case Type.Physic => TransitionFactory.translateTransitionFactory(Duration(200), playerRepresentation, action, byVal, 0, 2, autoReversible = true).play()
    case _ =>
      TransitionFactory.fadeTransitionFactory(Duration(150), magicAttack, TransitionFactory.DEFAULT_ON_FINISHED,1, 2, autoReversible = true).play()
      TransitionFactory.translateTransitionFactory(Duration(200), magicAttack, action, byVal, 0, 2, autoReversible = true).play()
  }

  private def defense(action: EventHandler[ActionEvent], cardType: Type): Unit = cardType match {
    case Type.Magic => TransitionFactory.fadeTransitionFactory(Duration(150), magicShield, action, 1, 2, autoReversible = true).play()
    case _ => TransitionFactory.fadeTransitionFactory(Duration(150), physicShield, action, 1, 2, autoReversible = true).play()
  }

  private def damage(): Unit = TransitionFactory.rotateTransitionFactory(Duration(20), playerRepresentation, TransitionFactory.DEFAULT_ON_FINISHED, 5, 20, autoReversible = true).play()
}

object BattlePlayerRepresentation {
  def apply(marginX: Double, marginY: Double, player:Player): BattlePlayerRepresentation = new BattlePlayerRepresentationImpl(marginX, marginY, player)
}
