package br.com.zup.compartilhado

import io.micronaut.aop.Around
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.*

@MustBeDocumented
@Retention(RUNTIME)
@Target(CLASS, FIELD, TYPE, FILE, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
@Around
annotation class ErrorHandler