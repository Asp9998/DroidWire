// import {onRequest} from "firebase-functions/v2/https";
// import type {Request, Response} from "express";
// import axios from "axios";
// import * as cheerio from "cheerio";
// import type {CheerioAPI, Cheerio} from "cheerio";
// import type {Element as DomElement, AnyNode} from "domhandler";

// /**
//  * Ensure Android Developers Blog URLs use the Blogger mobile view (?m=1).
//  * @param {string} url Original URL.
//  * @return {string} URL forced to mobile version if possible.
//  */
// function toMobile(url: string): string {
//   try {
//     const u = new URL(url);
//     if (!u.searchParams.has("m")) {
//       u.searchParams.set("m", "1");
//     }
//     return u.toString();
//   } catch {
//     return url.endsWith("?m=1") ? url : `${url}?m=1`;
//   }
// }

// /**
//  * Remove non-content noise from the DOM.
//  * @param {CheerioAPI} $ Cheerio root API.
//  * @return {void}
//  */
// function stripNoise($: CheerioAPI): void {
//   const removeGlobal = [
//     "script",
//     "style",
//     "noscript",
//     "iframe",
//     "ins",
//     "svg",
//     "header",
//     "footer",
//     "nav",
//     "[role=\"navigation\"]",
//   ].join(",");

//   $("body").children(removeGlobal).remove();

//   const removeChrome = [
//     ".header-outer",
//     ".content-header",
//     ".breadcrumbs",
//     ".post-share-buttons",
//     // keep post-footer safe: remove only if outside .post-body
//     "body > .post-footer",
//     ".post-timestamp",
//     ".jump-link",
//     ".byline",
//     ".date-outer",
//     ".widget",
//     ".widget-content",
//     ".sidebar",
//     ".menu",
//     ".top-bar",
//   ].join(",");

//   $("body").children(removeChrome).remove();
// }

// /**
//  * Extract 3–4 human-readable paragraphs from the article body.
//  * @param {CheerioAPI} $ Cheerio root API.
//  * @return {string} Description text.
//  */
// function extractDescription($: CheerioAPI): string {
//   const genericRe = new RegExp(
//     [
//       "^News and insights on the Android platform",
//       "^The latest Android and Google Play news",
//       "^Android Developers( →)?$",
//     ].join("|"),
//     "i",
//   );

//   // Try to find the real article container (Blogger variants).
//   const root =
//   $(".post-body, .post-body.entry-content,[itemprop='articleBody'], article")
//       .first();

//   // Collect paragraphs and trim whitespace/spans/fonts.
//   const paras = root
//     .find("p")
//     .map((_: number, el: AnyNode) => $(el).text().trim())
//     .get()
//     .map((t: string) => t.replace(/\s+/g, " "))
//     .filter((t: string) => {
//       if (!t) return false;
//       if (genericRe.test(t)) return false;
//       if (/^(☰|🔍)\s*$/.test(t)) return false;
//       // skip super-short junk like "Read more" etc.
//       return t.length > 30;
//     });

//   // Return first few readable paragraphs
//   if (paras.length > 0) {
//     return paras.slice(0, 4).join("\n\n");
//   }

//   // fallback: maybe meta description
//   const meta =
//     $("meta[property='og:description']").attr("content") ||
//     $("meta[name='twitter:description']").attr("content") ||
//     $("meta[name='description']").attr("content");

//   return meta?.trim() || "";
// }

// /**
//  * Collect a small set of content images (skip icons/social/pixels).
//  * @param {CheerioAPI} $ Cheerio root API.
//  * @param {Cheerio<AnyNode>} scope Node scope to search within.
//  * @return {string[]} Up to six image URLs.
//  */
// function extractImages(
//   $: CheerioAPI,
//   scope: Cheerio<AnyNode>,
// ): string[] {
//   const set = new Set<string>();

//   const og = $("meta[property=\"og:image\"]").attr("content")?.trim();
//   if (og) {
//     set.add(og);
//   }

//   const root: Cheerio<AnyNode> = scope.length ? scope : $("body");

//   root.find("img").each((_: number, el: DomElement) => {
//     const raw = $(el).attr("src") || $(el).attr("data-src");
//     if (!raw) {
//       return;
//     }

//     const s = raw.trim();
//     const isHttp = s.startsWith("http");
//     const isSvg = /\.svg($|\?)/i.test(s);
//     const isSocial = /gstatic|facebook|linkedin|twitter|icons?/i.test(s);
//     const isPixel = /pixel/i.test(s);

//     if (isHttp && !isSvg && !isSocial && !isPixel) {
//       set.add(s);
//     }
//   });

//   return Array.from(set).slice(0, 6);
// }

// /**
//  * Derive a reasonable title.
//  * @param {CheerioAPI} $ Cheerio root API.
//  * @param {string} id Fallback id for untitled pages.
//  * @return {string} Title.
//  */
// function extractTitle($: CheerioAPI, id: string): string {
//   const og = $("meta[property=\"og:title\"]").attr("content")?.trim();
//   if (og && og.length > 0) {
//     return og;
//   }
//   const t = $("title").first().text().trim();
//   return t.length > 0 ? t : `Article ${id}`;
// }

// /**
//  * Extract a site/source name from OG or hostname.
//  * @param {CheerioAPI} $ Cheerio root API.
//  * @param {string} originalUrl Original URL string.
//  * @return {string} Source/site name.
//  */
// function extractSource($: CheerioAPI, originalUrl: string): string {
//   const site = $("meta[property=\"og:site_name\"]").attr("content")?.trim();
//   if (site && site.length > 0) {
//     return site;
//   }
//   try {
//     return new URL(originalUrl).hostname.replace(/^www\./, "");
//   } catch {
//     return "Android Developers Blog";
//   }
// }

// /**
//  * Prefer OG published_time, else now.
//  * @param {CheerioAPI} $ Cheerio root API.
//  * @return {number} Milliseconds since epoch.
//  */
// function extractPublishedAt($: CheerioAPI): number {
//   const og = $("meta[property=\"article:published_time\"]")
//     .attr("content")
//     ?.trim();
//   if (og) {
//     const ts = Date.parse(og);
//     if (!Number.isNaN(ts)) {
//       return ts;
//     }
//   }
//   return Date.now();
// }

// /**
//  * Fetch and summarize an article page for the DroidWire client.
//  */
// export const article = onRequest(
//   {region: "northamerica-northeast2"},
//   async (req: Request, res: Response): Promise<void> => {
//     try {
//       const id = String(req.query.id ?? "").trim();
//       const uRaw = String(req.query.u ?? "").trim();

//       if (!id) {
//         res.status(400).json({error: "Missing ?id"});
//         return;
//       }

//       if (!uRaw) {
//         res.status(400).json({error: "Missing ?u (originalUrl)"});
//         return;
//       }

//       const originalUrl = decodeURIComponent(uRaw);
//       const fetchUrl = toMobile(originalUrl);

//       const resp = await axios.get(fetchUrl, {
//         timeout: 10000,
//         headers: {
//           "User-Agent":
//             "Mozilla/5.0(compatible; DroidWireBot/1.0; +https://example.com)",
//           "Accept":
//           "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
//         },
//       });

//       const html = String(resp.data ?? "");
//       const $ = cheerio.load(html);

//       stripNoise($);

//       const post =
//       $(".post-body,.post-body.entry-content,article .post-body").first();

//       const title = extractTitle($, id);
//       const description = extractDescription($);
//       const images = extractImages($, post);
//       const source = extractSource($, originalUrl);
//       const publishedAt = extractPublishedAt($);

//       res.json({
//         id: id,
//         title: title,
//         source: source,
//         publishedAt: publishedAt,
//         description: description.slice(0, 8000),
//         images: images,
//         originalUrl: originalUrl,
//       });
//       return;
//     } catch (e: unknown) {
//       const msg = e instanceof Error ? e.message : String(e);
//       // eslint-disable-next-line no-console
//       console.error("article endpoint error:", msg);

//       const id = String(req.query.id ?? "").trim() || "unknown";
//       let originalUrl = "";

//       try {
//         originalUrl = decodeURIComponent(String(req.query.u ?? ""));
//       } catch {
//         originalUrl = "";
//       }

//       res.json({
//         id: id,
//         title: `Article ${id}`,
//         source: "Unknown",
//         publishedAt: Date.now(),
//         description:
//           "Full article content unavailable. Open the source to read more.",
//         images: [],
//         originalUrl: originalUrl,
//       });
//       return;
//     }
//   },
// );
