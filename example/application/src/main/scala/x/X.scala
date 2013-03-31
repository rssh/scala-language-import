package x

import go.deffered._

object X
{

  def main(args:Array[String]): Unit =
  {
      Console.println("X.x")
      deffered{
        Console.println("this is deffered");
      }
      Console.println("Normal");
  }

}
