package br.com.zup.externos

import br.com.zup.conta.ContaResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client


@Client("\${itau.erp.url}")
interface ErpItauClient {

    @Get("/api/v1/clientes/{clienteId}/contas{?tipo}")
    fun buscaContaDoClientePorTipo(
        @PathVariable clienteId: String,
        @QueryValue tipo: String
    ): HttpResponse<ContaResponse>
}