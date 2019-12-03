package Utility

import model.{Card, Category, Type}

object GameObjectFactory {
  def createCards(level: Int): List[Card] = List(Card("Fireball", "images/mattack.png", (Category.Attack,Type.Magic), level),
    Card("Iceball", "images/mattack.png", (Category.Attack,Type.Magic), level),
    Card("Oblivion hole", "images/mattack.png", (Category.Attack,Type.Magic), level),
    Card("Energetic wave", "images/mattack.png", (Category.Attack,Type.Magic), level),
    Card("Supersonic fist", "images/attack.png", (Category.Attack,Type.Physic), level),
    Card("Lethal whiplash", "images/attack.png", (Category.Attack,Type.Physic), level),
    Card("Rotating stock", "images/attack.png", (Category.Attack,Type.Physic), level),
    Card("Dragon shot", "images/attack.png", (Category.Attack,Type.Physic), level),
    Card("Fire shield", "images/mdefense.png", (Category.Defense,Type.Magic), level),
    Card("Enchanted circle", "images/mdefense.png", (Category.Defense,Type.Magic), level),
    Card("Light barrier", "images/mdefense.png", (Category.Defense,Type.Magic), level),
    Card("Iron gate", "images/defense.png", (Category.Defense,Type.Physic), level),
    Card("Rock shield", "images/defense.png", (Category.Defense,Type.Physic), level),
    Card("Bone barrier", "images/defense.png", (Category.Defense,Type.Physic), level))
}
