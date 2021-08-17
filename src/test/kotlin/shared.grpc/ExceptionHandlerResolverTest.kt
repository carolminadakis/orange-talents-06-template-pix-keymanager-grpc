package shared.grpc

import br.com.zup.compartilhado.ExceptionHandler
import br.com.zup.compartilhado.ExceptionHandlerResolver
import br.com.zup.compartilhado.handlers.DefaultExceptionHandler
import io.grpc.Status
import junit.framework.Assert.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


internal class ExceptionHandlerResolverTest {

    lateinit var illegalArgumentExceptionHandler: ExceptionHandler<IllegalArgumentException>

    lateinit var resolver: ExceptionHandlerResolver

    @BeforeEach
    fun setup() {
        illegalArgumentExceptionHandler = object : ExceptionHandler<IllegalArgumentException> {

            override fun handle(e: IllegalArgumentException): ExceptionHandler.StatusWithDetails {
                TODO("Not yet implemented")
            }

            override fun supports(e: Exception) = e is java.lang.IllegalArgumentException
        }

        resolver = ExceptionHandlerResolver(handlers = listOf(illegalArgumentExceptionHandler))
    }

    @Test
    fun `deve retornar o ExceptionHandler especifico para o tipo de excecao`() {
        val resolved = resolver.resolve(IllegalArgumentException())

        assertSame(illegalArgumentExceptionHandler, resolved)
    }

    @Test
    fun `deve alterar o ExceptionHandler padrao e retorna-lo quando nenhum handler suportar o tipo da excecao`() {

        var testeExceptionHandler = TesteExceptionHandler()
        resolver = ExceptionHandlerResolver(handlers = listOf(testeExceptionHandler), defaultHanlder = testeExceptionHandler)

        val resolved = resolver.resolve(RuntimeException())

        assertTrue(resolved is TesteExceptionHandler)
    }

    @Test
    fun `deve retornar o ExceptionHandler padrao quando nenhum handler suportar o tipo da excecao`() {
        val resolved = resolver.resolve(RuntimeException())

        assertTrue(resolved is DefaultExceptionHandler)
        assertTrue(resolved.supports(RuntimeException()))
    }

    @Test
    fun `deve retornar Status UNKNOWN quando o tipo da excecao for inesperada`() {
        val resolved = resolver.resolve(RuntimeException()) as ExceptionHandler<Exception>
        val status = resolved.handle(RuntimeException())

        assertTrue(resolved is DefaultExceptionHandler)
        assertEquals(Status.UNKNOWN.code, status.status.code)
    }

    @Test
    fun `deve lancar um erro caso encontre mais de um ExceptionHandler que suporte a mesma excecao`() {
        resolver = ExceptionHandlerResolver(listOf(illegalArgumentExceptionHandler, illegalArgumentExceptionHandler))

        assertThrows<IllegalStateException> { resolver.resolve(IllegalArgumentException()) }
    }


    class TesteExceptionHandler : ExceptionHandler<Exception> {
        override fun handle(e: Exception): ExceptionHandler.StatusWithDetails {
            TODO("Not yet implemented")
        }

        override fun supports(e: Exception): Boolean {
            return true
        }
    }
}
