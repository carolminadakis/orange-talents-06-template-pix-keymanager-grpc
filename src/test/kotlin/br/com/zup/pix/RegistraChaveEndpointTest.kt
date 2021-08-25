package br.com.zup.pix


import br.com.zup.CadastraChavePixRequest
import br.com.zup.PixRegistrationServiceGrpc
import br.com.zup.TipoChave.CPF
import br.com.zup.TipoChave.EMAIL
import br.com.zup.TipoConta
import br.com.zup.conta.ContaEntity
import br.com.zup.conta.ContaResponse
import br.com.zup.conta.InstituicaoResponse
import br.com.zup.conta.TitularResponse
import br.com.zup.externos.ErpItauClient
import br.com.zup.pix.TipoChave
import br.com.zup.pix.cadastro.ChavePixEntity
import br.com.zup.pix.cadastro.repository.ChavePixRepository
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import shared.grpc.violations
import java.util.*
import javax.inject.Inject
import br.com.zup.pix.TipoChave as TipoDeChave

/*
Desabilitamos o controle transacional pois o gRPC Server roda numa thread separada,
caso contrário, não será possível preparar cenário dentro do método @Test
 */

@MicronautTest(transactional = false)
internal class RegistraChaveEndpointTest(
    val repository: ChavePixRepository,
    val grpcClient: PixRegistrationServiceGrpc.PixRegistrationServiceBlockingStub
) {
    @Inject
    lateinit var itauClient: ErpItauClient;

    companion object {
        val CLIENTE_ID = UUID.randomUUID()
    }

    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    @Test
    fun `deve registrar nova chave pix`() {
        // Cenário - Substituir o Client original pelo Mock
        Mockito.`when`(
            itauClient.buscaContaDoClientePorTipo(clienteId = CLIENTE_ID.toString(), tipo = "CONTA_CORRENTE")
        ).thenReturn(HttpResponse.ok(dadosDaContaResponse()))


        //ação
        val response = grpcClient.cadastra(
            CadastraChavePixRequest.newBuilder()
                .setClienteId(CLIENTE_ID.toString())
                .setTipoChave(EMAIL)
                .setValorChave("teste@gmail.com")
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build()
        )

        //avaliação
        with(response) {
            assertNotNull(pixId)
        }
    }

    @Test
    fun `nao deve registrar chave pix quando chave existente`() {
        //cenário
        repository.save(chaveFake(tipo = TipoChave.CPF, chave = "86135457004", clienteId = CLIENTE_ID))
        // Ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.cadastra(
                CadastraChavePixRequest.newBuilder()
                    .setClienteId(CLIENTE_ID.toString())
                    .setTipoChave(CPF)
                    .setValorChave("86135457004")
                    .setTipoConta(TipoConta.CONTA_CORRENTE)
                    .build()
            )
        }

        // Validação
        with(thrown) {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("A chave Pix '86135457004' já existe no banco", status.description)
        }
    }

    @Test
    fun `nao deve registrar chave pix quando nao encontrar dados da conta cliente`() {
        // Cenário - Mockar busca no Client Itau devolvendo status de não encontrado
        Mockito.`when`(
            itauClient.buscaContaDoClientePorTipo(
                clienteId = CLIENTE_ID.toString(),
                tipo = "CONTA_CORRENTE"
            )
        )
            .thenReturn(HttpResponse.notFound())

        // Ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.cadastra(
                CadastraChavePixRequest.newBuilder()
                    .setClienteId(CLIENTE_ID.toString())
                    .setTipoChave(CPF)
                    .setValorChave("86135457004")
                    .setTipoConta(TipoConta.CONTA_CORRENTE)
                    .build()
            )
        }

        // Validação
        with(thrown) {
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals("Cliente não encontrado no ITAU", status.description)
        }
    }

    // testa se @ValidPixKey está sendo usada
    @Test
    fun `nao deve registrar chave pix quando parametros forem invalidos`() {
        //cenário
        //ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.cadastra(
                CadastraChavePixRequest.newBuilder()
                    .setClienteId(CLIENTE_ID.toString())
                    .setTipoChave(CPF)
                    .setValorChave("961.345.a.570-04")
                    .setTipoConta(TipoConta.CONTA_CORRENTE)
                    .build()
            )
        }

        // Validação
        with(thrown) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Dados inválidos", status.description)
            MatcherAssert.assertThat(
                violations(), Matchers.containsInAnyOrder(
                    Pair("chave", "chave Pix inválida (CPF)")
                )
            )
        }
    }

    private fun dadosDaContaResponse(): ContaResponse {
        return ContaResponse(
            tipo = "CONTA_CORRENTE",
            instituicao = InstituicaoResponse("UNIBANCO ITAU SA", "60701190"),
            agencia = "0001",
            numero = "123455",
            titular = TitularResponse("Yuri Matheus", "86135457004")
        )
    }

    fun `nao deve cadastrar chave pix quando os parametros forem invalidos`() {
        // ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.cadastra(CadastraChavePixRequest.newBuilder().build())
        }
        //validação
        with(thrown) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Dados inválidos", status.description)
            MatcherAssert.assertThat(
                violations(), containsInAnyOrder(
                    Pair("clienteId", "não deve estar em branco"),
                    Pair("clienteId", "formato de UUID inválido"),
                    Pair("tipoConta", "não deve ser nulo"),
                    Pair("tipoChave", "não deve ser nulo"),
                )
            )
        }
    }

    private fun chaveFake(
        tipo: TipoDeChave,
        chave: String = UUID.randomUUID().toString(),
        clienteId: UUID = UUID.randomUUID()
    ): ChavePixEntity {
        return ChavePixEntity(
            clienteId = clienteId,
            tipoChave = tipo,
            valorChave = chave,
            tipoConta = TipoConta.CONTA_CORRENTE,
            conta = ContaEntity(
                nomeInstituicao = "UNIBANCO ITAU SA",
                nomeTitular = "Yuri Matheus",
                cpfTitular = "86135457004",
                agencia = "0001",
                numeroConta = "123455"
            )
        )
    }


    // Mock do client Http (para não ter que levantar todo o ambiente externo)
    @MockBean(ErpItauClient::class)
    fun itauClient(): ErpItauClient? {
        return Mockito.mock(ErpItauClient::class.java)
    }

    // Criação de um Client para consumir a resposta do endpoint gRPC
    @Factory
    class Clients {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): PixRegistrationServiceGrpc.PixRegistrationServiceBlockingStub {
            return PixRegistrationServiceGrpc.newBlockingStub(channel)
        }
    }
}