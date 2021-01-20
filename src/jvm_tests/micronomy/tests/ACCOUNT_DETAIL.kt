package kick.tests.micronomy.tests

import pen.now
import pen.tests.David
import iroha.protocol.Transaction
import iroha.protocol.payload
import iroha.protocol.reducedPayload
import iroha.protocol.setAccountDetail
import iroha.protocol.compareAndSetAccountDetail
import kick.*

/** David adds some account detail information, then changes it. */
val ACCOUNT_DETAIL = TxPair(

   first =
   Transaction {
      payload {
         reducedPayload {
            creatorAccountId = "david@crowbeach"
            createdTime = now()
            quorum = 1

            commands {
               command {
                  setAccountDetail {
                     accountId = "david@crowbeach"
                     key = "pk"
                     value = "ffffffff"
                  }
               }
            }
         }
      }
      sign( David.crypto )
   },

   second =
   Transaction {
      payload {
         reducedPayload {
            creatorAccountId = "david@crowbeach"
            createdTime = now()
            quorum = 1

            commands {
               command {
                  compareAndSetAccountDetail {
                     accountId = "david@crowbeach"
                     key = "pk"
                     value = "cccccccc"
                     oldValue = "ffffffff"
                  }
               }
            }
         }
      }
      sign( David.crypto )
   }
)

/*
import iroha.protocol.Primitive.GrantablePermission.can_set_my_account_detail
   grantPermission {
      accountId = "david@crowbeach"
      permission = can_set_my_account_detail }
*/
