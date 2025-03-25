package fe.linksheet.experiment.engine.fetcher.preview

import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.prop
import assertk.tableOf
import fe.linksheet.UnitTest
import fe.linksheet.experiment.engine.fetcher.preview.HtmlPreviewResult.RichPreviewResult
import kotlinx.coroutines.test.runTest
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
internal class HtmlMetadataParserTest : UnitTest {

    private val parser = HtmlMetadataParser()

    @Test
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

    @Test
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

    @Test
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

        assertThat(result)
            .isInstanceOf<RichPreviewResult>()
            .all {
                prop(RichPreviewResult::url).isEqualTo("https://tonsky.me/blog/diagrams")
                prop(RichPreviewResult::title).isEqualTo("Where Should Visual Programming Go?")
                prop(RichPreviewResult::description).isEqualTo("Visual programming and textual code should co-exist next to each other, not replace one another")
                prop(RichPreviewResult::favicon).isEqualTo("https://tonsky.me/i/favicon.png")
                prop(RichPreviewResult::thumbnail).isEqualTo("https://dynogee.com/gen?id=nm509093bpj50lv&title=Where+Should+Visual+Programming+Go%3F")
            }
    }
}
