package com.github.rssh.sli

import language.experimental.macros
import scala.annotation.MacroAnnotation
import scala.reflect.macros.Context
import scala.reflect.macros.AnnotationContext
import scala.reflect.api._


class MacrohookA extends MacroAnnotation
{

  def transform = macro MacrohookA.transformImpl

}


object MacrohookA
{

  def transformImpl(c:AnnotationContext): c.Tree =
  {
    import c.universe._
    val t = c.annottee
    // TODO: may-be find all implicit of this type and apply sequentially
    //   or build combination.
    c.inferImplicitValue(typeOf[DefaultRewriter]) match {
       case EmptyTree =>
               //System.err.println("macro processor not found")
               t
       case p@_ =>
               val pe = c.Expr[DefaultRewriter](c.resetAllAttrs(p))
               val pv = c.eval(pe)
               pv.transformAImpl(c)
    }
  }
  

}
