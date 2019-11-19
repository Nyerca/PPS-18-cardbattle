package model

trait Player {
  def name: String
  def level: Int
  def image: String
  def battleDeck: List[Card]
  def healthPoint: Double
}

class User(override val name: String, override val image: String, var level: Int,  var missingExperience: Double, var battleDeck: List[Card], var allCards: List[Card], var healthPoint: Double) extends Player {

  def addExperience(exp: Int): Unit = missingExperience - exp match {
    case 0 =>
      level += 1
      missingExperience = 5 * level - (exp - missingExperience)
    case _ => missingExperience -= exp
  }

  def gainCard(card: Card): Unit = allCards.find(c => c.name == card.name) match {
    case Some(c) => c.incrementCardNumber()
    case _ => allCards = card :: allCards
  }
}

case class Enemy(override val name: String, override val level: Int, override val image: String, override val battleDeck: List[Card], override val healthPoint: Double) extends Player

object Player {
  def UserFactory(name: String, image: String, allCards: List[Card] = List(), battleDeck: List[Card] = List(), level: Int = 1, healthPoint: Double = 3, missingExperience: Double = 4): User = new User(name, image, level, missingExperience, battleDeck, allCards, healthPoint)
  def EnemyFactory(name: String, image: String, battleDeck: List[Card], level: Int = 1, healthPoint: Double = 3): Enemy = Enemy(name, level, image, battleDeck, healthPoint)
}


