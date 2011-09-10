package com.foursquare.gol.compiler

case class FieldSchema[T](
  number: Int,
  shortName: String,
  manifest: Manifest[T],
  isOptional: Boolean,
  isRepeated: Boolean
) {
  def required_! : FieldSchema[T] = this.copy(isOptional = false)
  def repeated : FieldSchema[T] = this.copy(isRepeated = true)
}
