package fe.linksheet.feature.systeminfo.device

object XiaomiRedmiNote13_A15 : Device(
    codename = "aurora",
    fingerprint = "missi-user-15-AQ3A.240627.003-OS2.0.2.0.VNAEUXM-release-keys",
    buildProperties = parseTestBuildProperties(
        """
            ####################################
            # from generate-common-build-props
            # These properties identify this partition image.
            ####################################
            ro.product.product.brand=Xiaomi
            ro.product.product.device=miproduct
            ro.product.product.manufacturer=QUALCOMM
            ro.product.product.model=miproduct
            ro.product.product.cert=
            ro.product.product.marketname=
            ro.product.product.name=aurora
            ro.product.build.date=Thu Dec  5 11:42:46 CST 2024
            ro.product.build.date.utc=1733370166
            ro.product.build.fingerprint=Xiaomi/aurora/miproduct:15/AQ3A.240627.003/OS2.0.2.0.VNAEUXM:user/release-keys
            ro.product.build.id=AQ3A.240627.003
            ro.product.build.tags=release-keys
            ro.product.build.type=user
            ro.product.build.version.incremental=OS2.0.2.0.VNAEUXM
            ro.product.build.version.release=15
            ro.product.build.version.release_or_codename=15
            ro.product.build.version.sdk=35
            ####################################
            # from variable ADDITIONAL_PRODUCT_PROPERTIES
            ####################################
            ro.product.build.16k_page.enabled=false
            ro.build.characteristics=nosdcard
            ro.product.ab_ota_partitions=product
            ro.product.cpu.pagesize.max=16384
            ro.product.build.no_bionic_page_size_macro=true
            # Value overridden by post_process_props.py. Original value: default
            ro.dalvik.vm.enable_uffd_gc=true
            ####################################
            # from variable PRODUCT_PRODUCT_PROPERTIES
            ####################################
            # Removed by post_process_props.py because overridden by ro.miui.ui.version.code=816
            #ro.miui.ui.version.code?=15
            # Removed by post_process_props.py because overridden by ro.miui.ui.version.name=V816
            #ro.miui.ui.version.name?=V150
            ro.netflix.channel=004ee050-1a17-11e9-bb61-6f1da27fb55b
            ro.netflix.signup=1
            ro.wps.prop.channel.path=/cust/etc/wps.prop
            ro.csc.spotify.music.referrerid=xiaomi_mobile
            ro.csc.spotify.music.partnerid=xiaomi_mobile
            ro.booking.channel.path=/cust/etc/.booking.data.aid
            ro.netflix.channel=004ee050-1a17-11e9-bb61-6f1da27fb55b
            ro.netflix.signup=1
            ro.wps.prop.channel.path=/cust/etc/wps.prop
            ro.csc.spotify.music.referrerid=xiaomi_mobile
            ro.csc.spotify.music.partnerid=xiaomi_mobile
            ro.booking.channel.path=/cust/etc/.booking.data.aid
            ro.netflix.channel=004ee050-1a17-11e9-bb61-6f1da27fb55b
            ro.netflix.signup=1
            ro.wps.prop.channel.path=/cust/etc/wps.prop
            ro.csc.spotify.music.referrerid=xiaomi_mobile
            ro.csc.spotify.music.partnerid=xiaomi_mobile
            ro.booking.channel.path=/cust/etc/.booking.data.aid
            ro.netflix.channel=004ee050-1a17-11e9-bb61-6f1da27fb55b
            ro.netflix.signup=1
            ro.wps.prop.channel.path=/cust/etc/wps.prop
            ro.csc.spotify.music.referrerid=xiaomi_mobile
            ro.csc.spotify.music.partnerid=xiaomi_mobile
            ro.booking.channel.path=/cust/etc/.booking.data.aid
            ro.netflix.channel=004ee050-1a17-11e9-bb61-6f1da27fb55b
            ro.netflix.signup=1
            ro.wps.prop.channel.path=/cust/etc/wps.prop
            ro.csc.spotify.music.referrerid=xiaomi_mobile
            ro.csc.spotify.music.partnerid=xiaomi_mobile
            ro.booking.channel.path=/cust/etc/.booking.data.aid
            ro.netflix.channel=004ee050-1a17-11e9-bb61-6f1da27fb55b
            ro.netflix.signup=1
            ro.wps.prop.channel.path=/cust/etc/wps.prop
            ro.csc.spotify.music.referrerid=xiaomi_mobile
            ro.csc.spotify.music.partnerid=xiaomi_mobile
            ro.booking.channel.path=/cust/etc/.booking.data.aid
            ro.com.android.dataroaming=false
            debug.sf.disable_backpressure=1
            ro.miui.ui.version.code=816
            ro.miui.ui.version.name=V816
            ro.setupwizard.rotation_locked=true
            setupwizard.theme=glif_v3_light
            ro.opa.eligible_device=true
            ro.com.google.gmsversion=15_202409
            setupwizard.feature.baseline_setupwizard_enabled=true
            bluetooth.profile.asha.central.enabled=true
            bluetooth.profile.a2dp.source.enabled=true
            bluetooth.profile.avrcp.target.enabled=true
            # Removed by post_process_props.py because overridden by bluetooth.profile.bap.broadcast.assist.enabled=true
            #bluetooth.profile.bap.broadcast.assist.enabled?=false
            # Removed by post_process_props.py because overridden by bluetooth.profile.bap.unicast.client.enabled=true
            #bluetooth.profile.bap.unicast.client.enabled?=false
            # Removed by post_process_props.py because overridden by bluetooth.profile.bap.broadcast.source.enabled=true
            #bluetooth.profile.bap.broadcast.source.enabled?=false
            # Removed by post_process_props.py because overridden by bluetooth.profile.bas.client.enabled=true
            #bluetooth.profile.bas.client.enabled?=true
            # Removed by post_process_props.py because overridden by bluetooth.profile.ccp.server.enabled=true
            #bluetooth.profile.ccp.server.enabled?=false
            # Removed by post_process_props.py because overridden by bluetooth.profile.csip.set_coordinator.enabled=true
            #bluetooth.profile.csip.set_coordinator.enabled?=false
            bluetooth.profile.gatt.enabled=true
            # Removed by post_process_props.py because overridden by bluetooth.profile.hap.client.enabled=false
            #bluetooth.profile.hap.client.enabled?=false
            bluetooth.profile.hfp.ag.enabled=true
            bluetooth.profile.hid.host.enabled=true
            # Removed by post_process_props.py because overridden by bluetooth.profile.mcp.server.enabled=true
            #bluetooth.profile.mcp.server.enabled?=false
            bluetooth.profile.opp.enabled=true
            bluetooth.profile.pan.nap.enabled=true
            bluetooth.profile.pan.panu.enabled=true
            # Removed by post_process_props.py because overridden by bluetooth.profile.vcp.controller.enabled=true
            #bluetooth.profile.vcp.controller.enabled?=false
            persist.vendor.bt.a2dp.samplerate=true
            persist.vendor.adapt.sampler=true
            ro.miui.carrier.cota=true
            ro.miui.vicegwsd=true
            persist.vendor.radio.hidl_dev_service=true
            ro.vendor.radio.5g=3
            ro.vendor.radio.features_common=3
            persist.vendor.radio.enable_temp_dds=true
            ro.vendor.radio.fastdormancy=true
            ro.vendor.audio.ring.filter=false
            persist.sys.sf_charge_anim_supported=true
            dalvik.vm.ps-min-first-save-ms=8000
            persist.sys.element_transition_supported=true
            remote_provisioning.enable_rkpd=true
            remote_provisioning.hostname=remoteprovisioning.googleapis.com
            persist.wm.extensions.enabled=true
            vendor.audio_hal.period_size=192
            vendor.audio.tunnel.encode=false
            vendor.audio.offload.buffer.size.kb=32
            vendor.voice.path.for.pcm.voip=true
            vendor.audio.offload.multiaac.enable=true
            vendor.audio.parser.ip.buffer.size=262144
            vendor.audio.flac.sw.decoder.24bit=true
            vendor.audio.use.sw.alac.decoder=true
            vendor.audio.use.sw.ape.decoder=true
            vendor.audio.hw.aac.encoder=true
            af.fast_track_multiplier=1
            audio.offload.video=true
            audio.deep_buffer.media=true
            audio.sys.noisy.broadcast.delay=500
            audio.sys.mute.latency.factor=2
            audio.sys.routing.latency=0
            audio.offload.min.duration.secs=30
            audio.sys.offload.pstimeout.secs=3
            ro.af.client_heap_size_kbyte=7168
            media.stagefright.audio.deep=false
            aaudio.mmap_exclusive_policy=2
            aaudio.hw_burst_min_usec=2000
            persist.sys.offlinelog.bootlog=true
            bluetooth.profile.a2dp.source.enabled=true
            bluetooth.profile.avrcp.target.enabled=true
            bluetooth.profile.avrcp.controller.enabled=false
            bluetooth.profile.hfp.ag.enabled=true
            bluetooth.profile.gatt.enabled=true
            bluetooth.profile.hid.host.enabled=true
            bluetooth.profile.hid.device.enabled=true
            bluetooth.profile.map.server.enabled=true
            bluetooth.profile.opp.enabled=true
            bluetooth.profile.pan.nap.enabled=true
            bluetooth.profile.pan.panu.enabled=true
            bluetooth.profile.pbap.server.enabled=true
            ro.miui.notch=1
            persist.miui.density_v2=560
            persist.sys.mmms.lowmem.wmark.boost.factor=1
            persist.sys.enable_rtmode=true
            persist.miui.extm.enable=1
            persist.miui.miperf.enable=true
            persist.knock.wait_use_frame_time=20
            persist.sys.scout_binder_gki=true
            persist.sys.device_config_gki=true
            persist.sys.debug.enable_scout_memory_monitor=true
            persist.sys.debug.enable_scout_memory_resume=true
            persist.sys.scout_dumpbysocket=true
            persist.sys.miui_record_enable=true
            persist.sys.spc.cpulimit.enabled=true
            persist.sys.gz.enable=true
            persist.sys.spc.bindvisible.enabled=true
            persist.sys.mms.compact_enable=true
            persist.sys.use_boot_compact=true
            persist.sys.spc.enabled=true
            persist.sys.use_mi_new_strategy=true
            persist.sys.mmms.switch=true
            persist.sys.mms.kill_fas_cached_idle=false
            persist.sys.mms.bg_apps_limit=96
            persist.sys.mmms.throttled.thread=6400
            persist.sys.spc.protect.critical.count=2
            persist.sys.miui_animator_sched.bigcores=3-6
            persist.sys.miui_animator_sched.sched_threads=2
            persist.sys.miui_animator_sched.big_prime_cores=3-7
            persist.sys.enable_templimit=true
            persist.vendor.display.miui.composer_boost=4-7
            persist.sys.smartpower.intercept.enable=true
            persist.sys.miui_sptm_new.enable=true
            persist.sys.preload.enable=true
            dalvik.vm.heaptargetutilization=0.5
            dalvik.vm.heapminfree=2m
            ro.hwui.max_texture_allocation_size=314572800
            persist.sys.powmillet.enable=true
            persist.sys.brightmillet.enable=true
            persist.sys.millet.handshake=true
            persist.sys.millet.newversion=true
            ro.millet.netlink=31
            ro.display.screen_type=1
            ro.miui.cust_erofs=1
            ro.config.miui_orientation_projection_enable=true
            ro.config.miui_activity_embedding_enable=true
            ro.miui.preinstall_to_data=1
            ro.miui.cust_img_path=/data/preinstall/cust.img
            persist.sys.first.frame.accelerates=true
            persist.sys.hdr_dimmer_supported=true
            persist.sys.gallery_hdr_boost_max_factor=2.25
            persist.sys.spc.pressure.enable=false
            persist.sys.spc.proc_restart_enable=true
            persist.sys.smartpower.display.enable=true
            persist.sys.smartpower.display_camera_fps_enable=true
            persist.sys.dynamicbuffer.max_adjust_num=1
            persist.sys.resource_cache_limit.multiple=2
            persist.sys.usap_pool_enabled=false
            ro.config.miui_smart_orientation_enable=true
            remote_provisioning.hostname=remoteprovisioning.googleapis.com
            debug.media.video.frc=false
            debug.media.video.vpp=false
            ro.vendor.media.video.frc.support=true
            ro.vendor.media.video.vpp.support=true
            debug.config.media.video.frc.support=true
            debug.config.media.video.aie.support=true
            debug.config.media.video.ais.support=true
            debug.config.media.video.meeting.support=true
            debug.media.video.chipset=7
            vendor.media.vpp.debug.value.use=false
            vendor.media.vpp.aie.cade=100
            vendor.media.vpp.aie.ltm=1
            vendor.media.vpp.aie.ltmsatgain=55
            vendor.media.vpp.aie.ltmsatoff=55
            vendor.media.vpp.aie.ltmacestr=37
            vendor.media.vpp.aie.ltmacebril=20
            vendor.media.vpp.aie.ltmacebrih=0
            bluetooth.profile.bap.broadcast.assist.enabled=true
            bluetooth.profile.bap.broadcast.source.enabled=true
            bluetooth.profile.bap.unicast.client.enabled=true
            bluetooth.profile.bas.client.enabled=true
            bluetooth.profile.ccp.server.enabled=true
            bluetooth.profile.csip.set_coordinator.enabled=true
            bluetooth.profile.hap.client.enabled=false
            bluetooth.profile.mcp.server.enabled=true
            bluetooth.profile.vcp.controller.enabled=true
            persist.vendor.service.bt.is_lc3q_enhanced_gaming=true
            persist.vendor.btstack.is_src_supported_game_context_enable=true
            persist.enable.bluetooth.voipleawar=true
            persist.vendor.qcom.bluetooth.aptxadaptiver2_2_support=true
            ro.miui.affinity.sfui=2-6
            ro.miui.affinity.sfre=2-6
            ro.miui.affinity.sfuireset=0-6
            persist.sys.multithreaded.dexlayout.enable=true
            persist.sys.hybrid_verify.enabled=true
            persist.sys.multithreaded.dexloader.enable=true
            persist.sys.miui.sys_monitor=am.ActivityManagerService,wm.WindowManagerGlobalLock
            persist.sys.downgrade_after_inactive_days_V2=30
            persist.sys.mimd.reclaim.enable=true
            persist.sys.mimd.sleep.enable=true
            ro.vendor.media_performance_class=34
            ro.odm.build.media_performance_class=34
            persist.sys.dex2oat.setupstage.boost.cpulist=7
            persist.sys.systemui.mitileopt_enable=true
            persist.sys.stability.abreuse_status=on
            persist.sys.stability.smartfocusio=on
            persist.sys.testTrimMemActivityBg.wk.enable=true
            persist.sys.textureview_optimization.enable=true
            persist.sys.background_blur_supported=true
            persist.sys.background_blur_status_default=true
            persist.sys.advanced_visual_release=3
            persist.sys.add_blurnoise_supported=true
            persist.sys.spc.mi_extra_free_game_only=true
            persist.vendor.spc.mi_extra_free_game_only=true
            persist.device_config.mglru_native.lru_gen_config=none
            persist.sys.spc.process.tracker.enable=true
            persist.sys.stability.swapEnable=true
            persist.sys.smart_gc.enable=true
            persist.sys.smart_gc.packages=com.miui.home,com.android.systemui
            persist.sys.screen_anti_burn_enabled=true
            persist.sys.support_ultra_hdr=true
            dalvik.vm.monitortimeout.enable=true
            persist.sys.miui.sys_dumpstack=am.ActivityManagerService,wm.WindowManagerGlobalLock
            persist.sys.trim_rendernode.enable=true
            ro.surface_flinger.game_default_frame_rate_override=60
            persist.sys.activity_helper.enable=true
            persist.sys.parallel-image-loading=true
            persist.sys.app_dexfile_preload.enable=true
            persist.sys.dexpreload.cpu_cores=0-7
            persist.sys.dexpreload.big_prime_cores=7
            persist.sys.dexpreload.other_cores=0-6
            persist.sys.notification_launch=true
            persist.sys.art_startup_class_preload.enable=true
            persist.sys.expend_heap_size=268435456
            persist.sys.expend_min_ram_limit=8
            persist.sys.memory_standard.enable=true
            persist.sys.memory_standard.appheap.enable=true
            persist.sys.memory_standard.handle.time=300000
            persist.sys.prestart.proc=true
            persist.sys.prestart.feedback.enable=true
            persist.sys.cross_process_jump_response_opt=true
            persist.sys.bitmap_scale_opt_enable=true
            persist.sys.app_dexfile_preload.enable=true
            persist.sys.dexpreload.cpu_cores=0-7
            persist.sys.dexpreload.big_prime_cores=7
            persist.sys.dexpreload.other_cores=0-6
            persist.sys.install_resolve_eagerly.enable=false
            persist.sys.raise_region_sampling_prio=true
            # end of file
            audio.sys.noisy.broadcast.delay=500
            audio.sys.mute.latency.factor=2
            audio.sys.routing.latency=0
            audio.offload.min.duration.secs=30
            audio.sys.offload.pstimeout.secs=3
            ro.af.client_heap_size_kbyte=7168
            media.stagefright.audio.deep=false
            aaudio.mmap_exclusive_policy=2
            aaudio.hw_burst_min_usec=2000
            persist.sys.offlinelog.bootlog=true
            bluetooth.profile.a2dp.source.enabled=true
            bluetooth.profile.avrcp.target.enabled=true
            bluetooth.profile.avrcp.controller.enabled=false
            bluetooth.profile.hfp.ag.enabled=true
            bluetooth.profile.gatt.enabled=true
            bluetooth.profile.hid.host.enabled=true
            bluetooth.profile.hid.device.enabled=true
            bluetooth.profile.map.server.enabled=true
            bluetooth.profile.opp.enabled=true
            bluetooth.profile.pan.nap.enabled=true
            bluetooth.profile.pan.panu.enabled=true
            bluetooth.profile.pbap.server.enabled=true
            ro.miui.notch=1
            persist.miui.density_v2=560
            persist.sys.mmms.lowmem.wmark.boost.factor=1
            persist.sys.enable_rtmode=true
            persist.miui.extm.enable=1
            persist.miui.miperf.enable=true
            persist.knock.wait_use_frame_time=20
            persist.sys.scout_binder_gki=true
            persist.sys.device_config_gki=true
            persist.sys.debug.enable_scout_memory_monitor=true
            persist.sys.debug.enable_scout_memory_resume=true
            persist.sys.scout_dumpbysocket=true
            persist.sys.miui_record_enable=true
            persist.sys.spc.cpulimit.enabled=true
            persist.sys.gz.enable=true
            persist.sys.spc.bindvisible.enabled=true
            persist.sys.mms.compact_enable=true
            persist.sys.use_boot_compact=true
            persist.sys.spc.enabled=true
            persist.sys.use_mi_new_strategy=true
            persist.sys.mmms.switch=true
            persist.sys.mms.kill_fas_cached_idle=false
            persist.sys.mms.bg_apps_limit=96
            persist.sys.mmms.throttled.thread=6400
            persist.sys.spc.protect.critical.count=2
            persist.sys.miui_animator_sched.bigcores=3-6
            persist.sys.miui_animator_sched.sched_threads=2
            persist.sys.miui_animator_sched.big_prime_cores=3-7
            persist.sys.enable_templimit=true
            persist.vendor.display.miui.composer_boost=4-7
            persist.sys.smartpower.intercept.enable=true
            persist.sys.miui_sptm_new.enable=true
            persist.sys.preload.enable=true
            dalvik.vm.heaptargetutilization=0.5
            dalvik.vm.heapminfree=2m
            ro.hwui.max_texture_allocation_size=314572800
            persist.sys.powmillet.enable=true
            persist.sys.brightmillet.enable=true
            persist.sys.millet.handshake=true
            persist.sys.millet.newversion=true
            ro.millet.netlink=31
            ro.display.screen_type=1
            ro.miui.cust_erofs=1
            ro.config.miui_orientation_projection_enable=true
            ro.config.miui_activity_embedding_enable=true
            ro.miui.preinstall_to_data=1
            ro.miui.cust_img_path=/data/preinstall/cust.img
            persist.sys.first.frame.accelerates=true
            persist.sys.hdr_dimmer_supported=true
            persist.sys.gallery_hdr_boost_max_factor=2.25
            persist.sys.spc.pressure.enable=false
            persist.sys.spc.proc_restart_enable=true
            persist.sys.smartpower.display.enable=true
            persist.sys.smartpower.display_camera_fps_enable=true
            persist.sys.dynamicbuffer.max_adjust_num=1
            persist.sys.resource_cache_limit.multiple=2
            persist.sys.usap_pool_enabled=false
            ro.config.miui_smart_orientation_enable=true
            remote_provisioning.hostname=remoteprovisioning.googleapis.com
            debug.media.video.frc=false
            debug.media.video.vpp=false
            ro.vendor.media.video.frc.support=true
            ro.vendor.media.video.vpp.support=true
            debug.config.media.video.frc.support=true
            debug.config.media.video.aie.support=true
            debug.config.media.video.ais.support=true
            debug.config.media.video.meeting.support=true
            debug.media.video.chipset=7
            vendor.media.vpp.debug.value.use=false
            vendor.media.vpp.aie.cade=100
            vendor.media.vpp.aie.ltm=1
            vendor.media.vpp.aie.ltmsatgain=55
            vendor.media.vpp.aie.ltmsatoff=55
            vendor.media.vpp.aie.ltmacestr=37
            vendor.media.vpp.aie.ltmacebril=20
            vendor.media.vpp.aie.ltmacebrih=0
            bluetooth.profile.bap.broadcast.assist.enabled=true
            bluetooth.profile.bap.broadcast.source.enabled=true
            bluetooth.profile.bap.unicast.client.enabled=true
            bluetooth.profile.bas.client.enabled=true
            bluetooth.profile.ccp.server.enabled=true
            bluetooth.profile.csip.set_coordinator.enabled=true
            bluetooth.profile.hap.client.enabled=false
            bluetooth.profile.mcp.server.enabled=true
            bluetooth.profile.vcp.controller.enabled=true
            persist.vendor.service.bt.is_lc3q_enhanced_gaming=true
            persist.vendor.btstack.is_src_supported_game_context_enable=true
            persist.enable.bluetooth.voipleawar=true
            persist.vendor.qcom.bluetooth.aptxadaptiver2_2_support=true
            ro.miui.affinity.sfui=2-6
            ro.miui.affinity.sfre=2-6
            ro.miui.affinity.sfuireset=0-6
            persist.sys.multithreaded.dexlayout.enable=true
            persist.sys.hybrid_verify.enabled=true
            persist.sys.multithreaded.dexloader.enable=true
            persist.sys.miui.sys_monitor=am.ActivityManagerService,wm.WindowManagerGlobalLock
            persist.sys.downgrade_after_inactive_days_V2=30
            persist.sys.mimd.reclaim.enable=true
            persist.sys.mimd.sleep.enable=true
            ro.vendor.media_performance_class=34
            ro.odm.build.media_performance_class=34
            persist.sys.dex2oat.setupstage.boost.cpulist=7
            persist.sys.systemui.mitileopt_enable=true
            persist.sys.stability.abreuse_status=on
            persist.sys.stability.smartfocusio=on
            persist.sys.testTrimMemActivityBg.wk.enable=true
            persist.sys.textureview_optimization.enable=true
            persist.sys.background_blur_supported=true
            persist.sys.background_blur_status_default=true
            persist.sys.advanced_visual_release=3
            persist.sys.add_blurnoise_supported=true
            persist.sys.spc.mi_extra_free_game_only=true
            persist.vendor.spc.mi_extra_free_game_only=true
            persist.device_config.mglru_native.lru_gen_config=none
            persist.sys.spc.process.tracker.enable=true
            persist.sys.stability.swapEnable=true
            persist.sys.smart_gc.enable=true
            persist.sys.smart_gc.packages=com.miui.home,com.android.systemui
            persist.sys.screen_anti_burn_enabled=true
            persist.sys.support_ultra_hdr=true
            dalvik.vm.monitortimeout.enable=true
            persist.sys.miui.sys_dumpstack=am.ActivityManagerService,wm.WindowManagerGlobalLock
            persist.sys.trim_rendernode.enable=true
            ro.surface_flinger.game_default_frame_rate_override=60
            persist.sys.activity_helper.enable=true
            persist.sys.parallel-image-loading=true
            persist.sys.app_dexfile_preload.enable=true
            persist.sys.dexpreload.cpu_cores=0-7
            persist.sys.dexpreload.big_prime_cores=7
            persist.sys.dexpreload.other_cores=0-6
            persist.sys.notification_launch=true
            persist.sys.art_startup_class_preload.enable=true
            persist.sys.expend_heap_size=268435456
            persist.sys.expend_min_ram_limit=8
            persist.sys.memory_standard.enable=true
            persist.sys.memory_standard.appheap.enable=true
            persist.sys.memory_standard.handle.time=300000
            persist.sys.prestart.proc=true
            persist.sys.prestart.feedback.enable=true
            persist.sys.cross_process_jump_response_opt=true
            persist.sys.bitmap_scale_opt_enable=true
            persist.sys.app_dexfile_preload.enable=true
            persist.sys.dexpreload.cpu_cores=0-7
            persist.sys.dexpreload.big_prime_cores=7
            persist.sys.dexpreload.other_cores=0-6
            persist.sys.install_resolve_eagerly.enable=false
            persist.sys.raise_region_sampling_prio=true
            # end of file
            ####################################
            # from generate-common-build-props
            # These properties identify this partition image.
            ####################################
            ro.product.system.brand=Android
            ro.product.system.device=generic
            ro.product.system.manufacturer=Xiaomi
            ro.product.system.model=mainline
            ro.product.system.name=mainline
            ro.product.system.cert=
            ro.product.system.marketname=
            ro.system.product.cpu.abilist=arm64-v8a
            ro.system.product.cpu.abilist32=
            ro.system.product.cpu.abilist64=arm64-v8a
            ro.system.build.date=Thu Dec  5 11:42:36 CST 2024
            ro.system.build.date.utc=1733370156
            ro.system.build.fingerprint=qti/missi/missi:15/AQ3A.240627.003/OS2.0.2.0.VNAEUXM:user/release-keys
            ro.system.build.id=AQ3A.240627.003
            ro.system.build.tags=release-keys
            ro.system.build.type=user
            ro.system.build.version.incremental=OS2.0.2.0.VNAEUXM
            ro.system.build.version.release=15
            ro.system.build.version.release_or_codename=15
            ro.system.build.version.sdk=35
            ####################################
            # from out/target/product/missi/obj/ETC/buildinfo.prop_intermediates/buildinfo.prop
            ####################################
            # begin build properties
            # autogenerated by buildinfo.py
            ro.build.id=AQ3A.240627.003
            ro.build.display.id=AQ3A.240627.003
            ro.build.version.incremental=OS2.0.2.0.VNAEUXM
            ro.build.version.sdk=35
            ro.build.version.preview_sdk=0
            ro.build.version.preview_sdk_fingerprint=REL
            ro.build.version.codename=REL
            ro.build.version.all_codenames=REL
            ro.build.version.known_codenames=Base,Base11,Cupcake,Donut,Eclair,Eclair01,EclairMr1,Froyo,Gingerbread,GingerbreadMr1,Honeycomb,HoneycombMr1,HoneycombMr2,IceCreamSandwich,IceCreamSandwichMr1,JellyBean,JellyBeanMr1,JellyBeanMr2,Kitkat,KitkatWatch,Lollipop,LollipopMr1,M,N,NMr1,O,OMr1,P,Q,R,S,Sv2,Tiramisu,UpsideDownCake,VanillaIceCream
            ro.build.version.release=15
            ro.build.version.release_or_codename=15
            ro.build.version.release_or_preview_display=15
            ro.build.version.security_patch=2024-11-01
            ro.build.version.min_supported_target_sdk=28
            ro.build.date=Thu Dec  5 11:42:36 CST 2024
            ro.build.date.utc=1733370156
            ro.build.type=user
            ro.build.user=builder
            ro.build.host=pangu-build-component-system-361505-20nvn-tn9xd-fmfds
            ro.build.tags=release-keys
            ro.build.flavor=missi-user
            # ro.product.cpu.abi and ro.product.cpu.abi2 are obsolete,
            # use ro.product.cpu.abilist instead.
            ro.product.cpu.abi=arm64-v8a
            ro.product.locale=en-GB
            ro.wifi.channels=
            # ro.build.product is obsolete; use ro.product.device
            ro.build.product=missi
            # Do not try to parse description or thumbprint
            ro.build.description=missi-user 15 AQ3A.240627.003 OS2.0.2.0.VNAEUXM release-keys
            # end build properties
            ####################################
            # from device/xiaomi/missi/system.prop
            ####################################
            #
            # system.prop for qssi
            #
            rild.libpath=/vendor/lib64/libril-qc-hal-qmi.so
            #rild.libargs=-d /dev/smd0
            persist.rild.nitz_plmn=
            persist.rild.nitz_long_ons_0=
            persist.rild.nitz_long_ons_1=
            persist.rild.nitz_long_ons_2=
            persist.rild.nitz_long_ons_3=
            persist.rild.nitz_short_ons_0=
            persist.rild.nitz_short_ons_1=
            persist.rild.nitz_short_ons_2=
            persist.rild.nitz_short_ons_3=
            ril.subscription.types=NV,RUIM
            DEVICE_PROVISIONED=1
            # Set network mode to (NR_LTE_TDSCDMA_CDMA_EVDO_GSM_WCDMA, NR_LTE_TDSCDMA_CDMA_EVDO_GSM_WCDMA) for 8+8 mode device on DSDS mode
            ro.telephony.default_network=33,33
            dalvik.vm.heapsize=36m
            dalvik.vm.dex2oat64.enabled=true
            dev.pm.dyn_samplingrate=1
            #ro.hdmi.enable=true
            #persist.speaker.prot.enable=false
            qcom.hw.aac.encoder=true
            #
            # system props for the cne module
            #
            persist.vendor.cne.feature=1
            #system props for the MM modules
            media.stagefright.enable-player=true
            media.stagefright.enable-http=true
            media.stagefright.enable-aac=true
            media.stagefright.enable-qcp=true
            media.stagefright.enable-fma2dp=true
            media.stagefright.enable-scan=true
            media.stagefright.thumbnail.prefer_hw_codecs=true
            mmp.enable.3g2=true
            media.aac_51_output_enabled=true
            media.settings.xml=/vendor/etc/media_profiles_vendor.xml
            #16777215 is decimal sum of supported codecs in AAL
            #codecs:(PARSER_)AAC AC3 AMR_NB AMR_WB ASF AVI DTS FLV 3GP 3G2 MKV MP2PS MP2TS MP3 OGG QCP WAV FLAC AIFF APE DSD MOV MHAS
            vendor.mm.enable.qcom_parser=16777215
            persist.mm.enable.prefetch=true
            #
            # system props for the data modules
            #
            ro.vendor.use_data_netmgrd=true
            persist.vendor.data.mode=concurrent
            #system props for time-services
            persist.timed.enable=true
            #
            # system prop for opengles version
            #
            # 196608 is decimal for 0x30000 to report version 3
            # 196609 is decimal for 0x30001 to report version 3.1
            # 196610 is decimal for 0x30002 to report version 3.2
            ro.opengles.version=196610
            #
            # System props for telephony
            # System prop to turn on CdmaLTEPhone always
            telephony.lteOnCdmaDevice=1
            #Simulate sdcard on /data/media
            #
            persist.fuse_sdcard=true
            #System props for BT
            ro.bluetooth.library_name=libbluetooth.so
            persist.vendor.btstack.aac_frm_ctl.enabled=true
            # MIUI ADD: BT_MIUIBluetoothFrame
            persist.vendor.service.bdroid.soc.alwayson=true
            #system prop for wipower support
            ro.bluetooth.emb_wp_mode=false
            ro.bluetooth.wipower=false
            # END BT_MIUIBluetoothFrame
            #system prop for RmNet Data
            persist.rmnet.data.enable=true
            persist.data.wda.enable=true
            persist.data.df.dl_mode=5
            persist.data.df.ul_mode=5
            persist.data.df.agg.dl_pkt=10
            persist.data.df.agg.dl_size=4096
            persist.data.df.mux_count=8
            persist.data.df.iwlan_mux=9
            persist.data.df.dev_name=rmnet_usb0
            #property to enable user to access Google WFD settings
            persist.debug.wfd.enable=1
            ##property to choose between virtual/external wfd display
            persist.sys.wfd.virtual=0
            #property to enable HWC for VDS
            debug.sf.enable_hwc_vds=0
            #property to latch unsignaled buffer
            debug.sf.latch_unsignaled=1
            # enable tunnel encoding for amrwb
            tunnel.audio.encode=true
            #enable voice path for PCM VoIP by default
            use.voice.path.for.pcm.voip=true
            # system prop for NFC DT
            ro.nfc.port=I2C
            #initialize QCA1530 detection
            sys.qca1530=detect
            #Enable stm events
            persist.debug.coresight.config=stm-events
            #hwui properties
            ro.hwui.texture_cache_size=72
            ro.hwui.layer_cache_size=48
            ro.hwui.r_buffer_cache_size=8
            ro.hwui.path_cache_size=32
            ro.hwui.gradient_cache_size=1
            ro.hwui.drop_shadow_cache_size=6
            ro.hwui.texture_cache_flushrate=0.4
            ro.hwui.text_small_cache_width=1024
            ro.hwui.text_small_cache_height=1024
            ro.hwui.text_large_cache_width=2048
            ro.hwui.text_large_cache_height=1024
            debug.hwui.skia_atrace_enabled=false
            config.disable_rtt=true
            #Bringup properties
            persist.sys.force_sw_gles=1
            persist.vendor.radio.atfwd.start=true
            ro.kernel.qemu.gles=0
            qemu.hw.mainkeys=0
            #Expose aux camera for below packages
            vendor.camera.aux.packagelist=org.codeaurora.snapcam,com.xiaomi.runin,com.xiaomi.cameratest,com.xiaomi.factory.mmi
            vendor.camera.aux.packagelistext=com.xiaomi.factory.CameraTestItem,com.firefightcam1
            #Add snapcam in privapp list
            persist.vendor.camera.privapp.list=org.codeaurora.snapcam
            #enable IZat OptInApp overlay
            persist.vendor.overlay.izat.optin=rro
            # Property for backup NTP Server
            persist.backup.ntpServer="0.pool.ntp.org"
            #Partition source order for Product/Build properties pickup.
            ro.product.property_source_order=odm,vendor,product,system_ext,system
            #Property to enable Codec2 for audio and OMX for Video
            debug.stagefright.ccodec=1
            #Property to set native recorder's maximum base layer fps
            ro.media.recorder-max-base-layer-fps=60
            #Battery Property
            ro.charger.enable_suspend=1
            #Disable MTE Async for system server
            arm64.memtag.process.system_server=off
            # Disable blur on app launch
            ro.launcher.blur.appLaunch=0
            #button jack mode & switch
            persist.audio.button_jack.profile=volume
            persist.audio.button_jack.switch=0
            # MIUI ADD: Performance_AppInstallEnhance
            ro.miui.enable_cloud_verify=true
            #enable speed install
            ro.miui.pm.install.speedinstall=/data/apk-tmp
            # END Performance_AppInstallEnhance
            # xiaomi ugd
            ro.gfx.driver.0=com.xiaomi.ugd
            # enable modem dump
            persist.vendor.ssr.enable_ramdumps=1
            ####################################
            # from variable ADDITIONAL_SYSTEM_PROPERTIES
            ####################################
            ro.treble.enabled=true
            ro.llndk.api_level=202404
            ro.actionable_compatible_property.enabled=true
            ro.postinstall.fstab.prefix=/system
            ro.secure=1
            security.perf_harden=1
            ro.adb.secure=1
            ro.allow.mock.location=0
            ro.debuggable=0
            net.bt.name=Android
            ro.vendor.qti.va_aosp.support=1
            ro.force.debuggable=0
            ####################################
            # from variable PRODUCT_SYSTEM_PROPERTIES
            ####################################
            ro.com.google.lens.oem_camera_package=com.android.camera
            ro.com.google.lens.oem_image_package=com.miui.gallery
            ro.com.android.mobiledata=false
            sys.dfcservice.ctrl=false
            sys.dfcservice.compress.prog=0
            sys.dfcservice.enablelog.ctrl=false
            ro.miui.shell_anim_enable_fcb=true
            debug.atrace.tags.enableflags=0
            persist.traced.enable=1
            dalvik.vm.image-dex2oat-Xms=64m
            dalvik.vm.image-dex2oat-Xmx=64m
            dalvik.vm.dex2oat-Xms=64m
            dalvik.vm.dex2oat-Xmx=512m
            dalvik.vm.usejit=true
            dalvik.vm.dexopt.secondary=true
            dalvik.vm.dexopt.thermal-cutoff=2
            dalvik.vm.appimageformat=lz4
            ro.dalvik.vm.native.bridge=0
            pm.dexopt.boot-after-ota.concurrency=4
            pm.dexopt.post-boot=verify
            pm.dexopt.first-boot=verify
            pm.dexopt.boot-after-ota=verify
            pm.dexopt.boot-after-mainline-update=verify
            pm.dexopt.install=speed-profile
            pm.dexopt.install-fast=skip
            pm.dexopt.install-bulk=speed-profile
            pm.dexopt.install-bulk-secondary=verify
            pm.dexopt.install-bulk-downgraded=verify
            pm.dexopt.install-bulk-secondary-downgraded=verify
            pm.dexopt.bg-dexopt=speed-profile
            pm.dexopt.ab-ota=speed-profile
            pm.dexopt.inactive=verify
            pm.dexopt.cmdline=verify
            pm.dexopt.first-use=speed-profile
            pm.dexopt.install-create-dm=speed-profile
            pm.dexopt.baseline=speed-profile
            pm.dexopt.secondary=verify
            pm.dexopt.shared=speed
            dalvik.vm.dex2oat-resolve-startup-strings=true
            dalvik.vm.dex2oat-max-image-block-size=524288
            dalvik.vm.minidebuginfo=true
            dalvik.vm.dex2oat-minidebuginfo=true
            dalvik.vm.madvise.vdexfile.size=104857600
            dalvik.vm.madvise.odexfile.size=104857600
            dalvik.vm.madvise.artfile.size=4294967295
            dalvik.vm.usap_pool_enabled=false
            dalvik.vm.usap_refill_threshold=1
            dalvik.vm.usap_pool_size_max=3
            dalvik.vm.usap_pool_size_min=1
            dalvik.vm.usap_pool_refill_delay_ms=3000
            dalvik.vm.useartservice=true
            dalvik.vm.enable_pr_dexopt=true
            ro.apex.updatable=true
            ro.hwui.max_texture_allocation_size=209715200
            persist.vendor.connsysfw.enable=true
            ro.radio.device_type=1
            ro.miui.support_audiorecord_compress=true
            ro.miui.allow_app_playbackcapture=true
            persist.device_config.runtime_native_boot.iorap_perfetto_enable=true
            ro.vendor.audio.notification.single=true
            ####################################
            # from variable PRODUCT_SYSTEM_DEFAULT_PROPERTIES
            ####################################
            # end of file
        """.trimIndent()
    )
) {
}

object XiaomiRedmiNote13_A14 : Device(
    codename = "aurora",
    fingerprint = "missi_phone_cn_only64-user-14-UKQ1.231003.002-V816.0.21.0.UNACNXM-release-keys",
    buildProperties = parseTestBuildProperties(
        """
            ####################################
            # from generate-common-build-props
            # These properties identify this partition image.
            ####################################
            ro.product.product.brand=Xiaomi
            ro.product.product.device=miproduct
            ro.product.product.manufacturer=QUALCOMM
            ro.product.product.model=miproduct
            ro.product.product.name=aurorapro
            ro.product.product.cert=
            ro.product.product.marketname=
            ro.product.build.date=Tue Aug 20 06:04:48 UTC 2024
            ro.product.build.date.utc=1724133888
            ro.product.build.fingerprint=Xiaomi/aurorapro/miproduct:14/UKQ1.231003.002/V816.0.21.0.UNACNXM:user/release-keys
            ro.product.build.id=UKQ1.231003.002
            ro.product.build.tags=release-keys
            ro.product.build.type=user
            ro.product.build.version.incremental=V816.0.21.0.UNACNXM
            ro.product.build.version.release=14
            ro.product.build.version.release_or_codename=14
            ro.product.build.version.sdk=34
            ####################################
            # from variable ADDITIONAL_PRODUCT_PROPERTIES
            ####################################
            ro.product.vndk.version=34
            ro.build.characteristics=nosdcard
            ro.product.ab_ota_partitions=product
            ro.product.cpu.pagesize.max=4096
            ro.dalvik.vm.enable_uffd_gc=false
            ####################################
            # from variable PRODUCT_PRODUCT_PROPERTIES
            ####################################
            ro.miui.notch=1
            persist.miui.density_v2=560
            persist.sys.mmms.lowmem.wmark.boost.factor=1
            persist.sys.enable_rtmode=true
            persist.miui.extm.enable=1
            persist.miui.miperf.enable=true
            persist.knock.wait_use_frame_time=20
            persist.sys.scout_binder_gki=true
            persist.sys.device_config_gki=true
            persist.sys.debug.enable_scout_memory_monitor=true
            persist.sys.debug.enable_scout_memory_resume=true
            persist.sys.scout_dumpbysocket=true
            persist.sys.miui_record_enable=true
            persist.sys.spc.cpulimit.enabled=true
            persist.sys.gz.enable=true
            persist.sys.spc.bindvisible.enabled=true
            persist.sys.mms.compact_enable=true
            persist.sys.use_boot_compact=false
            persist.sys.spc.enabled=true
            persist.sys.use_mi_new_strategy=true
            persist.sys.mmms.switch=true
            persist.sys.mms.kill_fas_cached_idle=false
            persist.sys.mms.bg_apps_limit=96
            persist.sys.miui_animator_sched.bigcores=3-6
            persist.sys.miui_animator_sched.sched_threads=2
            persist.sys.miui_animator_sched.big_prime_cores=3-7
            persist.sys.enable_templimit=true
            persist.vendor.display.miui.composer_boost=4-7
            persist.sys.smartpower.intercept.enable=true
            persist.sys.miui_sptm_new.enable=true
            persist.sys.preload.enable=true
            dalvik.vm.heaptargetutilization=0.5
            dalvik.vm.heapminfree=2m
            ro.hwui.max_texture_allocation_size=314572800
            persist.sys.powmillet.enable=true
            persist.sys.brightmillet.enable=true
            persist.sys.millet.handshake=true
            persist.sys.millet.newversion=true
            ro.millet.netlink=31
            ro.display.screen_type=1
            ro.miui.cust_erofs=1
            ro.miui.preinstall_to_data=1
            ro.miui.cust_img_path=/data/preinstall/cust.img
            persist.sys.first.frame.accelerates=true
            persist.sys.hdr_dimmer_supported=true
            persist.sys.gallery_hdr_boost_max_factor=2.25
            persist.sys.spc.pressure.enable=false
            persist.sys.spc.proc_restart_enable=true
            persist.sys.smartpower.display.enable=true
            persist.sys.smartpower.display_camera_fps_enable=true
            persist.sys.dynamicbuffer.max_adjust_num=1
            persist.sys.resource_cache_limit.multiple=2
            persist.sys.usap_pool_enabled=true
            persist.sys.dynamic_usap_enabled=true
            ro.config.miui_smart_orientation_enable=true
            remote_provisioning.hostname=remoteprovisioning.googleapis.com
            persist.sys.app_resurrection.enable=true
            debug.media.video.frc=false
            debug.media.video.vpp=false
            ro.vendor.media.video.frc.support=true
            ro.vendor.media.video.vpp.support=true
            debug.config.media.video.frc.support=true
            debug.config.media.video.aie.support=true
            debug.config.media.video.ais.support=true
            debug.media.video.chipset=7
            vendor.media.vpp.debug.value.use=false
            vendor.media.vpp.aie.cade=100
            vendor.media.vpp.aie.ltm=1
            vendor.media.vpp.aie.ltmsatgain=55
            vendor.media.vpp.aie.ltmsatoff=55
            vendor.media.vpp.aie.ltmacestr=37
            vendor.media.vpp.aie.ltmacebril=20
            vendor.media.vpp.aie.ltmacebrih=0
            persist.sys.smart_gc.enable=true
            persist.sys.smart_gc.packages=com.miui.home,com.android.systemui
            bluetooth.profile.bap.broadcast.assist.enabled=true
            bluetooth.profile.bap.broadcast.source.enabled=true
            bluetooth.profile.bap.unicast.client.enabled=true
            bluetooth.profile.bas.client.enabled=true
            bluetooth.profile.ccp.server.enabled=true
            bluetooth.profile.csip.set_coordinator.enabled=true
            bluetooth.profile.hap.client.enabled=false
            bluetooth.profile.mcp.server.enabled=true
            bluetooth.profile.vcp.controller.enabled=true
            ro.miui.affinity.sfui=2-6
            ro.miui.affinity.sfre=2-6
            ro.miui.affinity.sfuireset=0-6
            persist.sys.multithreaded.dexlayout.enable=true
            persist.sys.hybrid_verify.enabled=true
            persist.sys.multithreaded.dexloader.enable=true
            persist.sys.miui.sys_monitor=am.ActivityManagerService,wm.WindowManagerGlobalLock
            persist.sys.mimd.reclaim.enable=true
            ro.vendor.media_performance_class=34
            ro.odm.build.media_performance_class=34
            persist.sys.dex2oat.setupstage.boost.cpulist=7
            persist.sys.systemui.mitileopt_enable=true
            persist.sys.stability.abreuse_status=on
            persist.sys.stability.smartfocusio=on
            persist.sys.testTrimMemActivityBg.wk.enable=true
            persist.sys.stability.swapEnable=true
            persist.sys.background_blur_supported=true
            persist.sys.background_blur_status_default=true
            persist.sys.background_blur_version=2
            persist.sys.add_blurnoise_supported=true
            persist.sys.spc.mi_extra_free_game_only=true
            persist.vendor.spc.mi_extra_free_game_only=true
            persist.device_config.mglru_native.lru_gen_config=none
            persist.sys.spc.process.tracker.enable=true
            persist.sys.launch_response_optimization.enable=true
            persist.sys.hyper_transition=true
            persist.sys.precache.enable=true
            persist.sys.precache.number=3
            persist.sys.precache.appstrs1=com.sina.weibo,com.ss.android.article.news,com.taobao.taobao,com.smile.gifmaker
            persist.sys.precache.appstrs2=com.ss.android.ugc.aweme,com.tencent.mm,tv.danmaku.bili,com.miui.personalassistant
            persist.sys.precache.appstrs3=com.miui.home,com.android.systemui
            persist.sys.activity_helper.enable=true
            persist.sys.prestart.proc=true
            persist.sys.prestart.feedback.enable=true
            persist.sys.screen_anti_burn_enabled=true
            persist.sys.expend_heap_size=268435456
            persist.sys.expend_min_ram_limit=8
            # Removed by post_process_props.py because overridden by ro.miui.ui.version.code=816
            #ro.miui.ui.version.code?=14
            # Removed by post_process_props.py because overridden by ro.miui.ui.version.name=V816
            #ro.miui.ui.version.name?=V140
            ro.com.android.dataroaming=false
            debug.sf.disable_backpressure=1
            ro.miui.ui.version.code=816
            ro.miui.ui.version.name=V816
            ro.com.android.mobiledata=false
            ro.com.google.clientidbase=android-xiaomi
            ro.com.google.clientidbase.ms=android-xiaomi
            ro.miui.has_gmscore=1
            bluetooth.profile.asha.central.enabled=true
            bluetooth.profile.a2dp.source.enabled=true
            bluetooth.profile.avrcp.target.enabled=true
            # Removed by post_process_props.py because overridden by bluetooth.profile.bap.broadcast.assist.enabled=true
            #bluetooth.profile.bap.broadcast.assist.enabled?=false
            # Removed by post_process_props.py because overridden by bluetooth.profile.bap.unicast.client.enabled=true
            #bluetooth.profile.bap.unicast.client.enabled?=false
            # Removed by post_process_props.py because overridden by bluetooth.profile.bap.broadcast.source.enabled=true
            #bluetooth.profile.bap.broadcast.source.enabled?=false
            # Removed by post_process_props.py because overridden by bluetooth.profile.bas.client.enabled=true
            #bluetooth.profile.bas.client.enabled?=true
            # Removed by post_process_props.py because overridden by bluetooth.profile.ccp.server.enabled=true
            #bluetooth.profile.ccp.server.enabled?=false
            # Removed by post_process_props.py because overridden by bluetooth.profile.csip.set_coordinator.enabled=true
            #bluetooth.profile.csip.set_coordinator.enabled?=false
            bluetooth.profile.gatt.enabled=true
            # Removed by post_process_props.py because overridden by bluetooth.profile.hap.client.enabled=false
            #bluetooth.profile.hap.client.enabled?=false
            bluetooth.profile.hfp.ag.enabled=true
            bluetooth.profile.hid.host.enabled=true
            # Removed by post_process_props.py because overridden by bluetooth.profile.mcp.server.enabled=true
            #bluetooth.profile.mcp.server.enabled?=false
            bluetooth.profile.opp.enabled=true
            bluetooth.profile.pan.nap.enabled=true
            bluetooth.profile.pan.panu.enabled=true
            # Removed by post_process_props.py because overridden by bluetooth.profile.vcp.controller.enabled=true
            #bluetooth.profile.vcp.controller.enabled?=false
            persist.vendor.bt.a2dp.samplerate=true
            persist.vendor.adapt.sampler=true
            ro.miui.carrier.cota=true
            ro.miui.vicegwsd=true
            persist.vendor.radio.hidl_dev_service=true
            ro.vendor.radio.5g=3
            ro.vendor.radio.features_common=3
            persist.vendor.radio.enable_temp_dds=true
            ro.vendor.radio.fastdormancy=true
            ro.vendor.audio.ring.filter=false
            dalvik.vm.ps-min-first-save-ms=8000
            remote_provisioning.enable_rkpd=true
            remote_provisioning.hostname=remoteprovisioning.googleapis.com
            vendor.audio_hal.period_size=192
            vendor.audio.tunnel.encode=false
            vendor.audio.offload.buffer.size.kb=32
            vendor.voice.path.for.pcm.voip=true
            vendor.audio.offload.multiaac.enable=true
            vendor.audio.parser.ip.buffer.size=262144
            vendor.audio.flac.sw.decoder.24bit=true
            vendor.audio.use.sw.alac.decoder=true
            vendor.audio.use.sw.ape.decoder=true
            vendor.audio.hw.aac.encoder=true
            af.fast_track_multiplier=1
            audio.offload.video=true
            audio.deep_buffer.media=true
            audio.sys.noisy.broadcast.delay=500
            audio.sys.mute.latency.factor=2
            audio.sys.routing.latency=0
            audio.offload.min.duration.secs=30
            audio.sys.offload.pstimeout.secs=3
            ro.af.client_heap_size_kbyte=7168
            media.stagefright.audio.deep=false
            aaudio.mmap_exclusive_policy=2
            aaudio.hw_burst_min_usec=2000
            persist.sys.offlinelog.bootlog=true
            bluetooth.profile.a2dp.source.enabled=true
            bluetooth.profile.avrcp.target.enabled=true
            bluetooth.profile.avrcp.controller.enabled=false
            bluetooth.profile.hfp.ag.enabled=true
            bluetooth.profile.asha.central.enabled=true
            bluetooth.profile.gatt.enabled=true
            bluetooth.profile.hid.host.enabled=true
            bluetooth.profile.hid.device.enabled=true
            bluetooth.profile.map.server.enabled=true
            bluetooth.profile.opp.enabled=true
            bluetooth.profile.pan.nap.enabled=true
            bluetooth.profile.pan.panu.enabled=true
            bluetooth.profile.pbap.server.enabled=true
            bluetooth.profile.sap.server.enabled=false
            # end of file
            ####################################
            # from generate-common-build-props
            # These properties identify this partition image.
            ####################################
            ro.product.system.brand=Android
            ro.product.system.device=generic
            ro.product.system.manufacturer=Xiaomi
            ro.product.system.model=mainline
            ro.product.system.cert=
            ro.product.system.name=mainline
            ro.product.system.marketname=
            ro.system.product.cpu.abilist=arm64-v8a,armeabi-v7a,armeabi
            ro.system.product.cpu.abilist32=armeabi-v7a,armeabi
            ro.system.product.cpu.abilist64=arm64-v8a
            ro.system.build.date=Tue Aug 20 06:04:47 UTC 2024
            ro.system.build.date.utc=1724133887
            ro.system.build.fingerprint=Android/missi_phone_cn_only64/missi:14/UKQ1.231003.002/V816.0.21.0.UNACNXM:user/release-keys
            ro.system.build.id=UKQ1.231003.002
            ro.system.build.tags=release-keys
            ro.system.build.type=user
            ro.system.build.version.incremental=V816.0.21.0.UNACNXM
            ro.system.build.version.release=14
            ro.system.build.version.release_or_codename=14
            ro.system.build.version.sdk=34
            ####################################
            # from out/target/product/missi/obj/PACKAGING/system_build_prop_intermediates/buildinfo.prop
            ####################################
            # begin build properties
            # autogenerated by buildinfo.sh
            ro.build.id=UKQ1.231003.002
            ro.build.keys=release-keys
            ro.build.version.incremental=V816.0.21.0.UNACNXM
            ro.build.version.sdk=34
            ro.build.version.preview_sdk=0
            ro.build.version.preview_sdk_fingerprint=REL
            ro.build.version.codename=REL
            ro.build.version.all_codenames=REL
            ro.build.version.known_codenames=Base,Base11,Cupcake,Donut,Eclair,Eclair01,EclairMr1,Froyo,Gingerbread,GingerbreadMr1,Honeycomb,HoneycombMr1,HoneycombMr2,IceCreamSandwich,IceCreamSandwichMr1,JellyBean,JellyBeanMr1,JellyBeanMr2,Kitkat,KitkatWatch,Lollipop,LollipopMr1,M,N,NMr1,O,OMr1,P,Q,R,S,Sv2,Tiramisu,UpsideDownCake
            ro.build.version.release=14
            ro.build.version.release_or_codename=14
            ro.build.version.release_or_preview_display=14
            ro.build.version.security_patch=2024-07-01
            ro.build.version.base_os=
            ro.build.version.min_supported_target_sdk=28
            ro.build.date=Tue Aug 20 06:04:47 UTC 2024
            ro.build.date.utc=1724133887
            ro.build.type=user
            ro.build.user=builder
            ro.build.host=pangu-build-component-system-155789-dwxtq-pp0wx-qr8rp
            ro.build.tags=release-keys
            ro.build.flavor=missi_phone_cn_only64-user
            # ro.product.cpu.abi and ro.product.cpu.abi2 are obsolete,
            # use ro.product.cpu.abilist instead.
            ro.product.cpu.abi=arm64-v8a
            ro.product.locale=zh-CN
            ro.wifi.channels=
            # Do not try to parse thumbprint
            # end build properties
            ####################################
            # from device/xiaomi/missi/system.prop
            ####################################
            #
            # system.prop for qssi
            #
            rild.libpath=/vendor/lib64/libril-qc-hal-qmi.so
            #rild.libargs=-d /dev/smd0
            persist.rild.nitz_plmn=
            persist.rild.nitz_long_ons_0=
            persist.rild.nitz_long_ons_1=
            persist.rild.nitz_long_ons_2=
            persist.rild.nitz_long_ons_3=
            persist.rild.nitz_short_ons_0=
            persist.rild.nitz_short_ons_1=
            persist.rild.nitz_short_ons_2=
            persist.rild.nitz_short_ons_3=
            ril.subscription.types=NV,RUIM
            DEVICE_PROVISIONED=1
            # Set network mode to (NR_LTE_TDSCDMA_CDMA_EVDO_GSM_WCDMA, NR_LTE_TDSCDMA_CDMA_EVDO_GSM_WCDMA) for 8+8 mode device on DSDS mode
            ro.telephony.default_network=33,33
            dalvik.vm.heapsize=36m
            dalvik.vm.dex2oat64.enabled=true
            dev.pm.dyn_samplingrate=1
            #ro.hdmi.enable=true
            #persist.speaker.prot.enable=false
            qcom.hw.aac.encoder=true
            #
            # system props for the cne module
            #
            persist.vendor.cne.feature=1
            #system props for the MM modules
            media.stagefright.enable-player=true
            media.stagefright.enable-http=true
            media.stagefright.enable-aac=true
            media.stagefright.enable-qcp=true
            media.stagefright.enable-fma2dp=true
            media.stagefright.enable-scan=true
            media.stagefright.thumbnail.prefer_hw_codecs=true
            mmp.enable.3g2=true
            media.aac_51_output_enabled=true
            media.settings.xml=/vendor/etc/media_profiles_vendor.xml
            #16777215 is decimal sum of supported codecs in AAL
            #codecs:(PARSER_)AAC AC3 AMR_NB AMR_WB ASF AVI DTS FLV 3GP 3G2 MKV MP2PS MP2TS MP3 OGG QCP WAV FLAC AIFF APE DSD MOV MHAS
            vendor.mm.enable.qcom_parser=16777215
            persist.mm.enable.prefetch=true
            #
            # system props for the data modules
            #
            ro.vendor.use_data_netmgrd=true
            persist.vendor.data.mode=concurrent
            #system props for time-services
            persist.timed.enable=true
            #
            # system prop for opengles version
            #
            # 196608 is decimal for 0x30000 to report version 3
            # 196609 is decimal for 0x30001 to report version 3.1
            # 196610 is decimal for 0x30002 to report version 3.2
            ro.opengles.version=196610
            #
            # System props for telephony
            # System prop to turn on CdmaLTEPhone always
            telephony.lteOnCdmaDevice=1
            #Simulate sdcard on /data/media
            #
            persist.fuse_sdcard=true
            #System props for BT
            ro.bluetooth.library_name=libbluetooth_qti.so
            persist.vendor.btstack.aac_frm_ctl.enabled=true
            persist.sys.btsatck.absvolfeature=true
            # MIUI ADD: BT_MIUIBluetoothFrame
            persist.vendor.service.bdroid.soc.alwayson=true
            #system prop for wipower support
            ro.bluetooth.emb_wp_mode=false
            ro.bluetooth.wipower=false
            # END BT_MIUIBluetoothFrame
            #system prop for RmNet Data
            persist.rmnet.data.enable=true
            persist.data.wda.enable=true
            persist.data.df.dl_mode=5
            persist.data.df.ul_mode=5
            persist.data.df.agg.dl_pkt=10
            persist.data.df.agg.dl_size=4096
            persist.data.df.mux_count=8
            persist.data.df.iwlan_mux=9
            persist.data.df.dev_name=rmnet_usb0
            #property to enable user to access Google WFD settings
            persist.debug.wfd.enable=1
            ##property to choose between virtual/external wfd display
            persist.sys.wfd.virtual=0
            #property to enable HWC for VDS
            debug.sf.enable_hwc_vds=0
            #property to latch unsignaled buffer
            debug.sf.latch_unsignaled=1
            # enable tunnel encoding for amrwb
            tunnel.audio.encode=true
            #enable voice path for PCM VoIP by default
            use.voice.path.for.pcm.voip=true
            # system prop for NFC DT
            ro.nfc.port=I2C
            #initialize QCA1530 detection
            sys.qca1530=detect
            #Enable stm events
            persist.debug.coresight.config=stm-events
            #hwui properties
            ro.hwui.texture_cache_size=72
            ro.hwui.layer_cache_size=48
            ro.hwui.r_buffer_cache_size=8
            ro.hwui.path_cache_size=32
            ro.hwui.gradient_cache_size=1
            ro.hwui.drop_shadow_cache_size=6
            ro.hwui.texture_cache_flushrate=0.4
            ro.hwui.text_small_cache_width=1024
            ro.hwui.text_small_cache_height=1024
            ro.hwui.text_large_cache_width=2048
            ro.hwui.text_large_cache_height=1024
            debug.hwui.skia_atrace_enabled=false
            config.disable_rtt=true
            #Bringup properties
            persist.sys.force_sw_gles=1
            persist.vendor.radio.atfwd.start=true
            ro.kernel.qemu.gles=0
            qemu.hw.mainkeys=0
            #Expose aux camera for below packages
            vendor.camera.aux.packagelist=org.codeaurora.snapcam,com.xiaomi.runin,com.xiaomi.cameratest,com.xiaomi.factory.mmi
            vendor.camera.aux.packagelistext=com.xiaomi.factory.CameraTestItem
            #Add snapcam in privapp list
            persist.vendor.camera.privapp.list=org.codeaurora.snapcam
            #enable IZat OptInApp overlay
            persist.vendor.overlay.izat.optin=rro
            # Property for backup NTP Server
            persist.backup.ntpServer="0.pool.ntp.org"
            #Property to enable Mag filter
            persist.vendor.sensors.enable.mag_filter=true
            #Partition source order for Product/Build properties pickup.
            ro.product.property_source_order=odm,vendor,product,system_ext,system
            #Property to enable Codec2 for audio and OMX for Video
            debug.stagefright.ccodec=1
            #Property to set native recorder's maximum base layer fps
            ro.media.recorder-max-base-layer-fps=60
            #Battery Property
            ro.charger.enable_suspend=1
            #Disable MTE Async for system server
            arm64.memtag.process.system_server=off
            # Disable blur on app launch
            ro.launcher.blur.appLaunch=0
            #button jack mode & switch
            persist.audio.button_jack.profile=volume
            persist.audio.button_jack.switch=0
            # MIUI ADD: Performance_AppInstallEnhance
            #enable speed install
            ro.miui.pm.install.speedinstall=/data/apk-tmp
            # END Performance_AppInstallEnhance
            # xiaomi ugd
            ro.gfx.driver.0=com.xiaomi.ugd
            # MIUI ADD: Performance_AppInstallEnhance
            ro.miui.enable_cloud_verify=true
            # END Performance_AppInstallEnhance
            #enable modem dump
            persist.vendor.ssr.enable_ramdumps=1
            # support night charge property
            persist.vendor.night.charge=true
            # support fuse passthrough
            persist.sys.fuse.passthrough.enable=true
            ####################################
            # from device/xiaomi/missi/system_fraction_only64.prop
            ####################################
            # This file is used to distinguish the file difference of system.prop between Qualcomm qssi and qssi_64
            # Only adapter groups can modify this file during the upgrade baseline
            ####################################
            # from variable ADDITIONAL_SYSTEM_PROPERTIES
            ####################################
            ro.treble.enabled=true
            ro.actionable_compatible_property.enabled=true
            ro.postinstall.fstab.prefix=/system
            ro.secure=1
            security.perf_harden=1
            ro.adb.secure=1
            ro.allow.mock.location=0
            ro.debuggable=0
            net.bt.name=Android
            ro.vendor.qti.va_aosp.support=1
            ro.force.debuggable=0
            ####################################
            # from variable PRODUCT_SYSTEM_PROPERTIES
            ####################################
            persist.sys.gpo.version=4
            persist.sys.gnss_back.opt=true
            persist.sys.gmo.version=0
            ro.miui.has_security_keyboard=1
            ro.miui.support_miui_ime_bottom=1
            ro.miui.support_super_clipboard=1
            persist.sys.support_super_clipboard=0
            ro.com.android.mobiledata=false
            sys.dfcservice.ctrl=false
            sys.dfcservice.compress.prog
            sys.dfcservice.enablelog.ctrl=false
            ro.miui.shell_anim_enable_fcb=true
            debug.atrace.tags.enableflags=0
            persist.traced.enable=1
            dalvik.vm.image-dex2oat-Xms=64m
            dalvik.vm.image-dex2oat-Xmx=64m
            dalvik.vm.dex2oat-Xms=64m
            dalvik.vm.dex2oat-Xmx=512m
            dalvik.vm.usejit=true
            dalvik.vm.dexopt.secondary=true
            dalvik.vm.dexopt.thermal-cutoff=2
            dalvik.vm.appimageformat=lz4
            ro.dalvik.vm.native.bridge=0
            pm.dexopt.first-boot=verify
            pm.dexopt.boot-after-ota=verify
            pm.dexopt.post-boot=extract
            pm.dexopt.boot-after-mainline-update=verify
            pm.dexopt.install=speed-profile
            pm.dexopt.install-fast=skip
            pm.dexopt.install-bulk=speed-profile
            pm.dexopt.install-bulk-secondary=verify
            pm.dexopt.install-bulk-downgraded=verify
            pm.dexopt.install-bulk-secondary-downgraded=extract
            pm.dexopt.bg-dexopt=speed-profile
            pm.dexopt.ab-ota=speed-profile
            pm.dexopt.inactive=verify
            pm.dexopt.cmdline=verify
            pm.dexopt.first-use=speed-profile
            pm.dexopt.install-create-dm=speed-profile
            pm.dexopt.baseline=speed-profile
            pm.dexopt.secondary=verify
            pm.dexopt.shared=speed
            dalvik.vm.dex2oat-resolve-startup-strings=true
            dalvik.vm.dex2oat-max-image-block-size=524288
            dalvik.vm.minidebuginfo=true
            dalvik.vm.dex2oat-minidebuginfo=true
            dalvik.vm.madvise.vdexfile.size=104857600
            dalvik.vm.madvise.odexfile.size=104857600
            dalvik.vm.madvise.artfile.size=4294967295
            dalvik.vm.usap_pool_enabled=false
            dalvik.vm.usap_refill_threshold=1
            dalvik.vm.usap_pool_size_max=3
            dalvik.vm.usap_pool_size_min=1
            dalvik.vm.usap_pool_refill_delay_ms=3000
            dalvik.vm.useartservice=true
            ro.apex.updatable=true
            ro.hwui.max_texture_allocation_size=209715200
            persist.vendor.connsysfw.enable=true
            ro.radio.device_type=1
            persist.device_config.runtime_native_boot.iorap_perfetto_enable=true
            ro.vendor.audio.notification.single=true
            ####################################
            # from variable PRODUCT_SYSTEM_DEFAULT_PROPERTIES
            ####################################
            tango.enabled=1
            tango.debug=off
            tango.pretrans.max_size=67108864
            tango.pretrans_on_install=true
            tango.pretrans.debug=off
            tango.pretrans.lib=true
            tango.pretrans.apk=true
            # end of file
        """.trimIndent()
    )
)
