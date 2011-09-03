package com.foursquare.gol

private[gol] trait HasIdRecord[T <: HasIdRecord[T]] extends Record[T] { self: T =>
  def id: Any

  override def equals(that: Any) = that match {
    case that : HasIdRecord[_] => this.id == that.id
    case _ => false
  }

  override def hashCode(): Int = id.hashCode
}
