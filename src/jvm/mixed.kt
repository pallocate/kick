package kick

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import pen.IrohaSigner
import iroha.protocol.Endpoint.ToriiResponse
import iroha.protocol.TxStatusRequest
import iroha.protocol.TransactionOuterClass.Transaction
import iroha.protocol.Primitive.Signature

/** Pair of transactions. Where the first should be committed before the second. */
typealias TxPair = Pair<Transaction, Transaction>

operator fun Boolean.inc() = true

interface Textable
{ fun text (string : String) }
object VoidTextable : Textable
{ override fun text (string : String) {} }

internal val HTML_HEAD_STYLE = """<html><head><style>
   p { 'color:black;' }
   .name { color:green; }
   .number { color:red; }
   .ordinal { color:blue; }
   .data { color:orange; }
   .cmd { color:maroon; }
   .hex { color:purple; }
</style></head><body>
"""
internal val END_BODY_HTML = "\n</body></html>"

fun createSignature (bytes : ByteArray, irohaSigner : IrohaSigner) : Signature
{
   val publicKey = irohaSigner.publicKey()
   val signature = irohaSigner.sign( bytes )

   return Signature
      .newBuilder()
      .setPublicKey( publicKey )
      .setSignature( signature )
      .build()
}

@kotlinx.coroutines.ExperimentalCoroutinesApi
internal suspend fun commitAndText (transaction : Transaction, textable : Textable) : Boolean
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

internal suspend fun statusRequest (txHash : String) : ReceiveChannel<ToriiResponse>
{
   val txStatusRequest = TxStatusRequest { this.txHash = txHash }
   return Stubs.commandStub.statusStream( txStatusRequest )
}
