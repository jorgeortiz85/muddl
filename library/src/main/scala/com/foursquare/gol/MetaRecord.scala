package com.foursquare.gol

trait MetaRecord[T <: Record[T]] {
  def fields: Seq[Field[T, _]]
}
