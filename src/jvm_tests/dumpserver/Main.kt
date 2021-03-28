package kick.tests.dumpserver

import io.grpc.ServerBuilder

object Main
{
   val server = ServerBuilder
      .forPort( 50051 )
      .addService( DumpCommandService )
      .addService( DumpQueryService )
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
