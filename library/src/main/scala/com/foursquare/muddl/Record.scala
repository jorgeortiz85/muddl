package com.foursquare.muddl

import scala.runtime.ScalaRunTime

/**
 * A Record is very similar to a case class.
 *
 * Like a case class, it implements Product (including productArity, productElement, and productPrefix).
 * Like a case class, it implements toString, hashCode, and equals.
 * In addition, it also references a MetaRecord.
 */
trait Record[T <: Record[T]] extends Product { self: T =>
  def meta: MetaRecord[T]

  override def productArity: Int = meta.fields.size
  override def productElement(n: Int): Any = meta.fields(n).get(this)
  override def productPrefix: String = this.getClass.getSimpleName
  // override def toString = ScalaRunTime._toString(this)
  override def toString = meta.fields.map(f => f.longName + "=" + f.get(this)).mkString(productPrefix + "(", ", ", ")")
  override def hashCode = ScalaRunTime._hashCode(this)
  override def equals(that: Any): Boolean = ScalaRunTime._equals(this, that)
}
