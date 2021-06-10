package kick.utils

import io.grpc.ServerBuilder

/** Dump services to mimic Iroha, for testing purposes. */
object DumpServer
{
   val server = ServerBuilder
      .forPort( 50051 )
      .addService( DumpQueryService )
      .addService( DumpCommandService )
      .directExecutor()
      .build()
      .start()

   @JvmStatic
   fun main (args : Array<String>)
   {
      Runtime.getRuntime().addShutdownHook(Thread() { server.shutdown() })
      server.awaitTermination()
   }
}
