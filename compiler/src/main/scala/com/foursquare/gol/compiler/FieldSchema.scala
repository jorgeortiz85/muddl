package com.foursquare.gol

case class FieldSchema[T](
  number: Int,
  shortName: String,
  manifest: Manifest[T],
  isOptional: Boolean
) {
  def required_! : FieldSchema[T] = this.copy(isOptional = false)
}
