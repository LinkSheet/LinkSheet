package fe.fastforwardkt

object FastForwardRules {
    const val fetchedAt = 1702072654608L
    val rules = mapOf(
        "path_base64" to listOf(
            Regex("https?:\\/\\/.*bursadrakor\\.com\\/protect-link\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*getfile\\.mobi\\/get\\/files\\/.*", RegexOption.IGNORE_CASE),
            Regex(".*:\\/\\/hikarinoakari\\.com\\/out\\/\\?.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*itscybertech\\.com\\/.*", RegexOption.IGNORE_CASE)
        ),
        "path_s_encoded" to listOf(Regex("https?:\\/\\/.*gslink\\.co\\/e\\/.*\\/s\\/.*", RegexOption.IGNORE_CASE)),
        "path_r_base64" to listOf(Regex("https?:\\/\\/.*linkspy\\.cc\\/r\\/.*", RegexOption.IGNORE_CASE)),
        "path_dl_base64" to listOf(
            Regex("https?:\\/\\/.*k2nblog\\.com\\/dl\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*filekita\\.me\\/page\\/dl\\/.*", RegexOption.IGNORE_CASE)
        ),
        "path_u_id_base64" to listOf(
            Regex(
                "https?:\\/\\/.*shrink-service\\.it\\/u\\/.*\\/.*",
                RegexOption.IGNORE_CASE
            )
        ),
        "path_ads_hex" to listOf(Regex("https?:\\/\\/.*leechall\\.com\\/ads\\/.*", RegexOption.IGNORE_CASE)),
        "query_raw" to listOf(
            Regex("https?:\\/\\/.*anonym\\.to\\/\\?.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*anonymz\\.com\\/\\?.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*hidereferrer\\.com\\/\\?.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*pixiv\\.net\\/jump\\.php\\?.*", RegexOption.IGNORE_CASE)
        ),
        "query_base64" to listOf(
            Regex("https?:\\/\\/.*hikarinoakariost\\.info\\/out\\/\\?.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*p3dm\\.ru\\/download-en\\.html\\?.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*iplhd\\.cf\\/.*\\?.*", RegexOption.IGNORE_CASE)
        ),
        "hash_base64" to listOf(Regex("https?:\\/\\/.*acorme\\.com\\/.*", RegexOption.IGNORE_CASE)),
        "param_url_general" to listOf(
            Regex(".*:\\/\\/.*\\/st\\?api=.*&url=.*", RegexOption.IGNORE_CASE),
            Regex(".*:\\/\\/.*\\/st\\/\\?api=.*&url=.*", RegexOption.IGNORE_CASE),
            Regex(".*:\\/\\/.*\\/full\\?api=.*&url=.*", RegexOption.IGNORE_CASE),
            Regex(".*:\\/\\/.*\\/full\\/\\?api=.*&url=.*", RegexOption.IGNORE_CASE),
            Regex(".*:\\/\\/.*\\/full\\?api=.*&url=.*&type=.*", RegexOption.IGNORE_CASE),
            Regex(".*:\\/\\/.*\\/full\\/\\?api=.*&url=.*&type=.*", RegexOption.IGNORE_CASE),
            Regex(".*:\\/\\/.*\\/api\\/.*\\/full-pages\\?api_key=.*&url=.*&type=.*", RegexOption.IGNORE_CASE)
        ),
        "param_url_encoded" to listOf(
            Regex(".*:\\/\\/.*\\/safeme\\/\\?.*", RegexOption.IGNORE_CASE),
            Regex(".*:\\/\\/.*\\/url\\/go\\.php\\?url=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*leechall\\.com\\/redirect\\.php\\?url=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*news-gg\\.com\\/l\\/\\?.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*mobile01\\.com\\/redirect\\.php\\?.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*nurhamka\\.com\\/.*\\?url=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*linepc\\.site\\/.*\\?url=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*adobedownload\\.org\\/redirect\\/\\?url=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*sopasti\\.com\\/anime\\.php\\?.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*zxro\\.com\\/u\\/.*\\?url=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*macdownload\\.org\\/redirect\\/\\?url=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*forek\\.info\\/.*\\?url=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*creditcable\\.info\\/.*\\/.*\\?.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*adfoc\\.us\\/serve\\/sitelinks\\/\\?.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*made-by\\.org\\/redirection\\/\\?url=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*itsbx\\.com\\/redirect\\/\\?url=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*forocoches\\.com\\/link\\.php\\?url=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*pixiv\\.net\\/jump\\.php\\?url=.*", RegexOption.IGNORE_CASE),
            Regex(".*:\\/\\/hdblurayindir\\.com\\/git\\.php\\?url=.*", RegexOption.IGNORE_CASE),
            Regex(".*:\\/\\/gate\\.sc\\/\\?url=.*", RegexOption.IGNORE_CASE),
            Regex(".*:\\/\\/freetutsdownload\\.net\\/redirect-to\\/\\?url=.*", RegexOption.IGNORE_CASE),
            Regex(".*:\\/\\/mcpedl\\.com\\/leaving\\/\\?url=.*", RegexOption.IGNORE_CASE),
            Regex(".*:\\/\\/go\\.redirectingat\\.com\\/\\?id=.*&url=.*", RegexOption.IGNORE_CASE)
        ),
        "param_url_raw" to listOf(
            Regex(
                "https?:\\/\\/.*steamcommunity\\.com\\/linkfilter\\/\\?url=.*",
                RegexOption.IGNORE_CASE
            )
        ),
        "param_q_encoded" to listOf(Regex("https?:\\/\\/.*youtube\\.com\\/redirect\\?.*", RegexOption.IGNORE_CASE)),
        "param_aurl_encoded" to listOf(
            Regex(
                "https?:\\/\\/.*folderenius\\.com\\/.*\\/.*\\?.*",
                RegexOption.IGNORE_CASE
            )
        ),
        "param_capital_url_encoded" to listOf(
            Regex(
                "https?:\\/\\/.*unlockapk\\.com\\/dl\\/mirror\\.php\\?.*",
                RegexOption.IGNORE_CASE
            )
        ),
        "param_rel_base64" to listOf(
            Regex("https?:\\/\\/.*kharismanews\\.com\\/\\?rel=.*", RegexOption.IGNORE_CASE),
            Regex(".*:\\/\\/out\\.x-forex\\.site\\/\\?rel=.*", RegexOption.IGNORE_CASE)
        ),
        "param_link_encoded" to listOf(Regex("https?:\\/\\/.*spaste\\.com\\/r\\/.*link=.*", RegexOption.IGNORE_CASE)),
        "param_link_base64" to listOf(
            Regex(
                "https?:\\/\\/.*leechpremium\\.link\\/cheat\\/\\?link=.*",
                RegexOption.IGNORE_CASE
            ),
            Regex("https?:\\/\\/.*bdzone\\.xyz\\/cheat\\.php\\?link=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*extramovies\\.casa\\/download\\.php\\?name=.*&link=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*gilakerja\\.com\\/.*\\/\\?link=.*", RegexOption.IGNORE_CASE),
            Regex(".*:\\/\\/sekolahgan\\.my\\.id\\/\\?link=.*", RegexOption.IGNORE_CASE)
        ),
        "param_link_encoded_base64" to listOf(
            Regex(
                "https?:\\/\\/.*safelinkgratis\\.info\\/.*\\?link=.*",
                RegexOption.IGNORE_CASE
            )
        ),
        "param_kesehatan_base64" to listOf(
            Regex(
                "https?:\\/\\/.*infosia\\.xyz\\/\\?kesehatan=.*",
                RegexOption.IGNORE_CASE
            )
        ),
        "param_wildcard_base64" to listOf(
            Regex("https?:\\/\\/.*pafpaf\\.info\\/\\?.*=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*binerfile\\.info\\/\\?.*=.*", RegexOption.IGNORE_CASE),
            Regex(".*:\\/\\/kurosafety\\.menantisenja\\.com\\/\\?.*=.*", RegexOption.IGNORE_CASE),
            Regex(".*:\\/\\/hightech\\.web\\.id\\/.*\\?.*=.*", RegexOption.IGNORE_CASE)
        ),
        "param_r_base64" to listOf(
            Regex(".*:\\/\\/.*\\/\\?r=.*", RegexOption.IGNORE_CASE),
            Regex(".*:\\/\\/.*\\/api\\?r=.*", RegexOption.IGNORE_CASE),
            Regex(".*:\\/\\/.*\\/api\\/\\?r=.*", RegexOption.IGNORE_CASE)
        ),
        "param_kareeI_base64_pipes" to listOf(
            Regex(
                "https?:\\/\\/.*blogspot\\.com\\/\\?kareeI=.*",
                RegexOption.IGNORE_CASE
            )
        ),
        "param_cr_base64" to listOf(Regex("https?:\\/\\/.*ouo\\.today\\/\\?.*", RegexOption.IGNORE_CASE)),
        "param_a_base64" to listOf(Regex("https?:\\/\\/.*adsafelink\\.net\\/generate\\?a=.*", RegexOption.IGNORE_CASE)),
        "param_url_base64" to listOf(
            Regex(".*:\\/\\/.*\\/p\\/.*\\.html\\?url=aHR0c.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*blogspot\\.com\\/.*\\/.*\\/.*\\?url=.*", RegexOption.IGNORE_CASE),
            Regex(".*:\\/\\/.*\\/descargar\\/index\\.php\\?url=.*", RegexOption.IGNORE_CASE),
            Regex(".*:\\/\\/.*\\/gotothedl\\.html\\?url=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*mispuani\\.xyz\\/.*\\?url=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*safelinku\\.icu\\/.*\\?url=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*ad4msan\\.win\\/safe\\?url=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*infotekno\\.net\\/vga\\?url=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*sehuruf\\.com\\/linkku\\/\\?url=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*novicearea\\.com\\/.*\\/\\?url=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*dikatekno\\.com\\/go\\/\\?url=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*blackmod\\.net\\/dl\\/\\?url=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*hulblog\\.com\\/.*\\/\\?url=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*omglyrics\\.com\\/.*\\/\\?url=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*nyantaidulu\\.com\\/linknya\\/\\?url=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*pixelshost\\.com\\/\\?url=.*", RegexOption.IGNORE_CASE)
        ),
        "param_id_base64" to listOf(
            Regex(".*:\\/\\/.*\\/p\\/.*\\.html\\?id=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*newsdecorate\\.com\\/\\?id=.*", RegexOption.IGNORE_CASE)
        ),
        "param_get_base64" to listOf(
            Regex(
                ".*:\\/\\/safelink\\.hargawebsite\\.com\\/\\?get=.*",
                RegexOption.IGNORE_CASE
            ), Regex(".*:\\/\\/safelink\\.hargawebsite\\.com\\/.*\\/\\?get=.*", RegexOption.IGNORE_CASE)
        ),
        "param_u_base64" to listOf(
            Regex("https?:\\/\\/.*rikucan\\.com\\/\\?u=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*noriskdomain\\.com\\/.*\\/analyze\\?.*", RegexOption.IGNORE_CASE)
        ),
        "param_go_base64" to listOf(
            Regex(".*:\\/\\/.*\\/api\\/index\\.php\\?go=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*telolet\\.in\\/\\?go=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*lompat\\.in\\/\\?go=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*grandmovie21\\.com\\/\\?go=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*postku\\.org\\/\\?go=.*", RegexOption.IGNORE_CASE)
        ),
        "param_site_base64" to listOf(
            Regex("https?:\\/\\/.*masreyhan\\.com\\/.*\\?site=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*pasardownload\\.com\\/.*\\?site=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*vius\\.info\\/.*\\?site=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*cariskuy\\.com\\/.*\\?site=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*losstor\\.com\\/ini\\/\\?site=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*giga74\\.com\\/.*\\?site=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*hfiz\\.site\\/\\?site=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*cemiw\\.net\\/.*\\?site=.*", RegexOption.IGNORE_CASE)
        ),
        "param_reff_base64" to listOf(
            Regex("https?:\\/\\/.*remiyu\\.me\\/.*\\?reff=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*ceksite\\.id\\/.*\\?reff=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*postku\\.org\\/.*\\?reff=.*", RegexOption.IGNORE_CASE)
        ),
        "param_s_encoded" to listOf(
            Regex("https?:\\/\\/.*ouo\\.io\\/.*\\?s=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*cpmlink\\.net\\/s\\/.*\\?s=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*shon\\.xyz\\/s\\/.*\\?s=.*", RegexOption.IGNORE_CASE)
        ),
        "param_dl_encoded_base64" to listOf(
            Regex(
                "https?:\\/\\/.*nimebatch\\.net\\/download\\/\\?dl=.*",
                RegexOption.IGNORE_CASE
            )
        ),
        "param_health_encoded_base64" to listOf(
            Regex(
                "https?:\\/\\/.*newhealthblog\\.com\\/\\?health=.*",
                RegexOption.IGNORE_CASE
            )
        ),
        "param_id_reverse_base64" to listOf(
            Regex(
                ".*:\\/\\/.*\\/instagram\\/campanha\\.php\\?.*",
                RegexOption.IGNORE_CASE
            )
        ),
        "param_token_base64" to listOf(
            Regex(
                "https?:\\/\\/.*mundodocinema\\.ga\\/redirecionamento_final\\?.*",
                RegexOption.IGNORE_CASE
            )
        ),
        "param_href_encoded" to listOf(
            Regex("https?:\\/\\/.*maranhesduve\\.club\\/\\?.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*sparbuttantowa\\.pro\\/.*\\?.*", RegexOption.IGNORE_CASE)
        ),
        "param_short_encoded" to listOf(Regex("https?:\\/\\/.*duit\\.cc\\/.*\\?.*", RegexOption.IGNORE_CASE)),
        "param_id_base64_replacements" to listOf(
            Regex(
                "https?:\\/\\/.*safelinkconverter\\.com\\/.*\\?.*",
                RegexOption.IGNORE_CASE
            ),
            Regex("https?:\\/\\/.*safelinkreview\\.com\\/.*\\?.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*safelinkreviewx\\.com\\/.*\\?.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*safelinkreview\\.co\\/.*\\?.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*awsubsco\\.ml\\/.*\\?.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*awsubsco\\.cf\\/.*\\?.*", RegexOption.IGNORE_CASE)
        ),
        "param_dest_encoded" to listOf(
            Regex(
                "https?:\\/\\/.*ecleneue\\.com\\/pushredirect\\/\\?.*",
                RegexOption.IGNORE_CASE
            )
        ),
        "param_go_hex" to listOf(
            Regex("https?:\\/\\/.*zflas\\.com\\/.*\\?go=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*365myoffice\\.com\\/\\?go=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*shiroyasha\\.me\\/\\?go=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*copydev\\.com\\/\\?go=.*", RegexOption.IGNORE_CASE)
        ),
        "param_to_base64" to listOf(
            Regex(
                "https?:\\/\\/.*beermoneyforum\\.com\\/redirect\\?.*",
                RegexOption.IGNORE_CASE
            ), Regex("https?:\\/\\/.*instant-hack\\.to\\/redirect.*\\?to=.*", RegexOption.IGNORE_CASE)
        ),
        "param_data_base64" to listOf(
            Regex("https?:\\/\\/.*magersari\\.web\\.id\\/\\?.*", RegexOption.IGNORE_CASE),
            Regex(".*:\\/\\/zodiark\\.xyz\\/\\?data=.*", RegexOption.IGNORE_CASE),
            Regex(".*:\\/\\/rahwana\\.xyz\\/\\?data=.*", RegexOption.IGNORE_CASE),
            Regex(".*:\\/\\/caraterbaik\\.my\\.id\\/\\?data=.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*wisatamu\\.my\\.id\\/\\?data=.*", RegexOption.IGNORE_CASE)
        ),
        "useragent_chrome" to listOf(Regex("https?:\\/\\/.*fluxteam\\.xyz\\/.*", RegexOption.IGNORE_CASE)),
        "useragent_empty" to listOf(
            Regex("https?:\\/\\/.*sh\\.st\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*ceesty\\.com\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*corneey\\.com\\/.*", RegexOption.IGNORE_CASE)
        ),
        "useragent_iphone" to listOf(Regex("https?:\\/\\/.*linkvertise\\.com\\/.*", RegexOption.IGNORE_CASE)),
        "redirect_persist_id_path" to listOf(
            Regex("https?:\\/\\/.*bercara\\.com\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*semawur\\.com\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*in11\\.site\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*droidtamvan\\.me\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*haipedia\\.com\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*shrinkads\\.com\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*modebaca\\.com\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*liveshootv\\.com\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*shrink\\.world\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*mymastah\\.xyz\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*sportif\\.id\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*healthinsider\\.online\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*cararoot\\.id\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*gubukbisnis\\.me\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*sekilastekno\\.com\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*megaurl\\.in\\/.*", RegexOption.IGNORE_CASE)
        ),
        "redirect_persist_id_path_1_letter" to listOf(
            Regex(
                "https?:\\/\\/.*wadooo\\.com\\/g\\/.*",
                RegexOption.IGNORE_CASE
            ),
            Regex("https?:\\/\\/.*gotravelgo\\.space\\/g\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*pantauterus\\.me\\/g\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*liputannubi\\.net\\/g\\/.*", RegexOption.IGNORE_CASE)
        ),
        "contribute_hash" to listOf(
            Regex("https?:\\/\\/.*wadooo\\.com\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*gotravelgo\\.space\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*pantauterus\\.me\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*liputannubi\\.net\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*squidssh\\.com\\/user\\/links", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*goodssh\\.com\\/user\\/links", RegexOption.IGNORE_CASE)
        ),
        "tracker" to listOf(
            Regex("https?:\\/\\/.*ow\\.ly\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*b\\.link\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*dis\\.gd\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*t2m\\.io\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*goo\\.gl\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*bit\\.ly\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*tiny\\.ie\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*cutt\\.ly\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*buff\\.ly\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*page\\.link\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*shortcm\\.li\\/.*", RegexOption.IGNORE_CASE),
            Regex(".*:\\/\\/click\\.linksynergy\\.com\\/link\\?.*", RegexOption.IGNORE_CASE),
            Regex(".*:\\/\\/click\\.linksynergy\\.com\\/deeplink\\?.*", RegexOption.IGNORE_CASE),
            Regex(".*:\\/\\/shareasale\\.com\\/r\\.cfm\\?.*", RegexOption.IGNORE_CASE),
            Regex(".*:\\/\\/shareasale-analytics\\.com\\/r\\.cfm\\?.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*disq\\.us\\/url\\?url=.*", RegexOption.IGNORE_CASE),
            Regex(".*:\\/\\/rebrand\\.ly\\/.*", RegexOption.IGNORE_CASE),
            Regex(".*:\\/\\/rb\\.gy\\/.*", RegexOption.IGNORE_CASE),
            Regex(".*:\\/\\/buff\\.ly\\/.*", RegexOption.IGNORE_CASE),
            Regex(".*:\\/\\/out\\.reddit\\.com\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/tinyurl\\.com\\/(?!app).+", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/buff\\.ly\\/.+", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/trib\\.al\\/.+", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/is\\.gd\\/.+$(?<!\\.php)", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/(?:.+\\.)?x.co\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/(?:.+\\.)?1w.tf\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/(?:.+\\.)?1b.yt\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/(?:.+\\.)?is.gd\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/(?:.+\\.)?po.st\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/(?:.+\\.)?plu.sh\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/(?:.+\\.)?zii.bz\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/(?:.+\\.)?bit.do\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/(?:.+\\.)?hive.am\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/(?:.+\\.)?snip.li\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/(?:.+\\.)?go2l.ink\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/(?:.+\\.)?snipli.com\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/(?:.+\\.)?send.digital\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/(?:.+\\.)?great.social\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/(?:.+\\.)?t.co\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/analytics.twitter.com/mob_idsync_click?.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/track.flexlinkspro.com/a.ashx?.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/r20.rs6.net/tn.jsp?.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/ay.link/i\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/analytics.supplyframe.com\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/clg.am/*", RegexOption.IGNORE_CASE)
        ),
        "tracker_force_http" to listOf(
            Regex("https?:\\/\\/ow.ly\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/nzn.me\\/.*", RegexOption.IGNORE_CASE)
        ),
        "ip_logger" to listOf(
            Regex("https?:\\/\\/.*viral\\.over-blog\\.com\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*gyazo\\.in\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*ps3cfw\\.com\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*urlz\\.fr\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*webpanel\\.space\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*steamcommumity\\.com\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*imgur\\.com\\.de\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*fuglekos\\.com\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*discord\\.kim\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*prntcrs\\.com\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*grabify\\.link\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*leancoding\\.co\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*stopify\\.co\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*freegiftcards\\.co\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*joinmy\\.site\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*curiouscat\\.club\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*catsnthings\\.fun\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*catsnthings\\.com\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*xn--yutube-iqc\\.com\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*gyazo\\.nl\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*yip\\.su\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*iplogger\\.com\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*iplogger\\.co\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*iplogger\\.org\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*iplogger\\.ru\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*iplogger\\.info\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*ipgraber\\.ru\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*ipgrabber\\.ru\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*2no\\.co\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*02ip\\.ru\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*iplis\\.ru\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*iplo\\.ru\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*ezstat\\.ru\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*whatstheirip\\.com\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*partpicker\\.shop\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*sportshub\\.bar\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*locations\\.quest\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*lovebird\\.guru\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*trulove\\.guru\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*dateing\\.club\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*shrekis\\.life\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*headshot\\.monster\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*gaming-at-my\\.best\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*progaming\\.monster\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*yourmy\\.monster\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*imageshare\\.best\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*screenshot\\.best\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*gamingfun\\.me\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*catsnthing\\.com\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*fortnitechat\\.site\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*fortnight\\.space\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*hondachat\\.com\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*bvog\\.com\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*youramonkey\\.com\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*pronosparadise\\.com\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*freebooter\\.pro\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*blasze\\.com\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*blasze\\.tk\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*ipgrab\\.org\\/.*", RegexOption.IGNORE_CASE),
            Regex("https?:\\/\\/.*gyazos\\.com\\/.*", RegexOption.IGNORE_CASE)
        )
    )
}
