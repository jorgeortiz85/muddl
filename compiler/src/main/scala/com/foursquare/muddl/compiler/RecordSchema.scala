package com.foursquare.muddl.compiler

/**
 * Inherit from this class to define a schema.
 */
trait RecordSchema {
  /**
   * Call this method to define a field in this schema. A field type, a field number, and
   * a field short name are all required.
   */
  def field[T : Manifest](number: Int, shortName: String): FieldSchema[T] =
    new FieldSchema[T](number, shortName, manifest[T], isOptional = true, isRepeated = false)
}
