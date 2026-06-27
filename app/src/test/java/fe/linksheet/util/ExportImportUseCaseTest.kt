package fe.linksheet.util

//@RunWith(AndroidJUnit4::class)
//@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
//class ExportImportUseCaseTest : BaseUnitTest {
//
//
////    @org.junit.Test
////    fun test() {
////        val useCase = ExportImportUseCase(repository, Json.Default, Toml.Default)
////        println(useCase.export(false))
////    }
//
////    @org.junit.Test
////    fun `test json`() {
////        val useCase = ExportImportUseCase(repository, Json.Default, Toml.Default)
////        val str = useCase.exportToString(ExportImportUseCase.Format.Json, true)
////        val expected = """{"preferences":[{"key":"always_show_package_name","value":"false"},{"key":"use_clear_urls","value":"false"},{"key":"fast_forward_rules","value":"false"},{"key":"follow_redirects_timeout","value":"15"},{"key":"show_as_referrer","value":"false"},{"key":"dev_mode_enabled","value":"false"},{"key":"first_run","value":"true"},{"key":"resolve_embeds","value":"false"},{"key":"last_version","value":"-1"},{"key":"home_clipboard_card","value":"true"},{"key":"preview_url","value":"true"},{"key":"auto_launch_single_browser","value":"false"},{"key":"browser_mode","value":"AlwaysAsk"},{"key":"in_app_browser_mode","value":"AlwaysAsk"},{"key":"unified_preferred_browser","value":"true"},{"key":"in_app_browser_setting","value":"UseAppSettings"},{"key":"hide_after_copying","value":"false"},{"key":"usage_stats_sorting","value":"false"},{"key":"grid_layout","value":"false"},{"key":"dont_show_filtered_item","value":"false"},{"key":"hide_bottom_sheet_choice_buttons","value":"false"},{"key":"expand_on_app_select","value":"true"},{"key":"bottom_sheet_native_label","value":"true"},{"key":"hide_referrer_from_sheet","value":"false"},{"key":"double_tap_url","value":"false"},{"key":"expand_fully","value":"false"},{"key":"url_bar_preview","value":"false"},{"key":"url_bar_preview_skip_browser","value":"false"},{"key":"tap_config_single","value":"SelectItem"},{"key":"tap_config_double","value":"OpenApp"},{"key":"tap_config_long","value":"OpenSettings"},{"key":"url_copied_toast","value":"true"},{"key":"download_started_toast","value":"true"},{"key":"opening_with_app_toast","value":"true"},{"key":"resolve_via_toast","value":"true"},{"key":"resolve_via_failed_toast","value":"true"},{"key":"enable_amp2html","value":"false"},{"key":"amp2html_local_cache","value":"true"},{"key":"amp2html_external_service","value":"false"},{"key":"amp2html_allow_darknets","value":"false"},{"key":"amp2html_allow_local_network","value":"false"},{"key":"amp2html_skip_browser","value":"true"},{"key":"enable_downloader","value":"false"},{"key":"downloader_mode","value":"Auto"},{"key":"downloaderCheckUrlMimeType","value":"false"},{"key":"downloader_request_timeout","value":"15"},{"key":"follow_redirects","value":"false"},{"key":"follow_redirects_mode","value":"Auto"},{"key":"follow_redirects_aggressive","value":"false"},{"key":"follow_redirects_local_cache","value":"true"},{"key":"follow_redirects_external_service","value":"false"},{"key":"follow_only_known_trackers","value":"false"},{"key":"follow_redirects_allow_darknets","value":"false"},{"key":"follow_redirects_allow_local_network","value":"false"},{"key":"follow_redirects_skip_browser","value":"true"},{"key":"theme_v2","value":"System"},{"key":"theme_material_you","value":"true"},{"key":"theme_amoled_enabled","value":"false"},{"key":"enable_lib_redirect","value":"false"},{"key":"enable_ignore_lib_redirect_button","value":"false"},{"key":"enable_shizuku","value":"false"},{"key":"auto_disable_link_handling","value":"false"},{"key":"enable_request_private_browsing_button","value":"false"},{"key":"bottom_sheet_profile_switcher","value":"false"},{"key":"send_target","value":"false"},{"key":"telemetry_dialog","value":"true"},{"key":"remote_config","value":"false"},{"key":"theme","value":"0"}]}"""
////        assertThat(str).isEqualTo(expected)
////    }
//}
