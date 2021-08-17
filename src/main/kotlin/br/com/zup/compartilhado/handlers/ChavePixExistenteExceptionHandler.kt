package br.com.zup.compartilhado.handlers

import br.com.zup.compartilhado.ExceptionHandler
import br.com.zup.compartilhado.ExceptionHandler.StatusWithDetails
import br.com.zup.excecoes.ChavePixExistenteException
import io.grpc.Status
import javax.inject.Singleton

@Singleton
class ChavePixExistenteExceptionHandler : ExceptionHandler<ChavePixExistenteException> {

    override fun handle(e: ChavePixExistenteException): StatusWithDetails {
        return StatusWithDetails(
            Status.ALREADY_EXISTS
                .withDescription(e.message)
                .withCause(e))
    }

    override fun supports(e: Exception): Boolean {
        return e is ChavePixExistenteException
    }
}