package model


import view.scenes.ObservableScene


abstract class Observable {
  private var observers: List[ObservableScene] = List()

  def addObserver(observer: ObservableScene): Unit = observers = observer :: observers

  def notifyObserver[A](model: A): Unit = observers.foreach(ob => ob.update(model))
}
