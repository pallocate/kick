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

/** In this minimalistic example there are only two consumer councils, each with only one member. */
val CONSUMER_COUNCILS = TxPair(

   first =
   Transaction {
      payload {
         reducedPayload {
            creatorAccountId = "admin@system"
            createdTime = now()
            quorum = 1

            commands {
               /* Consumer councils are a part of the commons. */
               command {
                  createAccount {
                     accountName = "artysan"
                     domainId = "commons"
                     publicKey = Artysan.publicKey()
                  }
               }
               command {
                  appendRole {
                     accountId = "artysan@commons"
                     roleName = "council"
                  }
               }
               command {
                  createAccount {
                     accountName = "crowbeach"
                     domainId = "commons"
                     publicKey = CrowBeach.publicKey()
                  }
               }
               command {
                  appendRole {
                     accountId = "crowbeach@commons"
                     roleName = "council"
                  }
               }
               /* But they also have their own domain. */
               command {
                  createDomain {
                     domainId = "artysan"
                     defaultRole = "default"
                  }
               }
               command {
                  createAccount {
                     accountName = "artysan"
                     domainId = "artysan"
                     publicKey = Artysan.publicKey()
                  }
               }
               command {
                  appendRole {
                     accountId = "artysan@artysan"
                     roleName = "council"
                  }
               }
               command {
                  createDomain {
                     domainId = "crowbeach"
                     defaultRole = "default"
                  }
               }
               command {
                  createAccount {
                     accountName = "crowbeach"
                     domainId = "crowbeach"
                     publicKey = CrowBeach.publicKey()
                  }
               }
               command {
                  appendRole {
                     accountId = "crowbeach@crowbeach"
                     roleName = "council"
                  }
               }
               /* All persons in the economy is a member of exactly one consumption council. */
               command {
                  createAccount {
                     accountName = "patricia"
                     domainId = "artysan"
                     publicKey = Patricia.publicKey()
                  }
               }
               command {
                  appendRole {
                     accountId = "patricia@artysan"
                     roleName = "member"
                  }
               }
               command {
                  createAccount {
                     accountName = "david"
                     domainId = "crowbeach"
                     publicKey = David.publicKey()
                  }
               }
               command {
                  appendRole {
                     accountId = "david@crowbeach"
                     roleName = "member"
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
               /* Councils are creditors, in the meaning that they can issue credits.
                * They are not allowed to issue more credits then what the common plan states.
                * Enforcement of this is not performed by the block chain, it must be done elsewise. */
               command {
                  appendRole {
                     accountId = "artysan@commons"
                     roleName = "creditor"
                  }
               }
               command {
                  appendRole {
                     accountId = "crowbeach@commons"
                     roleName = "creditor"
                  }
               }
               command {
                  appendRole {
                     accountId = "patricia@artysan"
                     roleName = "user"
                  }
               }
               command {
                  appendRole {
                     accountId = "david@crowbeach"
                     roleName = "user"
                  }
               }
            }
         }
      }
      sign( Credmin.crypto )
   }
)
