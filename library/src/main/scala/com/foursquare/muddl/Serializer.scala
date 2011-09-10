package com.foursquare.muddl

trait Serializer {
  type Elem
  type Obj <: Elem
  type Arr <: Elem

  def serializeObj(fieldValues: Seq[(Field[_, _], Option[Elem])]): Obj
  def serializeArr[T](arr: Seq[T], f: T => Elem): Option[Arr]
}
