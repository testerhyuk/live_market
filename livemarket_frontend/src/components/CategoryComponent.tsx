import { useEffect, useState, useCallback } from "react";
import { useParams, useNavigate } from "react-router";
import { getAll, getByCategory, getArticleImages, type Article } from "../api/articleApi";

const categoryMap: Record<string, string> = {
  all: "전체",
  ELECTRONICS: "전자기기",
  CLOTHES: "패션/잡화",
  FURNITURE: "가구",
  SPORTS: "스포츠",
  GAME: "게임/취미",
  BOOK: "도서",
  ETC: "기타",
};

export default function CategoryComponent() {
  const { category } = useParams<{ category: string }>();
  const navigate = useNavigate();

  const [articles, setArticles] = useState<Article[]>([]);
  const [articleImages, setArticleImages] = useState<Record<string, string[]>>({});
  const [lastArticleId, setLastArticleId] = useState<string | undefined>();
  const [loading, setLoading] = useState(false);
  const [hasMore, setHasMore] = useState(true);

  const loadArticles = useCallback(async () => {
    if (loading || !hasMore) return;
    setLoading(true);

    try {
      let data: Article[] = [];

      if (category === "all") {
        // 전체 게시글 가져오기
        data = await getAll(lastArticleId);
      } else {
        // 카테고리별 게시글 가져오기
        data = await getByCategory(category!, lastArticleId);
      }

      if (data && data.length > 0) {
        setArticles((prev) => [...prev, ...data]);
        setLastArticleId(data[data.length - 1].articleId.toString());

        // 이미지 로드
        data.forEach(async (article) => {
          const imgs = await getArticleImages(article.articleId.toString());
          setArticleImages((prev) => ({ ...prev, [article.articleId]: imgs }));
        });

        if (data.length < 7) setHasMore(false);
      } else {
        setHasMore(false);
      }
    } catch (err) {
      console.error(err);
    }

    setLoading(false);
  }, [category, lastArticleId, loading, hasMore]);

  useEffect(() => {
    // 카테고리 바뀌면 초기화
    setArticles([]);
    setArticleImages({});
    setLastArticleId(undefined);
    setHasMore(true);
  }, [category]);

  useEffect(() => {
    loadArticles();
  }, [category, lastArticleId]);

  return (
    <div className="max-w-3xl mx-auto px-4">
      <h2 className="text-lg font-semibold py-4">
        {categoryMap[category ?? "all"]}
      </h2>

      {articles.map((article) => (
        <div
          key={article.articleId}
          className="flex items-start border-b border-gray-200 py-4 space-x-4 cursor-pointer"
          onClick={() => navigate(`/read/${article.articleId}`)}
        >
          <div className="w-24 h-16 flex-shrink-0 rounded overflow-hidden bg-white">
            {articleImages[article.articleId] && articleImages[article.articleId].length > 0 ? (
              <img
                src={articleImages[article.articleId][0]}
                alt={article.title}
                className="w-full h-full object-cover"
              />
            ) : (
              <div className="w-full h-full bg-gray-100" />
            )}
          </div>
          <div className="flex-1 min-w-0">
            <h3 className="font-semibold text-base leading-snug text-gray-900 line-clamp-2">
              {article.title}
            </h3>
            <p className="mt-1 text-sm text-gray-600 line-clamp-2">
              {article.content}
            </p>
          </div>
        </div>
      ))}

      {loading && <div className="py-4 text-center text-gray-500">로딩중...</div>}
      {!hasMore && !loading && (
        <div className="py-4 text-center text-gray-400">더 이상 게시글이 없습니다.</div>
      )}
    </div>
  );
}
