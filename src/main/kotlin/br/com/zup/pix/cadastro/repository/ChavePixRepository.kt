package br.com.zup.pix.cadastro.repository

import br.com.zup.pix.cadastro.ChavePixEntity
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*


@Repository
interface ChavePixRepository : JpaRepository<ChavePixEntity, UUID> {
    fun existsByValorChave(valorChave: String?): Boolean

    fun existsByIdAndClienteId(pixId: UUID?, clienteId: UUID?): Boolean
}