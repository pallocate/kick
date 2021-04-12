package kick

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import com.github.marcoferrer.krotoplus.coroutines.withCoroutineContext
import iroha.protocol.TransactionOuterClass.Transaction
import iroha.protocol.Endpoint.TxList
import iroha.protocol.Endpoint.ToriiResponse
import iroha.protocol.TxStatusRequest

/** Pair of transactions. Where the first should be committed before the second. */
typealias TxPair = Pair<Transaction, Transaction>

interface Textable
{ fun text (string : String) }
object VoidTextable : Textable
{ override fun text (string : String) {} }

@kotlinx.coroutines.ExperimentalCoroutinesApi
suspend fun commitAndText (transaction : Transaction, textable : Textable) : Boolean
{
   var success = true
   Stubs.commandStub.torii( transaction )                                       // Send transaction to the block chain
   yield()

   val responseChannel = statusRequest( transaction.hash() )                    // Do a status request
   responseChannel.consumeEach { response ->

      if (response.errorCode > 0)
      {
         yield()
         textError( response, textable )                                        // Text errors to UI
         success = false
      }
    }

   return success
}

/* Texts failed transaction response messages to UI. */
internal suspend fun textError (response : ToriiResponse, textable : Textable) {

   if (textable !is VoidTextable)
   {
      runBlocking {

         val status = response.txStatus
            .toString()
            .replace( '_', ' ' )

         textable.text( status + if (status.contains( "FAILED" ))
               ", command nr. ${response.failedCmdIndex+1} (${response.errOrCmdName})"
            else
               ""
         )
      }
   }
}

suspend fun statusRequest (txHash : String) : ReceiveChannel<ToriiResponse>
{
   val txStatusRequest = TxStatusRequest { this.txHash = txHash }
   return Stubs.commandStub.statusStream( txStatusRequest )
}
