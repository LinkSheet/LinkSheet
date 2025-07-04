package fe.linksheet.feature.systeminfo.device

object XiaomiRedmiNote7Pro_DroidxA14 : Device(
    buildProperties = parseTestBuildProperties(
        """
            ro.actionable_compatible_property.enabled=true
            ro.adb.secure=1
            ro.allow.mock.location=0
            ro.apex.updatable=true
            ro.baseband=msm
            ro.board.api_frozen=true
            ro.board.api_level=202404
            ro.board.platform=sm6150
            ro.boot.baseband=msm
            ro.boot.boot_devices=soc/7c4000.sdhci
            ro.boot.bootdevice=7c4000.sdhci
            ro.boot.camera.config=_pro
            ro.boot.console=ttyMSM0
            ro.boot.cpuid=0xa1815689
            ro.boot.dp=0x0
            ro.boot.dtb_idx=0
            ro.boot.dtbo_idx=0
            ro.boot.dynamic_partitions=true
            ro.boot.flash.locked=1
            ro.boot.fpsensor=fpc
            ro.boot.hardware=qcom
            ro.boot.hwc=India
            ro.boot.hwlevel=MP
            ro.boot.hwversion=1.19.0
            ro.boot.keymaster=1
            ro.boot.secureboot=1
            ro.boot.selinux=enforcing
            ro.boot.super_partition=system
            ro.boot.vbmeta.avb_version=1.0
            ro.boot.vbmeta.device_state=locked
            ro.boot.verifiedbootstate=green
            ro.boot.veritymode=enforcing
            ro.boot.warranty_bit=0
            ro.bootloader=unknown
            ro.bootmode=unknown
            ro.build.characteristics=nosdcard
            ro.build.date=Fri Nov  1 1
            ro.build.date.utc=1730461603
            ro.build.description=- 14 AP2A.240905.003
            ro.build.display.id=AP2A.240905.003
            ro.build.expect.cn=CN,4.3.c2-00029,V11.0.1.0.QFHCNXM
            ro.build.expect.in=India,4.3.c2-00029,V12.0.0.2.QFHINXM
            ro.build.fingerprint=Xiaomi/droidx_violet/viole
            ro.build.flavor=droidx_violet-user
            ro.build.host=droidx
            ro.build.id=AP2A.240905.003
            ro.build.keys=release-keys
            ro.build.product=violet
            ro.build.tags=release-keys
            ro.build.type=user
            ro.build.user=europa
            ro.build.version.all_codenames=REL
            ro.build.version.base_os=
            ro.build.version.codename=REL
            ro.build.version.incremental=eng.europa.20241101.114646
            ro.build.version.known_codenames=Base,Base11,Cupcake,Donut,Eclair,Eclair01,EclairMr1,Froyo,Gingerbread,GingerbreadMr1,Honeycomb,HoneycombMr1,HoneycombMr2,IceCreamSandwich,IceCreamSandwichMr1,JellyBean,JellyBeanMr1,JellyBeanMr2,Kitkat,KitkatWatch,Lollipop,LollipopMr1,M,N,NMr1,O,OMr1,P,Q,R,S,Sv2,Tiramisu,UpsideDownCake,VanillaIceCream
            ro.build.version.min_supported_target_sdk=28
            ro.build.version.preview_sdk=0
            ro.build.version.preview_sdk_fingerprint=REL
            ro.build.version.release=14
            ro.build.version.release_or_codename=14
            ro.build.version.release_or_preview_display=14
            ro.build.version.sdk=34
            ro.build.version.security_patch=2024-10-01
            ro.carrier=unknown
            ro.com.android.dataroaming=true
            ro.com.android.mobiledata=false
            ro.com.google.clientidbase=android-xiaomi
            ro.com.google.ime.bs_theme=true
            ro.com.google.ime.system_lm_dir=/product/usr/share/ime/google/d3_lms
            ro.com.google.ime.theme_id=5
            ro.com.google.lens.oem_camera_package=com.android.camera
            ro.config.alarm_alert=Bright_morning.ogg
            ro.config.media_vol_default=10
            ro.config.media_vol_steps=25
            ro.config.nocheckin=1
            ro.config.notification_sound=Popcorn.ogg
            ro.config.ringtone=The_big_adventure.ogg
            ro.config.vc_call_vol_steps=6
            ro.control_privapp_permissions=enforce
            ro.crypto.allow_encrypt_override=true
            ro.crypto.dm_default_key.options_format.version=2
            ro.crypto.metadata.enabled=true
            ro.crypto.state=encrypted
            ro.crypto.type=file
            ro.crypto.volume.filenames_mode=aes-256-cts
            ro.crypto.volume.metadata.method=dm-default-key
            ro.dalvik.vm.enable_uffd_gc=true
            ro.dalvik.vm.native.bridge=0
            ro.droidx.build.date=20241101-1151
            ro.droidx.build.version=2.4
            ro.droidx.chipset=Snapdragon 675
            ro.droidx.codename=Europa
            ro.droidx.device=violet
            ro.droidx.display_resolution=6.3\" IPS LCD 1080x2340
            ro.droidx.maintainer=Dr.Opto
            ro.droidx.releasetype=OFFICIAL
            ro.droidx.releasevarient=Gapps
            ro.droidx.version=14-Europa
            ro.face.sense_service=true
            ro.fastbootd.available=true
            ro.force.debuggable=0
            ro.fota.oem=Xiaomi
            ro.fuse.bpf.is_running=false
            ro.hardware=qcom
            ro.hardware.egl=adreno
            ro.hardware.keystore_desede=true
            ro.hardware.vulkan=adreno
            ro.hwui.use_vulkan=true
            ro.input.video_enabled=false
            ro.kernel.android.checkjni=0
            ro.kernel.checkjni=0
            ro.kernel.version=4.14
            ro.launcher.blur.appLaunch=0
            ro.llndk.api_level=202404
            ro.lmk.critical_upgrade=true
            ro.lmk.debug=false
            ro.lmk.downgrade_pressure=60
            ro.lmk.kill_heaviest_task=true
            ro.lmk.kill_threshold_max=4096
            ro.lmk.kill_threshold_min=1536
            ro.lmk.kill_timeout_ms=15
            ro.lmk.upgrade_pressure=80
            ro.logd.size.stats=64K
            ro.max.fling_velocity=12000
            ro.media.recorder-max-base-layer-fps=60
            ro.min.fling_velocity=8000
            ro.miui.build.region=cn
            ro.miui.notch=1
            ro.miui.region=CN
            ro.miui.ui.version.code=13
            ro.miui.ui.version.name=V130
            ro.modversion=2.4-20241101-1151-OFFICIAL-violet
            ro.mot.eri.losalert.delay=100
            ro.netflix.bsp_rev=Q6150-17263-1
            ro.odm.build.date=Fri Nov  1 1
            ro.odm.build.date.utc=1730461603
            ro.odm.build.fingerprint=Xiaomi/droidx_violet/viole
            ro.odm.build.version.incremental=eng.europa.20241101.114646
            ro.opa.eligible_device=true
            ro.opengles.version=196610
            ro.postinstall.fstab.prefix=/system
            ro.product.board=sm6150
            ro.product.brand=Xiaomi
            ro.product.build.date=Fri Nov  1 1
            ro.product.build.date.utc=1730461603
            ro.product.build.fingerprint=Xiaomi/droidx_violet/viole
            ro.product.build.id=AP2A.240905.003
            ro.product.build.tags=release-keys
            ro.product.build.type=user
            ro.product.build.version.incremental=eng.europa.20241101.114646
            ro.product.build.version.release=14
            ro.product.build.version.release_or_codename=14
            ro.product.build.version.sdk=34
            ro.product.cpu.abi=arm64-v8a
            ro.product.cpu.abilist=arm64-v8a,armeabi-v7a,armeabi
            ro.product.cpu.abilist32=armeabi-v7a,armeabi
            ro.product.cpu.abilist64=arm64-v8a
            ro.product.cpu.pagesize.max=4096
            ro.product.device=violet
            ro.product.first_api_level=28
            ro.product.locale=en-US
            ro.product.manufacturer=Xiaomi
            ro.product.model=Redmi Note 7 Pro
            ro.product.name=droidx_violet
            ro.product.odm.brand=Xiaomi
            ro.product.odm.device=violet
            ro.product.odm.manufacturer=Xiaomi
            ro.product.odm.model=Redmi Note 7 Pro
            ro.product.odm.name=droidx_violet
            ro.product.product.brand=Xiaomi
            ro.product.product.device=violet
            ro.product.product.manufacturer=Xiaomi
            ro.product.product.model=Redmi Note 7 Pro
            ro.product.product.name=droidx_violet
            ro.product.system.brand=Xiaomi
            ro.product.system.device=violet
            ro.product.system.manufacturer=Xiaomi
            ro.product.system.model=Redmi Note 7 Pro
            ro.product.system.name=droidx_violeT
            ro.product.system_ext.brand=Xiaomi
            ro.product.system_ext.device=violet
            ro.product.system_ext.manufacturer=Xiaomi
            ro.product.system_ext.model=Redmi Note 7 Pro
            ro.product.system_ext.name=droidx_violet
            ro.product.vendor.brand=Xiaomi
            ro.product.vendor.device=violet
            ro.product.vendor.manufacturer=Xiaomi
            ro.product.vendor.model=Redmi Note 7 Pro
            ro.product.vendor.name=droidx_violet
            ro.product.vendor_dlkm.brand=Xiaomi
            ro.product.vendor_dlkm.device=violet
            ro.product.vendor_dlkm.manufacturer=Xiaomi
            ro.product.vendor_dlkm.model=Redmi Note 7 Pro
            ro.product.vendor_dlkm.name=droidx_violet
            ro.property_service.version=2
            ro.revision=0
            ro.ril.disable.power.collapse=0
            ro.ril.fast.dormancy.rule=1
            ro.ril.fast.dormancy.timeout=3
            ro.setupwizard.enterprise_mode=1
            ro.setupwizard.esim_cid_ignore=00000001
            ro.sf.lcd_density=440
            ro.soc.manufacturer=QTI
            ro.soc.model=SM6150
            ro.storage_manager.enabled=true
            ro.surface_flinger.force_hwc_copy_for_virtual_displays=true
            ro.surface_flinger.has_HDR_display=true
            ro.surface_flinger.has_wide_color_display=true
            ro.surface_flinger.max_frame_buffer_acquired_buffers=3
            ro.surface_flinger.max_virtual_display_dimension=4096
            ro.surface_flinger.protected_contents=true
            ro.surface_flinger.use_color_management=true
            ro.surface_flinger.wcg_composition_dataspace=143261696
            ro.system.build.date=Fri Nov  1 1
            ro.system.build.date.utc=1730461603
            ro.system.build.fingerprint=Xiaomi/droidx_violet/viole
            ro.system.build.id=AP2A.240905.003
            ro.system.build.tags=release-keys
            ro.system.build.type=user
            ro.system.build.version.incremental=eng.europa.20241101.114646
            ro.system.build.version.release=14
            ro.system.build.version.release_or_codename=14
            ro.system.build.version.sdk=34
            ro.system.product.cpu.abilist=arm64-v8a,armeabi-v7a,armeabi
            ro.system.product.cpu.abilist32=armeabi-v7a,armeabi
            ro.system.product.cpu.abilist64=arm64-v8a
            ro.system_ext.build.date=Fri Nov  1 1
            ro.system_ext.build.date.utc=1730461603
            ro.system_ext.build.fingerprint=Xiaomi/droidx_violet/viole
            ro.system_ext.build.id=AP2A.240905.003
            ro.system_ext.build.tags=release-keys
            ro.system_ext.build.type=user
            ro.system_ext.build.version.incremental=eng.europa.20241101.114646
            ro.system_ext.build.version.release=14
            ro.system_ext.build.version.release_or_codename=14
            ro.system_ext.build.version.sdk=34
            ro.telephony.block_binder_thread_on_incoming_calls=false
            ro.telephony.call_ring.multiple=false
            ro.telephony.default_cdma_sub=0
            ro.telephony.default_network=22,22
            ro.treble.enabled=true
            ro.vendor.api_level=28
            ro.vendor.build.date=Fri Nov  1 1
            ro.vendor.build.date.utc=1730461603
            ro.vendor.build.fingerprint=Xiaomi/droidx_violet/viole
            ro.vendor.build.id=AP2A.240905.003
            ro.vendor.build.security_patch=2024-10-01
            ro.vendor.build.tags=release-keys
            ro.vendor.build.type=user
            ro.vendor.build.version.incremental=eng.europa.20241101.114646
            ro.vendor.build.version.release=14
            ro.vendor.build.version.release_or_codename=14
            ro.vendor.build.version.sdk=34
            ro.vendor.product.cpu.abilist=arm64-v8a,armeabi-v7a,armeabi
            ro.vendor.product.cpu.abilist32=armeabi-v7a,armeabi
            ro.vendor.product.cpu.abilist64=arm64-v8a
            ro.vendor.qti.va_aosp.support=1
            ro.vendor.qti.va_odm.support=1
            ro.vendor_dlkm.build.date=Fri Nov  1 1
            ro.vendor_dlkm.build.date.utc=1730461603
            ro.vendor_dlkm.build.fingerprint=Xiaomi/droidx_violet/viole
            ro.vendor_dlkm.build.id=AP2A.240905.003
            ro.vendor_dlkm.build.tags=release-keys
            ro.vendor_dlkm.build.type=user
            ro.vendor_dlkm.build.version.incremental=eng.europa.20241101.114646
            ro.vendor_dlkm.build.version.release=14
            ro.vendor_dlkm.build.version.release_or_codename=14
            ro.vendor_dlkm.build.version.sdk=34
            ro.vold.umsdirtyratio=20
            ro.warranty_bit=0
            ro.wifi.channels=
            ro.zygote=zygote64_32
        """.trimIndent()
    )
)
