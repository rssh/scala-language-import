package com.github.rssh.sli

import scala.tools.nsc
import nsc._
import nsc.transform._
import plugins._

class MacrohookPlugin(val global: Global) extends Plugin
{


 val name="macrohook"
 val description = "insert macro hooks for ast transformation in imports"


 val components = List[PluginComponent](HookInsertComponent)

 object HookInsertComponent extends PluginComponent with Transform
 {
   override val runsAfter = List[String]("parser")
   override val runsBefore = List[String]("namer")

   val phaseName = name
   
   val global = MacrohookPlugin.this.global

   import global._

   protected def newTransformer(unit: CompilationUnit) = new HookInserter(unit)


   class HookInserter(unit: CompilationUnit) extends Transformer
   {
  
    def macroHookAnnotationTree = genSelectRootType("com","github","rssh","sli","MacrohookA")
    def macroHooktionTree = genSelectRootType("com","github","rssh","sli","MacrohookA")
    
    def preTransform(tree: Tree):Tree =
    {
     tree match {
       case ClassDef(mods, name, tparams, impl) => Annotated( macroHookAnnotationTree, tree )
       case ModuleDef(mods, name, impl) => Annotated( macroHookAnnotationTree, tree )
     }
    }

    override def transform(tree: Tree): Tree = {
      val t = preTransform(tree)
      super.transform(tree)
    }

    private def genSelectRootType(names: String*): Tree =
     genSelectType(Ident(newTermName("_root_")),names)


    private def genSelectType(prefix: Tree, names:Seq[String]): Tree =
     names match {
       case Seq(x) => Select(prefix,newTypeName(x))
       case Seq(x,r@_*) => genSelectType(Select(prefix,newTermName(x)),r)
     }

   }

 } 

}
