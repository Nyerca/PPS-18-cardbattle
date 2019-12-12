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

class User(override val name: String, override val image: String, var level: Int,  var experience: Int, var allCards: List[Card], var totalHealthPoint: Int, var actualHealthPoint: Int, var coins: Int) extends Player {
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

  def ->(card: Card): Option[Int] = allCards.find(c => c.name == card.name) match {
    case Some(c) => c.incrementCardNumber()
    case _ =>
      allCards = card :: allCards
      None
  }
}

case class Enemy(override val name: String, override val image: String, var battleDeck: List[Card], override val level: Int, override val totalHealthPoint: Int, var actualHealthPoint: Int, var experience: Int, override val coins: Int) extends Player with CellEvent

object Player {
  def userFactory(name: String, image: String, allCards: List[Card], level: Int = 1, healthPoint: Int = 10, missingExperience: Int = 1, coins: Int = 0): User = new User(name, image, level, missingExperience, allCards, healthPoint, healthPoint, coins)
  def enemyFactory(name: String, image: String, battleDeck: List[Card], level: Int, healthPoint: Int): Enemy = Enemy(name, image, battleDeck, level, healthPoint, healthPoint, level, 2 * level)
}

