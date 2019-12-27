package view.scenes.component

import utility.{GUIObjectFactory, TransitionFactory}
import javafx.event.{ActionEvent, EventHandler}
import model.{Card, Category}
import scalafx.scene.control.{Button, Label}
import scalafx.util.Duration

trait CardComponent {
  var card: Card = _
  def clickableCard: Button
  def cardName: Label
  def cardDamage: Label
  def cardLevel: Label
  def setCardInformation(card: Card): Unit
  def fadeOutAll(action: EventHandler[ActionEvent] = TransitionFactory.DEFAULT_ON_FINISHED): Unit
}

object CardComponent {
  private class CardComponentImpl(marginX: Double, marginY: Double, mouseTransparency: Boolean, action: EventHandler[ActionEvent]) extends CardComponent {

    override val clickableCard: Button = GUIObjectFactory.buttonFactory(marginX, marginY, mouseTransparency, action)("card")

    override val cardName: Label = GUIObjectFactory.labelFactory(marginX + 20, marginY + 11)

    override val cardDamage: Label = GUIObjectFactory.labelFactory(marginX + 20, marginY + 150)

    override val cardLevel: Label = GUIObjectFactory.labelFactory(marginX + 20, marginY + 132)

    override def setCardInformation(c: Card): Unit = {
      card = c
      fadeInAll()
      clickableCard.style = "-fx-background-image: url(" + card.image + ")"
      cardName.text = card.name
      cardDamage.text = if (card.family._1 == Category.Attack) card.family._2 + " " + card.family._1 + "\n\nDamage: " + card.value else card.family._2 + " " + card.family._1 + "\n\nDefense: " + card.value
      cardLevel.text = "Level: " + card.level
    }

    override def fadeOutAll(action: EventHandler[ActionEvent]): Unit = {
      TransitionFactory.fadeTransitionFactory(Duration(300), cardName, action).play()
      TransitionFactory.fadeTransitionFactory(Duration(300), clickableCard).play()
      TransitionFactory.fadeTransitionFactory(Duration(300), cardDamage).play()
      TransitionFactory.fadeTransitionFactory(Duration(300), cardLevel).play()
    }

    private def fadeInAll(): Unit = {
      clickableCard.opacity = 1
      cardName.opacity = 1
      cardDamage.opacity = 1
      cardLevel.opacity = 1
      clickableCard.mouseTransparent = mouseTransparency
    }
  }

  def apply(marginX: Double, marginY: Double, mouseTransparency: Boolean, action: EventHandler[ActionEvent]): CardComponent = new CardComponentImpl(marginX, marginY, mouseTransparency, action)
}