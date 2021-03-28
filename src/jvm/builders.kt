package kick

import pen.toHex
import pen.sha3Digest
import pen.IrohaSigner
import iroha.protocol.Commands.Command
import iroha.protocol.Queries.Query
import iroha.protocol.Queries.BlocksQuery
import iroha.protocol.TransactionOuterClass.Transaction
import iroha.protocol.Primitive.Signature
import iroha.protocol.TransactionOuterClass.Transaction.Payload.ReducedPayload

fun Query.Builder.sign (irohaSigner : IrohaSigner) = this.apply {
   signature = createSignature( payload.toByteArray(), irohaSigner )
}

fun BlocksQuery.Builder.sign (irohaSigner : IrohaSigner) = this.apply {
   signature = createSignature( meta.toByteArray(), irohaSigner )
}

fun Transaction.Builder.sign (irohaSigner : IrohaSigner) = addSignatures(createSignature( payload.toByteArray(), irohaSigner ))

fun createSignature (bytes : ByteArray, irohaSigner : IrohaSigner) : Signature
{
   val publicKey = irohaSigner.publicKey()
   val signature = irohaSigner.sign( bytes )

   return Signature
      .newBuilder()
      .setPublicKey( publicKey )
      .setSignature( signature )
      .build()
}

fun Transaction.hash () = sha3Digest( payload.toByteArray() ).toHex()

fun ReducedPayload.Builder.commands (block : CommandList.() -> Unit) =
   addAllCommands(CommandList().apply( block ))

/** Complementary command to the auto generated dsl, */
fun CommandList.command (block : Command.Builder.() -> Unit) =
   add(Command.newBuilder().apply( block ).build())

class CommandList : ArrayList<Command>()
