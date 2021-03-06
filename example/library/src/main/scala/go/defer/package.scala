package go

package object defer
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
      transformDef(c)(c.annottee)
    }

    def transformDef(c:Context)(x: c.Tree): c.Tree =
    {
      import c.universe._
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
         if (findDefer(c)(rhs)) {
             DefDef(mods, name, tparam, vparams, tpt, withDefer(c)(rhs))
         } else {
             x
         }
       case ValDef(mods, name, tpt, rhs) =>
         if (findDefer(c)(rhs)) {
            ValDef(mods, name, tpt, withDefer(c)(rhs))
         } else {
            x
         }
      }
    }


    def withDefer(c:Context)(x: c.Tree): c.Tree =
    {
     import c.universe._
     // TODO
     //   1. use fresh names.
     //   2. foreach must be in library [and think about suppressed exceptions ]
     Block(List(q"""val __d=collection.mutable.ArrayBuffer[()=>Any]()""",
               q"""@inline def defer(x: =>Any) = __d+=(()=>x)"""
              ),
          Try(
            x,
            Nil,
            q"""__d.foreach(_.apply)"""
          )
         )
    } 

    def findDeferInList(c:Context)(x: List[c.Tree]): Boolean = 
      x.find(findDefer(c)).isDefined

    def findDefer(c:Context)(x: c.Tree): Boolean = 
    {
      @inline def find(t: c.Tree) = findDefer(c)(t)
      @inline def findl(l: List[c.Tree]) = findDeferInList(c)(l)
   
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
              case Ident(TermName("defer")) => true
              case _ => find(fun) || findl(args)
            }
        case Select(qualifier, name) => find(qualifier)
        case Annotated(annot, arg) => find(arg)
        case _ => false
      }
    }

  }

}

