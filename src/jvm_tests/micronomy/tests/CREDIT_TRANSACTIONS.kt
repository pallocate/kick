package kick.tests.micronomy.tests

import pen.now
import pen.tests.Farmlands
import pen.tests.HardwareStore
import iroha.protocol.Transaction
import iroha.protocol.payload
import iroha.protocol.reducedPayload
import iroha.protocol.addAssetQuantity
import iroha.protocol.transferAsset
import iroha.protocol.subtractAssetQuantity
import kick.*

/** The Farmlands council issues some credits, buys some stuff and doles out salary. */
val CREDIT_TRANSACTIONS = TxPair(

   first =
   Transaction {
      payload {
         reducedPayload {
            creatorAccountId = "farmlands@commons"
            createdTime = now()
            quorum = 1

            commands {
               /* Farmlands workers council issues 500k commons credits in approved expences. */
               command {
                  addAssetQuantity {
                     assetId = "credit#commons"
                     amount = "500000.0000"
                  }
               }
               /* And uses some of it to buy tools from the hardware store. */
               command {
                  transferAsset {
                     srcAccountId = "farmlands@commons"
                     destAccountId = "hardware@supplier"
                     assetId = "credit#commons"
                     description = "27112005"
                     amount = "3400.0000"
                  }
               }

               /* It also issues and transfers 100k crow beach credits to david as salary. */
               command {
                  addAssetQuantity {
                     assetId = "credit#crowbeach"
                     amount = "100000"
                  }
               }
               command {
                  transferAsset {
                     srcAccountId = "farmlands@commons"
                     destAccountId = "david@crowbeach"
                     assetId = "credit#crowbeach"
                     description = "salary"
                     amount = "100000"
                  }
               }
            }
         }
      }
      sign( Farmlands.crypto )
   },

   second =
   Transaction {
      payload {
         reducedPayload {
            creatorAccountId = "hardware@supplier"
            createdTime = now()
            quorum = 1

            commands {
               /* The hardware store recieves the credits and destroys them, since they have been used. */
               command {
                  subtractAssetQuantity {
                     assetId = "credit#commons"
                     amount = "3400.0000"
                  }
               }
            }
         }
      }
      sign( HardwareStore.crypto )
   }
)
