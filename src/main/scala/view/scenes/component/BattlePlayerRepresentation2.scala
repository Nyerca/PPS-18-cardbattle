package view.scenes.component

import Utility.{GUIObjectFactory, TransitionFactory}
import javafx.beans.property.{SimpleDoubleProperty, SimpleStringProperty}
import javafx.event.{ActionEvent, EventHandler}
import model.{Category, Player2, Type}
import scalafx.Includes._
import scalafx.scene.control.{Button, Label, ProgressBar}
import scalafx.scene.layout.{Pane, StackPane}
import scalafx.util.Duration


trait BattlePlayerRepresentation2 extends Pane {

  protected val observableHealthPoint: (SimpleDoubleProperty, SimpleStringProperty) = (new SimpleDoubleProperty(player.actualHealthPoint.toDouble / player.totalHealthPoint.toDouble), new SimpleStringProperty(player.name + ": " + player.actualHealthPoint + "hp"))

  protected val playerRepresentation: Button = GUIObjectFactory.buttonFactory(0, 20, mouseTransparency = true, GUIObjectFactory.DEFAULT_ON_ACTION, "-fx-background-image: url(" + player.image + ");")("image")

  protected val life: StackPane = new StackPane {
    translateY = -10
    children = List(new ProgressBar {
      progress <== observableHealthPoint._1
      styleClass.add("life")
    }, new Label {
      styleClass.add("title")
      text <== observableHealthPoint._2
    })
  }

  protected val magicShield: Button = GUIObjectFactory.buttonFactory(playerRepresentation.translateX.value - 50, playerRepresentation.translateY.value - 20, mouseTransparency = true)( "magicShield")

  protected val magicAttack: Button = GUIObjectFactory.buttonFactory(
    playerRepresentation.translateX.value,
    playerRepresentation.translateY.value,
    mouseTransparency = true)("magicAttack")

  protected val physicShield: Button = GUIObjectFactory.buttonFactory(
    playerRepresentation.translateX.value,
    playerRepresentation.translateY.value + 60,
    mouseTransparency = true,
    GUIObjectFactory.DEFAULT_ON_ACTION)("physicShield")

  def marginX: Double

  def marginY: Double

  def player: Player2

  def playAnimation(byVal: Double = 0, family: (Category, Type), action: EventHandler[ActionEvent]): Unit = family._1 match {
    case Category.Attack =>
      attack(byVal, action, family._2)
    case Category.Defense => defense(action, family._2)
  }

  def updateHP(hp: Int): Unit = {
    if ( hp.toDouble / player.totalHealthPoint.toDouble != observableHealthPoint._1.value ) {
      observableHealthPoint._1.set(if ( hp > 0 ) hp.toDouble / player.totalHealthPoint.toDouble else 0)
      observableHealthPoint._2.set(if ( hp > 0 ) player.name + ": " + hp + "hp" else "Player: 0hp")
      checkDamageResult(hp)
    }
  }

  children = List(life,playerRepresentation, physicShield, magicShield, magicAttack)

  private def checkDamageResult(hp: Double): Unit = hp match {
    case n if n <= 0 =>
      playerRepresentation.style = "-fx-background-image: url('images/ghost.png'); -fx-pref-width:150; -fx-pref-height:150; -fx-background-size: 150 150;"
      life.opacity = 0
      TransitionFactory.translateTransitionFactory(Duration(2000), playerRepresentation, TransitionFactory.DEFAULT_ON_FINISHED, 0, -100).play()
    case _ => TransitionFactory.rotateTransitionFactory(Duration(20), playerRepresentation, TransitionFactory.DEFAULT_ON_FINISHED, 5, 20, autoReversible = true).play()
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
}

case class BattleUserRepresentation2(override val marginX: Double, override val marginY: Double, override val player: Player2) extends BattlePlayerRepresentation2 {

  translateX = marginX

  translateY = marginY

  physicShield.translateX = physicShield.translateX.value + 200

  physicShield.style = "-fx-background-image: url('images/fshield.png');"

  magicAttack.translateX = magicAttack.translateX.value + 130
}

case class BattleEnemyRepresentation2(override val marginX: Double, override val marginY: Double, override val player: Player2) extends BattlePlayerRepresentation2 {

  translateX = marginX

  translateY = marginY

  physicShield.translateX = physicShield.translateX.value - 100

  physicShield.style = "-fx-background-image: url('images/fshield2.png');"

  magicAttack.translateX = magicAttack.translateX.value - 90
}
