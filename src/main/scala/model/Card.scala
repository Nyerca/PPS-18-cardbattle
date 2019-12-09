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

trait Card  {
  def level: Int
  def value: Int
  def name: String
  def image: String
  def family: (Category, Type)
  def cardMissingForNextLevel: Int
  def ++(card: Card): Card
}

case class ConcreteCard(override val name: String, override val image: String, override val level: Int, override val family: (Category,Type), override val value: Int, override val cardMissingForNextLevel: Int) extends Serializable with Card {
  override def ++(card: Card): Card = {
    if (cardMissingForNextLevel - 1 == 0) {
      ConcreteCard(name, image, level + 1, family, value + 3, level + 1)
    } else {
      ConcreteCard(name, image, level, family, value, cardMissingForNextLevel - 1)
    }
  }
}

