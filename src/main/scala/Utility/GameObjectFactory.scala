package Utility

import model.{Card, Category, Type}




object GameObjectFactory {
  def createCards(level: Int): List[Card] = List(Card("Fireball", "fireball.png", (Category.Attack,Type.Magic), level),
    Card("Iceball", "iceball.png", (Category.Attack,Type.Magic), level),
    Card("Attacco magico 3", "iceball.png", (Category.Attack,Type.Magic), level),
    Card("Supersonic fist", "ariete.png", (Category.Attack,Type.Physic), level),
    Card("Fire shield", "magicShield.png", (Category.Defense,Type.Magic), level),
    Card("Ice shield", "magicShield.png", (Category.Defense,Type.Magic), level),
    Card("Light shield", "magicShield.png", (Category.Defense,Type.Magic), level),
    Card("Physic shield", "physicShield.png", (Category.Defense,Type.Physic), level),
    Card("Scudo fisico 2", "physicShield.png", (Category.Defense,Type.Physic), level),
    Card("Scudo fisico 3", "physicShield.png", (Category.Defense,Type.Physic), level))
}
