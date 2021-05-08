package kick.tests.dumpserver

import io.grpc.ServerBuilder

object Main
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
