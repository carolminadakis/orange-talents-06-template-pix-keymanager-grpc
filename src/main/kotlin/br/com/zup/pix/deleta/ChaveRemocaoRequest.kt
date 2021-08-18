package br.com.zup.pix.deleta

import br.com.zup.validacoes.ValidUUID
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class ChaveRemocaoRequest(
    @field:ValidUUID(message = "pixId com formato inválido")
    @field: NotBlank
    val pixId: String?,

    @field:ValidUUID(message = "clienteId com formato inválido")
    @field:NotBlank
    val clienteId: String?
)

