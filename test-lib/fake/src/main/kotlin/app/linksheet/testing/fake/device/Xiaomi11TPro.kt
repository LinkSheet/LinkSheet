package app.linksheet.testing.fake.device

object Xiaomi11TPro_A13 : Device(
    codename = "cas",
    fingerprint = "missi_phonenonab_cn-user-13-TKQ1.221114.001-V14.0.2.0.TJJCNXM-release-keys",
    buildProperties = parseTestBuildProperties(
        """
            ####################################
            # from generate-common-build-props
            # These properties identify this partition image.
            ####################################
            ro.product.product.brand=Xiaomi
            ro.product.product.device=missi
            ro.product.product.manufacturer=QUALCOMM
            ro.product.product.model=miproduct
            ro.product.product.cert=
            ro.product.product.name=cas
            ro.product.product.marketname=
            ro.product.build.date=Mon Jun  5 06:17:25 UTC 2023
            ro.product.build.date.utc=1685945845
            ro.product.build.fingerprint=Xiaomi/cas/missi:13/TKQ1.221114.001/V14.0.2.0.TJJCNXM:user/release-keys
            ro.product.build.id=TKQ1.221114.001
            ro.product.build.tags=release-keys
            ro.product.build.type=user
            ro.product.build.version.incremental=V14.0.2.0.TJJCNXM
            ro.product.build.version.release=13
            ro.product.build.version.release_or_codename=13
            ro.product.build.version.sdk=33
            ####################################
            # from variable ADDITIONAL_PRODUCT_PROPERTIES
            ####################################
            ro.product.vndk.version=33
            ro.build.characteristics=nosdcard
            ####################################
            # from variable PRODUCT_PRODUCT_PROPERTIES
            ####################################
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
            persist.sys.qseelogd=true
            ro.com.android.dataroaming=false
            debug.sf.disable_backpressure=1
            ro.miui.ui.version.code=14
            ro.miui.ui.version.name=V140
            ro.com.android.mobiledata=false
            ro.miui.build.region=cn
            ro.product.mod_device=cas
            persist.sys.disable_bganimate=false
            ro.miui.has_gmscore=1
            ro.miui.notch=1
            ro.sf.lcd_density=440
            persist.miui.density_v2=440
            debug.sf.disable_backpressure=1
            persist.sys.miui_animator_sched.bigcores=4-6
            persist.sys.miui_animator_sched.sched_threads=2
            persist.sys.miui.sf_cores=4-7
            persist.vendor.display.miui.composer_boost=4-7
            persist.sys.miui_animator_sched.big_prime_cores=4-7
            persist.sys.enable_templimit=true
            persist.sys.minfree_def=73728,92160,110592,154832,482560,579072
            persist.sys.minfree_6g=73728,92160,110592,258048,663552,903168
            persist.sys.minfree_8g=73728,92160,110592,387072,1105920,1451520
            persist.miui.dexopt.first_use=true
            persist.sys.usap_pool_enabled=false
            ro.build.shutdown_timeout=2
            ro.vendor.qti.sys.fw.bservice_enable=true
            ro.vendor.radio.features_common=3
            ro.miui.vicegwsd=true
            persist.sys.mms.use_legacy=true
            persist.sys.mms.kill_fas_cached_idle=false
            persist.sys.mms.bg_apps_limit=96
            persist.sys.gz.enable=true
            persist.miui.extm.enable=1
            dalvik.vm.heaptargetutilization=0.5
            dalvik.vm.heapminfree=2m
            persist.sys.miui_modify_heap_config.enable=true
            ro.millet.netlink=29
            persist.sys.cts.testTrimMemActivityBg.wk.enable=true
            # end of file
            ####################################
            # from generate-common-build-props
            # These properties identify this partition image.
            ####################################
            ro.product.system.brand=qti
            ro.product.system.device=missi
            ro.product.system.manufacturer=QUALCOMM
            ro.product.system.model=missi system image for arm64
            ro.product.system.cert=
            ro.product.system.name=missi
            ro.product.system.marketname=
            ro.system.product.cpu.abilist=arm64-v8a,armeabi-v7a,armeabi
            ro.system.product.cpu.abilist32=armeabi-v7a,armeabi
            ro.system.product.cpu.abilist64=arm64-v8a
            ro.system.build.date=Mon Jun  5 06:17:21 UTC 2023
            ro.system.build.date.utc=1685945841
            ro.system.build.fingerprint=qti/missi/missi:13/TKQ1.221114.001/V14.0.2.0.TJJCNXM:user/release-keys
            ro.system.build.id=TKQ1.221114.001
            ro.system.build.tags=release-keys
            ro.system.build.type=user
            ro.system.build.version.incremental=V14.0.2.0.TJJCNXM
            ro.system.build.version.release=13
            ro.system.build.version.release_or_codename=13
            ro.system.build.version.sdk=33
            ####################################
            # from out/target/product/missi/obj/PACKAGING/system_build_prop_intermediates/buildinfo.prop
            ####################################
            # begin build properties
            # autogenerated by buildinfo.sh
            ro.build.id=TKQ1.221114.001
            ro.build.keys=test-keys
            ro.build.version.incremental=V14.0.2.0.TJJCNXM
            ro.build.version.sdk=33
            ro.build.version.preview_sdk=0
            ro.build.version.preview_sdk_fingerprint=REL
            ro.build.version.codename=REL
            ro.build.version.all_codenames=REL
            ro.build.version.known_codenames=Base,Base11,Cupcake,Donut,Eclair,Eclair01,EclairMr1,Froyo,Gingerbread,GingerbreadMr1,Honeycomb,HoneycombMr1,HoneycombMr2,IceCreamSandwich,IceCreamSandwichMr1,JellyBean,JellyBeanMr1,JellyBeanMr2,Kitkat,KitkatWatch,Lollipop,LollipopMr1,M,N,NMr1,O,OMr1,P,Q,R,S,Sv2,Tiramisu
            ro.build.version.release=13
            ro.build.version.release_or_codename=13
            ro.build.version.release_or_preview_display=13
            ro.build.version.security_patch=2023-04-01
            ro.build.version.base_os=
            ro.build.version.min_supported_target_sdk=23
            ro.build.date=Mon Jun  5 06:17:21 UTC 2023
            ro.build.date.utc=1685945841
            ro.build.type=user
            ro.build.user=builder
            ro.build.host=pangu-build-component-system-193522-5jwjz-102cd-gnw1c
            ro.build.tags=release-keys
            ro.build.flavor=missi_phonenonab_cn-user
            ro.build.system_root_image=false
            ro.product.mod_device=cas
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
            # system.prop for missi
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
            persist.vendor.service.bdroid.soc.alwayson=true
            ro.bluetooth.emb_wp_mode=false
            ro.bluetooth.wipower=false
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
            debug.sf.enable_hwc_vds=1
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
            # Disable ftrace by default
            persist.debug.ftrace_events_disable=Yes
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
            vendor.camera.aux.packagelistext=com.xiaomi.factory.CameraTestItem,com.xiaomi.factorycamera
            #Add snapcam in privapp list
            persist.vendor.camera.privapp.list=org.codeaurora.snapcam
            #AON uses camera 9 on the L1-T platform. On platform 8550, AON uses camera 8
            persist.vendor.camera.aon.cameraId=8
            persist.vendor.camera.aon8475.cameraId=9
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
            #enable speed install
            ro.miui.pm.install.speedinstall=/data/apk-tmp
            #enable ramdump
            persist.vendor.ssr.enable_ramdumps=1
            #Performance disable iorapd
            ro.iorapd.enable=false
            # property for fuse passthrough
            persist.sys.fuse.passthrough.enable=true
            # nokia audio room for vts
            persist.vendor.audio.ozo.codec.enable=true
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
            ####################################
            # from variable PRODUCT_SYSTEM_PROPERTIES
            ####################################
            debug.atrace.tags.enableflags=0
            persist.traced.enable=1
            dalvik.vm.image-dex2oat-Xms=64m
            dalvik.vm.image-dex2oat-Xmx=64m
            dalvik.vm.dex2oat-Xms=64m
            dalvik.vm.dex2oat-Xmx=512m
            dalvik.vm.usejit=true
            dalvik.vm.usejitprofiles=true
            dalvik.vm.dexopt.secondary=true
            dalvik.vm.dexopt.thermal-cutoff=2
            dalvik.vm.appimageformat=lz4
            ro.dalvik.vm.native.bridge=0
            pm.dexopt.first-boot=verify
            pm.dexopt.boot-after-ota=verify
            pm.dexopt.post-boot=extract
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
            pm.dexopt.secondary=speed-profile
            pm.dexopt.shared=speed
            dalvik.vm.dex2oat-resolve-startup-strings=true
            dalvik.vm.dex2oat-max-image-block-size=524288
            dalvik.vm.minidebuginfo=true
            dalvik.vm.dex2oat-minidebuginfo=true
            dalvik.vm.madvise.vdexfile.size=104857600
            dalvik.vm.madvise.odexfile.size=104857600
            dalvik.vm.madvise.artfile.size=4294967295
            persist.device_config.runtime_native_boot.iorap_perfetto_enable=true
            ro.support_one_handed_mode=true
            persist.mtb.modemdump=true
            sys.dfcservice.ctrl=false
            sys.dfcservice.compress.prog
            sys.dfcservice.enablelog.ctrl=false
            persist.sys.gpo.version=4
            ro.miui.has_security_keyboard=1
            ro.miui.support_miui_ime_bottom=1
            ro.com.android.mobiledata=false
            ####################################
            # from variable PRODUCT_SYSTEM_DEFAULT_PROPERTIES
            ####################################
            # end of file
        """.trimIndent()
    )
) {
}

