package fe.linksheet.module.preference

import fe.linksheet.util.CryptoUtil
import fe.linksheet.util.Percentage
import java.math.BigInteger

object Bucket {

    fun isEligible(flag: String, userId: String, rolloutTo: Percentage, buckets: Long = 1_000_000L): Boolean {
        val input = ("$flag@$userId").encodeToByteArray()
        val hash = CryptoUtil.md5(input)

        val hashInt = BigInteger(hash)
        val modulus = BigInteger.valueOf(buckets)

        val userBucket = hashInt.mod(modulus).toLong()
        val threshold = (rolloutTo.fraction * buckets.toDouble()).toInt() - 1

        return userBucket in 0..threshold
    }
}
