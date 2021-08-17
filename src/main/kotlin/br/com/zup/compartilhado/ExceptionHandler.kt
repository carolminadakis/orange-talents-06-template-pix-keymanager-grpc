package br.com.zup.compartilhado

import io.grpc.Metadata
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto

interface ExceptionHandler<in E: Exception> {

    // Lida com a exceção e a mapeia para StatusWithDetails
    fun handle(e: E): StatusWithDetails

    // Para indicar se a implementação do Handler sabe lidar com a exceção
    fun supports(e: Exception) : Boolean

    // Apenas um wrapper para Status e Metadata (trailers)
    data class StatusWithDetails(val status: Status, val metadata: Metadata = Metadata()){
        constructor(se: StatusRuntimeException): this(se.status, se.trailers ?: Metadata())
        constructor(sp: com.google.rpc.Status): this(StatusProto.toStatusRuntimeException(sp))

        fun asRuntimeException(): StatusRuntimeException {
            return status.asRuntimeException(metadata)
        }
    }
}