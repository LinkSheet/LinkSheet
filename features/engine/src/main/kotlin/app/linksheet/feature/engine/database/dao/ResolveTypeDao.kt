package app.linksheet.feature.engine.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import app.linksheet.api.database.BaseDao
import app.linksheet.feature.engine.database.entity.ResolveType
import kotlinx.coroutines.flow.Flow

@Dao
interface ResolveTypeDao : BaseDao<ResolveType> {
    @Query("SELECT * FROM resolve_type")
    override fun getAll(): Flow<List<ResolveType>>
}
