package model

import scala.language.postfixOps
import scala.util.Random

trait Game {
  def user: Player
  def enemy: Player
  var healthPointPlayer1: Double = user healthPoint
  var healthPointPlayer2: Double = enemy healthPoint
  var deckPlayer1: List[Card] = Random.shuffle(user battleDeck)
  var deckPlayer2: List[Card] = Random.shuffle(enemy battleDeck)
  def checkWinner(): Option[Player]
  def fight(userCard: Card, enemyCard: Card): (Option[Player],Option[Player])
  def reinsertCard(player: Player, card: Card): Unit
}

class GameImpl(override val user: Player, override val enemy: Player) extends Game {

  override def checkWinner(): Option[Player] = (healthPointPlayer1, healthPointPlayer2) match {
    case (h1, h2) if h1 > 0 && h2 <= 0 => Some(user)
    case (h1, _)  if h1 <= 0  => Some(enemy)
    case (_,_) => None
  }

  override def fight(userCard: Card, enemyCard: Card): (Option[Player], Option[Player]) = (userCard.family._1, enemyCard.family._1) match {
    case (Category.Attack, Category.Attack) =>
      healthPointPlayer1 -= enemyCard.value
      healthPointPlayer2 -= userCard.value
      (Option(user), Option(enemy))
    case (Category.Defense, Category.Defense) => (None, None)
    case (Category.Attack, Category.Defense) =>
      calculateDamage(userCard, enemyCard, enemy)
      (None, Some(enemy))
    case (_,_) =>
      calculateDamage(userCard, enemyCard, user)
      (Option(user), None)
  }

  override def reinsertCard(player: Player, card: Card): Unit = player match {
    case _: User => deckPlayer1 = deckPlayer1.filter(cardNotToMove => cardNotToMove != card) :+ card
    case _ => deckPlayer2 = deckPlayer2.filter(cardNotToMove => cardNotToMove != card) :+ card
  }

  private def calculateDamage(card1: Card, card2: Card, player: Player): Unit = {
    if (card1.family._2 == card2.family._2) {
      hitPlayer(player, Math.abs(card1.value - card2.value))
    } else {
      hitPlayer(player, Math.abs(card1.value))
    }
  }
  private def hitPlayer(player: Player, damage: Double): Unit = player match {
    case _: User => println("user " + damage); healthPointPlayer1 -= damage
    case _ => println("enemy " + damage); healthPointPlayer2 -= damage
  }
}

object Game {
  def apply(user: Player, enemy: Player): Game = new GameImpl(user, enemy)
}