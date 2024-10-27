package fe.linksheet.debug.activity


import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import fe.linksheet.activity.BaseComponentActivity
import fe.linksheet.composable.ui.BoxAppHost
import fe.linksheet.image.ImageFactory
import fe.linksheet.module.app.AndroidPackageInfoService
import fe.linksheet.module.app.PackageCacheItem
import fe.linksheet.module.app.PackageInfoCacheService
import fe.linksheet.module.repository.app.AppDomainVerificationStateRepository
import fe.linksheet.module.repository.app.InstalledAppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@RequiresApi(Build.VERSION_CODES.S)
class InstalledAppCacheServiceTestActivity : BaseComponentActivity(), KoinComponent {
    private val installedAppRepository by inject<InstalledAppRepository>()
    private val appDomainVerificationStateRepository by inject<AppDomainVerificationStateRepository>()


    private val packageInfoService by lazy { AndroidPackageInfoService(this) }

    private val cacheService by lazy {
        PackageInfoCacheService(
            density = { resources.displayMetrics.density },
            packageInfoService = packageInfoService,
            installedAppRepository = installedAppRepository,
            appDomainVerificationStateRepository = appDomainVerificationStateRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent(edgeToEdge = true) {
            BoxAppHost(modifier = Modifier.systemBarsPadding()) {
                Content()
            }
        }
    }

    @Composable
    private fun Content() {
        val packages = remember { mutableStateListOf<PackageCacheItem>() }
        val state = rememberLazyListState()

        LaunchedEffect(key1 = packages.size) {
            if (state.isScrollInProgress) {
                return@LaunchedEffect
            }

            val idx = state.layoutInfo.totalItemsCount - 1

            runCatching { state.scrollToItem(idx.coerceAtLeast(0)) }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 4.dp)
        ) {
            Button(onClick = {
                lifecycleScope.launch(Dispatchers.IO) {
                    packages.clear()
                    cacheService.getAllInstalled().collect {
                        packages.add(it)
                    }
                }
            }) {
                Text(text = "Load all")
            }

            LazyColumn(state = state, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                itemsIndexed(items = packages, key = { _, item -> item.installedApp.packageName }) { idx, item ->
                    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                        AppInfo(idx = idx, item = item)
                    }
                }
            }
        }
    }


    @Composable
    private fun AppInfo(idx: Int, item: PackageCacheItem) {
        val installedApp = item.installedApp

        val bitmap = remember(installedApp.icon) {
            ImageFactory.convertToBitmap(installedApp.icon!!).asImageBitmap()
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "$idx")

            Image(
                modifier = Modifier.size(32.dp),
                bitmap = bitmap,
                contentDescription = "",
            )

            Column {
                Text(text = installedApp.label ?: "<no-label>", fontWeight = FontWeight.Bold)
                Text(text = installedApp.packageName)
                Text(text = installedApp.iconHash.toString())
            }
        }
    }
}
