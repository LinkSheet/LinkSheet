package fe.linksheet.debug.module

import app.linksheet.compose.debug.DebugPreferenceProvider
import app.linksheet.compose.debug.NoOpDebugPreferenceProvider
import app.linksheet.feature.remoteconfig.util.LinkAssets
import fe.android.preference.helper.Mapper
import fe.android.preference.helper.TypeMapper
import fe.android.preference.helper.Unmapper
import fe.composekit.preference.FakePreferences
import fe.linksheet.composable.ui.ThemeV2
import fe.linksheet.module.viewmodel.RootViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val TestRootModule = module {
    single<DebugPreferenceProvider> { NoOpDebugPreferenceProvider }
    viewModel<RootViewModel> { TestRootViewModel() }
}

class TestRootViewModel : RootViewModel() {
    override val themeV2 = FakePreferences.mapped(ThemeV2.System, ThemeV2).vm
    override val themeAmoled = FakePreferences.boolean(false).vm
    override val themeMaterialYou = FakePreferences.boolean(false).vm
    override val linkAssets = FakePreferences.mapped(
        default = mapOf(),
        mapper = object : TypeMapper<LinkAssets, String> {
            override val unmap: Unmapper<String, LinkAssets> = { mapOf() }
            override val map: Mapper<LinkAssets, String> = { "" }
        }
    ).vm
}
