package fe.linksheet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fe.linksheet.data.entity.LibRedirectDefault

@Dao
interface LibRedirectDefaultDao {
    @Query("SELECT * FROM lib_redirect_default WHERE serviceKey = :serviceKey")
    fun getLibRedirectDefaultByServiceKey(serviceKey: String): LibRedirectDefault

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(resolvedRedirect: LibRedirectDefault)
}