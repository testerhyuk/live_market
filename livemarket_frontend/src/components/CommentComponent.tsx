import { useEffect, useState } from "react";
import {
  getComments,
  createComment,
  updateComment,
  deleteCommentApi,
  type CommentResponse,
} from "../api/commentApi";
import { fetchNickname } from "../api/memberApi";

interface Props {
  articleId: string;
}

interface CommentWithNickname extends CommentResponse {
  nickname: string;
  showReplies?: boolean;
  replies?: CommentWithNickname[];
}

const CommentComponent = ({ articleId }: Props) => {
  const memberId = localStorage.getItem("memberId");
  const [comments, setComments] = useState<CommentWithNickname[]>([]);
  const [newComment, setNewComment] = useState("");
  const [editingId, setEditingId] = useState<string | null>(null);
  const [editingContent, setEditingContent] = useState("");
  const [replyTexts, setReplyTexts] = useState<Record<string, string>>({});

  useEffect(() => {
    const fetchCommentsWithNicknames = async () => {
      if (!articleId) return;

      const data = await getComments(articleId);

      // 각 comment마다 닉네임 가져오기
      const commentMap: Record<string, CommentWithNickname> = {};
      const topComments: CommentWithNickname[] = [];

      // 비동기 닉네임 가져오기
      await Promise.all(
        data.map(async (c) => {
          const nickname = await fetchNickname(c.writerId) || "알 수 없음";

          const comment: CommentWithNickname = {
            ...c,
            nickname,
            showReplies: false,
            replies: [],
          };

          commentMap[comment.commentId] = comment;
        })
      );

      // 댓글 구조 만들기 (답글 포함)
      data.forEach(c => {
        if (!c.parentCommentId || c.parentCommentId === c.commentId) {
          topComments.push(commentMap[c.commentId]);
        } else if (commentMap[c.parentCommentId]) {
          commentMap[c.parentCommentId].replies?.push(commentMap[c.commentId]);
        }
      });

      setComments(topComments);
    };

    fetchCommentsWithNicknames();
  }, [articleId]);


  const handleCreate = async (parentCommentId?: string) => {
    if (!memberId) { alert("로그인이 필요합니다."); return; }
    const content = parentCommentId ? replyTexts[parentCommentId] : newComment;
    if (!content?.trim()) return;

    const created = await createComment(articleId, content, memberId, parentCommentId);
    const newCommentObj: CommentWithNickname = {
      ...created,
      nickname: localStorage.getItem("nickname") || "알 수 없음",
      showReplies: false,
      replies: [],
    };

    if (parentCommentId) {
      setComments(prev =>
        prev.map(c =>
          c.commentId === parentCommentId
            ? { ...c, replies: [newCommentObj, ...(c.replies || [])], showReplies: true }
            : c
        )
      );
      setReplyTexts(prev => ({ ...prev, [parentCommentId]: "" }));
    } else {
      setComments(prev => [newCommentObj, ...prev]);
      setNewComment("");
    }
  };

  const handleEdit = (comment: CommentWithNickname) => {
    setEditingId(comment.commentId);
    setEditingContent(comment.content);
  };

  const handleUpdate = async () => {
    if (!editingId || !editingContent.trim()) return;
    const updated = await updateComment(editingId, editingContent);
    setComments(prev => prev.map(c => updateCommentInTree(c, updated)));
    setEditingId(null);
    setEditingContent("");
  };

  const updateCommentInTree = (comment: CommentWithNickname, updated: CommentResponse): CommentWithNickname => {
    if (comment.commentId === updated.commentId) {
      return { ...updated, nickname: comment.nickname, replies: comment.replies, showReplies: comment.showReplies };
    }
    return { ...comment, replies: comment.replies?.map(r => updateCommentInTree(r, updated)) };
  };

  const handleDelete = async (commentId: string) => {
    if (!window.confirm("댓글을 삭제하시겠습니까?")) return;
    await deleteCommentApi(commentId);

    setComments(prev => prev.map(c => deleteCommentInTree(c, commentId)).filter(c => c !== null) as CommentWithNickname[]);
  };

  const deleteCommentInTree = (comment: CommentWithNickname, targetId: string): CommentWithNickname | null => {
    if (comment.commentId === targetId) {
      if (!comment.replies || comment.replies.length === 0) return null;
      return { ...comment, content: "삭제 처리되었습니다", deleted: true };
    }
    return { ...comment, replies: comment.replies?.map(r => deleteCommentInTree(r, targetId)).filter(r => r !== null) as CommentWithNickname[] };
  };

  const toggleReplies = (commentId: string) => {
    setComments(prev => prev.map(c => c.commentId === commentId ? { ...c, showReplies: !c.showReplies } : c));
  };

  return (
    <div className="mt-6">
      {memberId && (
        <div className="mb-4">
          <textarea className="w-full p-2 border rounded" value={newComment} onChange={e => setNewComment(e.target.value)} placeholder="댓글을 입력하세요." />
          <button className="mt-2 px-3 py-1 bg-blue-500 text-white rounded" onClick={() => handleCreate()}>등록</button>
        </div>
      )}

      {comments.map(comment => (
        <div key={comment.commentId} className="border-b py-2">
          <div className="flex justify-between items-center">
            <div>
              <span className="font-bold">{comment.nickname}</span>
              <span className="ml-2 text-gray-500 text-sm">{new Date(comment.createdAt).toLocaleString()}</span>
            </div>
            {memberId === comment.writerId && !comment.deleted && (
              <div className="flex gap-2">
                <button className="px-2 py-1 bg-yellow-500 text-white rounded" onClick={() => handleEdit(comment)}>수정</button>
                <button className="px-2 py-1 bg-red-500 text-white rounded" onClick={() => handleDelete(comment.commentId)}>삭제</button>
              </div>
            )}
          </div>

          {editingId === comment.commentId ? (
            <>
              <textarea className="w-full p-1 border rounded mt-2" value={editingContent} onChange={e => setEditingContent(e.target.value)} />
              <div className="flex gap-2 mt-1">
                <button className="px-2 py-1 bg-green-500 text-white rounded" onClick={handleUpdate}>저장</button>
                <button className="px-2 py-1 bg-gray-400 text-white rounded" onClick={() => setEditingId(null)}>취소</button>
              </div>
            </>
          ) : (
            <p className="mt-1">{comment.content}</p>
          )}

          <button className="text-sm text-blue-500 mt-1 flex items-center gap-1" onClick={() => toggleReplies(comment.commentId)}>
            {comment.showReplies ? "▼" : "▶"} 답글 {comment.replies?.length || 0}
          </button>

          {comment.showReplies && (
            <div className="ml-6 mt-2">
              {memberId && !comment.deleted && (
                <div className="mb-2">
                  <textarea className="w-full p-1 border rounded mb-1" value={replyTexts[comment.commentId] || ""} onChange={e => setReplyTexts(prev => ({ ...prev, [comment.commentId]: e.target.value }))} placeholder="답글을 입력하세요." />
                  <button className="px-2 py-1 bg-blue-500 text-white rounded" onClick={() => handleCreate(comment.commentId)}>등록</button>
                </div>
              )}

              {comment.replies?.map(reply => (
                <div key={reply.commentId} className="ml-4 mt-2 p-2 border-l border-gray-300">
                  <div className="flex justify-between items-center">
                    <span className="font-bold">{reply.nickname}</span>
                    <span className="ml-2 text-gray-500 text-xs">{new Date(reply.createdAt).toLocaleString()}</span>
                    {memberId === reply.writerId && !reply.deleted && (
                      <div className="flex gap-2">
                        <button className="px-2 py-1 bg-yellow-500 text-white rounded" onClick={() => handleEdit(reply)}>수정</button>
                        <button className="px-2 py-1 bg-red-500 text-white rounded" onClick={() => handleDelete(reply.commentId)}>삭제</button>
                      </div>
                    )}
                  </div>

                  {editingId === reply.commentId ? (
                    <>
                      <textarea className="w-full p-1 border rounded mt-1" value={editingContent} onChange={e => setEditingContent(e.target.value)} />
                      <div className="flex gap-2 mt-1">
                        <button className="px-2 py-1 bg-green-500 text-white rounded" onClick={handleUpdate}>저장</button>
                        <button className="px-2 py-1 bg-gray-400 text-white rounded" onClick={() => setEditingId(null)}>취소</button>
                      </div>
                    </>
                  ) : (
                    <p className="mt-1">{reply.content}</p>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>
      ))}
    </div>
  );
};

export default CommentComponent;
