package com.foursquare.gol

trait MongoRecord[T <: MongoRecord[T]] extends HasIdRecord[T] { self: T => }
