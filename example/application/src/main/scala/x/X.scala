package x

import go.defer._

object X
{

  def main(args:Array[String]): Unit =
  {
    Console.println("X.x")
    defer{ Console.println("this is deferred"); }
    Console.println("Normal");
  }

}
