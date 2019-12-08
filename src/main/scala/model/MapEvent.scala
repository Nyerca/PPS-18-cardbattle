package model
trait CellEvent {

}

case class Statue(_moneyRequired: Int) extends CellEvent {
  def moneyRequired: Int = _moneyRequired
}
case class Pyramid() extends CellEvent {

}

trait MapEvent extends Serializable {
  def callEvent: CellEvent
  def playerRepresentation : PlayerRepresentation
  def playerRepresentation_(pRep : PlayerRepresentation):Unit
}

class MapEventImpl (_cellEvent: CellEvent,var _playerRepresentation: PlayerRepresentation) extends MapEvent {
  override def callEvent: CellEvent = _cellEvent
  override def playerRepresentation: PlayerRepresentation = _playerRepresentation
  override def playerRepresentation_(pRep : PlayerRepresentation):Unit = _playerRepresentation = pRep
}

object MapEvent {
  def createMapEvent(cellEvent: CellEvent, playerRepresentation: PlayerRepresentation) : MapEvent = {
    new MapEventImpl(cellEvent, playerRepresentation)
  }
}

