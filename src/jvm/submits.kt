package kick

import pen.newScope
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import iroha.protocol.Queries.Query
import iroha.protocol.Queries.BlocksQuery
import iroha.protocol.QryResponses.QueryResponse

sealed class Submitable
class KQuery (val query : Query) : Submitable () {}
class KBlocksQuery (val blocksQuery : BlocksQuery) : Submitable () {}

class KSubmiter ()
{
   private var scope = newScope()
   private val PRE = "<html><head><style>p { 'color:black;' }</style></head><body>"
   private val POST = "</body></html>"

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
            launch { textable.text(htmlResponse( queryResponse )) }  //queryResponse.toString()
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

               hasTransactionsPageResponse() -> {
                  val transactionsList = transactionsPageResponse.transactionsList
                  for (txNum in 0 until transactionsList.size)
                  {
                     ret += "<p style='color:rgb(0,0,41);'>tx (${txNum})</p>"
                     if (transactionsList[txNum].hasPayload())
                     {
                        val payload = transactionsList[txNum].payload
                        if (payload.hasReducedPayload())
                           for (command in payload.reducedPayload.commandsList)
                              ret += "<p style='color:rgb(125,100,16);'>${command}</p>"
                     }
                  }
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
