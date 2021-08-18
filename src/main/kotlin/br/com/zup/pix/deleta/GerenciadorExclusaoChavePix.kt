package br.com.zup.pix.deleta

import br.com.zup.excecoes.ChavePixNaoEncontradaException
import br.com.zup.pix.cadastro.repository.ChavePixRepository
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Singleton
@Validated
class GerenciadorExclusaoChavePix(val chavePixRepository: ChavePixRepository) {

    private val logger = LoggerFactory.getLogger(this::class.java)


    @Transactional
    fun tentaExcluir(@Valid chaveRemocaoRequest: ChaveRemocaoRequest) {
        val pixId = UUID.fromString(chaveRemocaoRequest.pixId)
        val clienteId = UUID.fromString(chaveRemocaoRequest.clienteId)

        //busca chave no banco, se não existir retorna 404 com mensagem amigável
        val chaveEncontrada = chavePixRepository.existsByIdAndClienteId(pixId, clienteId)
        if (!chaveEncontrada) throw ChavePixNaoEncontradaException("ChavePix não encontrada para este cliente")
        else chavePixRepository.deleteById(pixId)
    }
}
