package kick

import pen.newScope
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import iroha.protocol.TransactionOuterClass.Transaction
import iroha.protocol.Endpoint.TxList

class KCommiter ()
{
   private var scope = newScope()

   @kotlinx.coroutines.ExperimentalCoroutinesApi
   fun commit (transaction : Transaction, textable : Textable = VoidTextable)
   {
      if (!scope.isActive)
         scope = newScope()

      scope.launch( Dispatchers.IO ) {
         commitAndText( transaction, textable )
      }
   }

   @kotlinx.coroutines.ExperimentalCoroutinesApi
   fun commit (txPair : TxPair, textable : Textable = VoidTextable)
   {
      if (!scope.isActive)
         scope = newScope()

      scope.launch( Dispatchers.IO ) {
         commitAndText( txPair.first, textable )
         commitAndText( txPair.second, textable )
      }
   }

   fun commit (txl : TxList)
   {
      if (!scope.isActive)
         scope = newScope()

      scope.launch( Dispatchers.IO ) {
         Stubs.commandStub.listTorii( txl )
      }
   }

   fun cancel () = scope.cancel()
}
