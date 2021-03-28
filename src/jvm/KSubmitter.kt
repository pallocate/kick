package kick

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import iroha.protocol.Queries.Query
import iroha.protocol.Queries.BlocksQuery

class KSubmitter (var textable : Textable = VoidTextable)
{
   private var scope = newScope()

   fun submit (query : Query)
   {
      if (!scope.isActive)
         scope = newScope()

      scope.launch( Dispatchers.IO ) {

         val queryResponse = async {Stubs.queryStub.find( query )}.await()

         if (textable !is VoidTextable)
            launch {textable.text( queryResponse.toString() )}
      }
   }

   @ExperimentalCoroutinesApi
   suspend fun submit (blocksQuery : BlocksQuery)
   {
      val responseChannel = Stubs.queryStub.fetchCommits( blocksQuery )
      responseChannel.consumeEach {println( it.toString() )}
   }

   fun cancel () = scope.cancel()
}
