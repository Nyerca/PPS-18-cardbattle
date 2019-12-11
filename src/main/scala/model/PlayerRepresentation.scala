package model

class PlayerRepresentation  (var position : RectangleCell, var url :  String) extends Serializable {
  override def toString: String = "Url: " + url + " Position: " + position
}
