package kick.tests.micronomy.setup

import pen.now
import pen.tests.*
import iroha.protocol.Transaction
import iroha.protocol.payload
import iroha.protocol.reducedPayload
import iroha.protocol.createDomain
import iroha.protocol.createAccount
import iroha.protocol.appendRole
import kick.*

val WORKERS_COUNCILS = TxPair(

   first =
   Transaction {
      payload {
         reducedPayload {
            creatorAccountId = "admin@system"
            createdTime = now()
            quorum = 1

            commands {
               command {
                  createAccount {
                     accountName = "factory"
                     domainId = "commons"
                     publicKey = Factory.publicKey()
                  }
               }
               command {
                  appendRole {
                     accountId = "factory@commons"
                     roleName = "council"
                  }
               }

               command {
                  createAccount {
                     accountName = "farmlands"
                     domainId = "commons"
                     publicKey = Farmlands.publicKey()
                  }
               }
               command {
                  appendRole {
                     accountId = "farmlands@commons"
                     roleName = "council"
                  }
               }

               command {
                  createAccount {
                     accountName = "hospital"
                     domainId = "commons"
                     publicKey = Hospital.publicKey()
                  }
               }
               command {
                  appendRole {
                     accountId = "hospital@commons"
                     roleName = "council"
                  }
               }

               command {
                  createAccount {
                     accountName = "university"
                     domainId = "commons"
                     publicKey = University.publicKey()
                  }
               }
               command {
                  appendRole {
                     accountId = "university@commons"
                     roleName = "council"
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
               command {
                  appendRole {
                     accountId = "factory@commons"
                     roleName = "creditor"
                  }
               }
               command {
                  appendRole {
                     accountId = "farmlands@commons"
                     roleName = "creditor"
                  }
               }
               command {
                  appendRole {
                     accountId = "hospital@commons"
                     roleName = "creditor"
                  }
               }
               command {
                  appendRole {
                     accountId = "university@commons"
                     roleName = "creditor"
                  }
               }
            }
         }
      }
      sign( Credmin.crypto )
   }
)
