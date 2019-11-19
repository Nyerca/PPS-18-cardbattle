package model

trait Type

trait Category

object Category {
  case object Attack extends Category
  case object Defense extends Category
}

object Type {
  case object Magic extends Type
  case object Physic extends Type
}


trait Card {
  def name: String
  def image: String
  def level: Int
  def family: (Category, Type)
  def value: Double
  def cardMissingForNextLevel: Int
  def incrementCardNumber(): Unit
}

case class CardImpl(override val name: String, override val image: String, var level: Int, override val family: (Category,Type), var value: Double, var cardMissingForNextLevel: Int) extends Card {
  override def incrementCardNumber(): Unit = cardMissingForNextLevel - 1 match {
    case 0 =>
      level += 1
      value += 0.3
      cardMissingForNextLevel = 2 * level
    case _ => cardMissingForNextLevel -= 1
  }
}

object Card {
  def apply(name: String, image: String, family: (Category,Type), level: Int = 1, value: Double = 0.3, cardMissingForNextLevel: Int = 2): Card = CardImpl(name, image, level, family, value, cardMissingForNextLevel)
}
