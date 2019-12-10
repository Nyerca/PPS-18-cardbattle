package Utility

import model.{Card, Category, Type}

object GameObjectFactory {
  def createCards(level: Int): List[Card] = List(Card("Fireball", "images/mattack.png", level, (Category.Attack,Type.Magic), 2 + 2 * (level - 1), level),
    Card("Iceball", "images/mattack.png", level, (Category.Attack,Type.Magic), 2 + 2 * (level - 1), level),
    Card("Oblivion hole", "images/mattack.png", level, (Category.Attack,Type.Magic), 2 + 2 * (level - 1), level),
    Card("Energetic wave", "images/mattack.png", level, (Category.Attack,Type.Magic), 2 + 2 * (level - 1), level),
    Card("Supersonic fist", "images/attack.png", level, (Category.Attack,Type.Physic), 2 + 2 * (level - 1), level),
    Card("Lethal whiplash", "images/attack.png", level, (Category.Attack,Type.Physic), 2 + 2 * (level - 1), level),
    Card("Rotating stock", "images/attack.png", level, (Category.Attack,Type.Physic), 2 + 2 * (level - 1), level),
    Card("Dragon shot", "images/attack.png", level, (Category.Attack,Type.Physic), 2 + 2 * (level - 1), level),
    Card("Fire shield", "images/mdefense.png", level, (Category.Defense,Type.Magic), 2 + 2 * (level - 1), level),
    Card("Enchanted circle", "images/mdefense.png" ,level,  (Category.Defense,Type.Magic), 2 + 2 * (level - 1), level),
    Card("Light barrier", "images/mdefense.png", level, (Category.Defense,Type.Magic), 2 + 2 * (level - 1), level),
    Card("Iron gate", "images/defense.png", level, (Category.Defense,Type.Physic), 2 + 2 * (level - 1), level),
    Card("Rock shield", "images/defense.png", level, (Category.Defense,Type.Physic), 2 + 2 * (level - 1), level),
    Card("Bone barrier", "images/defense.png", level, (Category.Defense,Type.Physic), 2 + 2 * (level - 1), level))
}
