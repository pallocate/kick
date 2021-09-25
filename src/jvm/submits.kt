package kick

import pef.newScope
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import iroha.protocol.Queries.Query
import iroha.protocol.Queries.BlocksQuery

sealed class Submitable
class KQuery (val query : Query) : Submitable () {}
class KBlocksQuery (val blocksQuery : BlocksQuery) : Submitable () {}

/** Submits queries to the block chain.  */
class KSubmiter ()
{
   private var scope = newScope()

   fun submit (submitable : Submitable, textable : Textable = VoidTextable)
   {
      if (!scope.isActive)
         scope = newScope()

      when (submitable)
      {
         is KQuery -> submit( submitable.query, textable )
         is KBlocksQuery -> submit( submitable.blocksQuery, textable )
      }
   }

   fun submit (query : Query, textable : Textable = VoidTextable)
   {
      scope.launch( Dispatchers.IO ) {

         val queryResponse = async {Stubs.queryStub.find( query )}.await()

         if (textable !is VoidTextable)
            launch {textable.text( queryResponse.toHtml() )}
      }
   }

   @ExperimentalCoroutinesApi
   fun submit (blocksQuery : BlocksQuery, textable : Textable = VoidTextable)
   {
      scope.launch( Dispatchers.IO ) {
         val responseChannel = Stubs.queryStub.fetchCommits( blocksQuery )
         responseChannel.consumeEach {println( it.toString() )}
      }
   }

   fun cancel () = scope.cancel()
}
