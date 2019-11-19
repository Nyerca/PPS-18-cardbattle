package model

import scala.language.postfixOps
import scala.util.Random

trait TypeCheckResult

object TypeCheckResult {
  case object AttackAttack extends TypeCheckResult
  case object AttackDefense extends TypeCheckResult
  case object DefenseAttack extends TypeCheckResult
  case object DefenseDefense extends TypeCheckResult
}

trait Game {
  def user: Player
  def enemy: Player
  var healthPointPlayer1: Double = user healthPoint
  var healthPointPlayer2: Double = enemy healthPoint
  var deckPlayer1: List[Card] = Random.shuffle(user battleDeck)
  var deckPlayer2: List[Card] = Random.shuffle(enemy battleDeck)
  def checkWinner(): Option[Player]
  def fight(card1: Card, card2: Card): (Option[Player],Option[Player])
  def reinsertCard(player: Player, card: Card): Unit
}

class GameImpl(override val user: Player, override val enemy: Player) extends Game {

  override def checkWinner(): Option[Player] = (healthPointPlayer1, healthPointPlayer2) match {
    case (h1, h2) if h1 > 0 && h2 <= 0 => Some(user)
    case (h1, _)  if h1 <= 0  => Some(enemy)
    case (_,_) => None
  }

  override def fight(card1: Card, card2: Card): (Option[Player], Option[Player]) = typeCheck(card1, card2) match {
    case TypeCheckResult.AttackAttack =>
      calculateDamage(card2.value, 0, enemy)
      calculateDamage(card1.value, 0, user)
      (Option(user), Option(enemy))
    case TypeCheckResult.AttackDefense =>
      calculateDamage(card1.value, if (card1.family._2 == card2.family._2) card2.value else 0, user)
      (Option(user), None)
    case TypeCheckResult.DefenseAttack =>
      calculateDamage(card2.value, if (card1.family._2 == card2.family._2) card1.value else 0, enemy)
      (None, Option(enemy))
    case _ => (None, None)
  }

  override def reinsertCard(player: Player, card: Card): Unit = player match {
    case _: User => deckPlayer1 = deckPlayer1.filter(cardNotToMove => cardNotToMove != card) :+ card
    case _ => deckPlayer2 = deckPlayer2.filter(cardNotToMove => cardNotToMove != card) :+ card
  }

  private def calculateDamage(val1: Double, val2: Double, player: Player): Unit = player match {
    case _: User => healthPointPlayer2 -= (val1 - val2)
    case _ => healthPointPlayer1 -= (val2 - val1)
  }

  private def typeCheck(card1: Card, card2: Card): TypeCheckResult = (card1.family._1, card2.family._1) match {
    case (Category.Attack, Category.Attack) => TypeCheckResult.AttackAttack
    case (Category.Attack, Category.Defense) => TypeCheckResult.AttackDefense
    case (Category.Defense, Category.Attack) => TypeCheckResult.DefenseAttack
    case _ => TypeCheckResult.DefenseDefense
  }
}

object Game {
  def apply(user: Player, enemy: Player): Game = new GameImpl(user, enemy)
}