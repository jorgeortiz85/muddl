package com.foursquare.gol

import java.lang.reflect.Method

object Reflection {
  def annotateRecord(schema: RecordSchema): AnnotatedRecordSchema = {
    val klass = schema.getClass
    val packageName = klass.getPackage.getName
    val className = klass.getSimpleName
    val fields = annotateFields(schema)(klass.getMethods)
    AnnotatedRecordSchema(schema, className, packageName, fields)
  }

  def annotateFields(schema: RecordSchema)(methods: Seq[Method]): Seq[AnnotatedFieldSchema] = {
    // TODO(jorge): Do some validation on fields. Reserved words, valid characters, etc.
    def isField(method: Method): Boolean =
      classOf[FieldSchema[_]].isAssignableFrom(method.getReturnType) && method.getParameterTypes.size == 0

    Vector() ++ methods.filter(isField _).map(annotateField(schema) _).sortBy(_.number)
  }

  def annotateField(schema: RecordSchema)(method: Method): AnnotatedFieldSchema = {
    val field = method.invoke(schema).asInstanceOf[FieldSchema[_]]
    AnnotatedFieldSchema(field, method.getName)
  }

  def allSchema: Seq[RecordSchema] = error("synthetic method not defined by compiler plugin")
}
