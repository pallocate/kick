package kick

import kotlinx.coroutines.*
import pef.newScope
import iroha.protocol.TransactionOuterClass.Transaction
import iroha.protocol.Endpoint.TxList
import iroha.protocol.TxList as txList

class KSetuper ()
{
   private val scope = newScope()

   @kotlinx.coroutines.ExperimentalCoroutinesApi
   fun setup (createAccounts : Transaction, txPairs : List<TxPair>)
   {
      scope.launch( Dispatchers.IO ) {
         val accountsCreated = commitAndText( createAccounts, VoidTextable )
         yield()

         if (accountsCreated)
         {
            val txl1 = txList { addAllTransactions(txPairs.map { it.first }) }
            val txl2 = txList { addAllTransactions(txPairs.map { it.second }) }

            Stubs.commandStub.listTorii( txl1 )
            Stubs.commandStub.listTorii( txl2 )
         }
      }
   }

   fun commitBlocking (tx : Transaction, textable : Textable = VoidTextable)
   {
      runBlocking {
         commitAndText( tx, textable )
      }
   }
   
   fun cancel () = scope.cancel()
}
