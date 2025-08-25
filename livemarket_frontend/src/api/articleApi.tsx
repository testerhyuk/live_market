// src/api/articleApi.tsx
import axios from "axios";

const API_SERVER_HOST = "http://localhost:9008"; // API 주소

export interface ArticleCreateRequest {
  title: string;
  content: string;
  boardId: string;
  category: string;
  price: number;
}

export interface ArticleCreateResponse {
  articleId: string;
  title: string;
  content: string;
  boardId: string;
  writerId: string;
  category: string;
  createdAt: string;
  modifiedAt: string;
  price: number;
}

export interface Article {
    articleId : string
    boardId: string
    writerId: string
    title: string
    content: string
    category: string
    createdAt: string
    modifiedAt: string
    pageSize: number
    thumbnailUrl: string
}

export const getLikedArticlesByUserId = async (userId: string): Promise<Article[]> => {
  const token = localStorage.getItem("token");
  if (!token) throw new Error("로그인이 필요합니다");

  try {
    // 1️⃣ 좋아요한 글 ID만 가져오기
    const res = await axios.get<Article[]>(`${API_SERVER_HOST}/v1/article-likes/articles/member/${userId}`, {
      headers: { Authorization: `Bearer ${token}` },
      withCredentials: true,
    });

    const likes: { articleId: string }[] = res.data; // ArticleLikeResponse 배열에서 articleId만 사용

    // 2️⃣ 각 좋아요 글의 실제 게시글 정보 가져오기
    const articlesWithImages = await Promise.all(
      likes.map(async (like) => {
        const article = await getArticlesByUserId(userId); // 원래 getArticlesByUserId 호출
        const matchedArticle = article.find(a => a.articleId === like.articleId);

        const images = await getArticleImages(String(like.articleId));
        return {
          articleId: like.articleId,
          title: matchedArticle?.title ?? "제목 없음",
          imageUrl: images[0] || "/gray-background.png",
        };
      })
    );

    return articlesWithImages;
  } catch (err: any) {
    console.error("좋아요 게시글 API 호출 실패", err);
    return [];
  }
};

export const getArticlesByUserId = async (userId: string): Promise<Article[]> => {
  const token = localStorage.getItem("token");
  if (!token) throw new Error("로그인이 필요합니다");

  const res = await axios.get(`${API_SERVER_HOST}/v1/articles/member/${userId}`, {
    headers: { Authorization: `Bearer ${token}` },
    withCredentials: true,
  });

  return res.data as Article[];
};

export const getRankingArticles = async (dateStr: string): Promise<Article[]> => {
  const res = await axios.get(`${API_SERVER_HOST}/v1/hot-articles/articles/date/${dateStr}`);
  return res.data as Article[];
};

export const getAll = async (lastArticleId?: string): Promise<Article[]> => {
    const params: Record<string, string | number> = {
        boardId : "1",
        pageSize : "7"
    };

    if (lastArticleId) {
        params.lastArticleId = lastArticleId;
    }

    const res = await axios.get(`${API_SERVER_HOST}/v1/articles/infinite-scroll`, { params });

    return res.data;
}

export const getByCategory = async (
  category: string,
  lastArticleId?: string | null
): Promise<Article[]> => {
  const params: Record<string, string | number> = {
    boardId: "1",
    category,
    pageSize: 7,
  };
  if (lastArticleId) {
    params.lastArticleId = lastArticleId;
  }

  const res = await axios.get(`${API_SERVER_HOST}/v1/articles/category`, {
    params,
  });
  
  return res.data;
};

export const searchArticles = async (
  keyword: string,
  lastArticleId?: string | null
): Promise<Article[]> => {
  const params: Record<string, string | number> = {
    boardId: "1",
    keyword,
    pageSize: 7,
  };
  if (lastArticleId) {
    params.lastArticleId = lastArticleId;
  }

  const res = await axios.get(`${API_SERVER_HOST}/v1/articles/search`, { params });
  return res.data;
};

export const updateImages = async (params: {
  articleId: string;
  userId: string;
  newImageUrls: string[];
  remainingImageIds: string[];
}) => {
  const token = localStorage.getItem("token");
  if (!token) throw new Error("로그인이 필요합니다");

  return axios.put(`${API_SERVER_HOST}/v1/article-images`, params, {
    headers: { Authorization: `Bearer ${token}` },
    withCredentials: true,
  });
};

export const updateArticle = async (
  articleId: string,
  data: { title: string; content: string; category: string; price: number; }
) => {
  const token = localStorage.getItem("token");
  if (!token) throw new Error("로그인이 필요합니다");

  return axios.put(`${API_SERVER_HOST}/v1/articles/${articleId}`, data, {
    headers: { Authorization: `Bearer ${token}` },
    withCredentials: true,
  });
};

export const deleteArticleImages = async (articleId: string) => {
  const token = localStorage.getItem("token");
  const memberId = localStorage.getItem("memberId")

  if (!token) throw new Error("로그인이 필요합니다");

  await axios.delete(`${API_SERVER_HOST}/v1/article-images/article/${articleId}`, {
    headers: { Authorization: `Bearer ${token}`, "X-User-Id": memberId },
    withCredentials: true,
  });
};

export const deleteArticle = async (articleId: string) => {
  const token = localStorage.getItem("token");
  if (!token) throw new Error("로그인이 필요합니다");

  // 게시글 삭제 요청
  await axios.delete(`${API_SERVER_HOST}/v1/articles/${articleId}`, {
    headers: { Authorization: `Bearer ${token}` },
    withCredentials: true,
  });
};

/** 좋아요 상태 확인 */
export const getArticleLikeStatus = async (articleId: string, userId: string): Promise<boolean> => {
  const token = localStorage.getItem("token");
  if (!token) throw new Error("로그인이 필요합니다");

  try {
    const res = await axios.post(
      `${API_SERVER_HOST}/v1/article-likes/articles/${articleId}/status`,
      null,
      { headers: { 
        Authorization: `Bearer ${token}`,
        "X-User-Id" : userId
       }, withCredentials: true }
    );
    // 서버에서 likeStatus boolean 반환
    return res.data?.likeStatus ?? false;
  } catch (err: any) {
    if (err.response?.status === 404) return false; // 데이터 없으면 좋아요 안 함
    throw err;
  }
};

/** 좋아요 개수 조회 */
export const getLikeCount = async (articleId: string): Promise<number> => {
  try {
    const res = await axios.get(`${API_SERVER_HOST}/v1/article-likes/articles/${articleId}/count`);
    return res.data ?? 0;
  } catch (err: any) {
    if (err.response?.status === 404) return 0;
    throw err;
  }
};

/** 좋아요 */
export const likeArticle = async (articleId: string) => {
  const token = localStorage.getItem("token");
  const memberId = localStorage.getItem("memberId")
  if (!token) throw new Error("로그인이 필요합니다");

  try {
    await axios.post(
      `${API_SERVER_HOST}/v1/article-likes/articles/${articleId}`,
      null,
      { headers: { 
        Authorization: `Bearer ${token}`,
        "X-User-Id": memberId
      }, withCredentials: true }
    );
  } catch (err: any) {
    throw err;
  }
};

/** 좋아요 취소 */
export const unlikeArticle = async (articleId: string) => {
  const token = localStorage.getItem("token");
  const memberId = localStorage.getItem("memberId")

  if (!token) throw new Error("로그인이 필요합니다");

  try {
    await axios.delete(
      `${API_SERVER_HOST}/v1/article-likes/articles/${articleId}`,
      { headers: { 
        Authorization: `Bearer ${token}`,
        "X-User-Id": memberId
       }, withCredentials: true }
    );
  } catch (err: any) {
    throw err;
  }
};

export const increaseViewCount = async (articleId: string) => {
  const token = localStorage.getItem("token");
  const memberId = localStorage.getItem("memberId");

  if (!token) return; // 로그인 안 되어 있으면 그냥 반환

  const res = await axios.post(
    `${API_SERVER_HOST}/v1/article-views/articles/${articleId}`,
    null,
    {
      headers: {
        Authorization: `Bearer ${token}`,
        "X-User-Id": memberId
      },
      withCredentials: true,
    }
  );
  return res.data as number; // 증가 후 조회수
};

export const getViewCount = async (articleId: string) => {
  const res = await axios.get(`${API_SERVER_HOST}/v1/article-views/articles/${articleId}/count`, { withCredentials: true });
  return res.data as number;
};

export const getArticleImages = async (articleId: string): Promise<string[]> => {
  const token = localStorage.getItem("token");
  const res = await axios.get(`${API_SERVER_HOST}/v1/article-images/article/${articleId}`, {
    headers: { Authorization: `Bearer ${token}` },
    withCredentials: true,
  });

  return res.data;
};

export const getOne = async (articleId : string) => {
    const res = await axios.get(`${API_SERVER_HOST}/v1/articles/${articleId}`)
    
    return res.data
}

export const articleApi = {
  // 1️⃣ 게시글 생성
  createArticle: async (req: ArticleCreateRequest) => {
    const memberId = localStorage.getItem("memberId");

    return axios.post<ArticleCreateResponse>(
      `${API_SERVER_HOST}/v1/articles`,
      req,
      {
        headers: {
          "Content-Type": "application/json",
          "X-User-Id": memberId ?? "",
        },
      }
    );
  },

  // 2️⃣ Presigned URL 발급
  getPresignedUrls: async (filenames: string[]) => {
    const res = await axios.post(`${API_SERVER_HOST}/v1/article-images/presigned-urls`, filenames);
    
    return res.data; // { urls: [{ fileName, preSignedUrl }] }
  },

  // 3️⃣ S3 업로드
  uploadImageToS3: async (presignedUrl: string, file: File) => {
    return axios.put(presignedUrl, file, {
      headers: { "Content-Type": file.type },
    });
  },

  // 4️⃣ 이미지 메타데이터 저장
  saveImageMeta: async (params: {
    articleId: string;
    userId: string;
    imageUrls: any[]; // presigned 데이터나 순수 URL 배열 모두 허용
    }) => {
    // presigned 객체 배열이면 -> URL 문자열 배열로 변환
    const urls = params.imageUrls.map((u: any) =>
        typeof u === "string" ? u : u.preSignedUrl.split("?")[0]
    );

    return axios.post(`${API_SERVER_HOST}/v1/article-images`, {
        articleId: params.articleId,
        userId: params.userId,
        imageUrls: urls,
    });
    },
};
