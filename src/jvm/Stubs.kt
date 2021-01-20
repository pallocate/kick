package kick

import io.grpc.ManagedChannelBuilder
import iroha.protocol.CommandService_v1CoroutineGrpc as CommandService
import iroha.protocol.QueryService_v1CoroutineGrpc as QueryService

object Stubs
{
   private val managedChannel = ManagedChannelBuilder
      .forAddress( "localhost", 50051 )
      .directExecutor()
      .usePlaintext()
      .build()

   val commandStub by lazy {CommandService.newStub( managedChannel )}
   val queryStub by lazy {QueryService.newStub( managedChannel )}
}

