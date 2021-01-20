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
      /* Payload */
      if (request.hasPayload())
      {
         val payload = request.getPayload()

         /* BatchMeta */
         if (payload.hasBatch())
         {
            val batchMeta = payload.getBatch()
            println( "\nBatchMeta:" )
            println( "{ type: ${batchMeta.getType()} }" )

            val reducedHashes = batchMeta.getReducedHashesList()
            if (reducedHashes.size > 0)
            {
               print( "\nReduced hashes:\n{" )
               for (reducedHash in reducedHashes)
                  print( " $reducedHash," )
               println( "\b }" )
            }
         }

         /* ReducedPayload */
         if (payload.hasReducedPayload())
         {
            val reducedPayload = payload.getReducedPayload()
            println( "\nReduced payload:" )
            print( "{ creator: \"${reducedPayload.getCreatorAccountId()}\", " )
            println( "time: ${reducedPayload.getCreatedTime()}s }" )



            /* Commands */
            val commands = reducedPayload.getCommandsList()
            if (commands.size > 0)
            {
               println( "\nCommands:" )
               for (command in commands)
                  println( "$command" )
            }


         }
      }

      /* Signatures */
      val signatures = request.getSignaturesList()
      if (signatures.size > 0)
      {
         println( "\nSignatures:" )
         for (signature in signatures)
            println( "$signature" )
      }

      return Empty.newBuilder().build()
   }

   override suspend fun listTorii (request : TxList) : Empty
   {
      println( "listTorii called" )
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
         txStatus = TxStatus.STATEFUL_VALIDATION_FAILED                         // STATEFUL_VALIDATION_SUCCESS
      }

      responseChannel.send {
         txHash = hash
         txStatus = TxStatus.REJECTED                                           // COMMITTED
      }
   }
}
