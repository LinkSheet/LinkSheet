package fe.linksheet.module.database.entity.app

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "installed_app")
class InstalledApp(
    @PrimaryKey val packageName: String,
    val label: String? = null,
    val flags: Int,
    val iconHash: Int,
    val icon: ByteArray? = null,
)


@Entity(tableName = "app_domain_verification_state", primaryKeys = ["packageName", "domain"])
class AppDomainVerificationState(
    val packageName: String,
    val domain: String,
    val state: Int,
)
