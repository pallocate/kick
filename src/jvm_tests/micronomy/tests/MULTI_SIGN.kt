package kick.tests.micronomy.tests

import pen.now
import pen.tests.Patricia
import pen.tests.Artysan
import pen.tests.David
import iroha.protocol.Transaction
import iroha.protocol.payload
import iroha.protocol.reducedPayload
import iroha.protocol.setAccountQuorum
import iroha.protocol.addSignatory
import iroha.protocol.removeSignatory
import kick.*

/** Patricia adds and removes signatories and changes the number of required. */
val MULTI_SIGN = TxPair(

   first =
   Transaction {
      payload {
         reducedPayload {
            creatorAccountId = "patricia@artysan"
            createdTime = now()
            quorum = 1

            commands {
               /* Two signatories are added. */
               command {
                  addSignatory {
                     accountId = "patricia@artysan"
                     publicKey = David.publicKey()
                  }
               }
               command {
                  addSignatory {
                     accountId = "patricia@artysan"
                     publicKey = Artysan.publicKey()
                  }
               }
               /* Two signatures are required. */
               command {
                  setAccountQuorum {
                     accountId = "patricia@artysan"
                     quorum = 2
                  }
               }
            }
         }
      }
      sign( Patricia.crypto )
   },

   second =
   Transaction {
      payload {
         reducedPayload {
            creatorAccountId = "patricia@artysan"
            createdTime = now()
            quorum = 2

            commands {
               /* Only one signature is required. */
               command {
                  setAccountQuorum {
                     accountId = "patricia@artysan"
                     quorum = 1
                  }
               }
               /* One signatory is removed. */
               command {
                  removeSignatory {
                     accountId = "patricia@artysan"
                     publicKey = Artysan.publicKey()
                  }
               }
            }
         }
      }
      sign( David.crypto )
      sign( Artysan.crypto )
   }
)
