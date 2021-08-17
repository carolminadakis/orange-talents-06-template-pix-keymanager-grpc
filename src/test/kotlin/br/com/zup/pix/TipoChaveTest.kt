package br.com.zup.pix

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class TipoChaveTest {

    @Nested
    inner class CPF {
        // formato obrigatório: 12345678900
        @Test
        fun `deve ser valido se tipo CPF e chave tiver CPF valido`() {
            with(TipoChave.CPF) {
                assertTrue(valida("86135457004"))
            }
        }

        @Test
        fun `nao deve ser valido se tipo CPF e chave for nula ou vazia`() {
            with(TipoChave.CPF) {
                assertFalse(valida(null))
                assertFalse(valida(""))
            }
        }

        fun `nao deve ser valido se tipo CPF e chave tiver algo diferente de numeros`() {
            with(TipoChave.CPF) {
                assertFalse(valida("861.354.570-04"))
                assertFalse(valida("861A354b570-04"))
            }
        }

        @Test
        fun `nao deve ser valido se tipo CPF e chave for um CPF invalido`() {
            with(TipoChave.CPF) {
                assertFalse(valida("12345678910"))
            }
        }
    }

    @Nested
    inner class CELULAR {
        //formato obrigatório: +5517991209874

        @Test
        fun `deve ser valido se tipo CELULAR e chave tiver numero valido`() {
            with(TipoChave.CELULAR) {
                assertFalse(valida(null))
                assertFalse(valida(""))
            }
        }

        fun `nao deve ser valido se tipo CELULAR e chave tiver valor invalido`() {
            with(TipoChave.CELULAR) {
                assertFalse(valida("17987654321"))
                assertFalse(valida("5517987654321"))
                assertFalse(valida("+55a1798765432"))
            }
        }
    }

    @Nested
    inner class EMAIL {
        @Test
        fun `d4ve ser valido se tipo EMAIL e chave for valido`() {
            with(TipoChave.EMAIL) {
                assertTrue(valida("teste@gmail.com"))
            }
        }

        @Test
        fun `nao deve ser valido se tipo EMAIL e chave for nulo ou vazio`() {
            with(TipoChave.EMAIL) {
                assertFalse(valida(null))
                assertFalse(valida(""))
            }
        }

        @Test
        fun `nao deve ser valido se tipo EMAIL e chave for endereco invalido`() {
            with(TipoChave.EMAIL) {
                assertFalse(valida("teste"))
                assertFalse(valida("teste.teste.com.br"))
                assertFalse(valida("teste.teste@com."))
                assertFalse(valida("teste.@teste.com"))
            }
        }
    }

    @Nested
    inner class ALEATORIA {
        @Test
        fun `deve ser valido se tipo ALEATORIA e chave for nula ou vazia`() {
            with(TipoChave.ALEATORIA) {
                assertTrue(valida(null))
                assertTrue(valida(""))
            }
        }

        @Test
        fun `nao deve ser valido se tipo ALEATORIA e chave for preenchido`() {
            with(TipoChave.ALEATORIA) {
                assertFalse(valida("teste"))
            }
        }
    }
}