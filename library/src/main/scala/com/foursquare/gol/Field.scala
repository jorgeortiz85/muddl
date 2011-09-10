package com.foursquare.gol

trait UntypedField {
  def number: Int
  def shortName: String
  def longName: String
  def optional: Boolean
}

case class Field[T <: Record[T], F](
  get: T => F,
  number: Int,
  shortName: String,
  longName: String,
  optional: Boolean,
  manifest: Manifest[F]) extends UntypedField
