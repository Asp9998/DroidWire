import * as admin from "firebase-admin";

export interface Payload {
  id: string;
  title: string;
  summary: string;
  thumb: string | null;
  source: string;
  category: string; // "official"
  publishedAt: number; // millis
  contentUrl: string; // stub endpoint (Phase 4)
  url: string;
}

/**
 * Builds a standardized FCM payload object for Android Dev Blog items.
 *
 * @param {Object} args - The arguments used to create the payload.
 * @param {string} args.id - Unique article ID.
 * @param {string} args.title - Article title.
 * @param {string} args.summary - Short summary text.
 * @param {string|null} args.thumb - Thumbnail image URL or null.
 * @param {string} args.source - Source name (e.g., "Android Dev Blog").
 * @param {string} args.category - Category identifier (e.g., "official").
 * @param {number} args.publishedAt - Publish timestamp (in ms).
 * @param {string} args.contentUrl - URL to the full article content.
 * @return {Payload} Fully constructed FCM payload object.
 */
export function buildPayload(args: {
  id: string;
  title: string;
  summary: string;
  thumb: string | null;
  source: string;
  category: string;
  publishedAt: number;
  contentUrl: string;
  url: string;
}): Payload {
  return {
    id: args.id,
    title: args.title,
    summary: clampSummary(args.summary, 240),
    thumb: args.thumb,
    source: args.source,
    category: args.category,
    publishedAt: args.publishedAt,
    contentUrl: args.contentUrl,
    url: args.url,
  };
}

/**
 *
 * If the text is longer than the specified limit, it will:
 *  - Take the first `max` characters
 *  - Try to cut back to the last space (word boundary)
 *  - Append an ellipsis (…)
 *
 * @param {string} text - The full summary text to clamp.
 * @param {number} [max=240] - The maximum allowed length of the summary.
 * @return {string} The clamped summary string.
 */
function clampSummary(text: string, max = 240): string {
  if (text.length <= max) return text;
  const truncated = text.slice(0, max);
  const lastSpace = truncated.lastIndexOf(" ");
  const base = lastSpace > 0 ? truncated.slice(0, lastSpace) : truncated;
  return `${base.trimEnd()}…`;
}

/**
 * Sends an FCM data-only message to the "official"
 * topic with the given payload.
 *
 * @param {String} topic - The Category to send.
 * @param {Payload} p - The message payload to send.
 * @return {Promise<string>} The FCM message ID if the send succeeds.
 */
export async function sendToTopic(
  topic: string,
  p: Payload,
): Promise<string> {
  return admin.messaging().send({
    topic, // sends to /topics/{topic}
    data: {
      id: p.id,
      title: p.title,
      summary: p.summary,
      thumb: p.thumb ?? "",
      source: p.source,
      category: p.category,
      publishedAt: String(p.publishedAt),
      contentUrl: p.contentUrl,
      url: p.url,
    },
    android: {priority: "high"},
  });
}
