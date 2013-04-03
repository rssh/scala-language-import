package copyfile

import java.io._
import go.defer._

object Main
{

  def main(args:Array[String]):Unit =
  {
   if (args.length < 3) {
     System.err.println("usage: copy in out");
   }
   copy(new File(args(1)), new File(args(2)))
  }
  
  def copy(inf: File, outf: File): Long =
  {
    val in = new FileInputStream(inf)
    defer{ in.close() }
    val out = new FileOutputStream(outf);
    defer{ out.close() }
    out.getChannel() transferFrom(in.getChannel(), 0, Long.MaxValue)
  }


}
