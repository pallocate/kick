package kick

import pen.newScope
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import iroha.protocol.TransactionOuterClass.Transaction
import iroha.protocol.TxList

sealed class Commitable
class KTransaction (val transaction : Transaction) : Commitable () {}
/** Intended to be used when a second transaction depends on the first. */
class KTransactionPair (val pair : TxPair) : Commitable () {}
class KTransactionList (val transactions : List<Transaction>) : Commitable () {}

class KCommiter ()
{
   private var scope = newScope()

   @kotlinx.coroutines.ExperimentalCoroutinesApi
   fun commit (commitable : Commitable, textable : Textable = VoidTextable)
   {
      if (!scope.isActive)
         scope = newScope()

      when (commitable)
      {
         is KTransaction -> commit( commitable.transaction, textable )
         is KTransactionPair -> commit( commitable.pair, textable )
         is KTransactionList -> commit( commitable.transactions )
      }
   }

   @kotlinx.coroutines.ExperimentalCoroutinesApi
   private fun commit (transaction : Transaction, textable : Textable = VoidTextable)
   {
      scope.launch( Dispatchers.IO ) {
         commitAndText( transaction, textable )
      }
   }

   @kotlinx.coroutines.ExperimentalCoroutinesApi
   private fun commit (txPair : TxPair, textable : Textable = VoidTextable)
   {
      scope.launch( Dispatchers.IO ) {
         commitAndText( txPair.first, textable )
         commitAndText( txPair.second, textable )
      }
   }

   private fun commit (transactions : List<Transaction>)
   {
      val txl = TxList {addAllTransactions( transactions )}
      scope.launch( Dispatchers.IO ) {
         Stubs.commandStub.listTorii( txl )
      }
   }

   fun cancel () = scope.cancel()
}
