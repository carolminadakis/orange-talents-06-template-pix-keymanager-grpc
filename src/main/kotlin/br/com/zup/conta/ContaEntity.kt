package br.com.zup.conta

import javax.persistence.Column
import javax.persistence.Embeddable
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Embeddable
class ContaEntity(

    @field:NotBlank
    @Column(name = "conta_instituicao", nullable = false)
    val nomeInstituicao: String,

    @field:NotBlank
    @Column(name = "conta_titular_nome", nullable = false)
    val nomeTitular: String,

    @field:NotBlank
    @field:Size(max = 11)
    @Column(name = "conta_titular_cpf", nullable = false)
    val cpfTitular: String,

    @field:NotBlank @field:Size(max = 4)
    @Column(name = "conta_agencia", length = 4, nullable = false)
    val agencia: String,

    @field:NotBlank
    @field:Size(max = 6)
    @Column(name = "conta_numero", length = 6, nullable = false)
    val numeroConta: String
)