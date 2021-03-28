package kick.tests.dumpserver

import kotlinx.coroutines.channels.SendChannel
import com.google.protobuf.Empty
import iroha.protocol.CommandService_v1CoroutineGrpc.CommandService_v1ImplBase as CommandServiceImplBase
import iroha.protocol.Endpoint.TxStatusRequest
import iroha.protocol.Endpoint.ToriiResponse
import iroha.protocol.Endpoint.TxStatus
import iroha.protocol.Endpoint.TxList
import iroha.protocol.TransactionOuterClass.Transaction

object DumpCommandService : CommandServiceImplBase()
{
   override suspend fun torii (request : Transaction) : Empty
   {
      println( request ) 
      return Empty.newBuilder().build()
   }

   override suspend fun listTorii (request : TxList) : Empty
   {
      println( request )
      return Empty.newBuilder().build()
   }

   override suspend fun status (request : TxStatusRequest) : ToriiResponse
   {
      return ToriiResponse.newBuilder().setTxHash( request.getTxHash() ).setTxStatus( TxStatus.STATELESS_VALIDATION_FAILED ).build()
   }

   override suspend fun statusStream (request : TxStatusRequest, responseChannel : SendChannel<ToriiResponse>)
   {
      val hash = request.getTxHash()

      responseChannel.send {
         txHash = hash
         txStatus = TxStatus.ENOUGH_SIGNATURES_COLLECTED
      }

      responseChannel.send {
         txHash = hash
         txStatus = TxStatus.STATEFUL_VALIDATION_SUCCESS                        // STATEFUL_VALIDATION_FAILED 
      }

      responseChannel.send {
         txHash = hash
         txStatus = TxStatus.COMMITTED                                          // REJECTED 
      }
   }
}
