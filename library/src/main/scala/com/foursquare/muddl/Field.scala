package com.foursquare.muddl

/**
 * UntypedField contains information about one of a Record's fields.
 */
trait UntypedField {
  def number: Int
  def shortName: String
  def longName: String
  def optional: Boolean
}

/**
 * A Field contains information about one of a Record's fields.
 *
 * A Field's type includes both the type of its Record and the type of the fields value.
 */
case class Field[T <: Record[T], F](
  get: T => F,
  number: Int,
  shortName: String,
  longName: String,
  optional: Boolean,
  manifest: Manifest[F]) extends UntypedField
