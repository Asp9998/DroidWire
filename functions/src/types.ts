export interface ArticlePreview {
  id: string;
  title: string;
  summary: string;
  thumb: string | null;
  source: string; // "Android Dev Blog"
  category: string; // "official"
  publishedAt: number; // epoch millis
  url: string;
}
