package com.github.rssh.sli

import language.experimental.macros
import scala.annotation.MacroAnnotation
import scala.reflect.macros.Context
import scala.reflect.macros.AnnotationContext
import scala.reflect.api._


trait DefaultRewriter
{

  def transformAImpl(c: AnnotationContext): c.Tree = c.annottee

}
