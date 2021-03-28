package kick

import kotlinx.coroutines.*
import iroha.protocol.Endpoint.TxList
import iroha.protocol.TxList as txList

class KListCommiter ()
{
   private var scope = newScope()

   fun commit (txl : TxList)
   {
      if (!scope.isActive)
         scope = newScope()

      scope.launch( Dispatchers.IO ) {
         Stubs.commandStub.listTorii( txl )
      }
   }

   fun commit (txPairs : List<TxPair>)
   {
      if (!scope.isActive)
         scope = newScope()

      val txl1 = txList { addAllTransactions(txPairs.map { it.first }) }
      val txl2 = txList { addAllTransactions(txPairs.map { it.second }) }

      scope.launch( Dispatchers.IO ) {
         Stubs.commandStub.listTorii( txl1 )
         Stubs.commandStub.listTorii( txl2 )
      }
   }

   fun cancel () = scope.cancel()
}
