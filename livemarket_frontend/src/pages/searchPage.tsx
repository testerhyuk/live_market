import React, { useState } from "react";
import SearchComponent from "../components/SearchComponent";

export default function SearchPage() {
  const [keyword, setKeyword] = useState("");
  const [searchTerm, setSearchTerm] = useState(""); // 실제 검색에 사용되는 키워드

  const onSearch = () => {
    setSearchTerm(keyword.trim());
  };

  const onInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setKeyword(e.target.value);
  };

  return (
    <div className="max-w-3xl mx-auto p-4">
      <div className="flex space-x-2 mb-4">
        <input
          type="text"
          value={keyword}
          onChange={onInputChange}
          placeholder="검색어를 입력하세요"
          className="flex-grow border border-gray-300 rounded px-3 py-2"
          onKeyDown={(e) => {
            if (e.key === "Enter") {
              onSearch();
            }
          }}
        />
        <button
          onClick={onSearch}
          className="bg-blue-600 text-white px-4 rounded hover:bg-blue-700 cursor-pointer"
        >
          검색
        </button>
      </div>

      {/* 검색어가 있으면 결과 보여주기 */}
      {searchTerm ? (
        <SearchComponent keyword={searchTerm} boardId={"1"} />
      ) : (
        <div className="text-gray-500">검색어를 입력하고 검색 버튼을 눌러주세요.</div>
      )}
    </div>
  );
}
