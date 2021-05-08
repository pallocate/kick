package kick.tests.dumpserver

import kotlinx.coroutines.channels.SendChannel
import iroha.protocol.QueryService_v1CoroutineGrpc.QueryService_v1ImplBase as QueryServiceImplBase
import iroha.protocol.Queries.Query
import iroha.protocol.Queries.BlocksQuery
import iroha.protocol.QryResponses.*

object DumpQueryService : QueryServiceImplBase()
{
   override suspend fun find (request : Query) : QueryResponse
   {
      val queryResponse = QueryResponse.newBuilder()
      val requestPayload = request.payload

      with (requestPayload)
      {
         when (true)
         {
            hasGetAccount() -> 
            {  
               queryResponse.setAccountResponse(
                  AccountResponse.newBuilder().setAccount( 
                     Account.newBuilder().setJsonData( "{}" ).setDomainId( "bar" ).setAccountId( "foo@bar" ) 
                  )
               )
            }
            hasGetAccountAssets() -> 
            {
               queryResponse.setAccountAssetsResponse(
                  AccountAssetResponse.newBuilder().addAccountAssets( 
                     AccountAsset.newBuilder()
                        .setAccountId( "foo@bar" )
                        .setAssetId( "credit#bar" )
                        .setBalance( "0" )
                  )
               )
            }
            hasGetAccountDetail() -> 
            {
               queryResponse.setAccountDetailResponse(
                  AccountDetailResponse.newBuilder().setDetail( "{pk: FFFFFFFF}" )
               )
            }
            hasGetSignatories() -> 
            {
               queryResponse.setSignatoriesResponse( 
               SignatoriesResponse.newBuilder().addKeys( "aaaaaaaaaaaa" )
               )
            }
            else -> {}
         }
      }
      println( request )

      return queryResponse.build()
   }

   override suspend fun fetchCommits (request : BlocksQuery, responseChannel : SendChannel<BlockQueryResponse>)
   {
      println( request )
      responseChannel.send {
         blockErrorResponse = BlockErrorResponse.newBuilder().build()
      }
   }
}
