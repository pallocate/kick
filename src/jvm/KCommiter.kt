package kick

import pen.newScope
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import iroha.protocol.TransactionOuterClass.Transaction

class KCommiter (var textable : Textable = VoidTextable)
{
   private var scope = newScope()

   @kotlinx.coroutines.ExperimentalCoroutinesApi
   fun commit (tx : Transaction)
   {
      if (!scope.isActive)
         scope = newScope()

      scope.launch( Dispatchers.IO ) {
         commitAndText( tx )
      }
   }

   @kotlinx.coroutines.ExperimentalCoroutinesApi
   fun commit (txPair : TxPair)
   {
      if (!scope.isActive)
         scope = newScope()

      scope.launch( Dispatchers.IO ) {
         commitAndText( txPair.first )
         commitAndText( txPair.second )
      }
   }

   @kotlinx.coroutines.ExperimentalCoroutinesApi
   private suspend fun commitAndText (tx : Transaction)
   {
      Stubs.commandStub.torii( tx )                                             // Commit transaction
      yield()

      if (textable !is VoidTextable)
      {
         val responseChannel = statusRequest( tx.hash() )                       // Do a status request
         responseChannel.consumeEach {

            textUI( it, textable )                                              // Text responses to UI
            yield()
         }
      }
   }

   fun cancel () = scope.cancel()
}
