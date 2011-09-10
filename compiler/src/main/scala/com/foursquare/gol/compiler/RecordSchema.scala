package com.foursquare.gol.compiler

trait RecordSchema {
  def field[T : Manifest](number: Int, shortName: String): FieldSchema[T] =
    new FieldSchema[T](number, shortName, manifest[T], isOptional = true, isRepeated = false)
}
