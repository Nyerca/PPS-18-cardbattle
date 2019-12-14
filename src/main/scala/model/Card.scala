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

case class Card(name: String, image: String, level: Int, family: (Category,Type), value: Int, cardMissingForNextLevel: Int)

object Card {
  def ++(card: Card): Card = {
    if(card.cardMissingForNextLevel - 1 == 0) {
      card.copy(level = card.level + 1, value = card.value + 2, cardMissingForNextLevel = card.level + 1)
    } else {
      card.copy(cardMissingForNextLevel = card.cardMissingForNextLevel - 1)
    }
  }
}
