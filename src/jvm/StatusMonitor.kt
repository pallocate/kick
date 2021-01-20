package kick

import iroha.protocol.Endpoint.ToriiResponse

interface StatusMonitor
{
   fun newMonitorTask (txHash : String) : KMonitorTask
   fun acceptedTxHashes () : List<String>
   fun reportRejectedTx() : StatusMonitor
   /** Should return true if all committed transactions was accepted */
   fun allSuccess () : Boolean
}

class KMonitorTask (val txHash : String)
{
   val responses = ArrayList<ToriiResponse>()

   fun addResponse (response : ToriiResponse)
   { responses.add( response ) }

   fun success () = responses.none { it.errorCode > 0 }
}

open class KStatusMonitor () : StatusMonitor
{
   private val monitorTasks = ArrayList<KMonitorTask>()

   final override fun newMonitorTask (txHash : String) : KMonitorTask
   {
      val task = KMonitorTask( txHash )
      monitorTasks.add( task )

      return task
   }

   final override fun acceptedTxHashes () : List<String>
   {
      val acceptedHashes = ArrayList<String>()

      for (monitorTask in monitorTasks)
      {
         if (monitorTask.success())
            acceptedHashes.add( monitorTask.txHash )
      }

      return acceptedHashes
   }

   final override fun allSuccess () = monitorTasks.map { it.success() }.fold( true ) { acc, success -> acc && success }

   /** Prints out failed transaction responses. */
   override fun reportRejectedTx() : StatusMonitor
   {
      monitorTasks.forEachIndexed {idx, monitorTask ->
         monitorTask.run {

            if (!success())
            {
               println( "Transaction nr.${idx+1} failed!" )
               println( "responses: {" )

               for (response in responses)
               {
                  val status = response.txStatus.toString()
                  print( "   $status" )
                  println(
                     if (status.contains( "FAILED" ))
                        " - Command nr.${response.failedCmdIndex+1} (${response.errOrCmdName})"
                     else
                        ""
                  )
               }

               println( "}\n" )
            }
         }
      }

      return this
   }
}
