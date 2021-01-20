package kick.tests

import kotlinx.coroutines.runBlocking
import kick.Utils.setup
import kick.Utils.test
import kick.submit
import kick.tests.dumpserver.KDumpServer
import kick.tests.micronomy.setup.*
import kick.tests.micronomy.tests.*
import kick.tests.micronomy.queries.ROLES

object Main
{
   val SUCCESS = true
   val FAIL = false

   @JvmStatic
   @kotlinx.coroutines.ExperimentalCoroutinesApi
   fun main (args : Array<String>)
   {
      if (args.contains( "-s" ))
         KDumpServer()
      else
         runBlocking {

            /* Setup the micronomy example. */
            if (setup(listOf( CONSUMER_COUNCILS, WORKERS_COUNCILS, ETC )))
            {
               /* Run some tests. */
               val testsSucceeded = when (FAIL)
               {
                  test( CREDIT_TRANSACTIONS ) -> FAIL
                  test( MULTI_SIGN ) -> FAIL
                  test( ACCOUNT_DETAIL ) -> FAIL
                  else -> SUCCESS
               }

               if (testsSucceeded)
               {
                  /* Query the block chain. */
                  submit( ROLES )
               }
            }
         }
   }
}
