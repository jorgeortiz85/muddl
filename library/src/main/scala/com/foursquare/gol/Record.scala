package com.foursquare.gol

trait Record[T <: Record[T]] { self: T =>
  def meta: MetaRecord[T]
  override def toString =
    meta.fields.map(f => f.shortName + ": " + f.get(this)).mkString("{", ", ", "}")
}
