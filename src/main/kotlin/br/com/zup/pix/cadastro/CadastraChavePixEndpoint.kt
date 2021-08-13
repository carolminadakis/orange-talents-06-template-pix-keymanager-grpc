package br.com.zup.pix.cadastro


import br.com.zup.*
import br.com.zup.pix.TipoChave as TipoDeChave
import br.com.zup.compartilhado.handlers.ExceptionHandler


import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Singleton
@ExceptionHandler
class CadastraChavePixEndpoint(val gerenciadorCadastroChavePix: GerenciadorCadastroChavePix) :
    PixServiceGrpc.PixServiceImplBase() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun cadastra(
        request: CadastraChavePixRequest,
        responseObserver: StreamObserver<CadastraChavePixResponse>
    ) {
        val novaChavePixRequest = request.toNovaChavePixRequest()
        logger.info("novaChavePixRequest: $novaChavePixRequest")

        val chavePixCadastrada = gerenciadorCadastroChavePix.tentaCadastrar(novaChavePixRequest)
        logger.info("chavePixCadastrada: $chavePixCadastrada")

        responseObserver.onNext(
            CadastraChavePixResponse.newBuilder()
                .setPixId(chavePixCadastrada.id.toString()).build()
        )
        responseObserver.onCompleted()
    }
}

fun CadastraChavePixRequest.toNovaChavePixRequest(): NovaChavePixRequest {
    return NovaChavePixRequest(
        clienteId = clienteId,
        tipoChave = if(tipoChave.equals(TipoChave.UNKNOWN_CHAVE)) null else TipoDeChave.valueOf(tipoChave.name),
        valorChave = valorChave,
        tipoConta = if(tipoConta.equals(TipoConta.UNKNOWN_CONTA)) null else TipoConta.valueOf(tipoConta.name)
    )
}