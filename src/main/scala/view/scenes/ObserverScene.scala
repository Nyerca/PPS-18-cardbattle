package view.scenes

trait ObserverScene {
  def update[A](model: A): Unit
}
