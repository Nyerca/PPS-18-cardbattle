package model

trait Player {
  def name: String
  def level: Int
  def image: String
  def battleDeck: List[Card]
  def healthPoint: Int
}

class User(override val name: String, override val image: String, var level: Int,  var missingExperience: Double, var allCards: List[Card], var healthPoint: Int) extends Player {
  var battleDeck = allCards
  def addExperience(exp: Int): Unit = missingExperience - exp match {
    case 0 =>
      level += 1
      missingExperience = 5 * level - (exp - missingExperience)
      healthPoint += 5
    case _ => missingExperience -= exp
  }

  def gainCard(card: Card): Unit = allCards.find(c => c.name == card.name) match {
    case Some(c) => c.incrementCardNumber()
    case _ => allCards = card :: allCards
  }
}

case class Enemy(override val name: String, override val level: Int, override val image: String, override val battleDeck: List[Card], override val healthPoint: Int) extends Player

object Player {
  def userFactory(name: String, image: String, allCards: List[Card], level: Int = 1, healthPoint: Int = 30, missingExperience: Double = 4): User = new User(name, image, level, missingExperience, allCards, healthPoint)
  def enemyFactory(name: String, image: String, battleDeck: List[Card], level: Int = 1, healthPoint: Int): Enemy = Enemy(name, level, image, battleDeck, healthPoint)
}

