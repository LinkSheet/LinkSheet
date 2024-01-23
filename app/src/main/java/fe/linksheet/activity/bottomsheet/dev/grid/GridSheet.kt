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
//            item(key = "previewUrl", span = { GridItemSpan(maxCurrentLineSpan) }) {
//                if (previewUrl && result.uri != null) {
//                    UrlBar(
//                        uri = result.uri,
//                        clipboardManager = viewModel.clipboardManager,
//                        urlCopiedToast = viewModel.urlCopiedToast,
//                        hideAfterCopying = viewModel.hideAfterCopying,
//                        showToast = {
//                            showToast(it)
//                        },
//                        hideDrawer = hideDrawer,
//                        shareUri = {
//                            startActivity(shareUri(result.uri))
//                            finish()
//                        }
//                    )
//                }
//            }
//            if (doesSelectedBrowserSupportsIncognitoLaunch.value) {
//                item(span = { GridItemSpan(maxCurrentLineSpan) }) {
//                    Column(modifier = Modifier
//                        .fillMaxWidth()
//                        .clickable {
//                            bottomSheetViewModel.appInfo.value?.let {
//                                launchApp(
//                                    result,
//                                    it, always = false
//                                )
//                            }
//                        }) {
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(start = 15.dp, end = 15.dp)
//                                .heightIn(min = preferredAppItemHeight),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Image(
//                                bitmap = bottomSheetViewModel.appInfo.value!!.iconBitmap,
//                                contentDescription = bottomSheetViewModel.appInfo.value!!.label,
//                                modifier = Modifier.size(32.dp)
//                            )
//
//                            Spacer(modifier = Modifier.width(10.dp))
//
//                            Column {
//                                Text(
//                                    text = stringResource(
//                                        id = R.string.open_with_app,
//                                        bottomSheetViewModel.appInfo.value!!.label,
//                                    ),
//                                    fontFamily = HkGroteskFontFamily,
//                                    fontWeight = FontWeight.SemiBold
//                                )
//                                if (showPackage) {
//                                    Text(
//                                        text = bottomSheetViewModel.appInfo.value!!.packageName,
//                                        fontSize = 12.sp
//                                    )
//                                }
//                            }
//                        }
//                        Button(
//                            onClick = {
//                                bottomSheetViewModel.appInfo.value?.let {
//                                    launchApp(
//                                        result,
//                                        info = it,
//                                        privateBrowsingBrowser = bottomSheetViewModel.privateBrowser.value
//                                    )
//                                }
//                            }, modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(start = 15.dp, end = 15.dp)
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.Shield, contentDescription = null,
//                                modifier = Modifier.size(20.dp)
//                            )
//                            Spacer(modifier = Modifier.width(5.dp))
//                            Text(
//                                text = stringResource(id = R.string.request_private_browsing),
//                                textAlign = TextAlign.Center,
//                                fontFamily = HkGroteskFontFamily,
//                                fontWeight = FontWeight.SemiBold
//                            )
//                        }
//                        Button(
//                            onClick = {
//                                bottomSheetViewModel.appInfo.value?.let {
//                                    launchApp(
//                                        result,
//                                        info = it
//                                    )
//                                }
//                            }, modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(start = 15.dp, end = 15.dp)
//                        ) {
//                            Text(
//                                text = "Open in Standard Mode",
//                                textAlign = TextAlign.Center,
//                                fontFamily = HkGroteskFontFamily,
//                                fontWeight = FontWeight.SemiBold
//                            )
//                        }
//                    }
//                }
//                return@LazyVerticalGrid
//            }
//            if (forPreferred) {
//                val filteredItem = result.filteredItem!!
//                item(span = { GridItemSpan(maxCurrentLineSpan) }) {
//                    Column(modifier = Modifier
//                        .fillMaxWidth()
//                        .clickable {
//                            launchApp(result, filteredItem, always = false)
//                        }) {
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(start = 15.dp, end = 15.dp)
//                                .heightIn(min = preferredAppItemHeight),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Image(
//                                bitmap = filteredItem.iconBitmap,
//                                contentDescription = filteredItem.label,
//                                modifier = Modifier.size(32.dp)
//                            )
//
//                            Spacer(modifier = Modifier.width(10.dp))
//
//                            Column {
//                                Text(
//                                    text = stringResource(
//                                        id = R.string.open_with_app,
//                                        filteredItem.label,
//                                    ),
//                                    fontFamily = HkGroteskFontFamily,
//                                    fontWeight = FontWeight.SemiBold
//                                )
//                                if (showPackage) {
//                                    Text(
//                                        text = filteredItem.packageName, fontSize = 12.sp
//                                    )
//                                }
//                            }
//                        }
//                    }
//                }
//                item(span = { GridItemSpan(maxCurrentLineSpan) }) {
//                    ButtonColumn(
//                        bottomSheetViewModel = bottomSheetViewModel,
//                        enabled = true,
//                        uri = result.uri,
//                        onClick = { launchApp(result, filteredItem, always = it) },
//                        hideDrawer = hideDrawer
//                    )
//                }
//                item(span = { GridItemSpan(maxCurrentLineSpan) }) {
//                    HorizontalDivider(
//                        modifier = Modifier.padding(
//                            start = 25.dp, end = 25.dp, top = 5.dp, bottom = 5.dp
//                        ), color = MaterialTheme.colorScheme.outline.copy(0.25f)
//                    )
//                }
//                item(span = { GridItemSpan(maxCurrentLineSpan) }) {
//                    Spacer(modifier = Modifier.height(10.dp))
//                }
//                item(span = { GridItemSpan(maxCurrentLineSpan) }) {
//                    Text(
//                        modifier = Modifier.padding(start = 15.dp),
//                        text = stringResource(id = R.string.use_a_different_app),
//                        fontFamily = HkGroteskFontFamily,
//                        fontWeight = FontWeight.SemiBold,
//                    )
//                }
//                item(span = { GridItemSpan(maxCurrentLineSpan) }) {
//                    Spacer(modifier = Modifier.height(5.dp))
//                }
//            } else {
//                item(key = R.string.open_with, span = { GridItemSpan(maxCurrentLineSpan) }) {
//                    Text(
//                        text = stringResource(id = R.string.open_with),
//                        fontFamily = HkGroteskFontFamily,
//                        fontWeight = FontWeight.SemiBold,
//                        modifier = Modifier.padding(
//                            start = 15.dp, top = if (previewUrl) 10.dp else 0.dp
//                        )
//                    )
//                }
//                item(span = { GridItemSpan(maxCurrentLineSpan) }) {
//                    Spacer(modifier = Modifier.height(5.dp))
//                }
//            }
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
