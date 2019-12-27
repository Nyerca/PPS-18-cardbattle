package model

trait PlayerRepresentation extends Serializable {
  def position: RectangleCell
  def url: String
}

object PlayerRepresentation {

  private case class PlayerRepresentationImpl(var position : RectangleCell, var url :  String) extends PlayerRepresentation

  def apply(position : RectangleCell, url :  String) : PlayerRepresentation = PlayerRepresentationImpl(position, url)
}