package br.com.zup.pix.cadastro


import br.com.zup.TipoConta
import br.com.zup.conta.ContaEntity
import br.com.zup.pix.TipoChave as TipoDeChave
import br.com.zup.pix.TipoChave
import br.com.zup.validacoes.ValidPixKey
import br.com.zup.validacoes.ValidUUID

import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ValidPixKey
@Introspected
data class NovaChavePixRequest(
    @field:ValidUUID
    @field:NotBlank
    val clienteId: String?,

    @field:NotNull
    val tipoChave: TipoChave?,

    @field:Size(max = 77)
    val valorChave: String?,

    @field:NotNull
    val tipoConta: TipoConta?
){

    fun toModel(conta: ContaEntity) : ChavePixEntity {
        return ChavePixEntity(
            clienteId = UUID.fromString(this.clienteId),
            tipoChave = TipoDeChave.valueOf(this.tipoChave!!.name),
            valorChave = if(this.tipoChave == TipoChave.ALEATORIA) UUID.randomUUID().toString() else this.valorChave!!,
            tipoConta = TipoConta.valueOf(this.tipoConta!!.name),
            conta = conta
        )
    }
}