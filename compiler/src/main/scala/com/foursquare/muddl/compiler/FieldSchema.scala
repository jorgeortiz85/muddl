package com.foursquare.muddl.compiler

/**
 * This class describes a field in a schema.
 * Fields must have a number, a short name, and a type.
 * Fields can be optional, required, or repeated.
 */
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
