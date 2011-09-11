package com.foursquare.muddl

/**
 * This trait defines the basics of serialization. Serializer implementations must support
 * Objects, Arrays, and Elements.
 */
trait Serializer {
  type Elem
  type Obj <: Elem
  type Arr <: Elem

  def serializeObj(fieldValues: Seq[(Field[_, _], Option[Elem])]): Obj
  def serializeArr[T](arr: Seq[T], f: T => Elem): Option[Arr]
}
