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
typealias TxTriple = Triple<Transaction, Transaction, Transaction>

interface Textable
{ fun text (string : String) }
object VoidTextable : Textable
{ override fun text (string : String) {} }

/* Texts failed transaction response messages to UI. */
internal suspend fun textUI (response : ToriiResponse, textable : Textable) {

   if (response.errorCode > 0 && textable !is VoidTextable)
      runBlocking {  //withContext( Dispatchers.Main ) {

         val status = response.txStatus
            .toString()
//            .toLowerCase()
            .replace( '_', ' ' )

         textable.text( status + if (status.contains( "FAILED" ))
               ", command nr. ${response.failedCmdIndex+1} (${response.errOrCmdName})"
            else
               ""
         )
      }
}

suspend fun statusRequest (txHash : String) : ReceiveChannel<ToriiResponse>
{
   val txStatusRequest = TxStatusRequest { this.txHash = txHash }
   return Stubs.commandStub.statusStream( txStatusRequest )
}
