package model


trait Player extends Observable with Serializable {

  var actualHealthPoint: Int
  def name: String
  def level: Int
  def image: String
  def battleDeck: List[Card]
  def totalHealthPoint: Int
  def coins: Int
  def experience: Int
  def -(hp: Int): Unit = actualHealthPoint -= hp
}

class User(override val name: String, override val image: String, var level: Int, var allCards: List[Card], var totalHealthPoint: Int, var actualHealthPoint: Int, var experience: Int, var  coins: Int) extends Player {
  var battleDeck: List[Card] = allCards
  def ++(enemy: Player): Unit = {
    coins += enemy.coins
    if ( experience - enemy.experience <= 0 ) {
      level += 1
      totalHealthPoint += 5
      actualHealthPoint = totalHealthPoint
      experience = 3 * level - (enemy.experience - experience)
    } else experience -= enemy.experience
    notifyObserver(this)
  }

  def ++(money: Int): Unit = {
    coins += money
    if(money < 0) actualHealthPoint = totalHealthPoint
    notifyObserver(this)
  }

  def ++(card: Card): Unit =  allCards.find(c => c.name == card.name) match {
    case Some(_) =>
      allCards = card.up :: allCards.filter(c => c != card)
      battleDeck = allCards.filter(c => battleDeck.contains(c))
    case _ => allCards = card :: allCards
  }

  override def -(hp: Int): Unit = super.-(hp); notifyObserver(this)

}

class Enemy(override val name: String, override val image: String, override val level: Int, override val battleDeck: List[Card], override val totalHealthPoint: Int,  var actualHealthPoint: Int, override val experience: Int, override val coins: Int) extends Player with CellEvent

object Player {
  def Enemy(name: String, image: String, level: Int, battleDeck: List[Card], totalHealthPoint: Int, actualHealthPoint: Int, exp: Int, coins: Int): Enemy = new Enemy(name, image, level , battleDeck, totalHealthPoint, actualHealthPoint, exp, coins)
  def User(name: String, image: String, level: Int, allCards: List[Card], totalHealthPoint: Int, actualHealthPoint: Int, exp: Int, coins: Int): User  = new User(name, image, level , allCards, totalHealthPoint, actualHealthPoint, exp, coins)
}