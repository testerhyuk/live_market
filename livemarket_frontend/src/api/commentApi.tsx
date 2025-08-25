import axios from "axios";

export const API_SERVER_HOST = 'http://localhost:9008';
const prefix = `${API_SERVER_HOST}/v1/comments`;

export interface CommentResponse {
  commentId: string;
  articleId: string;
  parentCommentId: string | null;
  writerId: string;
  content: string;
  createdAt: string;
  deleted: boolean;
}

export const getCommentsByUserId = async (userId: string): Promise<CommentResponse[]> => {
  const token = localStorage.getItem("token");
  if (!token) throw new Error("로그인이 필요합니다");

  const res = await axios.get(`${prefix}/member/${userId}`, {
    headers: { Authorization: `Bearer ${token}` },
    withCredentials: true,
  });

  return res.data as CommentResponse[];
};

// 댓글 목록 조회 (무한 스크롤용)
export const getComments = async (
  articleId: string,
  lastParentCommentId?: string,
  lastCommentId?: string,
  pageSize = 20
): Promise<CommentResponse[]> => {
  const res = await axios.get(`${prefix}/infinite-scroll`, {
    params: { articleId, lastParentCommentId, lastCommentId, pageSize },
  });
  return res.data;
};

// 댓글 생성
export const createComment = async (
  articleId: string,
  content: string,
  writerId: string,
  parentCommentId?: string
) => {
    const memberId = localStorage.getItem("memberId")

  const res = await axios.post(`${prefix}`, {
    articleId,
    writerId,
    parentCommentId: parentCommentId || null,
    content,
  },{
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
        "X-User-Id" : memberId
      },
      withCredentials: true,
    });
  return res.data;
};

// 댓글 수정
export const updateComment = async (commentId: string, content: string) => {
    const memberId = localStorage.getItem("memberId")
  const res = await axios.put(`${prefix}/${commentId}`, { comment: content },
    {
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
        "X-User-Id" : memberId
      },
      withCredentials: true,
    }
  );
  return res.data;
};

// 댓글 삭제
export const deleteCommentApi = async (commentId: string) => {
    const memberId = localStorage.getItem("memberId")
  await axios.delete(`${prefix}/${commentId}`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
        "X-User-Id" : memberId
      },
      withCredentials: true,
    });
};

export const getCommentCount = async (articleId: string) => {
  const res = await axios.get(`${prefix}/articles/${articleId}/count`);
  
  return res.data;
};