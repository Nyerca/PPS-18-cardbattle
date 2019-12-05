package model
trait CellEvent {

}

case class Statue(_moneyRequired: Int) extends CellEvent {
  def moneyRequired: Int = _moneyRequired
}

trait MapEvent {
  def callEvent: CellEvent
  def playerRepresentation : PlayerRepresentation
}

class MapEventImpl (_cellEvent: CellEvent, _playerRepresentation: PlayerRepresentation) extends MapEvent {
  override def callEvent: CellEvent = _cellEvent
  override def playerRepresentation: PlayerRepresentation = _playerRepresentation
}

object MapEvent {
  def createMapEvent(cellEvent: CellEvent, playerRepresentation: PlayerRepresentation) : MapEvent = {
    new MapEventImpl(cellEvent, playerRepresentation)
  }
}

