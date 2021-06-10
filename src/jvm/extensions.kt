package kick

import pen.toHex
import pen.sha3Digest
import pen.IrohaSigner
import iroha.protocol.Commands.Command
import iroha.protocol.Queries.Query
import iroha.protocol.Queries.BlocksQuery
import iroha.protocol.QryResponses.*
import iroha.protocol.Primitive.Peer
import iroha.protocol.Primitive.Signature
import iroha.protocol.Primitive.RolePermission
import iroha.protocol.TransactionOuterClass.Transaction
import iroha.protocol.TransactionOuterClass.Transaction.Payload.ReducedPayload
import iroha.protocol.BlockOuterClass.Block_v1

fun Query.Builder.sign (irohaSigner : IrohaSigner)
{
   if (hasPayload())
      signature = createSignature( payload.toByteArray(), irohaSigner )
}

fun BlocksQuery.Builder.sign (irohaSigner : IrohaSigner)
{
   if (hasMeta())
      signature = createSignature( meta.toByteArray(), irohaSigner )
}

fun Transaction.Builder.sign (irohaSigner : IrohaSigner) 
{  
   if (hasPayload())
      addSignatures(createSignature( payload.toByteArray(), irohaSigner ))
}

fun Transaction.hash () = if (hasPayload())
   sha3Digest( payload.toByteArray() ).toHex()
else
   ""

fun PendingTransactionsPageResponse.Builder.transactions (block : TransactionList.() -> Unit) =
   addAllTransactions(TransactionList().apply( block ))

fun TransactionsPageResponse.Builder.transactions (block : TransactionList.() -> Unit) =
   addAllTransactions(TransactionList().apply( block ))

fun TransactionsResponse.Builder.transactions (block : TransactionList.() -> Unit) =
   addAllTransactions(TransactionList().apply( block ))
fun TransactionList.transaction (block : Transaction.Builder.() -> Unit) =
   add(Transaction.newBuilder().apply( block ).build())
class TransactionList : ArrayList<Transaction>()


fun Block_v1.Payload.Builder.transactions (block : TransactionList.() -> Unit) =
   addAllTransactions(TransactionList().apply( block ))

fun Block_v1.Builder.signatures (block : SignatureList.() -> Unit) =
   addAllSignatures(SignatureList().apply( block ))
fun Transaction.Builder.signatures (block : SignatureList.() -> Unit) =
   addAllSignatures(SignatureList().apply( block ))
fun SignatureList.signature (block : Signature.Builder.() -> Unit) =
   add(Signature.newBuilder().apply( block ).build())
class SignatureList : ArrayList<Signature>()


fun ReducedPayload.Builder.commands (block : CommandList.() -> Unit) =
   addAllCommands(CommandList().apply( block ))
fun CommandList.command (block : Command.Builder.() -> Unit) =
   add(Command.newBuilder().apply( block ).build())
class CommandList : ArrayList<Command>()


fun AccountAssetResponse.Builder.accountAssets (block : AccountAssetList.() -> Unit) =
   addAllAccountAssets(AccountAssetList().apply( block ))
fun AccountAssetList.asset (block : AccountAsset.Builder.() -> Unit) =
   add(AccountAsset.newBuilder().apply( block ).build())
class AccountAssetList : ArrayList<AccountAsset>()


fun PeersResponse.Builder.peers (block : PeerList.() -> Unit) =
   addAllPeers(PeerList().apply( block ))
fun PeerList.peer (block : Peer.Builder.() -> Unit) =
   add(Peer.newBuilder().apply( block ).build())
class PeerList : ArrayList<Peer>()


fun SignatoriesResponse.Builder.keys (keyList : List<String>) = addAllKeys( keyList )
fun RolesResponse.Builder.roles (roleList : List<String>) = addAllRoles( roleList )
fun AccountResponse.Builder.roles (roleList : List<String>) = addAllAccountRoles(roleList)
fun RolePermissionsResponse.Builder.rolePermissions (permissionList : List<RolePermission>) = addAllPermissions( permissionList )
