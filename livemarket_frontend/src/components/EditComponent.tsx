// src/components/EditComponent.tsx
import { useEffect, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router";
import {
  articleApi,
  getArticleImages,
  getOne,
  updateArticle,
  updateImages,
  type ArticleCreateResponse,
} from "../api/articleApi";

const categoryMap: Record<string, string> = {
  ELECTRONICS: "전자기기",
  CLOTHES: "패션/잡화",
  FURNITURE: "가구",
  SPORTS: "스포츠",
  GAME: "게임/취미",
  BOOK: "도서",
  ETC: "기타",
};
const categories = Object.keys(categoryMap);

const EditComponent: React.FC = () => {
  const { articleId } = useParams<{ articleId: string }>();
  const navigate = useNavigate();

  const editorRef = useRef<HTMLDivElement>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const titleRef = useRef<HTMLInputElement>(null);
  const categoryRef = useRef<HTMLSelectElement>(null);
  const priceRef = useRef<HTMLInputElement>(null);

  const [imageFiles, setImageFiles] = useState<File[]>([]);
  const [existingImages, setExistingImages] = useState<string[]>([]);

  useEffect(() => {
    if (!articleId) return;

    const fetchArticle = async () => {
      try {
        const article: ArticleCreateResponse = await getOne(articleId);
        titleRef.current!.value = article.title;
        categoryRef.current!.value = article.category;
        priceRef.current!.value = String(article.price);
        if (editorRef.current) editorRef.current.innerText = article.content;

        const imgs = await getArticleImages(articleId);
        setExistingImages(imgs);
      } catch (err) {
        console.error(err);
        alert("게시글 로드 실패");
      }
    };

    fetchArticle();
  }, [articleId]);

  const handleImageInsert = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files;
    if (!files || files.length === 0) return;
    setImageFiles((prev) => [...prev, ...Array.from(files)]);
    if (fileInputRef.current) fileInputRef.current.value = "";
  };

  const handleDeleteExistingImage = (url: string) => {
    setExistingImages((prev) => prev.filter((img) => img !== url));
  };

  const handleSubmit = async () => {
  if (!articleId) return;

  const title = titleRef.current?.value.trim() || "";
  const category = categoryRef.current?.value || "";
  const content = editorRef.current?.innerText.trim() || "";
  const price = Number(priceRef.current?.value.trim() || "0");

  if (!title || !content) {
    alert("제목과 내용을 입력하세요.");
    return;
  }

  try {
    // 1️⃣ 글 수정
    await updateArticle(articleId, { title, content, category, price });

    // 2️⃣ 이미지 업데이트
    let newImageUrls: string[] = [];

    // 새 이미지가 있으면 S3에 업로드
    if (imageFiles.length > 0) {
      const filenames = imageFiles.map((f) => f.name);
      const presignedRes = await articleApi.getPresignedUrls(filenames);
      const urls: string[] = presignedRes.urls.map((u: any) => u.preSignedUrl);

      await Promise.all(
        imageFiles.map((file, i) => articleApi.uploadImageToS3(urls[i], file))
      );

      newImageUrls = urls.map((u) => u.split("?")[0]);
    }

    // 기존 이미지 중 남길 이미지
    const remainingImageIds = existingImages;

    // 서버에 한 번에 업데이트 요청
    await updateImages({
        articleId,
        userId: String(localStorage.getItem("memberId")),
        newImageUrls,
        remainingImageIds,
    })

    alert("수정 완료!");
    navigate(`/read/${articleId}`);
  } catch (err) {
    console.error(err);
    alert("게시글 수정 중 오류 발생");
  }
};

  return (
    <div style={{ display: "flex", flexDirection: "column", gap: "10px", marginTop: "10px" }}>
      <select ref={categoryRef}>
        {categories.map((cat) => (
          <option key={cat} value={cat}>
            {categoryMap[cat]}
          </option>
        ))}
      </select>

      <input type="text" placeholder="제목" ref={titleRef} style={{ padding: "8px", fontSize: "16px" }} />

      <div style={{ display: "flex", alignItems: "center", gap: "4px" }}>
        <input type="number" placeholder="가격" ref={priceRef} style={{ padding: "8px", fontSize: "16px", flex: 1 }} />
        <span style={{ fontSize: "16px" }}>원</span>
      </div>

      <div ref={editorRef} contentEditable style={{ border: "1px solid #ccc", padding: "10px", minHeight: "200px" }} />

      <div>
        {/* 기존 이미지 */}
        {existingImages.map((url, idx) => (
          <div key={url + idx} style={{ position: "relative" }}>
            <img src={url} style={{ maxWidth: "100%", marginBottom: "8px" }} />
            <button type="button" onClick={() => handleDeleteExistingImage(url)}
              style={{ position: "absolute", top: 0, right: 0, background: "red", color: "white", border: "none" }}>X</button>
          </div>
        ))}

        {/* 새로 추가할 이미지 */}
        {imageFiles.map((file, idx) => (
          <div key={file.name + idx} style={{ position: "relative" }}>
            <img src={URL.createObjectURL(file)} style={{ maxWidth: "100%", marginBottom: "8px" }} />
            <button type="button" onClick={() => setImageFiles((prev) => prev.filter((f) => f !== file))}
              style={{ position: "absolute", top: 0, right: 0, background: "red", color: "white", border: "none" }}>X</button>
          </div>
        ))}
      </div>

      <input type="file" accept="image/*" ref={fileInputRef} onChange={handleImageInsert} multiple />

      <button onClick={handleSubmit} style={{ marginTop: "10px", padding: "10px", background: "blue", color: "white", border: "none", borderRadius: "5px" }}>
        수정 완료
      </button>
    </div>
  );
};

export default EditComponent;
