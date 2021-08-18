package br.com.zup.pix.deleta

import br.com.zup.PixExclusionServiceGrpc
import br.com.zup.RemoveChavePixRequest
import br.com.zup.RemoveChavePixResponse
import br.com.zup.compartilhado.ErrorHandler
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Singleton
@ErrorHandler
class RemoveChavePixEndpoint(private val gerenciadorExclusaoChavePix: GerenciadorExclusaoChavePix) :
    PixExclusionServiceGrpc.PixExclusionServiceImplBase() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    //converte request em model
    override fun remove(request: RemoveChavePixRequest, responseObserver: StreamObserver<RemoveChavePixResponse>) {
        val chaveRequest = request.toChaveRemocaoRequest()

        // valida e exclui chave do banco
        val chavePìxCadastrada = gerenciadorExclusaoChavePix.tentaExcluir(chaveRequest)

        //retorna uma resposta
        responseObserver.onNext(
            RemoveChavePixResponse.newBuilder()
                .setRemovido(true)
                .build()
        )
        responseObserver.onCompleted()
    }
}

fun RemoveChavePixRequest.toChaveRemocaoRequest() = ChaveRemocaoRequest(pixId, clienteId)