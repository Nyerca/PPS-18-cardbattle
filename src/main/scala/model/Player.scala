package model


trait Player extends Observable with Serializable {
  def name: String
  def level: Int
  def image: String
  def battleDeck: List[Card]
  def actualHealthPoint: Int
  def totalHealthPoint: Int
  def coins: Int
  def experience: Int
  def -(hp: Int): Player
}

case class User(override val name: String, override val image: String, override val level: Int, override val battleDeck: List[Card], override val totalHealthPoint: Int, override val actualHealthPoint: Int, override val experience: Int, override val  coins: Int, allCards: List[Card]) extends Player {

  def ++(enemy: Player): User = {
    if(experience - enemy.experience <= 0) {
      notifyObserver(copy(level = level + 1, totalHealthPoint = 5 + totalHealthPoint, actualHealthPoint = 5 + totalHealthPoint, experience = 3 * level - (experience - enemy.experience), coins = coins + enemy.coins), true)
      copy(level = level + 1, totalHealthPoint = 5 + totalHealthPoint, actualHealthPoint = 5 + totalHealthPoint, experience = 3 * level - (experience - enemy.experience), coins = coins + enemy.coins)
    } else {
      notifyObserver(copy(experience = experience - enemy.experience, coins = coins + enemy.coins), false)
      copy(experience = experience - enemy.experience, coins = coins + enemy.coins)
    }
  }

  def ++(money: Int): User = if(money > 0) {
    notifyObserver(copy(coins = coins + money), false)
    copy(coins = coins + money)
  } else {
    notifyObserver(copy(actualHealthPoint = totalHealthPoint, coins = coins + money), false)
    copy(actualHealthPoint = totalHealthPoint, coins = coins + money)
  }

  def ++(card: Card): User = allCards.find(c => c.name == card.name) match {
    case Some(_) => copy(allCards = allCards.filter(c => c != card) :+ card.up, battleDeck = if(battleDeck.contains(card)) battleDeck.filter(x => x != card) :+ card.up else battleDeck)
    case _ => copy(allCards = card :: allCards)
  }

  def setDeck(deck: List[Card]): User = copy(battleDeck = deck)

  override def -(hp: Int): User = {
    notifyObserver(copy(actualHealthPoint = actualHealthPoint - hp), false)
    copy(actualHealthPoint = actualHealthPoint - hp)
  }

}

case class Enemy(override val name: String, override val image: String, override val level: Int, override val battleDeck: List[Card], override val totalHealthPoint: Int, override val actualHealthPoint: Int, override val experience: Int, override val coins: Int) extends Player with CellEvent {
  override def -(hp: Int): Enemy = copy(actualHealthPoint = actualHealthPoint - hp)
}
