package model

trait CellEvent
trait Statue extends CellEvent {
  def moneyRequired: Int
}
trait Pyramid extends CellEvent

object Statue {
  private final case class StatueImpl(moneyRequired: Int) extends Statue
  def apply(moneyRequired: Int): Statue = new StatueImpl(moneyRequired)
}
object Pyramid {
  private final case class PyramidImpl() extends Pyramid
  def apply(): CellEvent = new PyramidImpl()
}


trait MapEvent extends Serializable {
  def cellEvent: CellEvent
  def playerRepresentation : PlayerRepresentation
}



object MapEvent {
  private final case class MapEventImpl (cellEvent: CellEvent, playerRepresentation: PlayerRepresentation) extends MapEvent

  def apply(cellEvent: CellEvent, playerRepresentation: PlayerRepresentation) : MapEvent = new MapEventImpl(cellEvent, playerRepresentation)
}

