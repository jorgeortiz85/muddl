package com.foursquare.muddl

/**
 * A MutableRecord is a Record that keeps a map of mutations, or changes to one of its fields.
 *
 * It overrides hashCode and equals to use the system defaults.
 */
trait MutableRecord[T <: Record[T]] extends Record[T] { self: T =>
  protected def underlying: T with Record[T]
  protected var _mutations: scala.collection.mutable.Map[Field[T, _], FieldMutation[_]] =
    scala.collection.mutable.Map()

  // The only reasonable hashCode/equals semantics for mutable objects
  override def hashCode = System.identityHashCode(this)
  override def equals(that: Any) = this eq that.asInstanceOf[AnyRef]
  override def canEqual(that: Any) = this eq that.asInstanceOf[AnyRef]
}
