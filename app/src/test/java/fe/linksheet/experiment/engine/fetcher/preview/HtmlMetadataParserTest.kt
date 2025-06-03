package fe.linksheet.experiment.engine.fetcher.preview

import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isEqualToIgnoringGivenProperties
import assertk.assertions.isInstanceOf
import assertk.tableOf
import fe.linksheet.testlib.core.BaseUnitTest
import kotlinx.coroutines.test.runTest
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import fe.linksheet.testlib.core.JunitTest
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class HtmlMetadataParserTest : BaseUnitTest {

    private val parser = HtmlMetadataParser()

    @JunitTest
    fun `test size parsing`() {
        tableOf("size", "expected")
            .row<String, Int?>("", null)
            .row("1", null)
            .row("1x", null)
            .row("x1", null)
            .row("axb", null)
            .row("1x0", 1)
            .forAll { size, expected ->
                assertThat(parser.parseSize(size)).isEqualTo(expected)
            }
    }

    @JunitTest
    fun `test largest icon finder`() {
        fun Element.createElement(href: String, sizes: String? = null): Element {
            return appendElement("link")
                .attr("rel", "icon")
                .attr("href", href).apply {
                    if (sizes != null) attr("sizes", sizes)
                }
        }

        tableOf("linkElements", "expected")
            .row(
                Element("head").apply { createElement("https://single.com") },
                "https://single.com"
            )
            .row(
                Element("head").apply {
                    createElement("https://smaller.com", "100x100")
                    createElement("https://larger.com", "200x100")
                },
                "https://larger.com"
            )
            .forAll { elements, expected ->
                assertThat(parser.findLargestIconOrNull(elements)).isEqualTo(expected)
            }
    }

    @JunitTest
    fun `test parse unfurling`() = runTest {
        val html = """<!doctype html>
        <html lang="en" prefix="og: http://ogp.me/ns#" xmlns:og="http://opengraphprotocol.org/schema/">
         <head>
          <meta http-equiv="content-type" content="text/html;charset=UTF-8">
          <meta name="viewport" content="width=640">
          <meta name="theme-color" content="#FDDB29">
          <link href="/i/favicon.png" rel="icon" sizes="32x32">
          <link href="/fonts/fonts.css?t=1737982063" rel="stylesheet" type="text/css">
          <link href="/style.css?t=1737982063" rel="stylesheet" type="text/css">
          <title>Where Should Visual Programming Go? @ tonsky.me</title>
          <link href="/atom.xml" rel="alternate" title="Nikita Prokopov’s blog" type="application/atom+xml">
          <meta name="author" content="Nikita Prokopov">
          <meta property="og:title" content="Where Should Visual Programming Go?">
          <meta property="og:url" content="https://tonsky.me/blog/diagrams">
          <meta name="twitter:title" content="Where Should Visual Programming Go?">
          <meta property="twitter:domain" content="tonsky.me">
          <meta property="twitter:url" content="https://tonsky.me/blog/diagrams">
          <meta property="article:published_time" content="2024-07-18">
          <meta property="og:type" content="article">
          <meta property="og:image" content="https://dynogee.com/gen?id=24m2qx9uethuw6p&amp;title=Where+Should+Visual+Programming+Go%3F">
          <meta name="twitter:card" content="summary_large_image">
          <meta name="twitter:image" content="https://dynogee.com/gen?id=nm509093bpj50lv&amp;title=Where+Should+Visual+Programming+Go%3F">
          <meta property="og:description" content="Visual programming and textual code should co-exist next to each other, not replace one another">
          <meta name="twitter:description" content="Visual programming and textual code should co-exist next to each other, not replace one another">
          <meta property="og:site_name" content="tonsky.me">
          <meta property="article:author" content="https://www.facebook.com/nikitonsky">
          <meta property="profile:first_name" content="Nikita">
          <meta property="profile:last_name" content="Prokopov">
          <meta property="profile:username" content="tonsky">
          <meta property="profile:gender" content="male">
          <meta name="twitter:creator" content="@nikitonsky">
          <script src="/script.js?t=1737982063" defer async></script>
         </head>
         <body>
          <div class="page">
           <ul class="menu">
            <li class="inside"><a href="/">Blog</a></li>
            <li><a href="/talks/">Talks</a></li>
            <li><a href="/projects/">Projects</a></li>
            <li><a href="/design/">Logos</a></li>
            <div class="spacer"></div>
            <div class="winter"></div>
            <div class="dark_mode">
             <div id="darkModeGlow"></div>
            </div>
            <div class="hamburger">
             <input type="checkbox" id="checkbox"> <label for="checkbox"></label>
             <ul>
              <li><a href="/sign-in/">Sign In</a></li>
              <li><a href="/personal-information/">Personal Information</a></li>
              <li><a href="/user-agreement/">User Agreement</a></li>
              <li><a href="/patrons/">Patrons</a></li>
             </ul>
            </div>
           </ul>
           <article class="content">
            <h1 class="title">Where Should Visual Programming Go?</h1>
            <p>There’s a wonderful article by Sebastian Bensusan: “<a href="https://blog.sbensu.com/posts/demand-for-visual-programming/" target="_blank">We need visual programming. No, not like that.</a>” (the dot is part of the title ¯\_(ツ)_/¯).</p>
            <p>In it, Sebastian argues that we shouldn’t try to replace all code with visual programming but instead only add graphics where it makes sense:</p>
            <blockquote>
             <p>Most visual programming environments fail to get any usage. Why? They try to replace code syntax and business logic but developers never try to visualize that. Instead, developers visualize state transitions, memory layouts, or network requests.</p>
             <p>In my opinion, those working on visual programming would be more likely to succeed if they started with aspects of software that developers already visualize.</p>
            </blockquote>
            <p>I love diagrams myself! Whenever I encounter a complicated task and try to solve it in code, it always gets messy. But after drawing a diagram, my understanding improves, and the code gets cleaner. Win-win!</p>
            <p>Here’s one I made for button states in Humble UI:</p>
            <figure>
             <img src="button.webp?t=1737982063" style="aspect-ratio: 720/545; " width="720" height="545">
            </figure>
            <p>I bet you thought buttons are easy? Me too, at first. But after certain threshold your head just can’t fit all the states and transitions.</p>
            <p>Or for an image upload component:</p>
            <figure>
             <img src="image_upload.webp?t=1737982063" style="aspect-ratio: 1000/430; " width="1000" height="430">
            </figure>
            <p>Again: it would’ve been easy if not for error handling. But with a principled approach, you can get through any of that.</p>
            <p>Sebastian gives many more examples of useful visualizations in his article, too.</p>
            <p>But now, how does all this relate to code? I think there’re four levels.</p>
            <h1 id="level-0-diagrams-live-separately">Level 0: Diagrams live separately</h1>
            <p>You draw them in a separate tool, then use that to help you write code. Maybe put them on a wiki for other people to see. The point is: the diagram lives completely separate from the code.</p>
            <p>Downsides: hard to discover, can get out of date.</p>
            <p>This is what I did in the two examples above, and I guess what most of us can do given modern tools. But hey—it’s still not that bad!</p>
            <h1 id="level-1-diagrams-live-next-to-code">Level 1: Diagrams live next to code</h1>
            <p>One simple trick would solve the problem of discovery: what if we could put images into our text files?</p>
            <p>Currently, the best you can do is this:</p>
            <pre><code>                   +-----+  --&gt;
                           | N_4 |------     &lt;--- +-----+
                           +-----+     |    |-----| R_3 |
                              |    15  |    | 5   +-----+
                              |50      |    |        |
            +-----+  ---&gt;     |        +-----+       | 70
            | N_2 |------     |        | N_3 |       |
            +-----+     |     |        +-----+       |
             |       15 |     |            | 30      |
             | 10       |   +-----+  &lt;---  |         |
          @  |          ----|  S  |--------|         |
          @  |       &lt;@@@   +-----+                  |
          V  |                 |   |                 |
             |              10 |   |                 |
          +-----+              |   V                 |
          | R_2 |          +-----+                   |
          +-----+          |  E  |                   |
        |  |               +-----+                   |
        |  | 40             |  |                     |
        V  |             10 |  |                     |
           |    +-----+     |  V                     |
           -----| R_1 |-----|                        |
                +-----+                              |
                   |     ---&gt;         +-----+        |
                   |------------------|  D  |---------
                           10         +-----+</code></pre>
            <p>But it gets messy real quick. What if we could do this instead?</p>
            <figure>
             <img src="sublime.webp?t=1737982063" style="aspect-ratio: 720/965; " width="720" height="965">
            </figure>
            <p>Upsides: easy to implement (once everybody agrees on <em>how</em> to do that), universal (probably many other use cases).</p>
            <p>Downsides: still can get out of date. “Comments are not code”—the same applies here.</p>
            <p>Oh, and if you are coding in a terminal, this party is not for you. Sorry. We are thinking about the future here.</p>
            <h1 id="level-2-diagrams-are-generated-from-code">Level 2: Diagrams are generated from code</h1>
            <p>This is what Sebastian was hinting at. Code and diagrams co-exist, one is generated from the other.</p>
            <p>Generating diagrams from code is definitely something IDEs can do:</p>
            <figure>
             <img src="autogenerated.webp?t=1737982063" style="aspect-ratio: 1000/667; " width="1000" height="667">
            </figure>
            <p>Upsides:</p>
            <ul>
             <li>Always up to date.</li>
             <li>Non-invasive: can be integrated into IDE without affecting how code is stored.</li>
            </ul>
            <p>Downsides:</p>
            <ul>
             <li>It can help you understand, but can it help you think?</li>
             <li>Probably not very visually appealing, as these things tend to be. It’s hard to automatically lay out a good diagram.</li>
            </ul>
            <h1 id="level-3-diagrams-are-code">Level 3: Diagrams are code</h1>
            <p>This is what the endgame should be IMO. Some things are better represented as text. Some are best understood visually. We should mix and match what works best on a case-by-case basis. Don’t try to visualize simple code. Don’t try to write code where a diagram is better.</p>
            <p>One of the attempts was Luna. They tried dual representation: everything is code <em>and</em> diagram at the same time, and you can switch between the two:</p>
            <figure>
             <img src="luna.webp?t=1737982063" style="aspect-ratio: 1000/700; " width="1000" height="700">
             <figcaption>
              From <a href="https://web.archive.org/web/20160730111343/http://www.luna-lang.org/" target="_blank">luna-lang.org</a>
             </figcaption>
            </figure>
            <p>But this way, you are not only getting benefits of both ways, you are also constrained by both text <em>and</em> visual media at the same time. You can’t do stuff that’s hard to visualize (loops, recursions, abstractions) AND you can’t do stuff that’s hard to code.</p>
            <p>No, I think textual coding should stay textual where it works, BUT we should also be able to jump into a diagram tool, draw a state machine there and execute it the same way we execute text code.</p>
            <figure>
             <img src="new_file@2x.png?t=1737982063" style="aspect-ratio: 720/638; " width="720" height="638">
            </figure>
            <p>And when I mean draw, I mean draw. With direct manipulation, all that jazz. And <em>without</em> converting it back to text.</p>
            <p>So what I’m saying is: diagrams should not replace or “augment” text. They should be just another tool that lives <em>next</em> to the text. But a tool on its own.</p>
            <p>Think of it as a game engine like Godot or Unity. In them, you can write normal text code, but you can <em>also</em> create and edit scenes. These scenes are stored in their own files, have specialized editors that know how to edit them, and have no code representation. Because why? The visual way <em>in this particular case</em> is better.</p>
            <figure>
             <img src="godot.jpg?t=1737982063" style="aspect-ratio: 960/526; " width="960" height="526">
            </figure>
            <p>So the challenge here is not about integrating diagrams, but to think about which types of diagrams can be useful, can work better than code, and be directly executed.</p>
            <h1 id="non-goal-diagrams-replace-code">Non-goal: Diagrams replace code</h1>
            <p>Important note: we are not talking about doing code graphically. This is just a less convenient way of doing things that text already does.</p>
            <figure>
             <img src="blockly.webp?t=1737982063" style="aspect-ratio: 1000/238; " width="1000" height="238">
            </figure>
            <p>We are also not talking about no-code platforms: sometimes code is just better.</p>
            <p>But until this bright future arrives, put a diagram or two on the wiki. Your teammates will thank you for that.</p>
            <p class="footer"><span>July 18, 2024</span><span class="separator">·</span><span>Discuss on</span> <a href="https://news.ycombinator.com/item?id=41080644" target="_blank">HackerNews</a></p>
           </article>
           <div class="about">
            <div class="about_photo"></div>
            <div class="about_inner">
             <p>Hi!</p>
             <p>I’m Niki. Here I write about programming and UI design <a href="/subscribe/" class="btn-action">Subscribe</a></p>
             <p>I consult companies on all things Clojure: web, backend, Datomic, DataScript, performance, etc. Get in touch: <a href="mailto:niki@tonsky.me" class="btn-action">niki@tonsky.me</a></p>
             <p>I also create open-source stuff: Fira Code, DataScript, Clojure Sublimed, Humble&nbsp;UI. Support it on <a action="_blank" href="https://patreon.com/tonsky" class="btn-action" target="_blank">Patreon</a> or <a action="_blank" href="https://github.com/sponsors/tonsky" class="btn-action" target="_blank">Github</a></p>
            </div>
           </div>
          </div><img src="/i/flashlight.webp?t=1737982063" id="flashlight" style="aspect-ratio: 250/250; " width="250" height="250">
          <div class="pointers"></div>
         </body>
        </html>"""
        val document = Jsoup.parse(html, "https://tonsky.me/blog/diagrams")

        val result = HtmlMetadataParser().parse(document, document.html())

        assertThat(result).isInstanceOf<HtmlPreviewResult.Rich>().isEqualToIgnoringGivenProperties(
            HtmlPreviewResult.Rich(
                url = "https://tonsky.me/blog/diagrams",
                htmlText = "<< no-html >>",
                title = "Where Should Visual Programming Go?",
                description = "Visual programming and textual code should co-exist next to each other, not replace one another",
                favicon = "https://tonsky.me/i/favicon.png",
                thumbnail = "https://dynogee.com/gen?id=nm509093bpj50lv&title=Where+Should+Visual+Programming+Go%3F"
            ),
            HtmlPreviewResult.Rich::htmlText
        )
    }

    @JunitTest
    fun `test youtube rich preview`() = runTest {
        val html = """<!DOCTYPE html>
        <html style="font-size: 10px;font-family: Roboto, Arial, sans-serif;" lang="en" darker-dark-theme
              darker-dark-theme-deprecate system-icons typography typography-spacing>
        <head>
            <script data-id="_gd" nonce="6YK3r86b0Hdoc3rpYF-mRw">window.WIZ_global_data = {
                "MUE6Ne": "youtube_web",
                "MuJWjd": true,
                "UUFaWc": "%.@.null,1000,2]",
                "cfb2h": "youtube.web-front-end-critical_20250429.10_p0",
                "fPDxwd": [],
                "iCzhFc": false,
                "nQyAE": {},
                "oxN3nb": {
                    "1": false,
                    "0": false,
                    "610401301": false,
                    "899588437": false,
                    "725719775": false,
                    "513659523": false,
                    "568333945": false,
                    "1331761403": false,
                    "651175828": false,
                    "722764542": false,
                    "748402145": false,
                    "1981196515": false
                },
                "u4g7r": "%.@.null,1,2]",
                "vJQk6": false,
                "xnI9P": true,
                "xwAfE": true,
                "yFnxrf": 2486
            };</script>
            <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
            <meta http-equiv="origin-trial"
                  content="ApvK67ociHgr2egd6c2ZjrfPuRs8BHcvSggogIOPQNH7GJ3cVlyJ1NOq/COCdj0+zxskqHt9HgLLETc8qqD+vwsAAABteyJvcmlnaW4iOiJodHRwczovL3lvdXR1YmUuY29tOjQ0MyIsImZlYXR1cmUiOiJQcml2YWN5U2FuZGJveEFkc0FQSXMiLCJleHBpcnkiOjE2OTUxNjc5OTksImlzU3ViZG9tYWluIjp0cnVlfQ=="/>
            <script nonce="6YK3r86b0Hdoc3rpYF-mRw">var ytcfg = {
                d: function () {
                    return window.yt && yt.config_ || ytcfg.data_ || (ytcfg.data_ = {})
                }, get: function (k, o) {
                    return k in ytcfg.d() ? ytcfg.d()[k] : o
                }, set: function () {
                    var a = arguments;
                    if (a.length > 1) ytcfg.d()[a[0]] = a[1]; else {
                        var k;
                        for (k in a[0]) ytcfg.d()[k] = a[0][k]
                    }
                }
            };
            window.ytcfg.set('EMERGENCY_BASE_URL', '\/error_204?t\x3djserror\x26level\x3dERROR\x26client.name\x3d1\x26client.version\x3d2.20250502.01.00');</script>
            <script nonce="6YK3r86b0Hdoc3rpYF-mRw">(function () {
                window.yterr = window.yterr || true;
                window.unhandledErrorMessages = {};
                window.unhandledErrorCount = 0;
                window.onerror = function (msg, url, line, columnNumber, error) {
                    var err;
                    if (error) err = error; else {
                        err = new Error;
                        err.stack = "";
                        err.message = msg;
                        err.fileName = url;
                        err.lineNumber = line;
                        if (!isNaN(columnNumber)) err["columnNumber"] = columnNumber
                    }
                    var message = String(err.message);
                    if (!err.message || message in window.unhandledErrorMessages || window.unhandledErrorCount >= 5) return;
                    window.unhandledErrorCount += 1;
                    window.unhandledErrorMessages[message] = true;
                    var img = new Image;
                    window.emergencyTimeoutImg = img;
                    img.onload = img.onerror = function () {
                        delete window.emergencyTimeoutImg
                    };
                    var combinedLineAndColumn = err.lineNumber;
                    if (!isNaN(err["columnNumber"])) combinedLineAndColumn = combinedLineAndColumn + (":" + err["columnNumber"]);
                    var stack = err.stack || "";
                    var values = {
                        "msg": message,
                        "type": err.name,
                        "client.params": "unhandled window error",
                        "file": err.fileName,
                        "line": combinedLineAndColumn,
                        "stack": stack.substr(0, 500)
                    };
                    var thirdPartyScript = !err.fileName || err.fileName === "<anonymous>" || stack.indexOf("extension://") >= 0;
                    var replaced = stack.replace(/https:\/\/www.youtube.com\//g, "");
                    if (replaced.match(/https?:\/\/[^/]+\//)) thirdPartyScript =
                        true; else if (stack.indexOf("trapProp") >= 0 && stack.indexOf("trapChain") >= 0) thirdPartyScript = true; else if (message.indexOf("redefine non-configurable") >= 0) thirdPartyScript = true;
                    var baseUrl = window["ytcfg"].get("EMERGENCY_BASE_URL", "https://www.youtube.com/error_204?t=jserror&level=ERROR");
                    var unsupported = message.indexOf("window.customElements is undefined") >= 0;
                    if (thirdPartyScript || unsupported) baseUrl = baseUrl.replace("level=ERROR", "level=WARNING");
                    var parts = [baseUrl];
                    var key;
                    for (key in values) {
                        var value =
                            values[key];
                        if (value) parts.push(key + "=" + encodeURIComponent(value))
                    }
                    img.src = parts.join("&")
                };
                (function () {
                    function _getExtendedNativePrototype(tag) {
                        var p = this._nativePrototypes[tag];
                        if (!p) {
                            p = Object.create(this.getNativePrototype(tag));
                            var p${'$'} = Object.getOwnPropertyNames(window["Polymer"].Base);
                            var i = 0;
                            var n = void 0;
                            for (; i < p${'$'}.length && (n = p${'$'}[i]); i++) if (!window["Polymer"].BaseDescriptors[n]) try {
                                p[n] = window["Polymer"].Base[n]
                            } catch (e) {
                                throw new Error("Error while copying property: " + n + ". Tag is " + tag);
                            }
                            try {
                                Object.defineProperties(p, window["Polymer"].BaseDescriptors)
                            } catch (e) {
                                throw new Error("Polymer define property failed for " +
                                    Object.keys(p));
                            }
                            this._nativePrototypes[tag] = p
                        }
                        return p
                    }

                    function handlePolymerError(msg) {
                        window.onerror(msg, window.location.href, 0, 0, new Error(Array.prototype.join.call(arguments, ",")))
                    }

                    var origPolymer = window["Polymer"];
                    var newPolymer = function (config) {
                        if (!origPolymer._ytIntercepted && window["Polymer"].Base) {
                            origPolymer._ytIntercepted = true;
                            window["Polymer"].Base._getExtendedNativePrototype = _getExtendedNativePrototype;
                            window["Polymer"].Base._error = handlePolymerError;
                            window["Polymer"].Base._warn = handlePolymerError
                        }
                        return origPolymer.apply(this,
                            arguments)
                    };
                    var origDescriptor = Object.getOwnPropertyDescriptor(window, "Polymer");
                    Object.defineProperty(window, "Polymer", {
                        set: function (p) {
                            if (origDescriptor && origDescriptor.set && origDescriptor.get) {
                                origDescriptor.set(p);
                                origPolymer = origDescriptor.get()
                            } else origPolymer = p;
                            if (typeof origPolymer === "function") Object.defineProperty(window, "Polymer", {
                                value: origPolymer,
                                configurable: true,
                                enumerable: true,
                                writable: true
                            })
                        }, get: function () {
                            return typeof origPolymer === "function" ? newPolymer : origPolymer
                        }, configurable: true,
                        enumerable: true
                    })
                })();
            }).call(this);
            </script>
            <script nonce="6YK3r86b0Hdoc3rpYF-mRw">window.Polymer = window.Polymer || {};
            window.Polymer.legacyOptimizations = true;
            window.Polymer.setPassiveTouchGestures = true;
            window.ShadyDOM = { force: true, preferPerformance: true, noPatch: true };
            window.polymerSkipLoadingFontRoboto = true;
            window.ShadyCSS = { disableRuntime: true };</script>
            <link rel="shortcut icon" href="https://www.youtube.com/s/desktop/3747f4fc/img/logos/favicon.ico"
                  type="image/x-icon">
            <link rel="icon" href="https://www.youtube.com/s/desktop/3747f4fc/img/logos/favicon_32x32.png" sizes="32x32">
            <link rel="icon" href="https://www.youtube.com/s/desktop/3747f4fc/img/logos/favicon_48x48.png" sizes="48x48">
            <link rel="icon" href="https://www.youtube.com/s/desktop/3747f4fc/img/logos/favicon_96x96.png" sizes="96x96">
            <link rel="icon" href="https://www.youtube.com/s/desktop/3747f4fc/img/logos/favicon_144x144.png" sizes="144x144">
            <script nonce="6YK3r86b0Hdoc3rpYF-mRw">if ('undefined' == typeof Symbol || 'undefined' == typeof Symbol.iterator) {
                delete Array.prototype.entries;
            }</script>
            <script nonce="6YK3r86b0Hdoc3rpYF-mRw">var ytcsi = {
                gt: function (n) {
                    n = (n || "") + "data_";
                    return ytcsi[n] || (ytcsi[n] = { tick: {}, info: {}, gel: { preLoggedGelInfos: [] } })
                },
                now: window.performance && window.performance.timing && window.performance.now && window.performance.timing.navigationStart ? function () {
                    return window.performance.timing.navigationStart + window.performance.now()
                } : function () {
                    return (new Date).getTime()
                },
                tick: function (l, t, n) {
                    var ticks = ytcsi.gt(n).tick;
                    var v = t || ytcsi.now();
                    if (ticks[l]) {
                        ticks["_" + l] = ticks["_" + l] || [ticks[l]];
                        ticks["_" + l].push(v)
                    }
                    ticks[l] =
                        v
                },
                info: function (k, v, n) {
                    ytcsi.gt(n).info[k] = v
                },
                infoGel: function (p, n) {
                    ytcsi.gt(n).gel.preLoggedGelInfos.push(p)
                },
                setStart: function (t, n) {
                    ytcsi.tick("_start", t, n)
                }
            };
            (function (w, d) {
                function isGecko() {
                    if (!w.navigator) return false;
                    try {
                        if (w.navigator.userAgentData && w.navigator.userAgentData.brands && w.navigator.userAgentData.brands.length) {
                            var brands = w.navigator.userAgentData.brands;
                            var i = 0;
                            for (; i < brands.length; i++) if (brands[i] && brands[i].brand === "Firefox") return true;
                            return false
                        }
                    } catch (e) {
                        setTimeout(function () {
                            throw e;
                        })
                    }
                    if (!w.navigator.userAgent) return false;
                    var ua = w.navigator.userAgent;
                    return ua.indexOf("Gecko") > 0 && ua.toLowerCase().indexOf("webkit") < 0 && ua.indexOf("Edge") <
                        0 && ua.indexOf("Trident") < 0 && ua.indexOf("MSIE") < 0
                }

                ytcsi.setStart(w.performance ? w.performance.timing.responseStart : null);
                var isPrerender = (d.visibilityState || d.webkitVisibilityState) == "prerender";
                var vName = !d.visibilityState && d.webkitVisibilityState ? "webkitvisibilitychange" : "visibilitychange";
                if (isPrerender) {
                    var startTick = function () {
                        ytcsi.setStart();
                        d.removeEventListener(vName, startTick)
                    };
                    d.addEventListener(vName, startTick, false)
                }
                if (d.addEventListener) d.addEventListener(vName, function () {
                        ytcsi.tick("vc")
                    },
                    false);
                if (isGecko()) {
                    var isHidden = (d.visibilityState || d.webkitVisibilityState) == "hidden";
                    if (isHidden) ytcsi.tick("vc")
                }
                var slt = function (el, t) {
                    setTimeout(function () {
                        var n = ytcsi.now();
                        el.loadTime = n;
                        if (el.slt) el.slt()
                    }, t)
                };
                w.__ytRIL = function (el) {
                    if (!el.getAttribute("data-thumb")) if (w.requestAnimationFrame) w.requestAnimationFrame(function () {
                        slt(el, 0)
                    }); else slt(el, 16)
                }
            })(window, document);
            </script>
            <script nonce="6YK3r86b0Hdoc3rpYF-mRw">(function () {
                var img = new Image().src = "https://i.ytimg.com/generate_204";
            })();</script>
            <script src="https://www.youtube.com/s/desktop/3747f4fc/jsbin/web-animations-next-lite.min.vflset/web-animations-next-lite.min.js"
                    nonce="6YK3r86b0Hdoc3rpYF-mRw"></script>
            <script src="https://www.youtube.com/s/desktop/3747f4fc/jsbin/webcomponents-all-noPatch.vflset/webcomponents-all-noPatch.js"
                    nonce="6YK3r86b0Hdoc3rpYF-mRw"></script>
            <script src="https://www.youtube.com/s/desktop/3747f4fc/jsbin/fetch-polyfill.vflset/fetch-polyfill.js"
                    nonce="6YK3r86b0Hdoc3rpYF-mRw"></script>
            <script src="https://www.youtube.com/s/desktop/3747f4fc/jsbin/intersection-observer.min.vflset/intersection-observer.min.js"
                    nonce="6YK3r86b0Hdoc3rpYF-mRw"></script>
            <script nonce="6YK3r86b0Hdoc3rpYF-mRw">if (window.ytcsi) {
                window.ytcsi.tick('lpcs', null, '');
            }</script>
            <script nonce="6YK3r86b0Hdoc3rpYF-mRw">if (window.ytcsi) {
                window.ytcsi.tick('csl', null, '');
            }</script>
            <link rel="stylesheet"
                  href="//fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&family=YouTube+Sans:wght@300..900&display=swap"
                  nonce="41OleJe7luwoDsDsEZe3LA">
            <script name="www-roboto" nonce="6YK3r86b0Hdoc3rpYF-mRw">if (document.fonts && document.fonts.load) {
                document.fonts.load("400 10pt Roboto", "");
                document.fonts.load("500 10pt Roboto", "");
            }</script>
            <link rel="stylesheet" href="/s/player/14cfd4c0/www-player.css" nonce="41OleJe7luwoDsDsEZe3LA">
            <link rel="stylesheet"
                  href="https://www.youtube.com/s/desktop/3747f4fc/cssbin/www-main-desktop-watch-page-skeleton.css"
                  nonce="41OleJe7luwoDsDsEZe3LA">
            <link rel="stylesheet" href="https://www.youtube.com/s/desktop/3747f4fc/cssbin/www-main-desktop-player-skeleton.css"
                  nonce="41OleJe7luwoDsDsEZe3LA">
            <link rel="stylesheet" href="https://www.youtube.com/s/desktop/3747f4fc/cssbin/www-onepick.css"
                  nonce="41OleJe7luwoDsDsEZe3LA">
            <link rel="stylesheet"
                  href="https://www.youtube.com/s/_/ytmainappweb/_/ss/k=ytmainappweb.kevlar_base.GOBSl-B0ku8.L.X.O/am=AAAgUA/d=0/rs=AGKMywFk92kOMd0xwlDQSEsjC4djFuzRyg"
                  nonce="41OleJe7luwoDsDsEZe3LA">
            <style class="global_styles" nonce="41OleJe7luwoDsDsEZe3LA">body {
                padding: 0;
                margin: 0;
                overflow-y: scroll
            }

            body.autoscroll {
                overflow-y: auto
            }

            body.no-scroll {
                overflow: hidden
            }

            body.no-y-scroll {
                overflow-y: hidden
            }

            .hidden {
                display: none
            }

            textarea {
                --paper-input-container-input_-_white-space: pre-wrap
            }

            .grecaptcha-badge {
                visibility: hidden
            }</style>
            <style class="masthead_shell" nonce="41OleJe7luwoDsDsEZe3LA">ytd-masthead.shell {
                background-color: #fff !important;
                position: fixed;
                top: 0;
                right: 0;
                left: 0;
                display: -ms-flex;
                display: -webkit-flex;
                display: -webkit-box;
                display: -moz-box;
                display: -ms-flexbox;
                display: flex;
                height: 56px;
                -ms-flex-align: center;
                -webkit-align-items: center;
                -webkit-box-align: center;
                -moz-box-align: center;
                align-items: center
            }

            ytd-masthead.shell #menu-icon {
                margin-left: 16px
            }

            ytd-app > ytd-masthead.chunked {
                position: fixed;
                top: 0;
                width: 100%
            }

            ytd-masthead.shell.dark, ytd-masthead.shell.theater {
                background-color: #0f0f0f !important
            }

            ytd-masthead.shell.full-window-mode {
                background-color: #0f0f0f !important;
                opacity: 0;
                -webkit-transform: translateY(calc(-100% - 5px));
                transform: translateY(calc(-100% - 5px))
            }

            ytd-masthead.shell > :first-child {
                padding-left: 16px
            }

            ytd-masthead.shell > :last-child {
                padding-right: 16px
            }

            ytd-masthead #masthead-logo {
                display: -ms-flex;
                display: -webkit-flex;
                display: -webkit-box;
                display: -moz-box;
                display: -ms-flexbox;
                display: flex
            }

            ytd-masthead #masthead-logo #country-code {
                margin-right: 2px
            }

            ytd-masthead.shell #yt-logo-red-svg, ytd-masthead.shell #yt-logo-red-updated-svg, ytd-masthead.shell #yt-logo-svg, ytd-masthead.shell #yt-logo-updated-svg {
                -webkit-align-self: center;
                -ms-flex-item-align: center;
                align-self: center;
                margin-left: 8px;
                padding: 0;
                color: #000
            }

            ytd-masthead.shell #a11y-skip-nav {
                display: none
            }

            ytd-masthead.shell svg {
                width: 40px;
                height: 40px;
                padding: 8px;
                margin-right: 8px;
                -moz-box-sizing: border-box;
                box-sizing: border-box;
                color: #606060;
                fill: currentColor
            }

            ytd-masthead .external-icon {
                width: 24px;
                height: 24px
            }

            ytd-masthead .yt-icons-ext {
                fill: currentColor;
                color: #606060
            }

            ytd-masthead.shell.dark .yt-icons-ext ytd-masthead.shell.theater .yt-icons-ext {
                fill: #fff
            }

            ytd-masthead svg#yt-logo-svg {
                width: 80px
            }

            ytd-masthead svg#yt-logo-red-svg {
                width: 106.4px
            }

            ytd-masthead svg#yt-logo-updated-svg {
                width: 90px
            }

            ytd-masthead svg#yt-logo-red-updated-svg {
                width: 97px
            }

            @media (max-width: 656px) {
                ytd-masthead.shell > :first-child {
                    padding-left: 8px
                }

                ytd-masthead.shell > :last-child {
                    padding-right: 8px
                }

                ytd-masthead.shell svg {
                    margin-right: 0
                }

                ytd-masthead #masthead-logo {
                    -ms-flex: 1 1 0.000000001px;
                    -webkit-flex: 1;
                    -webkit-box-flex: 1;
                    -moz-box-flex: 1;
                    flex: 1;
                    -webkit-flex-basis: 0.000000001px;
                    -ms-flex-preferred-size: 0.000000001px;
                    flex-basis: 0.000000001px
                }

                ytd-masthead.shell #yt-logo-red-svg, ytd-masthead.shell #yt-logo-svg {
                    margin-left: 4px
                }
            }

            @media (min-width: 876px) {
                ytd-masthead #masthead-logo {
                    width: 129px
                }
            }

            #masthead-skeleton-icons {
                display: -webkit-box;
                display: -webkit-flex;
                display: -moz-box;
                display: -ms-flexbox;
                display: flex;
                -webkit-box-flex: 1;
                -webkit-flex: 1;
                -moz-box-flex: 1;
                -ms-flex: 1;
                flex: 1;
                -webkit-box-orient: horizontal;
                -webkit-box-direction: normal;
                -webkit-flex-direction: row;
                -moz-box-orient: horizontal;
                -moz-box-direction: normal;
                -ms-flex-direction: row;
                flex-direction: row;
                -webkit-box-pack: end;
                -webkit-justify-content: flex-end;
                -moz-box-pack: end;
                -ms-flex-pack: end;
                justify-content: flex-end
            }

            ytd-masthead.masthead-finish #masthead-skeleton-icons {
                display: none
            }

            .masthead-skeleton-icon {
                border-radius: 50%;
                height: 32px;
                width: 32px;
                margin: 0 8px;
                background-color: #e3e3e3
            }

            ytd-masthead.dark .masthead-skeleton-icon {
                background-color: #292929
            }</style>
            <style class="masthead_custom_styles" is="custom-style" id="ext-styles"
                   nonce="41OleJe7luwoDsDsEZe3LA">:-stv-set-elsewhere {
                --yt-spec-icon-active-other: initial
            }

            ytd-masthead .yt-icons-ext {
                color: var(--yt-spec-icon-active-other)
            }

            ytd-masthead svg#yt-logo-red-svg #youtube-red-paths path, ytd-masthead svg#yt-logo-red-updated-svg #youtube-red-paths path, ytd-masthead svg#yt-logo-svg #youtube-paths path, ytd-masthead svg#yt-logo-updated-svg #youtube-paths path {
                fill: #282828
            }

            ytd-masthead.dark svg#yt-logo-red-svg #youtube-red-paths path, ytd-masthead.dark svg#yt-logo-red-updated-svg #youtube-red-paths path, ytd-masthead.dark svg#yt-logo-svg #youtube-paths path, ytd-masthead.dark svg#yt-logo-updated-svg #youtube-paths path, ytd-masthead.theater svg#yt-logo-red-svg #youtube-red-paths path, ytd-masthead.theater svg#yt-logo-svg #youtube-paths path {
                fill: #fff
            }</style>
            <style class="searchbox" nonce="41OleJe7luwoDsDsEZe3LA">#search-input.ytd-searchbox-spt input {
                -webkit-appearance: none;
                -webkit-font-smoothing: antialiased;
                background-color: transparent;
                border: none;
                box-shadow: none;
                color: inherit;
                font-family: Roboto, Noto, sans-serif;
                font-size: 16px;
                font-weight: 400;
                line-height: 24px;
                margin-left: 4px;
                max-width: 100%;
                outline: none;
                text-align: inherit;
                width: 100%;
                -ms-flex: 1 1 0.000000001px;
                -webkit-flex: 1;
                -webkit-box-flex: 1;
                -moz-box-flex: 1;
                flex: 1;
                -webkit-flex-basis: 0.000000001px;
                -ms-flex-preferred-size: 0.000000001px;
                flex-basis: 0.000000001px
            }

            #search-container.ytd-searchbox-spt {
                pointer-events: none;
                position: absolute;
                top: 0;
                right: 0;
                bottom: 0;
                left: 0
            }

            #search-input.ytd-searchbox-spt #search::-webkit-input-placeholder {
                color: #888
            }

            #search-input.ytd-searchbox-spt #search::-moz-input-placeholder {
                color: #888
            }

            #search-input.ytd-searchbox-spt #search:-ms-input-placeholder {
                color: #888
            }</style>
            <style class="kevlar_global_styles" nonce="41OleJe7luwoDsDsEZe3LA">html {
                background-color: #fff !important;
                -webkit-text-size-adjust: none
            }

            html[dark] {
                background-color: #0f0f0f !important
            }

            #logo-red-icon-container.ytd-topbar-logo-renderer {
                width: 86px
            }</style>
            <meta name="theme-color" content="rgba(255, 255, 255, 0.98)">
            <link rel="search" type="application/opensearchdescription+xml"
                  href="https://www.youtube.com/opensearch?locale=en_US" title="YouTube">
            <link rel="manifest" href="/manifest.webmanifest" crossorigin="use-credentials">
            <script nonce="6YK3r86b0Hdoc3rpYF-mRw">if (window.ytcsi) {
                window.ytcsi.tick('bc', null, '');
            }
            var ytimg = {
                count: 0, preload: function (src) {
                    var img = new Image;
                    var count = ++ytimg.count;
                    ytimg[count] = img;
                    img.onload = img.onerror = function () {
                        delete ytimg[count]
                    };
                    img.src = src
                }
            };
            ytimg.preload('https:\/\/rr2---sn-1gi7znes.googlevideo.com\/generate_204');
            ytimg.preload('https:\/\/rr2---sn-1gi7znes.googlevideo.com\/generate_204?conn2');</script>
            <link rel="canonical" href="https://www.youtube.com/watch?v=x1J-gd0Z-RU">
            <link rel="alternate" media="handheld" href="https://m.youtube.com/watch?v=x1J-gd0Z-RU">
            <link rel="alternate" media="only screen and (max-width: 640px)" href="https://m.youtube.com/watch?v=x1J-gd0Z-RU">
            <title>Best Android Apps - April 2023! - YouTube</title>
            <meta name="title" content="Best Android Apps - April 2023!">
            <meta name="description"
                  content="Get UPDF with 54% OFF and 2 FREE gifts (One License for Windows, Mac, iOS &amp; Android):http://bit.ly/3lgZNft_______________________________________­­▣ HowToPer...">
            <meta name="keywords"
                  content="howtomen, android, Top Android Apps April 2023, Best Apps April 2023, Best Android Apps April 2023, free apps April 2023, Top Apps April 2023, android April 2023, April 2023 android apps, best android apps, android apps, android apps 2023, apps 2023, best android apps 2023, best apps, top apps, top apps 2023, best apps 2023, best apps for android, best apps for android 2023, best apps of the month 2023, best top android apps april 2023, top android games 2023">
            <link rel="shortlinkUrl" href="https://youtu.be/x1J-gd0Z-RU">
            <link rel="alternate" href="android-app://com.google.android.youtube/http/www.youtube.com/watch?v=x1J-gd0Z-RU">
            <link rel="alternate" href="ios-app://544007664/vnd.youtube/www.youtube.com/watch?v=x1J-gd0Z-RU">
            <link rel="alternate" type="application/json+oembed"
                  href="https://www.youtube.com/oembed?format=json&amp;url=https%3A%2F%2Fwww.youtube.com%2Fwatch%3Fv%3Dx1J-gd0Z-RU"
                  title="Best Android Apps - April 2023!">
            <link rel="alternate" type="text/xml+oembed"
                  href="https://www.youtube.com/oembed?format=xml&amp;url=https%3A%2F%2Fwww.youtube.com%2Fwatch%3Fv%3Dx1J-gd0Z-RU"
                  title="Best Android Apps - April 2023!">
            <link rel="image_src" href="https://i.ytimg.com/vi/x1J-gd0Z-RU/maxresdefault.jpg">
            <meta property="og:site_name" content="YouTube">
            <meta property="og:url" content="https://www.youtube.com/watch?v=x1J-gd0Z-RU">
            <meta property="og:title" content="Best Android Apps - April 2023!">
            <meta property="og:image" content="https://i.ytimg.com/vi/x1J-gd0Z-RU/maxresdefault.jpg">
            <meta property="og:image:width" content="1280">
            <meta property="og:image:height" content="720">
            <meta property="og:description"
                  content="Get UPDF with 54% OFF and 2 FREE gifts (One License for Windows, Mac, iOS &amp; Android):http://bit.ly/3lgZNft_______________________________________­­▣ HowToPer...">
            <meta property="al:ios:app_store_id" content="544007664">
            <meta property="al:ios:app_name" content="YouTube">
            <meta property="al:ios:url" content="vnd.youtube://www.youtube.com/watch?v=x1J-gd0Z-RU&amp;feature=applinks">
            <meta property="al:android:url" content="vnd.youtube://www.youtube.com/watch?v=x1J-gd0Z-RU&amp;feature=applinks">
            <meta property="al:web:url" content="http://www.youtube.com/watch?v=x1J-gd0Z-RU&amp;feature=applinks">
            <meta property="og:type" content="video.other">
            <meta property="og:video:url" content="https://www.youtube.com/embed/x1J-gd0Z-RU">
            <meta property="og:video:secure_url" content="https://www.youtube.com/embed/x1J-gd0Z-RU">
            <meta property="og:video:type" content="text/html">
            <meta property="og:video:width" content="1280">
            <meta property="og:video:height" content="720">
            <meta property="al:android:app_name" content="YouTube">
            <meta property="al:android:package" content="com.google.android.youtube">
            <meta property="og:video:tag" content="howtomen">
            <meta property="og:video:tag" content="android">
            <meta property="og:video:tag" content="Top Android Apps April 2023">
            <meta property="og:video:tag" content="Best Apps April 2023">
            <meta property="og:video:tag" content="Best Android Apps April 2023">
            <meta property="og:video:tag" content="free apps April 2023">
            <meta property="og:video:tag" content="Top Apps April 2023">
            <meta property="og:video:tag" content="android April 2023">
            <meta property="og:video:tag" content="April 2023 android apps">
            <meta property="og:video:tag" content="best android apps">
            <meta property="og:video:tag" content="android apps">
            <meta property="og:video:tag" content="android apps 2023">
            <meta property="og:video:tag" content="apps 2023">
            <meta property="og:video:tag" content="best android apps 2023">
            <meta property="og:video:tag" content="best apps">
            <meta property="og:video:tag" content="top apps">
            <meta property="og:video:tag" content="top apps 2023">
            <meta property="og:video:tag" content="best apps 2023">
            <meta property="og:video:tag" content="best apps for android">
            <meta property="og:video:tag" content="best apps for android 2023">
            <meta property="og:video:tag" content="best apps of the month 2023">
            <meta property="og:video:tag" content="best top android apps april 2023">
            <meta property="og:video:tag" content="top android games 2023">
            <meta property="fb:app_id" content="87741124305">
            <meta name="twitter:card" content="player">
            <meta name="twitter:site" content="@youtube">
            <meta name="twitter:url" content="https://www.youtube.com/watch?v=x1J-gd0Z-RU">
            <meta name="twitter:title" content="Best Android Apps - April 2023!">
            <meta name="twitter:description"
                  content="Get UPDF with 54% OFF and 2 FREE gifts (One License for Windows, Mac, iOS &amp; Android):http://bit.ly/3lgZNft_______________________________________­­▣ HowToPer...">
            <meta name="twitter:image" content="https://i.ytimg.com/vi/x1J-gd0Z-RU/maxresdefault.jpg">
            <meta name="twitter:app:name:iphone" content="YouTube">
            <meta name="twitter:app:id:iphone" content="544007664">
            <meta name="twitter:app:name:ipad" content="YouTube">
            <meta name="twitter:app:id:ipad" content="544007664">
            <meta name="twitter:app:url:iphone"
                  content="vnd.youtube://www.youtube.com/watch?v=x1J-gd0Z-RU&amp;feature=applinks">
            <meta name="twitter:app:url:ipad" content="vnd.youtube://www.youtube.com/watch?v=x1J-gd0Z-RU&amp;feature=applinks">
            <meta name="twitter:app:name:googleplay" content="YouTube">
            <meta name="twitter:app:id:googleplay" content="com.google.android.youtube">
            <meta name="twitter:app:url:googleplay" content="https://www.youtube.com/watch?v=x1J-gd0Z-RU">
            <meta name="twitter:player" content="https://www.youtube.com/embed/x1J-gd0Z-RU">
            <meta name="twitter:player:width" content="1280">
            <meta name="twitter:player:height" content="720">
            <div id="watch7-content" class="watch-main-col" itemscope itemid="https://www.youtube.com/watch?v=x1J-gd0Z-RU"
                 itemtype="http://schema.org/VideoObject">
                <link itemprop="url" href="https://www.youtube.com/watch?v=x1J-gd0Z-RU">
                <meta itemprop="name" content="Best Android Apps - April 2023!">
                <meta itemprop="description"
                      content="Get UPDF with 54% OFF and 2 FREE gifts (One License for Windows, Mac, iOS &amp; Android):http://bit.ly/3lgZNft_______________________________________­­▣ HowToPer...">
                <meta itemprop="requiresSubscription" content="False">
                <meta itemprop="identifier" content="x1J-gd0Z-RU">
                <meta itemprop="duration" content="PT10M10S">
                <span itemprop="author" itemscope itemtype="http://schema.org/Person"><link itemprop="url"
                                                                                            href="http://www.youtube.com/@howtomen"><link
                        itemprop="name" content="HowToMen"></span><span itemscope
                                                                        itemtype="https://schema.org/BreadcrumbList"><span
                    itemprop="itemListElement" itemscope itemtype="https://schema.org/ListItem"><meta itemprop="position"
                                                                                                      content="1"/><span
                    itemprop="item" itemid="http://www.youtube.com/@howtomen" itemscope itemtype="https://schema.org/Thing"><meta
                    itemprop="name" content="HowToMen"/></span></span></span>
                <link itemprop="thumbnailUrl" href="https://i.ytimg.com/vi/x1J-gd0Z-RU/maxresdefault.jpg">
                <span itemprop="thumbnail" itemscope itemtype="http://schema.org/ImageObject"><link itemprop="url"
                                                                                                    href="https://i.ytimg.com/vi/x1J-gd0Z-RU/maxresdefault.jpg"><meta
                        itemprop="width" content="1280"><meta itemprop="height" content="720"></span>
                <link itemprop="embedUrl" href="https://www.youtube.com/embed/x1J-gd0Z-RU">
                <meta itemprop="playerType" content="HTML5 Flash">
                <meta itemprop="width" content="1280">
                <meta itemprop="height" content="720">
                <meta itemprop="isFamilyFriendly" content="true">
                <meta itemprop="regionsAllowed"
                      content="AD,AE,AF,AG,AI,AL,AM,AO,AQ,AR,AS,AT,AU,AW,AX,AZ,BA,BB,BD,BE,BF,BG,BH,BI,BJ,BL,BM,BN,BO,BQ,BR,BS,BT,BV,BW,BY,BZ,CA,CC,CD,CF,CG,CH,CI,CK,CL,CM,CN,CO,CR,CU,CV,CW,CX,CY,CZ,DE,DJ,DK,DM,DO,DZ,EC,EE,EG,EH,ER,ES,ET,FI,FJ,FK,FM,FO,FR,GA,GB,GD,GE,GF,GG,GH,GI,GL,GM,GN,GP,GQ,GR,GS,GT,GU,GW,GY,HK,HM,HN,HR,HT,HU,ID,IE,IL,IM,IN,IO,IQ,IR,IS,IT,JE,JM,JO,JP,KE,KG,KH,KI,KM,KN,KP,KR,KW,KY,KZ,LA,LB,LC,LI,LK,LR,LS,LT,LU,LV,LY,MA,MC,MD,ME,MF,MG,MH,MK,ML,MM,MN,MO,MP,MQ,MR,MS,MT,MU,MV,MW,MX,MY,MZ,NA,NC,NE,NF,NG,NI,NL,NO,NP,NR,NU,NZ,OM,PA,PE,PF,PG,PH,PK,PL,PM,PN,PR,PS,PT,PW,PY,QA,RE,RO,RS,RU,RW,SA,SB,SC,SD,SE,SG,SH,SI,SJ,SK,SL,SM,SN,SO,SR,SS,ST,SV,SX,SY,SZ,TC,TD,TF,TG,TH,TJ,TK,TL,TM,TN,TO,TR,TT,TV,TW,TZ,UA,UG,UM,US,UY,UZ,VA,VC,VE,VG,VI,VN,VU,WF,WS,YE,YT,ZA,ZM,ZW">
                <div itemprop="interactionStatistic" itemscope itemtype="https://schema.org/InteractionCounter">
                    <meta itemprop="interactionType" content="https://schema.org/LikeAction">
                    <meta itemprop="userInteractionCount" content="15812">
                </div>
                <div itemprop="interactionStatistic" itemscope itemtype="https://schema.org/InteractionCounter">
                    <meta itemprop="interactionType" content="https://schema.org/WatchAction">
                    <meta itemprop="userInteractionCount" content="303799">
                </div>
                <meta itemprop="datePublished" content="2023-04-01T00:04:01-07:00">
                <meta itemprop="uploadDate" content="2023-04-01T00:04:01-07:00">
                <meta itemprop="genre" content="Howto &amp; Style">
            </div>
        </head>
        <body dir="ltr" no-y-overflow>
        </body>
        </html>"""
        val result = HtmlMetadataParser().parse(html, "https://www.youtube.com/watch?v=x1J-gd0Z-RU")

        assertThat(result).isInstanceOf<HtmlPreviewResult.Rich>().isEqualToIgnoringGivenProperties(
            HtmlPreviewResult.Rich(
                url = "https://www.youtube.com/watch?v=x1J-gd0Z-RU", htmlText = "<< no-html >>",
                title = "Best Android Apps - April 2023!",
                description = "Get UPDF with 54% OFF and 2 FREE gifts (One License for Windows, Mac, iOS & Android):http://bit.ly/3lgZNft_______________________________________\u00AD\u00AD▣ HowToPer...",
                favicon = "https://www.youtube.com/s/desktop/3747f4fc/img/logos/favicon.ico",
                thumbnail = "https://i.ytimg.com/vi/x1J-gd0Z-RU/maxresdefault.jpg"
            ),
            HtmlPreviewResult.Rich::htmlText
        )
    }
}
