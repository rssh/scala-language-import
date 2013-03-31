package go


package object deffered
{
  import language.experimental.macros
  import scala.annotation.MacroAnnotation
  import scala.reflect.macros.Context
  import scala.reflect.macros.AnnotationContext
  import scala.reflect.api._

  import com.github.rssh.sli._

  implicit object GoDefaultRewriter extends DefaultRewriter
  {

    override def transformAImpl(c:AnnotationContext): c.Tree =
    {
      System.err.println("Go.annotationImpl")
      transformDef(c)(c.annottee)
    }

    override def transformFImpl(c:Context)(x: c.Tree): c.Tree =
    {
      System.err.println("in Go.transformImpl, x="+x);
      if (findDeffered(c)(x)) {
         withDeffered(c)(x)
      } else {
       x
      }
    }


    def transformDef(c:Context)(x: c.Tree): c.Tree =
    {
      import c.universe._
      System.err.println("raw, x="+showRaw(x))
      x match {
       case ClassDef(mods,name,tparams,
                         Template(parents,self,body)) => 
              ClassDef(mods,name,tparams,
                         Template(parents, self, body map (transformDef(c)(_))))
       case PackageDef(pid,stats) => 
               PackageDef(pid, stats map (transformDef(c)(_)))
       case ModuleDef(mods,name,
                         Template(parents, self, body)) => 
               ModuleDef(mods,name, 
                         Template(parents, self, body map (transformDef(c)(_))))
       case DefDef(mods, name, tparam, vparams, tpt, rhs) =>
         if (findDeffered(c)(rhs)) {
             DefDef(mods, name, tparam, vparams, tpt, withDeffered(c)(rhs))
         } else {
             x
         }
       case ValDef(mods, name, tpt, rhs) =>
         if (findDeffered(c)(rhs)) {
            ValDef(mods, name, tpt, withDeffered(c)(rhs))
         } else {
            x
         }
      }
    }


    def withDeffered(c:Context)(x: c.Tree): c.Tree =
    {
     import c.universe._
     Block(List(q"""val __d=collection.mutable.ArrayBuffer[()=>Any]()""",
               q"""@inline def deffered(x: =>Any) = __d+=(()=>x)"""
              ),
          Try(
            x,
            Nil,
            q"""__d.foreach(_.apply)"""
          )
         )
    } 

    def findDefferedInList(c:Context)(x: List[c.Tree]): Boolean = 
      x.find(findDeffered(c)).isDefined

    def findDeffered(c:Context)(x: c.Tree): Boolean = 
    {
      @inline def find(t: c.Tree) = findDeffered(c)(t)
      @inline def findl(l: List[c.Tree]) = findDefferedInList(c)(l)
   
      import c.universe._
      x match {
        case ClassDef(_,_,_,_) => false
        case ModuleDef(_,_,_) => false
        case ValDef(_,_,tpt,rhs) => find(rhs)
        case x: DefDef => false
        case x: TypeDef => false
        case LabelDef(_,_,rhs) => find(rhs)
        case Block(stats, expr) => findl(stats) || find(expr)
        case Match(selector, cases) =>  find(selector) || findl(cases)
        case CaseDef(pat, guard, body) => find(body)
        case Alternative(trees) => false // impossible
        case Function(vparams, body) => find(body)
        case Assign(lhs, rhs) => find(lhs) || find(rhs)
        case AssignOrNamedArg(lhs, rhs) =>  find(rhs)
        case If(cond, thenp, elsep) =>  find(cond) || find(thenp) || find(elsep)
        case Return(expr) => find(expr)
        case Try(block, catches, finalizer) => find(block) || findl(catches) || find(finalizer)
        case Typed(expr, tpt) => find(expr)
        case Apply(fun, args) =>
            fun match {
              case Ident(TermName("deffered")) => true
              case _ => find(fun) || findl(args)
            }
        case Select(qualifier, name) => find(qualifier)
        case Annotated(annot, arg) => find(arg)
        case _ => false
      }
    }

  }

}

