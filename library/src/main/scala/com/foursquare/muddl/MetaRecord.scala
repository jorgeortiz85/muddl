package com.foursquare.muddl

/**
 * A MetaRecord has information about a Record's fields.
 */
trait MetaRecord[T <: Record[T]] {
  def fields: Seq[Field[T, _]]
}
