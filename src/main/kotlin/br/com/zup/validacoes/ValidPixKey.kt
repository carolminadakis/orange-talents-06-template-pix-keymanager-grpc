package br.com.zup.validacoes
import br.com.zup.pix.cadastro.NovaChavePixRequest
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*
import kotlin.reflect.KClass

@MustBeDocumented
@Target(CLASS, TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = [ValidPixKeyValidator::class])
annotation class ValidPixKey(
    val message: String = "chave Pix inválida (\${validatedValue.tipoChave})",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<out Payload>> = [],
)


@Singleton
class ValidPixKeyValidator : ConstraintValidator<ValidPixKey, NovaChavePixRequest> {
    override fun isValid(value: NovaChavePixRequest?, context: ConstraintValidatorContext): Boolean {
        // já verifico se o tipo é @NotNull na classe
        if(value?.tipoChave == null) {
            return true
        }

        /* Uma validação diferente para cada tipo de chave,
         * posso usar um Enum para cada tipo saber como fazer
         * sua própria validação */
        val valido = value.tipoChave.valida(value.valorChave)

        if (!valido) {
            context.disableDefaultConstraintViolation()
            context
                .buildConstraintViolationWithTemplate(context.defaultConstraintMessageTemplate)
                .addPropertyNode(ValidPixKey::class.simpleName).addConstraintViolation()
        }

        return valido
    }

}