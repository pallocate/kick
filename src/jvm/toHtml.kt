package kick

import iroha.protocol.QryResponses.QueryResponse
import iroha.protocol.QryResponses.BlockResponse
import iroha.protocol.QryResponses.BlockQueryResponse
import iroha.protocol.TransactionOuterClass.Transaction

fun BlockQueryResponse.toHtml () = 
   HTML_HEAD_STYLE + 

   if (hasBlockResponse()) blockResponseToHtml( blockResponse ) else { "" } + 

   END_BODY_HTML

fun QueryResponse.toHtml () : String
{
   val html = StringBuilder( HTML_HEAD_STYLE )
   var delimit = false

   when (true)
   {
      hasAccountResponse() ->
      {
         val account = accountResponse.account
         html.append( "<p>Account: <span class='name'>${account.getAccountId()}</span></p>" )
         html.append( "<p>Quorum: <span class='number'>${account.getQuorum()}</span></p>" )
         html.append( "<p>Roles: <span class='name'>${accountResponse.accountRolesList}</span></p>" )
         html.append( "<p>Products consumed:</p><p class='data'>${account.jsonData}</p>" )
      }
      hasAccountAssetsResponse() ->
         accountAssetsResponse.accountAssetsList.forEach { asset ->
            html.append( "<p>Asset: <span class='name'>${asset.assetId}</span></p>" )
            html.append( "<p>Balance: <span class='number'>${asset.balance}</span></p>" )
         }
      hasAssetResponse() ->
         if (assetResponse.hasAsset())
         {
            val asset = assetResponse.asset
            html.append( "<p>Asset: <span class='name'>${asset.assetId}</span></p>" )
            html.append( "<p>Precision: <span class='number'>${asset.precision}</span></p>" )
         }
      hasTransactionsResponse() ->
         html.append(transactionListToHtml( transactionsResponse.transactionsList, false, true, true ))

      hasTransactionsPageResponse() ->
         html.append(transactionListToHtml( transactionsPageResponse.transactionsList, false, true, true ))

      hasPendingTransactionsPageResponse() -> 
         html.append(transactionListToHtml( pendingTransactionsPageResponse.transactionsList, false, true, true ))

      hasAccountDetailResponse() ->
         html.append( "<p>Products consumed:</p><p class='data'>${accountDetailResponse.detail}</p>" )
         
      hasSignatoriesResponse() ->
         html.append( "<p>Signatories:</p><p class='hex'>${signatoriesResponse.keysList}</p>" )

      hasRolesResponse() ->
      {
         html.append( "<p>Roles: </p><p class='name'>[" )
         rolesResponse.rolesList.asByteStringList().forEach { byteString ->
            if (delimit++)
               html.append( ", " )
            html.append( "${byteString.toStringUtf8()}" )
         }
         html.append( "]</p>" )
      }
      
      hasRolePermissionsResponse() ->
      {
         html.append( "<p>Permissions: </p><p class='name'>[" )
         rolePermissionsResponse.permissionsList.forEach { rolePermission ->
            if (delimit++)
               html.append( ", " )
            html.append( "${rolePermission.valueDescriptor.name}" )
         }
         html.append( "]</p>" )
      }
      
      hasBlockResponse() ->
         html.append(blockResponseToHtml( blockResponse ))

      hasPeersResponse() -> peersResponse.peersList.forEach { peer ->
            html.append( "<p>Peer: <span class='number'>${peer.address}</span>, <span class='hex'>${peer.peerKey}</span></p>" )
         }

      hasErrorResponse() ->
      {
         html.append( "<p>Reason: <span class='ordinal'>${errorResponse.reason}</span></p>" )
         html.append( "<p>Message: <span class='number'> ${errorResponse.message}</span></p>" )
      }
      
      else -> {}
   }

   html.append( END_BODY_HTML )
   return html.toString()
}

private fun blockResponseToHtml (blockResponse : BlockResponse) : String
{
   val html = StringBuilder()

   if (blockResponse.hasBlock())
   {
      val block = blockResponse.block
      if (block.hasBlockV1())
      {
         val blockV1 = block.blockV1

         if (blockV1.hasPayload())
         {
            val payload = blockV1.payload
            html.append( "<p>Created time: <span class='number'>${payload.createdTime}</span></p>" )
            html.append( "<p>Accepted tx: <span class='number'>${payload.txNumber}</span></p>" )
            html.append( "<p>Rejected tx: <span class='number'>${payload.rejectedTransactionsHashesCount}</span></p>" )
            html.append(transactionListToHtml( payload.transactionsList ))
         }
         
         var delimit = false
         html.append( "<p>Signatures: </p><p class='hex'>[" )

         blockV1.signaturesList.forEach { signature ->
            if (delimit++)
               html.append( ", " )
            html.append( "${signature.signature}" )
         }
         html.append( "]</p>" )
      }
   }

   return html.toString()
}

private fun transactionListToHtml (transactionList : List<Transaction>, includeHash : Boolean = true, includeInfo : Boolean = false, includeCommands : Boolean = false) : StringBuilder
{
   val html = StringBuilder()
   
   transactionList.forEachIndexed { index, transaction ->

      html.append( "<p>Transaction nr: <span class='number'>${index}</span></p>" )

      if (includeHash)
         html.append( "<p>Hash: <span class='hex'>${transaction.hash()}</span></p>" )        

      if (transaction.hasPayload())
      {
         val payload = transaction.payload
         if (payload.hasBatch())
            html.append( "<p>Batch type: <span class='ordinal'>${payload.batch.type.valueDescriptor.name}</span></p>" )
            
         if (payload.hasReducedPayload())
         {
            val reducedPayload = payload.reducedPayload
            if (includeInfo)
            {
               html.append( "<p>Creator: <span class='name'>${reducedPayload.creatorAccountId}</span></p>" )
               html.append( "<p>Created time: <span class='number'>${reducedPayload.createdTime}</span></p>" )
               html.append( "<p>Quorum: <span class='number'>${reducedPayload.quorum}</span></p>" )
            }
                     
            if (includeCommands)
            {
               html.append( "<p>Commands:</p>" )
               for (command in payload.reducedPayload.commandsList)
                  html.append( "<p class='cmd'>${command}</p>" )
            }
         }
      }

      if (includeInfo)
      {
         var delimit = false
         html.append( "<p>Signatures: </p><p class='hex'>[" )
         transaction.signaturesList.forEach { signature ->
            if (delimit++)
               html.append( ", " )
            html.append( "${signature.signature}" )
         }
         html.append( "]</p>" )
      }   
   }

   return html
}
