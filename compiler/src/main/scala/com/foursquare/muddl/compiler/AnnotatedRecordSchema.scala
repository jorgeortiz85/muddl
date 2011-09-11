package com.foursquare.muddl.compiler

/**
 * This class gives a full description of a record schema.
 */
case class AnnotatedRecordSchema(
  schema: RecordSchema,
  className: String,
  packageName: String,
  fields: Seq[AnnotatedFieldSchema]
)
