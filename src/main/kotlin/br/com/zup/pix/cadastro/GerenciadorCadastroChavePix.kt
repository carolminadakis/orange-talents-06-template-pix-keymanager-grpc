package br.com.zup.pix.cadastro

import br.com.zup.excecoes.ChavePixExistenteException
import br.com.zup.externos.ErpItauClient
import br.com.zup.pix.cadastro.repository.ChavePixRepository
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class GerenciadorCadastroChavePix(
    val chavePixRepository: ChavePixRepository,
    val itauClient: ErpItauClient
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun tentaCadastrar(@Valid novaChavePixRequest: NovaChavePixRequest) : ChavePixEntity {
        // Verifica se chave já existe no banco
        if (chavePixRepository.existsByValorChave(novaChavePixRequest.valorChave))
            throw ChavePixExistenteException("A chave Pix '${novaChavePixRequest.valorChave}' já existe no banco")

        // Busca dados da conta no ERP do ITAU
        val contaResponse = itauClient.buscaContaDoClientePorTipo(novaChavePixRequest.clienteId!!, novaChavePixRequest.tipoConta!!.name)
        val conta = contaResponse.body()?.toModel() ?: throw IllegalStateException("Cliente não encontrado no ITAU")


        // Salva no banco de dados
        val chavePix = novaChavePixRequest.toModel(conta)
        chavePixRepository.save(chavePix)

        return chavePix
    }
}