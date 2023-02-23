package fe.linksheet.activity.bottomsheet

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import com.junkfood.seal.ui.component.BottomDrawer
import com.tasomaniac.openwith.resolver.DisplayActivityInfo
import com.tasomaniac.openwith.resolver.IntentResolverResult
import fe.linksheet.R
import fe.linksheet.ui.theme.AppTheme
import fe.linksheet.ui.theme.HkGroteskFontFamily
import fe.linksheet.util.getBitmapFromImage
import fe.linksheet.util.sourceIntent
import kotlinx.coroutines.ExperimentalCoroutinesApi

class BottomSheetActivity : ComponentActivity() {
    private val bottomSheetViewModel: BottomSheetViewModel by viewModels()

    @OptIn(
        ExperimentalMaterialApi::class, ExperimentalMaterialApi::class,
        ExperimentalCoroutinesApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { v, insets ->
            v.setPadding(0, 0, 0, 0)
            insets
        }

        window.run {
            setBackgroundDrawable(ColorDrawable(0))
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            } else {
                setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
            }
        }

        val result = bottomSheetViewModel.resolve(this, intent)
        result.invokeOnCompletion {
            val completed = result.getCompleted()
            completed?.alwaysPreferred?.let { alwaysPreferred ->
                if (alwaysPreferred) {
                    val app = completed.filteredItem ?: completed.resolved[0]
                    launchApp(app, true)
//                    Toast.makeText(
//                        this@BottomSheetActivity,
//                        getString(R.string.opening_with_app, app.displayLabel),
//                        Toast.LENGTH_SHORT
//                    ).show()
                }
            }
        }

        setContent {
            AppTheme {
                val drawerState = rememberModalBottomSheetState(
                    initialValue = ModalBottomSheetValue.Expanded,
                    skipHalfExpanded = false
                )

                LaunchedEffect(drawerState.currentValue) {
                    if (drawerState.currentValue == ModalBottomSheetValue.Hidden) {
                        this@BottomSheetActivity.finish()
                    }
                }

                BottomDrawer(drawerState = drawerState, sheetContent = {
                    if (bottomSheetViewModel.result != null) {
                        if (bottomSheetViewModel.result?.filteredItem == null) {
                            OpenWith(result = bottomSheetViewModel.result!!)
                        } else {
                            OpenWithPreferred(result = bottomSheetViewModel.result!!)
                        }
                    }
                })
            }
        }
    }

    @Composable
    private fun OpenWithPreferred(result: IntentResolverResult) {
        val filteredItem = result.filteredItem!!
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .height(60.dp)
        ) {
            Image(
                bitmap = getBitmapFromImage(
                    this@BottomSheetActivity,
                    filteredItem.displayIcon!!
                ).asImageBitmap(),
                contentDescription = filteredItem.displayLabel,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(5.dp))

            Column {
                Text(
                    text = stringResource(
                        id = R.string.open_with_app,
                        filteredItem.displayLabel
                    )
                )
            }
        }

        ButtonRow(enabled = true, onClick = { launchApp(filteredItem, it) })

        Divider(color = MaterialTheme.colorScheme.tertiary, thickness = 0.5.dp)

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(id = R.string.use_a_different_app),
            fontFamily = HkGroteskFontFamily,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 28.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))

        Column(
            modifier = Modifier.nestedScroll(rememberNestedScrollInteropConnection())
        ) {
            AppList(
                result = result,
                selectedItem = -1,
                onSelectedItemChange = { })
        }

    }

    @Composable
    private fun OpenWith(result: IntentResolverResult) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(id = R.string.open_with),
            fontFamily = HkGroteskFontFamily,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 28.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        var selectedItem by remember { mutableStateOf(-1) }

        Column(
            modifier = Modifier
                .height(290.dp)
                .nestedScroll(rememberNestedScrollInteropConnection())
        ) {
            AppList(
                result = result,
                selectedItem = selectedItem,
                onSelectedItemChange = { selectedItem = it })

            ButtonRow(selectedItem != -1) { always ->
                launchApp(result.resolved[selectedItem], always)
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun AppList(
        result: IntentResolverResult,
        selectedItem: Int,
        onSelectedItemChange: (Int) -> Unit
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 3.dp)
                .height(240.dp),
            content = {
                itemsIndexed(items = result.resolved) { index, info ->
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(
                                RoundedCornerShape(6.dp)
                            )
                            .combinedClickable(
                                onClick = {
                                    if (selectedItem == index) launchApp(info)
                                    else onSelectedItemChange(index)
                                },
                                onDoubleClick = {
                                    launchApp(info)
                                },
                                onLongClick = { }
                            )
                            .background(if (selectedItem == index) MaterialTheme.colorScheme.secondaryContainer else androidx.compose.ui.graphics.Color.Transparent)
                            .padding(10.dp)

                    ) {
                        Image(
                            bitmap = getBitmapFromImage(
                                this@BottomSheetActivity,
                                info.displayIcon!!
                            ).asImageBitmap(),
                            contentDescription = info.displayLabel,
                            modifier = Modifier.size(32.dp)
                        )

                        Spacer(modifier = Modifier.width(5.dp))

                        Column {
                            Text(text = info.displayLabel)
                            if (result.showExtended) {
                                Text(text = info.packageName())
                            }
                        }
                    }
                }
            })
    }

    @Composable
    private fun ButtonRow(enabled: Boolean, onClick: (always: Boolean) -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 15.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                enabled = enabled,
                onClick = { onClick(false) }) {
                Text(
                    text = stringResource(id = R.string.just_once),
                    fontFamily = HkGroteskFontFamily,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.width(5.dp))

            TextButton(
                enabled = enabled,
                onClick = { onClick(true) }) {
                Text(
                    text = stringResource(id = R.string.always),
                    fontFamily = HkGroteskFontFamily,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }

    private fun launchApp(info: DisplayActivityInfo, always: Boolean = false) {
        val intentFrom = info.intentFrom(intent.sourceIntent())
        this.startActivity(intentFrom)
        this.finish()

        bottomSheetViewModel.persistSelectedIntent(intentFrom, always)
    }
}