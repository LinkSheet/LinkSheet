package fe.linksheet.activity.bottomsheet.dev.grid

//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//private fun BtmSheetGridUI(
//    bottomSheetViewModel: BottomSheetViewModel,
//    hideDrawer: () -> Unit,
//    showPackage: Boolean,
//    previewUrl: Boolean,
//    forPreferred: Boolean
//) {
//    val result = bottomSheetViewModel.resolveResult!!
//    if (result !is BottomSheetResult.BottomSheetSuccessResult) return
//    if (result.isEmpty) {
//        Row(
//            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
//        ) {
//            Text(
//                text = stringResource(id = R.string.no_app_to_handle_link_found)
//            )
//        }
//
//        Spacer(modifier = Modifier.height(10.dp))
//        return
//    }
//    var selectedItem by remember { mutableIntStateOf(-1) }
//    val doesSelectedBrowserSupportsIncognitoLaunch = rememberSaveable {
//        mutableStateOf(false)
//    }
//    val modifier: AppListModifier = @Composable { index, info ->
//        Modifier
//            .fillMaxWidth()
//            .padding(start = 10.dp, end = 10.dp)
//            .clip(defaultRoundedCornerShape)
//            .combinedClickable(onClick = {
//                bottomSheetViewModel.privateBrowser.value = shouldShowRequestPrivate(info)
//                bottomSheetViewModel.appInfo.value = info
//                doesSelectedBrowserSupportsIncognitoLaunch.value =
//                    bottomSheetViewModel.privateBrowser.value != null
//                if (bottomSheetViewModel.privateBrowser.value == null) {
//                    if (bottomSheetViewModel.singleTap.value || forPreferred) {
//                        launchApp(result, info)
//                    } else {
//                        if (selectedItem == index) launchApp(result, info)
//                        else selectedItem = index
//                    }
//                }
//            }, onDoubleClick = {
//                doesSelectedBrowserSupportsIncognitoLaunch.value =
//                    shouldShowRequestPrivate(info) != null
//                if (!bottomSheetViewModel.singleTap.value && !doesSelectedBrowserSupportsIncognitoLaunch.value) {
//                    launchApp(result, info)
//                } else {
//                    startPackageInfoActivity(info)
//                }
//            }, onLongClick = {
//                if (bottomSheetViewModel.singleTap.value) {
//                    selectedItem = index
//                } else {
//                    startPackageInfoActivity(info)
//                }
//            })
//            .background(
//                if (selectedItem == index) LocalContentColor.current.copy(
//                    0.1f
//                ) else Color.Transparent
//            )
//            .padding(appListItemPadding)
//    }
//    if (forPreferred) {
//        val filteredItem = result.filteredItem!!
//        LaunchedEffect(key1 = filteredItem) {
//            bottomSheetViewModel.appInfo.value = filteredItem
//            bottomSheetViewModel.privateBrowser.value = shouldShowRequestPrivate(filteredItem)
//        }
//    }
//
//    // https://stackoverflow.com/questions/69382494/jetpack-compose-vertical-grid-single-item-span-size
//    LazyVerticalGrid(columns = GridCells.Adaptive(85.dp),
//        modifier = Modifier
//            .fillMaxWidth()
//            .wrapContentHeight()
//            .animateContentSize(),
//        content = {
//
//
//            itemsIndexed(
//                items = result.resolved,
//                key = { _, item -> item.flatComponentName }) { index, info ->
//                Column(
//                    modifier = modifier(
//                        index, info
//                    ), horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Image(
//                        bitmap = info.iconBitmap,
//                        contentDescription = info.label,
//                        modifier = Modifier.size(32.dp)
//                    )
//
//                    Text(
//                        text = info.label,
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis,
//                        fontSize = 14.sp,
//                        modifier = Modifier.padding(top = 3.dp)
//                    )
//
//                    if (showPackage) {
//                        Text(
//                            text = info.packageName,
//                            fontSize = 12.sp,
//                            overflow = TextOverflow.Ellipsis,
//                            maxLines = 1
//                        )
//                    }
//                }
//            }
//            if (!forPreferred) {
//                item(span = { GridItemSpan(maxLineSpan) }) {
//                    ButtonColumn(
//                        bottomSheetViewModel = bottomSheetViewModel,
//                        enabled = selectedItem != -1,
//                        uri = null,
//                        onClick = { always ->
//                            launchApp(
//                                result, result.resolved[selectedItem], always
//                            )
//                        },
//                        hideDrawer = hideDrawer
//                    )
//                }
//            }
//        })
//}
