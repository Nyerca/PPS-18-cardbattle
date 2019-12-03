package model

trait Player {
  def name: String
  def level: Int
  def image: String
  def battleDeck: List[Card]
  def healthPoint: Int
  def experience: Int
}

class User(override val name: String, override val image: String, var level: Int,  var experience: Int, var allCards: List[Card], var healthPoint: Int, var coins: Int) extends Player {
  var battleDeck: List[Card] = allCards
  def addExperience(exp: Int): Option[Int] = {
    experience -= exp
    if (experience <= 0) {
      level += 1
      healthPoint += 5
      Some(level)
    } else {
      None
    }
  }

  def gainCard(card: Card): Option[Int] = allCards.find(c => c.name == card.name) match {
    case Some(c) => c.incrementCardNumber()
    case _ =>
      allCards = card :: allCards
      None
  }
}

case class Enemy(override val name: String, override val level: Int, override val image: String, override val battleDeck: List[Card], override val healthPoint: Int, var experience: Int, reward: Int) extends Player

object Player {
  def userFactory(name: String, image: String, allCards: List[Card], level: Int = 1, healthPoint: Int = 10, missingExperience: Int = 1, coins: Int = 0): User = new User(name, image, level, missingExperience, allCards, healthPoint, coins)
  def enemyFactory(name: String, image: String, battleDeck: List[Card], level: Int , healthPoint: Int): Enemy = Enemy(name, level, image, battleDeck, healthPoint, level, 2 * level)
}

