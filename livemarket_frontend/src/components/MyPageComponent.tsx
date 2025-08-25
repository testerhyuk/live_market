import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import type { RootState } from "../redux/store";
import { useNavigate } from "react-router";
import { getCommentsByUserId, type CommentResponse } from "../api/commentApi";
import { getArticleImages, getArticlesByUserId, getLikedArticlesByUserId, getOne } from "../api/articleApi";
import { fetchNickname } from "../api/memberApi";

const MyPageComponent = () => {
  const navigate = useNavigate();

  const memberId = useSelector((state: RootState) => state.auth.memberId);

  const [myArticles, setMyArticles] = useState<any[]>([]);
  const [likedArticles, setLikedArticles] = useState<any[]>([]);
  const [myComments, setMyComments] = useState<CommentResponse[]>([]);
  const [nickname, setNickname] = useState<string>()

  useEffect(() => {
    const fetchData = async () => {
        try {
            const getNickname = await fetchNickname(String(memberId));
            setNickname(getNickname);
        } catch (err) {
        console.error("내 닉네임 불러오기 실패", err);
        }
    };

    fetchData();
    }, [memberId]);



  // 내가 작성한 게시글
  useEffect(() => {
    getArticlesByUserId(memberId)
        .then(async (articles) => {
          const articlesWithImages = await Promise.all(
            articles.map(async (article) => {
              const images = await getArticleImages(String(article.articleId));
              return {
                ...article,
                imageUrl: images[0] || "/gray-background.png",
              };
            })
          );
          setMyArticles(articlesWithImages);
        })
        .catch((err) => console.error("내 게시글 불러오기 실패", err));
  }, [memberId]);

  // 내가 좋아요한 게시글
  useEffect(() => {
    const fetchLikedArticles = async () => {
      if (!memberId) return;

      try {
        const likes = await getLikedArticlesByUserId(memberId);

        const articlesWithImages = await Promise.all(
          likes.map(async (like) => {
            // 각 좋아요 게시글 실제 데이터 가져오기
            const article = await getOne(String(like.articleId));
            const images = await getArticleImages(String(like.articleId));
            return {
              articleId: like.articleId,
              title: article?.title ?? "제목 없음",
              imageUrl: images[0] || "/gray-background.png",
            };
          })
        );

        setLikedArticles(articlesWithImages);
      } catch (err) {
        console.error("좋아요 게시글 불러오기 실패", err);
        setLikedArticles([]);
      }
    };

    fetchLikedArticles();
  }, [memberId]);

  // 내가 단 댓글
  useEffect(() => {
    if (memberId) {
      getCommentsByUserId(memberId)
        .then((comments) => setMyComments(comments))
        .catch((err) => console.error("내 댓글 불러오기 실패", err));
    }
  }, [memberId]);

  const goToEditProfile = () => navigate("/profile/edit");
  const goToArticle = (articleId: string) => navigate(`/read/${articleId}`);

  // 드래그 상태 관리
  let isDown = false;
  let startX = 0;
  let scrollLeft = 0;

  let dragStartX = 0;
  let dragStartY = 0;
  let isDragging = false;

  const handleMouseDown = (e: React.MouseEvent<HTMLDivElement, MouseEvent>) => {
    const container = e.currentTarget;
    isDown = true;
    dragStartX = e.pageX;
    dragStartY = e.pageY;
    isDragging = false;
    startX = e.pageX - container.offsetLeft;
    scrollLeft = container.scrollLeft;
    container.style.cursor = "grabbing";
  };

  const handleMouseLeave = (e: React.MouseEvent<HTMLDivElement, MouseEvent>) => {
    isDown = false;
    e.currentTarget.style.cursor = "grab";
  };

  const handleMouseUp = (e: React.MouseEvent<HTMLDivElement, MouseEvent>) => {
    isDown = false;
    e.currentTarget.style.cursor = "grab";
  };

  const handleMouseMove = (e: React.MouseEvent<HTMLDivElement, MouseEvent>) => {
    if (!isDown) return;
    e.preventDefault();
    const container = e.currentTarget;
    const x = e.pageX - container.offsetLeft;
    const walk = (x - startX) * 1;
    container.scrollLeft = scrollLeft - walk;

    if (Math.abs(e.pageX - dragStartX) > 5 || Math.abs(e.pageY - dragStartY) > 5) {
      isDragging = true;
    }
  };

  const handleClickArticle = (articleId: string) => {
    if (!isDragging) goToArticle(articleId);
  };

  return (
    <div className="my-page p-5">
      {/* 마이페이지 헤더 */}
      <h2 className="text-3xl font-bold mb-4 text-gray-800">마이페이지</h2>

      <div className="mt-8 mb-2 text-lg">
        <strong className="text-gray-600">닉네임:</strong>{" "}
        <span className="text-gray-900 font-medium">{nickname ?? "알 수 없음"}</span>
      </div>

      <button
        onClick={goToEditProfile}
        className="mt-3 px-5 py-2 bg-blue-500 text-white font-semibold rounded-lg shadow hover:bg-blue-600 transition-colors duration-200 cursor-pointer"
      >
        회원 정보 수정
      </button>

      {/* 내가 작성한 게시글 */}
 
        <h3 className="mt-8 text-xl font-semibold text-gray-700">내가 작성한 게시글</h3>
        {myArticles.length > 0 ? (
        <div
            className="mt-5 flex gap-4 py-2 cursor-grab select-none overflow-hidden"
            onMouseDown={handleMouseDown}
            onMouseLeave={handleMouseLeave}
            onMouseUp={handleMouseUp}
            onMouseMove={handleMouseMove}
        >
            {myArticles.map((article) => (
            <div
                key={article.articleId}
                onClick={() => handleClickArticle(article.articleId)}
                className="min-w-[200px] border border-gray-300 rounded-lg p-3 cursor-pointer flex-shrink-0 bg-white shadow-md transition-transform duration-200 hover:scale-105"
            >
                <img
                src={article.imageUrl}
                alt={article.title}
                className="w-full h-[120px] object-cover rounded-sm select-none pointer-events-none"
                />
                <h4 className="mt-2 text-base truncate select-none">
                {article.title.length > 25 ? article.title.slice(0, 15) + "..." : article.title}
                </h4>
            </div>
            ))}
        </div>
        ) : (
        <p className="mt-2 text-gray-500">작성한 게시글이 없습니다.</p>
        )}

      {/* 내가 좋아요한 게시글 */}
      <h3 className="mt-8 text-xl font-semibold text-gray-700">내가 좋아요한 게시글</h3>
      {likedArticles.length > 0 ? (
        <div
          className="mt-5 flex gap-4 py-2 cursor-grab select-none overflow-hidden"
          onMouseDown={handleMouseDown}
          onMouseLeave={handleMouseLeave}
          onMouseUp={handleMouseUp}
          onMouseMove={handleMouseMove}
        >
          {likedArticles.map((article) => (
            <div
              key={article.articleId}
              onClick={() => handleClickArticle(article.articleId)}
              className="min-w-[200px] border border-gray-300 rounded-lg p-3 cursor-pointer flex-shrink-0 bg-white shadow-md transition-transform duration-200 hover:scale-105"
            >
              <img
                src={article.imageUrl}
                alt={article.title}
                className="w-full h-[120px] object-cover rounded-sm select-none pointer-events-none"
              />
              <h4 className="mt-2 text-base break-words select-none">
                {article.title.length > 15 ? article.title.slice(0, 15) + "..." : article.title}
              </h4>
            </div>
          ))}
        </div>
      ) : (
        <p className="mt-2 text-gray-500">좋아요한 게시글이 없습니다.</p>
      )}

      {/* 내가 단 댓글 */}
      <h3 className="mt-8 text-xl font-semibold text-gray-700">내가 단 댓글</h3>
      {myComments.length > 0 ? (
        <div className="flex flex-col gap-2 mt-5">
          {myComments.map((comment) => (
            <div
              key={comment.commentId}
              onClick={() => goToArticle(comment.articleId)}
              className="p-3 border border-gray-300 rounded-lg bg-white shadow-sm cursor-pointer hover:bg-gray-50"
            >
              <p className="text-sm break-words">{comment.content}</p>
              <span className="text-xs text-gray-400">
                작성일: {new Date(comment.createdAt).toLocaleString()}
              </span>
            </div>
          ))}
        </div>
      ) : (
        <p className="mt-2 text-gray-500">작성한 댓글이 없습니다.</p>
      )}
    </div>
  );
};

export default MyPageComponent;
