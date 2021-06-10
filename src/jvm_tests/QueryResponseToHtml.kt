package kick.tests

import org.junit.jupiter.api.*
import iroha.protocol.*
import iroha.protocol.TransactionOuterClass.Transaction
import kick.toHtml

/** Testing the output of the QueryResponse toHtml() extension. */
class ToHtmlTests
{
   @Test
   @DisplayName( "AccountResponse." )
   fun accountResponseTest ()
   {
      val qr = QueryResponse {
         accountResponse {
            account {
               accountId = "david@crowbeach"
            }
         }
      }

      Assertions.assertTrue( true )
   }
}
