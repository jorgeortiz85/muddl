package com.foursquare.muddl

/**
 * A FieldMutation represents a change in the value of one of a Record's Fields.
 */
case class FieldMutation[F](oldValue: F, newValue: F)
