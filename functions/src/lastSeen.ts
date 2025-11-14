// import * as admin from "firebase-admin";
// import {LAST_SEEN_DOC_OFFICIAL} from "./config";

// export interface LastSeen {
//   lastTs: number;
//   lastId: string;
// }

// /**
//  * Reads the 'last seen' metadata for the official Android Dev Blog
//  * from Firestore.
//  *
//  * @return {Promise<LastSeen>} The last seen timestamp and ID.
//  */
// export async function readLastSeenOfficial(): Promise<LastSeen> {
//   const snap = await admin.firestore().doc(LAST_SEEN_DOC_OFFICIAL).get();
//   if (!snap.exists) return {lastTs: 0, lastId: ""};
//   const data = snap.data() as Partial<LastSeen>;
//   return {
//     lastTs: Number(data?.lastTs ?? 0),
//     lastId: String(data?.lastId ?? ""),
//   };
// }

// /**
//  * Updates the Firestore document that tracks the latest seen entry
//  * from the official Android Dev Blog feed.
//  *
//  * @param {LastSeen} v - Object containing latest timestamp and entry ID.
//  * @return {Promise<void>} Resolves when Firestore write completes.
//  */
// export async function writeLastSeenOfficial(v: LastSeen): Promise<void> {
//   await admin.firestore().doc(LAST_SEEN_DOC_OFFICIAL).set(
//     {
//       lastTs: v.lastTs,
//       lastId: v.lastId,
//     },
//     {merge: true},
//   );
// }
import * as admin from "firebase-admin";

const db = admin.firestore();

export interface LastSeen {
  lastTs: number;
  lastId: string;
}

/**
 * Read the last-seen metadata from Firestore.
 *
 * @param {string} path
 *   Firestore doc path (e.g. "lastSeen/official_androiddevblog").
 * @return {Promise<LastSeen>}
 *   The last-seen structure, or default values if missing.
 */
export async function readLastSeen(path: string): Promise<LastSeen> {
  const snap = await db.doc(path).get();

  if (!snap.exists) {
    return {lastTs: 0, lastId: ""};
  }

  const data = snap.data() as Partial<LastSeen>;

  return {
    lastTs: data.lastTs ?? 0,
    lastId: data.lastId ?? "",
  };
}

/**
 * Write last-seen metadata to Firestore.
 *
 * @param {string} path
 *   Firestore document path.
 * @param {LastSeen} value
 *   Last-seen info to persist.
 * @return {Promise<void>}
 *   Completes when data is written.
 */
export async function writeLastSeen(
  path: string,
  value: LastSeen,
): Promise<void> {
  await db.doc(path).set(value, {merge: true});
}
