package Utility

import model.{Card, Category, Type}




object GameObjectFactory {
  def createCards(level: Int): List[Card] = List(Card("Fireball", "images/attack.png", (Category.Attack,Type.Magic), level),
    Card("Iceball", "images/attack.png", (Category.Attack,Type.Magic), level),
    Card("Attacco magico 3", "images/attack.png", (Category.Attack,Type.Magic), level),
    Card("Supersonic fist", "images/attack.png", (Category.Attack,Type.Physic), level),
    Card("Fire shield", "images/defense.png", (Category.Defense,Type.Magic), level),
    Card("Ice shield", "images/defense.png", (Category.Defense,Type.Magic), level),
    Card("Light shield", "images/defense.png", (Category.Defense,Type.Magic), level),
    Card("Physic shield", "images/defense.png", (Category.Defense,Type.Physic), level),
    Card("Scudo fisico 2", "images/defense.png", (Category.Defense,Type.Physic), level),
    Card("Scudo fisico 3", "images/defense.png", (Category.Defense,Type.Physic), level))
}
