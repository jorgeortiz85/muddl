package com.foursquare.gol

trait MutableRecord[T <: Record[T]] {
  protected var _mutations: scala.collection.mutable.Map[Field[T, _], FieldMutation[_]] =
    scala.collection.mutable.Map()
}
