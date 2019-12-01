package model

trait Player {
  def name: String
  def level: Int
  def image: String
  def battleDeck: List[Card]
  def healthPoint: Int
}

class User(override val name: String, override val image: String, var level: Int,  var missingExperience: Double, var allCards: List[Card], var healthPoint: Int) extends Player {
  var battleDeck: List[Card] = allCards
  def addExperience(exp: Int): Option[Int] = missingExperience - exp match {
    case 0 =>
      level += 1
      missingExperience = 5 * level - (exp - missingExperience)
      healthPoint += 5
      Some(level)
    case _ =>
      missingExperience -= exp
      None
  }

  def gainCard(card: Card): Option[Int] = allCards.find(c => c.name == card.name) match {
    case Some(c) => c.incrementCardNumber()
    case _ =>
      allCards = card :: allCards
      None
  }
}

case class Enemy(override val name: String, override val level: Int, override val image: String, override val battleDeck: List[Card], override val healthPoint: Int, experience: Int) extends Player

object Player {
  def userFactory(name: String, image: String, allCards: List[Card], level: Int = 1, healthPoint: Int = 30, missingExperience: Double = 4): User = new User(name, image, level, missingExperience, allCards, healthPoint)
  def enemyFactory(name: String, image: String, battleDeck: List[Card], level: Int , healthPoint: Int, experience: Int): Enemy = Enemy(name, level, image, battleDeck, healthPoint, experience)
}

