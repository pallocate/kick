package kick.tests.dumpserver

import kotlinx.coroutines.channels.SendChannel
import iroha.protocol.QueryService_v1CoroutineGrpc.QueryService_v1ImplBase as QueryServiceImplBase
import iroha.protocol.Queries.Query
import iroha.protocol.Queries.BlocksQuery
import iroha.protocol.QryResponses.QueryResponse
import iroha.protocol.QryResponses.BlockQueryResponse
import iroha.protocol.QryResponses.BlockErrorResponse

object DumpQueryService : QueryServiceImplBase()
{
   override suspend fun find (request : Query) : QueryResponse
   {
      return QueryResponse.newBuilder().build()
   }

   override suspend fun fetchCommits (request : BlocksQuery, responseChannel : SendChannel<BlockQueryResponse>)
   {
      responseChannel.send {
         blockErrorResponse = BlockErrorResponse.newBuilder().build()
      }
   }
}
