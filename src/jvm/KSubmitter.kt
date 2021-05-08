package kick

import pen.newScope
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import iroha.protocol.Queries.Query
import iroha.protocol.Queries.BlocksQuery
import iroha.protocol.QryResponses.QueryResponse

class KSubmitter (var textable : Textable = VoidTextable)
{
   private var scope = newScope()
   private val PRE = "<html><head><style>p { 'color:black;' }</style></head><body>"
   private val POST = "</body></html>"

   fun submit (query : Query)
   {
      if (!scope.isActive)
         scope = newScope()

      scope.launch( Dispatchers.IO ) {

         val queryResponse = async {Stubs.queryStub.find( query )}.await()

         if (textable !is VoidTextable)
            launch { textable.text(htmlResponse( queryResponse )) }  //queryResponse.toString()
      }
   }

   @ExperimentalCoroutinesApi
   suspend fun submit (blocksQuery : BlocksQuery)
   {
      val responseChannel = Stubs.queryStub.fetchCommits( blocksQuery )
      responseChannel.consumeEach {println( it.toString() )}
   }


   fun htmlResponse (response : QueryResponse?) : String
   {
      var ret = ""
      
      if (response != null)
      {
         ret = PRE

         with (response)
         {
            when (true)
            {
               hasAccountResponse() ->
               {
                  val account = accountResponse.account
                  ret += "<p>${account.getAccountId()}</p>"
                  ret += "<p>quorum: ${account.getQuorum()}</p>"
                  ret += "<p>data:</p><p style='color:orange;'>${account.getJsonData()}</p>"
               }
               hasAccountAssetsResponse() ->
               {
                  val aar = accountAssetsResponse
                  val accountAssetList = aar.accountAssetsList.forEach {
                     ret += "<p style='color:rgb(41,38,0);'>${it.assetId}:</p><p style='color:rgb(41,38,0);'>${it.balance}</p>" 
                  }
               }
               hasAccountDetailResponse() ->
               { ret += "<p style='color:rgb(41,38,0);'>${accountDetailResponse.detail}</p>" }
               hasSignatoriesResponse() ->
               {
                  val signatoriesList = signatoriesResponse.keysList
                  ret += "<p>signatories:</p><p>$signatoriesList</p>"
               }

               hasErrorResponse() -> {
                  ret += "<p style='color:red;'>${errorResponse.reason}</p><p style='color:red;'>${errorResponse.message}</p>"
               }
               else -> {}
            }
         }
         
         ret += POST
      }
      
      return ret
   }

   fun cancel () = scope.cancel()
}
