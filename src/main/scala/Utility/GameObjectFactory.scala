package Utility

import model.{Card, Category, ConcreteCard, Type}

object GameObjectFactory {
  def createCards(level: Int): List[Card] = List(ConcreteCard("Fireball", "images/mattack.png", level, (Category.Attack,Type.Magic), 2 * level, level),
    ConcreteCard("Iceball", "images/mattack.png", level, (Category.Attack,Type.Magic), 2 * level, level),
    ConcreteCard("Oblivion hole", "images/mattack.png", level, (Category.Attack,Type.Magic), 2 * level, level),
    ConcreteCard("Energetic wave", "images/mattack.png", level, (Category.Attack,Type.Magic), 2 * level, level),
    ConcreteCard("Supersonic fist", "images/attack.png", level, (Category.Attack,Type.Magic), 2 * level, level),
    ConcreteCard("Lethal whiplash", "images/attack.png", level, (Category.Attack,Type.Magic), 2 * level, level),
    ConcreteCard("Rotating stock", "images/attack.png", level, (Category.Attack,Type.Magic), 2 * level, level),
    ConcreteCard("Dragon shot", "images/attack.png", level, (Category.Attack,Type.Magic), 2 * level, level),
    ConcreteCard("Fire shield", "images/mdefense.png", level, (Category.Attack,Type.Magic), 2 * level, level),
    ConcreteCard("Enchanted circle", "images/mdefense.png", level, (Category.Attack,Type.Magic), 2 * level, level),
    ConcreteCard("Light barrier", "images/mdefense.png", level, (Category.Attack,Type.Magic), 2 * level, level),
    ConcreteCard("Iron gate", "images/defense.png", level, (Category.Attack,Type.Magic), 2 * level, level),
    ConcreteCard("Rock shield", "images/defense.png", level, (Category.Attack,Type.Magic), 2 * level, level),
    ConcreteCard("Bone barrier", "images/defense.png", level, (Category.Attack,Type.Magic), 2 * level, level))
}
