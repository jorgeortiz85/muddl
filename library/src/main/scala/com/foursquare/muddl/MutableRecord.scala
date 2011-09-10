package com.foursquare.muddl

import java.lang.Object
import scala.ScalaObject

trait MutableRecord[T <: Record[T]] extends Record[T] { self: T =>
  protected var _mutations: scala.collection.mutable.Map[Field[T, _], FieldMutation[_]] =
    scala.collection.mutable.Map()

  // The only reasonable hashCode/equals semantics for mutable objects
  override def hashCode = System.identityHashCode(this)
  override def equals(that: Any) = this eq that.asInstanceOf[AnyRef]
  override def canEqual(that: Any) = this eq that.asInstanceOf[AnyRef]
}
