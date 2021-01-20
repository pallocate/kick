package kick.tests.micronomy.queries

import pen.now
import kick.sign
import pen.tests.Admin
import pen.tests.HardwareStore
import iroha.protocol.Query
import iroha.protocol.BlocksQuery
import iroha.protocol.payload
import iroha.protocol.meta
import iroha.protocol.getRoles
import iroha.protocol.getAccountDetail
import iroha.protocol.paginationMeta
import iroha.protocol.firstRecordId

val ROLES = Query {
   payload {
      getRoles {}
      meta {
         creatorAccountId = "admin@system"
         createdTime = now()
         queryCounter = 1L
      }
   }

   sign( Admin.crypto )
}

val GROCERY_DETAILS = Query {
   payload {
      getAccountDetail {
         accountId = "hardware@supplier"
         key = "axe"
         writer = "hardware@supplier"
         paginationMeta {
            pageSize = 1
         }
      }
      meta {
         creatorAccountId = "hardware@supplier"
         createdTime = now()
         queryCounter = 2L
      }
   }

   sign( HardwareStore.crypto )
}

val BLOCKS = BlocksQuery {

   meta {
      creatorAccountId = "admin@system"
      createdTime = now()
      queryCounter = 0L
   }

   sign( Admin.crypto )
}
