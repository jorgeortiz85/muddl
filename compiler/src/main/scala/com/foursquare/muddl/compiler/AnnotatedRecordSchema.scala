package com.foursquare.muddl.compiler

case class AnnotatedRecordSchema(
  schema: RecordSchema,
  className: String,
  packageName: String,
  fields: Seq[AnnotatedFieldSchema]
)
