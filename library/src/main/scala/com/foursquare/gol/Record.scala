package com.foursquare.gol

import scala.runtime.ScalaRunTime

trait Record[T <: Record[T]] extends Product { self: T =>
  def meta: MetaRecord[T]

  override def productArity: Int = meta.fields.size
  override def productElement(n: Int): Any = meta.fields(n).get(this)
  override def productPrefix: String = this.getClass.getSimpleName
  override def toString = ScalaRunTime._toString(this)
  override def hashCode = ScalaRunTime._hashCode(this)
  override def equals(that: Any): Boolean = ScalaRunTime._equals(this, that)
}
