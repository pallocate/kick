package kick

import kotlinx.coroutines.ExperimentalCoroutinesApi
import iroha.protocol.TransactionOuterClass.Transaction
import iroha.protocol.TxList

/** Pair of transactions. Where the first should be committed before the second. */
typealias TxPair = Pair<Transaction, Transaction>

object Utils
{
   @kotlinx.coroutines.ExperimentalCoroutinesApi
   suspend fun setup (txPairs : List<TxPair>) : Boolean
   {
      var success : Boolean

      val firstTxList = TxList { addAllTransactions(txPairs.map { it.first }) }
      success = monitorTxStatus(commit( firstTxList )).reportRejectedTx().allSuccess()

      if (success)
      {
         println( "First set of transactions completed successfully." )

         val secondTxList = TxList { addAllTransactions(txPairs.map { it.second }) }
         success = monitorTxStatus(commit( secondTxList )).reportRejectedTx().allSuccess()

         if (success)
            println( "Second set of transactions completed successfully." )
      }

      return success
   }

   @kotlinx.coroutines.ExperimentalCoroutinesApi
   suspend fun test (txPair : TxPair) : Boolean
   {
      var success : Boolean

      success = monitorTxStatus(commit( txPair.first )).reportRejectedTx().allSuccess()
      if (success)
      {
         println( "First transaction completed successfully." )
         success = monitorTxStatus(commit( txPair.second )).reportRejectedTx().allSuccess()
         if (success)
            println( "Second transaction completed successfully." )
      }

      return success
   }
}
