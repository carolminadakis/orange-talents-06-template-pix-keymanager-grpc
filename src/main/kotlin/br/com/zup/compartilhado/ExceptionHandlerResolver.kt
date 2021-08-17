package br.com.zup.compartilhado

import br.com.zup.compartilhado.handlers.DefaultExceptionHandler
import javax.inject.Singleton

@Singleton
class ExceptionHandlerResolver(private val handlers: List<ExceptionHandler<*>>) {

    private var defaultHandler: ExceptionHandler<Exception> = DefaultExceptionHandler()

    // Podemos alterar o defaultHandler passando outro pelo construtor
    constructor(handlers: List<ExceptionHandler<Exception>>, defaultHanlder: ExceptionHandler<Exception>) : this(handlers) {
        this.defaultHandler = defaultHandler
    }

    fun resolve(e: Exception): ExceptionHandler<*> {
        // encontra o handler que sabe lidar com a exceção passada
        val foundHandlers = handlers.filter { it.supports(e) }

        // não deve ter mais de um Handler para a mesma Exceção
        if(foundHandlers.size > 1) {
            throw IllegalStateException("Too many handlers supporting the same exception '${e.javaClass.name}': $foundHandlers")
        }

        return foundHandlers.firstOrNull() ?: defaultHandler
    }
}