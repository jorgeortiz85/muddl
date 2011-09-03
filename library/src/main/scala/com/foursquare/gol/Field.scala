package com.foursquare.gol

case class Field[T <: Record[T], F](
  get: T => F,
  number: Int,
  shortName: String,
  longName: String,
  optional: Boolean,
  manifest: Manifest[F])
