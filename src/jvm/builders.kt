package kick

import java.security.KeyPair
import org.bouncycastle.jcajce.provider.digest.SHA3
import jp.co.soramitsu.crypto.ed25519.Ed25519Sha3
import pen.toHex
import pen.sha3Digest
import pen.par.KCrypto
import iroha.protocol.Commands.Command
import iroha.protocol.Queries.Query
import iroha.protocol.Queries.BlocksQuery
import iroha.protocol.TransactionOuterClass.Transaction
import iroha.protocol.Primitive.Signature
import iroha.protocol.TransactionOuterClass.Transaction.Payload.ReducedPayload

fun Query.Builder.sign (crypto : KCrypto) = this.apply {
   signature = signs( payload.toByteArray(), crypto )
}

fun BlocksQuery.Builder.sign (crypto : KCrypto) = this.apply {
   signature = signs( meta.toByteArray(), crypto )
}

fun Transaction.Builder.sign (crypto : KCrypto) = addSignatures(signs( payload.toByteArray(), crypto ))


internal fun signs (bytes : ByteArray, crypto : KCrypto) : Signature
{
   val ed25519Sha3 = crypto.ed25519Sha3()
   val publicKey = ed25519Sha3.publicKey().toHex()
   val signature = ed25519Sha3.prove( bytes ).toHex()

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
