package view.scenes

trait ObservableScene {
  def update[A](model: A): Unit
}
