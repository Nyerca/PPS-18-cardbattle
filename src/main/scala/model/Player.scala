package model

trait Player extends Serializable {
  def name: String
  def level: Int
  def image: String
  var battleDeck: List[Card]
  var actualHealthPoint: Int
  def coins: Int
  def totalHealthPoint: Int
  def experience: Int
}

class User(override val name: String, override val image: String, var allCards: List[Card], var level: Int, var totalHealthPoint: Int, var actualHealthPoint: Int, var experience: Int, var coins: Int) extends Player {
  var battleDeck: List[Card] = allCards
  def ++(enemy: Enemy): Option[Int] = {
    experience -= enemy.experience
    coins += enemy.coins
    if (experience <= 0) {
      level += 1
      totalHealthPoint += 5
      actualHealthPoint = totalHealthPoint
      Some(level)
    } else {
      None
    }
  }

  def ->(card: Card): Option[Card] = allCards.find(c => c.name == card.name) match {
    case Some(c) =>
      val newCard: Card = c.up
      updateDecks(c, newCard)
      Some(newCard)
    case _ =>
      allCards = card :: allCards
      None
  }

  private def updateDecks(oldCard: Card, newCard: Card): Unit = {
    allCards = allCards.filter(c => c != oldCard) :+ newCard
    battleDeck = allCards.filter(card => battleDeck.map(c => c.name).contains(card.name))
  }

}

case class Enemy(override val name: String, override val image: String, var battleDeck: List[Card], override val level: Int, override val totalHealthPoint: Int, var actualHealthPoint: Int, var experience: Int, override val coins: Int) extends Player with CellEvent

object Player {
  def apply(name: String, image: String, allCards: List[Card], level: Int = 1, healthPoint: Int = 10, missingExperience: Int = 1, coins: Int = 0): Player = coins match {
    case 0 => new User(name, image, allCards, level, healthPoint, healthPoint, missingExperience, coins)
    case _ => Enemy(name, image, allCards, level, healthPoint, healthPoint, level, level)
  }
}

