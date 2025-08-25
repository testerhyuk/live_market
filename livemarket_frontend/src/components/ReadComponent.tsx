import { useEffect, useState } from "react";
import { deleteArticle, deleteArticleImages, getArticleImages, getArticleLikeStatus, getLikeCount, getOne, getViewCount, increaseViewCount, likeArticle, unlikeArticle } from "../api/articleApi";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { useNavigate } from "react-router";
import { faEye, faHeart as solidHeart } from "@fortawesome/free-solid-svg-icons";
import { faHeart as regularHeart } from "@fortawesome/free-regular-svg-icons";
import CommentComponent from "./CommentComponent";
import { fetchNickname } from "../api/memberApi";
import { createVideoCallSession, sendVideoCallRequest } from "../api/chatApi";
import NotificationComponent from "./NotificationComponent";

function ReadComponent({ articleId }: { articleId: string }) {
  const [article, setArticle] = useState<any>(null);
  const [images, setImages] = useState<string[]>([]);
  const [viewCount, setViewCount] = useState(0);
  const [liked, setLiked] = useState(false);
  const [loadingLike, setLoadingLike] = useState(true);
  const [likeCount, setLikeCount] = useState(0);
  const [nickname, setNickname] = useState<string>()
  const [dropdownOpen, setDropdownOpen] = useState(false);

  const memberId = localStorage.getItem("memberId")
  const token = localStorage.getItem("token");

  const navigate = useNavigate();

  const generateRoomId = (id1: string, id2: string) => [id1, id2].sort().join("_");

  useEffect(() => {
    const fetchArticle = async () => {
      try {
        const data = await getOne(articleId);
        setArticle(data);

        const name = await fetchNickname(String(data.writerId))
        setNickname(name)

        const imageUrls = await getArticleImages(articleId);
        setImages(imageUrls)

        const count = await getViewCount(articleId) || 0;
        setViewCount(count)

        const countLike = await getLikeCount(articleId) || 0;
        setLikeCount(countLike)

        if (memberId && token) {
          increaseViewCount(articleId).catch(() => console.warn("조회수 증가 실패"))

          const status = await getArticleLikeStatus(articleId, String(memberId))
          setLiked(status)
        }
      } catch (error) {
        console.error("Error fetching article:", error);
      } finally {
        setLoadingLike(false)
      }
    };

    fetchArticle();
  }, [articleId, memberId, token]);

  if (!article) return <div>Loading...</div>;

  const toggleLike = async () => {
    if (loadingLike) return;
    if (!memberId || !token) return alert("로그인이 필요합니다");

    setLiked((prev) => {
      setLikeCount(prev ? likeCount - 1 : likeCount + 1);
      return !prev;
    });

    try {
      if (liked) await unlikeArticle(articleId);
      else await likeArticle(articleId);
    } catch (err) {
      console.error("좋아요 토글 실패:", err);
      setLiked((prev) => {
        setLikeCount(prev ? likeCount + 1 : likeCount - 1);
        return prev;
      });
    }
  };

  const handleVideoCallRequest = async () => {
    if (!memberId) return alert("로그인이 필요합니다");

    try {
      await sendVideoCallRequest(memberId, String(article.writerId));
      alert("화상채팅 요청 전송 완료");
    } catch (err) {
      console.error(err);
      alert("화상채팅 요청 실패");
    }
  };

  const handleDelete = async () => {
    if (!window.confirm("게시글을 삭제하시겠습니까?")) return;

    try {
      await deleteArticleImages(articleId);
      await deleteArticle(articleId);
      alert("게시글이 삭제되었습니다.");
      navigate("/");
    } catch (err) {
      console.error("삭제 실패:", err);
      alert("삭제 중 오류가 발생했습니다.");
    }
  };

  const formatPrice = (price: number | string) => (!price ? "0" : Number(price).toLocaleString());

  const formatDate = (dateStr: string) => {
    const d = new Date(dateStr);
    return d.toLocaleString(undefined, {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  return (
    <article className="max-w-3xl mx-auto p-6 bg-white rounded shadow-md my-8">
      <header className="mb-4 flex justify-between items-center">
        <h1 className="text-3xl font-bold">{article.title}</h1>
        <div className="flex items-center gap-3">
          <button
            className={`flex items-center text-red-500 ${loadingLike ? "opacity-50 cursor-not-allowed" : ""}`}
            onClick={toggleLike} disabled={loadingLike}
          >
            <FontAwesomeIcon icon={liked ? solidHeart : regularHeart} />
            <span className="ml-1">{likeCount}</span>
          </button>

          {nickname === String(localStorage.getItem("nickname")) && (
            <>
              <button onClick={() => navigate(`/edit/${articleId}`)} className="px-3 py-1 bg-blue-500 text-white rounded">수정</button>
              <button onClick={handleDelete} className="px-3 py-1 bg-red-500 text-white rounded">삭제</button>
            </>
          )}
        </div>
      </header>

      <div className="mb-4">
        <span className="font-semibold text-gray-700 mr-1">가격:</span>
        <span className="text-lg text-black-600">{formatPrice(article.price)} 원</span>
      </div>

      <div className="flex items-center space-x-4 text-gray-500 text-sm mb-4">
        {String(article.writerId) === String(memberId) ? (
          <span className="text-xs">작성자: {nickname}</span>
        ) : (
          <>
            <button
              onClick={() => setDropdownOpen((prev) => !prev)}
              className="text-sm font-semibold text-gray-700 hover:underline"
            >
              작성자: {nickname}
            </button>
            {dropdownOpen && (
              <div className="absolute left-0 mt-2 w-32 bg-white border rounded shadow-lg z-10">
                <ul className="py-1 text-sm text-gray-700">
                  <li>
                    <button
                      onClick={() => {
                        setDropdownOpen(false);
                        navigate(`/chat/${article.writerId}`, {
                          state: {
                            senderId: memberId,
                            roomId: generateRoomId(String(memberId), article.writerId),
                          },
                        });
                      }}
                      className="block w-full text-left px-4 py-2 hover:bg-gray-100"
                    >
                      1:1 채팅
                    </button>
                  </li>
                  <li>
                    <button
                      onClick={() => {
                        setDropdownOpen(false);
                        handleVideoCallRequest();
                      }}
                      className="block w-full text-left px-4 py-2 hover:bg-gray-100"
                    >
                      화상채팅 요청
                    </button>
                  </li>
                </ul>
              </div>
            )}
          </>
        )}
      </div>

      <div className="flex items-center space-x-4 text-gray-500 text-sm mb-4">
        <span className="text-xs">작성일: {formatDate(article.createdAt)}</span>
        <span className="text-xs">수정일: {formatDate(article.modifiedAt)}</span>
        <span className="text-xs flex items-center space-x-1">
          <FontAwesomeIcon icon={faEye} />
          <span>{viewCount}</span>
        </span>
      </div>

      <section className="prose prose-lg whitespace-pre-line mt-4">
        {images.map((url, idx) => (
          <img key={idx} src={url} alt={`image-${idx}`} style={{ maxWidth: "100%", margin: "10px 0" }} />
        ))}
        <p>{article.content}</p>
      </section>

      <CommentComponent articleId={articleId} />
      <div className="hidden">
        <NotificationComponent />
      </div>
    </article>
  );
}

export default ReadComponent;
