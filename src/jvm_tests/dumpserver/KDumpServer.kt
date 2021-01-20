package kick.tests.dumpserver

import io.grpc.ServerBuilder

class KDumpServer
{
   val server = ServerBuilder
      .forPort( 50051 )
      .addService( DumpCommandService )
      .addService( DumpQueryService )
      .directExecutor()
      .build()
      .start()

   init
   {
      Runtime.getRuntime().addShutdownHook(Thread() { server.shutdown() })
      server.awaitTermination()
   }
}
