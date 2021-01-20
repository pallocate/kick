package kick

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import com.github.marcoferrer.krotoplus.coroutines.withCoroutineContext
import iroha.protocol.TransactionOuterClass.Transaction
import iroha.protocol.Endpoint.TxList
import iroha.protocol.Queries.Query
import iroha.protocol.Queries.BlocksQuery
import iroha.protocol.TxStatusRequest

suspend fun submit (query : Query)
{
   val queryResponse = Stubs.queryStub.find( query )
   println( queryResponse.toString() )
}

@ExperimentalCoroutinesApi
suspend fun submit (blocksQuery : BlocksQuery)
{
   val responseChannel = Stubs.queryStub.withCoroutineContext().fetchCommits( blocksQuery )
   responseChannel.consumeEach {println( it.toString() )}
}

suspend fun commit (txList : TxList) : List<String>
{
   Stubs.commandStub.listTorii( txList )
   return txList.transactionsList.map { it.hash() }
}

suspend fun commit (tx : Transaction) : List<String>
{
   Stubs.commandStub.torii( tx )
   return listOf( tx.hash() )
}

@ExperimentalCoroutinesApi
suspend fun monitorTxStatus (transactionHashes : List<String>, statusMonitor : StatusMonitor = KStatusMonitor()) : StatusMonitor
{
   coroutineScope {

      for (txHash in transactionHashes)
      {
         val statusRequest = TxStatusRequest { this.txHash = txHash }
         val responseChannel = Stubs.commandStub.statusStream( statusRequest )

         launch {
            val monitorTask = statusMonitor.newMonitorTask( txHash )
            responseChannel.consumeEach { monitorTask.addResponse( it ) }
         }
      }
   }

   return statusMonitor
}
