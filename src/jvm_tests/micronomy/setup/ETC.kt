package kick.tests.micronomy.setup

import pen.now
import pen.tests.Admin
import pen.tests.Credmin
import pen.tests.HardwareStore
import iroha.protocol.Transaction
import iroha.protocol.payload
import iroha.protocol.reducedPayload
import iroha.protocol.createDomain
import iroha.protocol.createAccount
import iroha.protocol.createAsset
import iroha.protocol.appendRole
import kick.*

val ETC = TxPair(

   first =
   Transaction {
      payload {
         reducedPayload {
            creatorAccountId = "admin@system"
            createdTime = now()
            quorum = 1

            commands {
               /* A hardware store. */
               command {
                  createAccount {
                     accountName = "hardware"
                     domainId = "supplier"
                     publicKey = HardwareStore.publicKey()
                  }
               }
            }
         }
      }
      sign( Admin.crypto )
   },

   second =
   Transaction {
      payload {
         reducedPayload {
            creatorAccountId = "credmin@system"
            createdTime = now()
            quorum = 1

            commands {
               /* Hardware store is a debitor in the meaning that it receives and destroys credits. */
               command {
                  appendRole {
                     accountId = "hardware@supplier"
                     roleName = "debitor"
                  }
               }

               /* Creating the credits. */
               command {
                  createAsset {
                     assetName = "credit"
                     domainId = "commons"
                     precision = 4
                  }
               }
               command {
                  createAsset {
                     assetName = "credit"
                     domainId = "artysan"
                     precision = 0
                  }
               }
               command {
                  createAsset {
                     assetName = "credit"
                     domainId = "crowbeach"
                     precision = 0
                  }
               }
            }
         }
      }
      sign( Credmin.crypto )
   }
)
