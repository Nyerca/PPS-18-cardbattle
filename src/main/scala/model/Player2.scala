package model


trait Player2 extends Observable with Serializable {
  def name: String
  def level: Int
  def image: String
  def battleDeck: List[Card]
  def actualHealthPoint: Int
  def totalHealthPoint: Int
  def coins: Int
  def experience: Int
  def -(hp: Int): Player2
}

case class User2(override val name: String, override val image: String, override val level: Int, override val battleDeck: List[Card], override val totalHealthPoint: Int, override val actualHealthPoint: Int, override val experience: Int, override val  coins: Int, allCards: List[Card]) extends Player2 {

  def ++(enemy: Player2): User2 = {
    if(experience - enemy.experience <= 0) {
      copy(level = level + 1, totalHealthPoint = 5 + totalHealthPoint, actualHealthPoint = 5 + totalHealthPoint, experience = 3 * level - (experience - enemy.experience), coins = coins + enemy.coins)
    } else {
      copy(experience = experience - enemy.experience, coins = coins + enemy.coins)
    }
  }

  def ++(money: Int): User2 = if(money > 0) copy(coins = coins + money) else copy(actualHealthPoint = totalHealthPoint, coins = coins + money)

  def ++(card: Card): User2 = allCards.find(c => c.name == card.name) match {
    case Some(_) => copy(allCards = allCards.filter(c => c != card) :+ card.up, battleDeck = if(battleDeck.contains(card)) battleDeck.filter(x => x != card) :+ card.up else battleDeck)
    case _ => copy(allCards = card :: allCards)
  }

  def setDeck(deck: List[Card]): User2 = copy(battleDeck = deck)

  override def -(hp: Int): User2 = copy(actualHealthPoint = actualHealthPoint - hp)

}

case class Enemy2(override val name: String, override val image: String, override val level: Int, override val battleDeck: List[Card], override val totalHealthPoint: Int, override val actualHealthPoint: Int, override val experience: Int, override val coins: Int) extends Player2 with CellEvent2 {
  override def -(hp: Int): Enemy2 = copy(actualHealthPoint = actualHealthPoint - hp)
}
