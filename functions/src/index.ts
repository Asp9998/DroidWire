// import * as admin from "firebase-admin";
// admin.initializeApp();

// import {onRequest} from "firebase-functions/v2/https";
// import {ArticlePreview} from "./types";
// import {FEEDS} from "./config";
// import {
//   fetchOfficialPreviews,
//   fetchAndroidPreviews,
//   fetchKotlinPreviews,
//   fetchJetpackPreviews,
// } from "./rss";
// import {buildPayload, sendToTopic} from "./push";
// import {readLastSeen, writeLastSeen} from "./lastSeen";

// // Map category -> fetch function
// const FETCHERS: Record<string, () => Promise<ArticlePreview[]>> = {
//   official: fetchOfficialPreviews,
//   android: fetchAndroidPreviews,
//   kotlin: fetchKotlinPreviews,
//   jetpack: fetchJetpackPreviews,
// };

// /**
//  * GET /pushAll
//  * - Loop FEEDS
//  * - For each: fetch previews, diff vs lastSeen, send to /topics/{category}
//  * - Advance lastSeen after successful sends
//  *
//  * Query params:
//  *  limit: number (cap per feed, default 5)
//  *  dry: "true" (log only, no FCM)
//  */
// export const pushAll = onRequest(
//   {region: "northamerica-northeast2"},
//   async (req, res) => {
//     try {
//       const limit = Number(req.query.limit ?? 5);
//       const dry = String(req.query.dry ?? "false") === "true";

//       const baseArticleUrl =
//         "https://northamerica-northeast2-droidwire-57e82.cloudfunctions" +
//         ".net/article";

//    const results: Array<{category: string; sent: number; message?: string}> =
//         [];

//       for (const config of FEEDS) {
//         const fetcher = FETCHERS[config.category];

//         if (!fetcher) {
//        console.warn(`No fetcher for category=${config.category}, skipping`);
//           results.push({
//             category: config.category,
//             sent: 0,
//             message: "no_fetcher",
//           });
//           continue;
//         }

//         const previews = await fetcher();
//         const lastSeen = await readLastSeen(config.lastSeenDoc);

//         const newOnes = previews
//           .filter((p) => p.publishedAt > lastSeen.lastTs)
//           .slice(0, Math.max(0, limit));

//         if (newOnes.length === 0) {
//           console.log(`No new items for category=${config.category}`);
//           results.push({
//             category: config.category,
//             sent: 0,
//             message: "no_new",
//           });
//           continue;
//         }

//         const payloads = newOnes.map((p) =>
//           buildPayload({
//             id: String(p.id),
//             title: String(p.title),
//             summary: String(p.summary),
//             thumb: p.thumb ?? "",
//             source: String(p.source),
//             category: String(p.category),
//             publishedAt: p.publishedAt,
//             contentUrl:
//               `${baseArticleUrl}?id=${encodeURIComponent(String(p.id))}` +
//               `&u=${encodeURIComponent(p.url)}`,
//             url: p.url,
//           }),
//         );

//         if (dry) {
//           console.log(
//             `DRY RUN: would send ${payloads.length} messages for ` +
//               `category=${config.category}`,
//           );
//           payloads.forEach((pl) => console.log(JSON.stringify(pl)));
//           results.push({
//             category: config.category,
//             sent: 0,
//             message: "dry_run",
//           });
//           continue;
//         }

//         const msgIds: string[] = [];
//         for (const pl of payloads) {
//           const msgId = await sendToTopic(config.category, pl);
//           msgIds.push(msgId);
//         }

//         const newest = newOnes.reduce((a, b) =>
//           a.publishedAt >= b.publishedAt ? a : b,
//         );

//         await writeLastSeen(config.lastSeenDoc, {
//           lastTs: newest.publishedAt,
//           lastId: String(newest.id),
//         });

//         results.push({category: config.category, sent: msgIds.length});
//       }

//       res.json({ok: true, results});
//     } catch (e: unknown) {
//       const msg = e instanceof Error ? e.message : String(e);
//       console.error(msg);
//       res.status(500).json({ok: false, error: msg});
//     }
//   },
// );

import {onRequest} from "firebase-functions/v2/https";
import type {Request, Response} from "express";
import axios from "axios";
import * as cheerio from "cheerio";
import type {CheerioAPI, Cheerio} from "cheerio";
import type {Element as DomElement, AnyNode} from "domhandler";

import {findPreviewByUrl} from "./rss";
import type {ArticlePreview} from "./types";

/**
 * Ensure Android Developers Blog URLs use the Blogger mobile view (?m=1).
 * @param {string} url Original URL.
 * @return {string} URL forced to mobile version if possible.
 */
function toMobile(url: string): string {
  try {
    const u = new URL(url);
    if (!u.searchParams.has("m")) {
      u.searchParams.set("m", "1");
    }
    return u.toString();
  } catch {
    return url.endsWith("?m=1") ? url : `${url}?m=1`;
  }
}

/**
 * Remove non-content noise from the DOM.
 * @param {CheerioAPI} $ Cheerio root API.
 * @return {void}
 */
function stripNoise($: CheerioAPI): void {
  const removeGlobal = [
    "script",
    "style",
    "noscript",
    "iframe",
    "ins",
    "svg",
    "header",
    "footer",
    "nav",
    "[role=\"navigation\"]",
  ].join(",");

  $("body").children(removeGlobal).remove();

  const removeChrome = [
    ".header-outer",
    ".content-header",
    ".breadcrumbs",
    ".post-share-buttons",
    // keep post-footer safe: remove only if outside .post-body
    "body > .post-footer",
    ".post-timestamp",
    ".jump-link",
    ".byline",
    ".date-outer",
    ".widget",
    ".widget-content",
    ".sidebar",
    ".menu",
    ".top-bar",
  ].join(",");

  $("body").children(removeChrome).remove();
}

/**
 * Extract 3–4 human-readable paragraphs from the article body.
 * (Used only as a fallback when RSS summary is not available.)
 * @param {CheerioAPI} $ Cheerio root API.
 * @return {string} Description text.
 */
function extractDescription($: CheerioAPI): string {
  const genericRe = new RegExp(
    [
      "^News and insights on the Android platform",
      "^The latest Android and Google Play news",
      "^Android Developers( →)?$",
    ].join("|"),
    "i",
  );

  // Try to find the real article container (Blogger variants).
  const root =
  $(".post-body, .post-body.entry-content,[itemprop='articleBody'], article")
    .first();

  // Collect paragraphs and trim whitespace/spans/fonts.
  const paras = root
    .find("p")
    .map((_: number, el: AnyNode) => $(el).text().trim())
    .get()
    .map((t: string) => t.replace(/\s+/g, " "))
    .filter((t: string) => {
      if (!t) return false;
      if (genericRe.test(t)) return false;
      if (/^(☰|🔍)\s*$/.test(t)) return false;
      // skip super-short junk like "Read more" etc.
      return t.length > 30;
    });

  // Return first few readable paragraphs
  if (paras.length > 0) {
    return paras.slice(0, 4).join("\n\n");
  }

  // fallback: maybe meta description
  const meta =
    $("meta[property='og:description']").attr("content") ||
    $("meta[name='twitter:description']").attr("content") ||
    $("meta[name='description']").attr("content");

  return meta?.trim() || "";
}

/**
 * Collect a small set of content images (skip icons/social/pixels).
 * @param {CheerioAPI} $ Cheerio root API.
 * @param {Cheerio<AnyNode>} scope Node scope to search within.
 * @return {string[]} Up to six image URLs.
 */
function extractImages(
  $: CheerioAPI,
  scope: Cheerio<AnyNode>,
): string[] {
  const set = new Set<string>();

  const og = $("meta[property=\"og:image\"]").attr("content")?.trim();
  if (og) {
    set.add(og);
  }

  const root: Cheerio<AnyNode> = scope.length ? scope : $("body");

  root.find("img").each((_: number, el: DomElement) => {
    const raw = $(el).attr("src") || $(el).attr("data-src");
    if (!raw) {
      return;
    }

    const s = raw.trim();
    const isHttp = s.startsWith("http");
    const isSvg = /\.svg($|\?)/i.test(s);
    const isSocial = /gstatic|facebook|linkedin|twitter|icons?/i.test(s);
    const isPixel = /pixel/i.test(s);

    if (isHttp && !isSvg && !isSocial && !isPixel) {
      set.add(s);
    }
  });

  return Array.from(set).slice(0, 6);
}

/**
 * Derive a reasonable title.
 * @param {CheerioAPI} $ Cheerio root API.
 * @param {string} id Fallback id for untitled pages.
 * @return {string} Title.
 */
function extractTitle($: CheerioAPI, id: string): string {
  const og = $("meta[property=\"og:title\"]").attr("content")?.trim();
  if (og && og.length > 0) {
    return og;
  }
  const t = $("title").first().text().trim();
  return t.length > 0 ? t : `Article ${id}`;
}

/**
 * Extract a site/source name from OG or hostname.
 * @param {CheerioAPI} $ Cheerio root API.
 * @param {string} originalUrl Original URL string.
 * @return {string} Source/site name.
 */
function extractSource($: CheerioAPI, originalUrl: string): string {
  const site = $("meta[property=\"og:site_name\"]").attr("content")?.trim();
  if (site && site.length > 0) {
    return site;
  }
  try {
    return new URL(originalUrl).hostname.replace(/^www\./, "");
  } catch {
    return "Android Developers Blog";
  }
}

/**
 * Prefer OG published_time, else now.
 * @param {CheerioAPI} $ Cheerio root API.
 * @return {number} Milliseconds since epoch.
 */
function extractPublishedAt($: CheerioAPI): number {
  const og = $("meta[property=\"article:published_time\"]")
    .attr("content")
    ?.trim();
  if (og) {
    const ts = Date.parse(og);
    if (!Number.isNaN(ts)) {
      return ts;
    }
  }
  return Date.now();
}

/**
 * Fetch and summarize an article page for the DroidWire client.
 *
 * - Text (title/summary/source/publishedAt) comes from RSS preview
 *   when possible (same pipeline as pushAll).
 * - Images (array) come from HTML scraping (excluding icons/logos/pixels).
 */
export const article = onRequest(
  {region: "northamerica-northeast2"},
  async (req: Request, res: Response): Promise<void> => {
    try {
      const id = String(req.query.id ?? "").trim();
      const uRaw = String(req.query.u ?? "").trim();

      if (!id) {
        res.status(400).json({error: "Missing ?id"});
        return;
      }

      if (!uRaw) {
        res.status(400).json({error: "Missing ?u (originalUrl)"});
        return;
      }

      const originalUrl = decodeURIComponent(uRaw);

      // 1) Try to get preview from RSS (same source as pushAll)
      const preview: ArticlePreview | null =
        await findPreviewByUrl(originalUrl);

      // 2) Fetch HTML to extract images (and fallback data if needed)
      const fetchUrl = toMobile(originalUrl);

      const resp = await axios.get(fetchUrl, {
        timeout: 10000,
        headers: {
          "User-Agent":
            "Mozilla/5.0(compatible; DroidWireBot/1.0; +https://example.com)",
          "Accept":
          "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
        },
      });

      const html = String(resp.data ?? "");
      const $ = cheerio.load(html);

      stripNoise($);

      const post =
        $(".post-body,.post-body.entry-content,article .post-body").first();

      const images = extractImages($, post);

      // 3) Prefer RSS data for title/source/description/publishedAt
      if (preview) {
        res.json({
          id: String(preview.id),
          title: String(preview.title),
          source: String(preview.source),
          publishedAt: preview.publishedAt,
          // Description = RSS summary, same as first function
          description: String(preview.summary).slice(0, 8000),
          // All content images (filtered)
          images: images,
          originalUrl: preview.url,
        });
        return;
      }

      // 4) Fallback: no preview found → use scraped data
      const title = extractTitle($, id);
      const description = extractDescription($);
      const source = extractSource($, originalUrl);
      const publishedAt = extractPublishedAt($);

      res.json({
        id: id,
        title: title,
        source: source,
        publishedAt: publishedAt,
        description: description.slice(0, 8000),
        images: images,
        originalUrl: originalUrl,
      });
      return;
    } catch (e: unknown) {
      const msg = e instanceof Error ? e.message : String(e);
      // eslint-disable-next-line no-console
      console.error("article endpoint error:", msg);

      const id = String(req.query.id ?? "").trim() || "unknown";
      let originalUrl = "";

      try {
        originalUrl = decodeURIComponent(String(req.query.u ?? ""));
      } catch {
        originalUrl = "";
      }

      res.json({
        id: id,
        title: `Article ${id}`,
        source: "Unknown",
        publishedAt: Date.now(),
        description:
          "Full article content unavailable. Open the source to read more.",
        images: [],
        originalUrl: originalUrl,
      });
      return;
    }
  },
);
