import { useRef, useState } from "react";
import { articleApi } from "../api/articleApi";
import { useNavigate } from "react-router";

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

const WriteComponent: React.FC = () => {
  const editorRef = useRef<HTMLDivElement>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const titleRef = useRef<HTMLInputElement>(null);
  const categoryRef = useRef<HTMLSelectElement>(null);
  const navigate = useNavigate();

  const [imageFiles, setImageFiles] = useState<File[]>([]);

  const priceRef = useRef<HTMLInputElement>(null);

  const handleImageInsert = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files;
    if (!files || files.length === 0) return;
    setImageFiles((prev) => [...prev, ...Array.from(files)]);
    if (fileInputRef.current) fileInputRef.current.value = "";
  };

  const handleSubmit = async () => {
    if (!editorRef.current) return;
    const title = titleRef.current?.value.trim() || "";
    const category = categoryRef.current?.value || "";
    const contentText = editorRef.current.innerText.trim();
    const boardId = "1"; // 필요 시 동적으로 선택 가능
    const userId = String(localStorage.getItem("memberId"));
    const price = priceRef.current?.value.trim() || "0";

    if (!title || !contentText) {
      alert("제목과 내용을 입력하세요.");
      return;
    }

    try {
      // 1️⃣ 게시글 생성
      const articleRes = await articleApi.createArticle({
        boardId,
        title,
        content: contentText,
        category,
        price
      });
      const articleId = articleRes.data.articleId;

      // 2️⃣ 이미지 업로드
      if (imageFiles.length > 0) {
        const filenames = imageFiles.map((f) => f.name);
        const presignedRes = await articleApi.getPresignedUrls(filenames);
        const urls: string[] = presignedRes.urls.map((u: any) => u.preSignedUrl);

        // S3에 업로드
        await Promise.all(
          imageFiles.map((file, i) =>
            articleApi.uploadImageToS3(urls[i], file)
          )
        );

        // 업로드된 파일 URL (쿼리스트링 제거)
        const uploadedImageUrls = urls.map((u) => u.split("?")[0]);

        // 3️⃣ 이미지 메타데이터 저장
        await articleApi.saveImageMeta({
          articleId,
          userId,
          imageUrls: uploadedImageUrls,
        });
      }

      alert("작성 완료!");
      navigate("/");
    } catch (err) {
      console.error(err);
      alert("게시글 작성 중 오류가 발생했습니다.");
    }
  };

  return (
    <div style={{ display: "flex", flexDirection: "column", gap: "10px", marginTop: "10px" }}>
      <select ref={categoryRef} defaultValue={categories[0]}>
        {categories.map((cat) => (
          <option key={cat} value={cat}>
            {categoryMap[cat]}
          </option>
        ))}
      </select>

      <input
        type="text"
        placeholder="제목"
        ref={titleRef}
        style={{ padding: "8px", fontSize: "16px" }}
      />

      {/* 가격 입력 */}
      <div style={{ display: "flex", alignItems: "center", gap: "4px" }}>
        <input
          type="number"
          placeholder="가격"
          ref={priceRef} // 새로 useRef 추가 필요
          style={{ padding: "8px", fontSize: "16px", flex: 1 }}
        />
        <span style={{ fontSize: "16px" }}>원</span>
      </div>

      <div
        ref={editorRef}
        contentEditable
        style={{
          border: "1px solid #ccc",
          padding: "10px",
          minHeight: "200px",
        }}
      ></div>

      {/* 이미지 미리보기 */}
      <div>
        {imageFiles.map((file, idx) => (
          <div key={file.name + idx} style={{ position: "relative" }}>
            <img
              src={URL.createObjectURL(file)}
              style={{ maxWidth: "100%", marginBottom: "8px" }}
            />
            <button
              type="button"
              onClick={() =>
                setImageFiles((prev) => prev.filter((f) => f !== file))
              }
              style={{
                position: "absolute",
                top: 0,
                right: 0,
                background: "red",
                color: "white",
                border: "none",
              }}
            >
              X
            </button>
          </div>
        ))}
      </div>

      <input
        type="file"
        accept="image/*"
        ref={fileInputRef}
        onChange={handleImageInsert}
        multiple
      />

      <button
        onClick={handleSubmit}
        style={{
          marginTop: "10px",
          padding: "10px",
          background: "blue",
          color: "white",
          border: "none",
          borderRadius: "5px",
        }}
      >
        작성 완료
      </button>
    </div>
  );
};

export default WriteComponent;
