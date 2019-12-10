package Utility

object UrlFactory {
  def getParameters(top: Boolean, right: Boolean, bottom: Boolean, left: Boolean): (String, Int) = (top,right,bottom,left) match {
    case(true,true,true,true) => ("/roads/4road.png", 0)
    case(true,true,false,true) => ("/roads/3road.png", 180)
    case(true, true, true, false) => ("/roads/3road.png", 270)
    case(true, false, true, true) => ("/roads/3road.png", 90)
    case(false, true, true, true) => ("/roads/3road.png", 0)
    case(true, false, true, false) => ("/roads/2roadLine.png", 90)
    case(false, true, false, true) => ("/roads/2roadLine.png", 0)
    case(true, true, false, false) => ("/roads/2road.png", 270)
    case(true, false, false, true) => ("/roads/2road.png", 180)
    case(false, true, true, false) => ("/roads/2road.png", 0)
    case(false, false, true, true) => ("/roads/2road.png", 90)
    case(false, false, true, false) => ("/roads/1road.png", 90)
    case(true, false, false, false) => ("/roads/1road.png", 270)
    case(false, true, false, false) => ("/roads/1road.png", 0)
    case (false, false, false, true) => ("/roads/1road.png", 180)
    case _ => ("", 0)
  }
}


