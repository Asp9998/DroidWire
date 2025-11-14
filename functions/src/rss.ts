// import {XMLParser} from "fast-xml-parser";
// import {createHash} from "crypto";
// import {ArticlePreview} from "./types";
// import {
//   CATEGORY_OFFICIAL,
//   FEED_URL_OFFICIAL,
//   SOURCE_OFFICIAL,
// } from "./config";

// /**
//  * Fetches Android Dev Blog Atom feed and returns normalized previews.
//  * Ensures all preview fields are plain strings and stable ids.
//  *
//  * @return {Promise<ArticlePreview[]>} List of normalized article previews.
//  */
// export async function fetchOfficialPreviews(): Promise<ArticlePreview[]> {
//   const xml = await fetch(FEED_URL_OFFICIAL).then((r) => r.text());

//   const parser = new XMLParser({
//     ignoreAttributes: false,
//     attributeNamePrefix: "@_",
//   });

//   const doc = parser.parse(xml);

//   const entriesRaw = (doc?.feed?.entry ??
//     []) as unknown as AtomEntry | AtomEntry[];
//   const entries = Array.isArray(entriesRaw) ? entriesRaw : [entriesRaw];

//   const previews: ArticlePreview[] = entries.map((e) => {
//     const title = nodeText(e.title) || "Untitled";
//     const url = resolveAtomLink(e);

//     const publishedIso =
//       nodeText(e.updated) ||
//       nodeText(e.published) ||
//       new Date().toISOString();
//     const publishedAt = Date.parse(publishedIso) || Date.now();

//     const contentHtmlRaw =
//       nodeText(e.content) || nodeText(e.summary) || "";
//     const summary = buildSummary(stripHtml(contentHtmlRaw), 240);
//     const thumb = extractFirstImgSrc(contentHtmlRaw);

//     const id = shortHash(`${url}|${publishedAt}`);

//     return {
//       id,
//       title,
//       summary,
//       thumb,
//       source: SOURCE_OFFICIAL,
//       category: CATEGORY_OFFICIAL,
//       publishedAt,
//       url,
//     };
//   });

//   previews.sort((a, b) => b.publishedAt - a.publishedAt);
//   return previews;
// }

// /**
//  * Atom entry subset used by this normalizer.
//  * Keys may be strings or nested nodes with #text.
//  */
// type AtomText = string | { [k: string]: unknown } | undefined;

// interface AtomLinkObj {
//   ["@_rel"]?: unknown;
//   ["@_href"]?: unknown;
// }

// type AtomLink = AtomLinkObj | AtomLinkObj[] | undefined;

// interface AtomEntry {
//   title?: AtomText;
//   link?: AtomLink;
//   updated?: AtomText;
//   published?: AtomText;
//   content?: AtomText;
//   summary?: AtomText;
// }

// /**
//  * Returns plain text from an Atom node which may be a string or an
//  * object carrying a "#text" field. Falls back to the first string
//  * value found, then String(n).
//  *
//  * @param {AtomText} n - Atom node value to extract text from.
//  * @return {string} Extracted text value.
//  */
// function nodeText(n: AtomText): string {
//   if (n == null) return "";
//   if (typeof n === "string") return n;
//   if (typeof n === "object") {
//     const o = n as Record<string, unknown>;
//     const t = o["#text"];
//     if (typeof t === "string") return t;
//     for (const v of Object.values(o)) {
//       if (typeof v === "string") return v;
//     }
//   }
//   return String(n);
// }

// /**
//  * Resolves the article URL from Atom link(s), preferring rel="alternate".
//  *
//  * @param {AtomEntry} entry - Atom entry containing link(s).
//  * @return {string} The resolved article URL, or an empty string.
//  */
// function resolveAtomLink(entry: AtomEntry): string {
//   const link = entry.link;
//   if (!link) return "";
//   if (Array.isArray(link)) {
//     const alt = link.find(
//       (l) =>
//         typeof l === "object" &&
//         (l as AtomLinkObj)["@_rel"] === "alternate" &&
//         (l as AtomLinkObj)["@_href"],
//     ) as AtomLinkObj | undefined;
//     if (alt?.["@_href"]) return String(alt["@_href"]);
//     const first = link.find(
//       (l) => typeof l === "object" && (l as AtomLinkObj)["@_href"],
//     ) as AtomLinkObj | undefined;
//     if (first?.["@_href"]) return String(first["@_href"]);
//     return "";
//   }
//   const href = (link as AtomLinkObj)["@_href"];
//   return href ? String(href) : "";
// }

// /**
//  * Strips HTML, scripts, and styles; collapses whitespace.
//  *
//  * @param {string} html - HTML string to clean.
//  * @return {string} Plain text string with HTML removed.
//  */
// function stripHtml(html: string): string {
//   return html
//     .replace(
//       /<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi,
//       "",
//     )
//     .replace(
//       /<style\b[^<]*(?:(?!<\/style>)<[^<]*)*<\/style>/gi,
//       "",
//     )
//     .replace(/<[^>]+>/g, " ")
//     .replace(/\s+/g, " ")
//     .trim();
// }

// /**
//  * Truncates text to a maximum length, adding an ellipsis if needed.
//  *
//  * @param {string} text - The text to truncate.
//  * @param {number} maxLen - The maximum length allowed.
//  * @return {string} Truncated text with ellipsis if applicable.
//  */
// function buildSummary(text: string, maxLen: number): string {
//   if (!text) return "";
//   if (text.length <= maxLen) return text;
//   return `${text.slice(0, maxLen - 1)}…`;
// }

// /**
//  * Extracts the first <img src="..."> URL from an HTML snippet.
//  *
//  * @param {string} html - HTML content to search for image.
//  * @return {string|null} The first image URL, or null if none found.
//  */
// function extractFirstImgSrc(html: string): string | null {
//   const m = html.match(/<img[^>]+src=["']([^"']+)["']/i);
//   return m ? m[1] : null;
// }

// /**
//  * Produces a short, stable SHA-1 hash from a string.
//  *
//  * @param {string} s - Input string to hash.
//  * @return {string} 16-character hex digest.
//  */
// function shortHash(s: string): string {
//   return createHash("sha1").update(s).digest("hex").slice(0, 16);
// }
import {XMLParser} from "fast-xml-parser";
import {createHash} from "crypto";
import {ArticlePreview} from "./types";
import {
  CATEGORY_OFFICIAL,
  FEED_URL_OFFICIAL,
  SOURCE_OFFICIAL,
  // NEW:
  FEED_URL_ANDROID,
  SOURCE_ANDROID,
  CATEGORY_ANDROID,
  FEED_URL_KOTLIN,
  SOURCE_KOTLIN,
  CATEGORY_KOTLIN,
  FEED_URL_JETPACK,
  SOURCE_JETPACK,
  CATEGORY_JETPACK,
} from "./config";


/**
 * Fetches Android Dev Blog Atom feed and returns normalized previews.
 * Ensures all preview fields are plain strings and stable ids.
 *
 * @return {Promise<ArticlePreview[]>} List of normalized article previews.
 */
export async function fetchOfficialPreviews(): Promise<ArticlePreview[]> {
  return fetchFeedPreviews(
    FEED_URL_OFFICIAL,
    SOURCE_OFFICIAL,
    CATEGORY_OFFICIAL,
  );
}

/**
 * Fetches Android category feed and returns normalized previews.
 */
export async function fetchAndroidPreviews(): Promise<ArticlePreview[]> {
  return fetchFeedPreviews(
    FEED_URL_ANDROID,
    SOURCE_ANDROID,
    CATEGORY_ANDROID,
  );
}

/**
 * Fetches Kotlin category feed and returns normalized previews.
 */
export async function fetchKotlinPreviews(): Promise<ArticlePreview[]> {
  return fetchFeedPreviews(
    FEED_URL_KOTLIN,
    SOURCE_KOTLIN,
    CATEGORY_KOTLIN,
  );
}

/**
 * Fetches Jetpack category feed and returns normalized previews.
 */
export async function fetchJetpackPreviews(): Promise<ArticlePreview[]> {
  return fetchFeedPreviews(
    FEED_URL_JETPACK,
    SOURCE_JETPACK,
    CATEGORY_JETPACK,
  );
}

/**
 * Generic helper: fetches an Atom-like feed and normalizes to ArticlePreview[]
 * using the given source and category.
 *
 * @param {string} feedUrl - URL of the feed to fetch.
 * @param {string} source - Source label to attach to each preview.
 * @param {string} category - Category key to attach to each preview.
 * @return {Promise<ArticlePreview[]>} List of normalized article previews.
 */
async function fetchFeedPreviews(
  feedUrl: string,
  source: string,
  category: string,
): Promise<ArticlePreview[]> {
  const xml = await fetch(feedUrl).then((r) => r.text());

  const parser = new XMLParser({
    ignoreAttributes: false,
    attributeNamePrefix: "@_",
  });

  const doc = parser.parse(xml);

  const entriesRaw = (doc?.feed?.entry ??
    []) as unknown as AtomEntry | AtomEntry[];
  const entries = Array.isArray(entriesRaw) ? entriesRaw : [entriesRaw];

  const previews: ArticlePreview[] = entries.map((e) => {
    const title = nodeText(e.title) || "Untitled";
    const url = resolveAtomLink(e);

    const publishedIso =
      nodeText(e.updated) ||
      nodeText(e.published) ||
      new Date().toISOString();
    const publishedAt = Date.parse(publishedIso) || Date.now();

    const contentHtmlRaw =
      nodeText(e.content) || nodeText(e.summary) || "";

    const contentText = stripHtml(contentHtmlRaw);
    const summary = contentText.replace(/\s+/g, " ").trim();
    const thumb = extractFirstImgSrc(contentHtmlRaw);
    const id = shortHash(`${url}|${publishedAt}`);

    return {
      id,
      title,
      summary,
      thumb,
      source, // <- use function arg
      category, // <- use function arg
      publishedAt,
      url,
    };
  });

  previews.sort((a, b) => b.publishedAt - a.publishedAt);
  return previews;
}

/**
 * Atom entry subset used by this normalizer.
 * Keys may be strings or nested nodes with #text.
 */
type AtomText = string | { [k: string]: unknown } | undefined;

interface AtomLinkObj {
  ["@_rel"]?: unknown;
  ["@_href"]?: unknown;
}

type AtomLink = AtomLinkObj | AtomLinkObj[] | undefined;

interface AtomEntry {
  title?: AtomText;
  link?: AtomLink;
  updated?: AtomText;
  published?: AtomText;
  content?: AtomText;
  summary?: AtomText;
}

/**
 * Returns plain text from an Atom node which may be a string or an
 * object carrying a "#text" field. Falls back to the first string
 * value found, then String(n).
 *
 * @param {AtomText} n - Atom node value to extract text from.
 * @return {string} Extracted text value.
 */
function nodeText(n: AtomText): string {
  if (n == null) return "";
  if (typeof n === "string") return n;
  if (typeof n === "object") {
    const o = n as Record<string, unknown>;
    const t = o["#text"];
    if (typeof t === "string") return t;
    for (const v of Object.values(o)) {
      if (typeof v === "string") return v;
    }
  }
  return String(n);
}

/**
 * Resolves the article URL from Atom link(s), preferring rel="alternate".
 *
 * @param {AtomEntry} entry - Atom entry containing link(s).
 * @return {string} The resolved article URL, or an empty string.
 */
function resolveAtomLink(entry: AtomEntry): string {
  const link = entry.link;
  if (!link) return "";
  if (Array.isArray(link)) {
    const alt = link.find(
      (l) =>
        typeof l === "object" &&
        (l as AtomLinkObj)["@_rel"] === "alternate" &&
        (l as AtomLinkObj)["@_href"],
    ) as AtomLinkObj | undefined;
    if (alt?.["@_href"]) return String(alt["@_href"]);
    const first = link.find(
      (l) => typeof l === "object" && (l as AtomLinkObj)["@_href"],
    ) as AtomLinkObj | undefined;
    if (first?.["@_href"]) return String(first["@_href"]);
    return "";
  }
  const href = (link as AtomLinkObj)["@_href"];
  return href ? String(href) : "";
}

/**
 * Strips HTML, scripts, and styles; collapses whitespace.
 *
 * @param {string} html - HTML string to clean.
 * @return {string} Plain text string with HTML removed.
 */
function stripHtml(html: string): string {
  return html
    .replace(
      /<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi,
      "",
    )
    .replace(
      /<style\b[^<]*(?:(?!<\/style>)<[^<]*)*<\/style>/gi,
      "",
    )
    .replace(/<[^>]+>/g, " ")
    .replace(/\s+/g, " ")
    .trim();
}


/**
 * Extracts the first <img src="..."> URL from an HTML snippet.
 *
 * @param {string} html - HTML content to search for image.
 * @return {string|null} The first image URL, or null if none found.
 */
function extractFirstImgSrc(html: string): string | null {
  const m = html.match(/<img[^>]+src=["']([^"']+)["']/i);
  return m ? m[1] : null;
}

/**
 * Produces a short, stable SHA-1 hash from a string.
 *
 * @param {string} s - Input string to hash.
 * @return {string} 16-character hex digest.
 */
function shortHash(s: string): string {
  return createHash("sha1").update(s).digest("hex").slice(0, 16);
}

/**
 * Try to find an ArticlePreview for a given article URL by checking
 * all known feeds.
 *
 * @param {string} url The original article URL from the feed.
 * @return {Promise<ArticlePreview|null>} Matching preview or null.
 */
export async function findPreviewByUrl(
  url: string,
): Promise<ArticlePreview | null> {
  const fetchers: Array<() => Promise<ArticlePreview[]>> = [
    fetchOfficialPreviews,
    fetchAndroidPreviews,
    fetchKotlinPreviews,
    fetchJetpackPreviews,
  ];

  for (const fetch of fetchers) {
    const previews = await fetch();
    const match = previews.find((p) => p.url === url);
    if (match) {
      return match;
    }
  }

  return null;
}
