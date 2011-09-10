package com.foursquare.muddl

trait MetaRecord[T <: Record[T]] {
  def fields: Seq[Field[T, _]]
}
