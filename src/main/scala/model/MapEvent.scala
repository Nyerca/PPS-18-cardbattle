package model

trait CellEvent
trait Statue extends CellEvent {
  def moneyRequired: Int
}
trait Pyramid extends CellEvent
trait Chest extends CellEvent{
  def money: Int
}

object Statue {
  private final case class StatueImpl(moneyRequired: Int) extends Statue
  def apply(moneyRequired: Int): Statue = StatueImpl(moneyRequired)
}
object Pyramid {
  private final case class PyramidImpl() extends Pyramid
  def apply(): CellEvent = PyramidImpl()
}
object Chest {
  private final case class ChestImpl(money: Int) extends Chest
  def apply(money: Int): Chest = ChestImpl(money)
}

trait MapEvent extends Serializable {
  def cellEvent: CellEvent
  def playerRepresentation : PlayerRepresentation
}

object MapEvent {
  private final case class MapEventImpl(cellEvent: CellEvent, playerRepresentation: PlayerRepresentation) extends MapEvent

  def apply(cellEvent: CellEvent, playerRepresentation: PlayerRepresentation) : MapEvent = MapEventImpl(cellEvent, playerRepresentation)
}

