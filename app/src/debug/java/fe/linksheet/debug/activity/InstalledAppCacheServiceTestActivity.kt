package fe.linksheet.debug.activity


//import fe.linksheet.module.app.PackageCacheItem
//import fe.linksheet.module.app.PackageInfoCacheService
//import fe.linksheet.module.repository.app.AppDomainVerificationStateRepository
//import fe.linksheet.module.repository.app.InstalledAppRepository
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import fe.linksheet.activity.BaseComponentActivity
import fe.linksheet.module.app.AndroidPackageInfoService
import org.koin.core.component.KoinComponent

@RequiresApi(Build.VERSION_CODES.S)
class InstalledAppCacheServiceTestActivity : BaseComponentActivity(), KoinComponent {
//    private val installedAppRepository by inject<InstalledAppRepository>()
//    private val appDomainVerificationStateRepository by inject<AppDomainVerificationStateRepository>()


    private val packageInfoService by lazy { AndroidPackageInfoService(this) }

//    private val cacheService by lazy {
//        PackageInfoCacheService(
//            density = { resources.displayMetrics.density },
//            packageInfoService = packageInfoService,
//            installedAppRepository = installedAppRepository,
//            appDomainVerificationStateRepository = appDomainVerificationStateRepository
//        )
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent(edgeToEdge = true) {
//            BoxAppHost(modifier = Modifier.systemBarsPadding()) {
//                Content()
//            }
        }
    }

//    @Composable
//    private fun Content() {
//        val packages = remember { mutableStateListOf<PackageCacheItem>() }
//        val state = rememberLazyListState()
//
//        LaunchedEffect(key1 = packages.size) {
//            if (state.isScrollInProgress) {
//                return@LaunchedEffect
//            }
//
//            val idx = state.layoutInfo.totalItemsCount - 1
//
//            runCatching { state.scrollToItem(idx.coerceAtLeast(0)) }
//        }
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(all = 4.dp)
//        ) {
////            Button(onClick = {
////                lifecycleScope.launch(Dispatchers.IO) {
////                    packages.clear()
////                    cacheService.getAllInstalled().collect {
////                        packages.add(it)
////                    }
////                }
////            }) {
////                Text(text = "Load all")
////            }
////
////            LazyColumn(state = state, verticalArrangement = Arrangement.spacedBy(8.dp)) {
////                itemsIndexed(items = packages, key = { _, item -> item.installedApp.packageName }) { idx, item ->
////                    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
////                        AppInfo(idx = idx, item = item)
////                    }
////                }
////            }
//        }
//    }
//
//
//    @Composable
//    private fun AppInfo(idx: Int, item: PackageCacheItem) {
//        val installedApp = item.installedApp
//
//        val bitmap = remember(installedApp.icon) {
//            ImageFactory.convertToBitmap(installedApp.icon!!).asImageBitmap()
//        }

//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(all = 4.dp),
//            horizontalArrangement = Arrangement.spacedBy(4.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(text = "$idx")
//
//            Image(
//                modifier = Modifier.size(32.dp),
//                bitmap = bitmap,
//                contentDescription = "",
//            )
//
//            Column {
//                Text(text = installedApp.label ?: "<no-label>", fontWeight = FontWeight.Bold)
//                Text(text = installedApp.packageName)
//                Text(text = installedApp.iconHash.toString())
//            }
//        }
//    }
}
