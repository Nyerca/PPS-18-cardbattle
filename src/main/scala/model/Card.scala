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

case class Card(name: String, image: String, level: Int, family: (Category,Type), value: Int, cardMissingForNextLevel: Int) {
  def up: Card = {
    if(cardMissingForNextLevel - 1 == 0) {
      copy(level = level + 1, value = value + 2, cardMissingForNextLevel = level + 1)
    } else {
      copy(cardMissingForNextLevel = cardMissingForNextLevel - 1)
    }
  }
}

