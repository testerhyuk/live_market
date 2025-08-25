import { useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router";
import { searchArticles, type Article } from "../api/articleApi";

type Props = {
  keyword: string;
  boardId: string;
};

export default function SearchComponent({ keyword, boardId }: Props) {
  const [articles, setArticles] = useState<Article[]>([]);
  const [lastArticleId, setLastArticleId] = useState<string | undefined>(undefined);
  const [loading, setLoading] = useState(false);
  const [hasMore, setHasMore] = useState(true);
  const navigate = useNavigate();

  const loadArticles = useCallback(async () => {
    if (loading || !hasMore || !keyword) return;
    setLoading(true);

    try {
      const data = await searchArticles(keyword, lastArticleId);
      if (data.length > 0) {
        setArticles((prev) => [...prev, ...data]);
        setLastArticleId(data[data.length - 1].articleId.toString());

        if (data.length < 7) setHasMore(false);
      } else {
        setHasMore(false);
      }
    } catch (error) {
      console.error(error);
    }

    setLoading(false);
  }, [boardId, keyword, lastArticleId, loading, hasMore]);

  useEffect(() => {
    setArticles([]);
    setLastArticleId(undefined);
    setHasMore(true);
  }, [keyword, boardId]);

  useEffect(() => {
    loadArticles();
  }, [loadArticles]);

  useEffect(() => {
    const onScroll = () => {
      if (
        window.innerHeight + window.scrollY >= document.body.offsetHeight - 300 &&
        !loading &&
        hasMore
      ) {
        loadArticles();
      }
    };
    window.addEventListener("scroll", onScroll);
    return () => window.removeEventListener("scroll", onScroll);
  }, [loading, hasMore, loadArticles]);

  return (
    <div>
      {articles.map((article) => (
        <div
          key={article.articleId}
          className="border-b border-gray-300 py-4 cursor-pointer"
          onClick={() => navigate(`/read/${article.articleId}`)}
        >
          <h3 className="font-semibold text-lg">{article.title}</h3>
          <p className="text-gray-600 line-clamp-2">{article.content}</p>
        </div>
      ))}

      {loading && <div className="text-center py-4">로딩중...</div>}
      {!hasMore && !loading && <div className="text-center py-4 text-gray-500">더 이상 게시글이 없습니다.</div>}
    </div>
  );
}
