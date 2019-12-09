package model


abstract class Monoid[A] {
  def add(x: A, y: A): A
  def unit: A
}

object Monoid {
  implicit val cellMonoid: Monoid[Double] = new Monoid[Double] {
    def add(pos: Double, traslation: Double): Double = pos - traslation - pos % 200
    def unit: Double = 0
  }

  implicit val cardMonoid: Monoid[Card] = new Monoid[Card] {
    override def add(x: Card, y: Card): Card = x ++ y
    override def unit: Card = ConcreteCard("", "", 1, null, 2, 2)
  }

  def sum[A](a1: A, a2: A)(implicit m: Monoid[A]): A =
    m.add(a1, a2)
}