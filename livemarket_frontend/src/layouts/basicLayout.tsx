import React, { useEffect, useRef, useState } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faBars, faUser } from "@fortawesome/free-solid-svg-icons";
import logo from "../assets/logo.png";
import { Outlet, useNavigate } from "react-router";
import { useDispatch, useSelector } from "react-redux";
import { logout } from "../redux/authSlice";
import { addNotification, clearNotifications, setSseConnected } from "../redux/notificationSlice";
import type { RootState, AppDispatch } from "../redux/store";

const BasicLayout: React.FC = () => {
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
  const [menuOpen, setMenuOpen] = useState(false);
  const [profileOpen, setProfileOpen] = useState(false);

  const menuRef = useRef<HTMLDivElement>(null);
  const profileRef = useRef<HTMLDivElement>(null);

  const dispatch = useDispatch<AppDispatch>();
  const navigate = useNavigate();

  const { memberId, token } = useSelector((state: RootState) => state.auth);
  const notifications = useSelector((state: RootState) => state.notifications.list);
  const isLoggedIn = !!memberId && !!token;

  const notifySseRef = useRef<EventSource | null>(null);

  // 🔹 SSE 연결 추가
  useEffect(() => {
  if (!memberId) return;

  const sse = new EventSource(`http://localhost:9008/v1/video-call-notify/sse?memberId=${memberId}`);
  notifySseRef.current = sse;

  sse.onopen = () => {
    console.log("SSE 연결 열림", memberId);
    dispatch(setSseConnected(true)); // 연결 상태 true
  };

  sse.onerror = (err) => {
    console.error("SSE 오류", err);
    dispatch(setSseConnected(false)); // 연결 상태 false
    sse.close();
  };

  sse.addEventListener("VIDEO_CALL", (event) => {
    const data = JSON.parse(event.data);
    if (String(data.toMemberId) !== String(memberId)) return;

    dispatch(addNotification({
      id: data.sessionId + "-" + Date.now(),
      type: "chat",
      message: data.message || "새 화상 알림이 도착했습니다",
      roomId: data.sessionId,
      createdAt: new Date(data.createdAt).toISOString(),
      read: false,
      chatType: "video-created",
      fromMemberId: data.fromMemberId,
      toMemberId: data.toMemberId
    }));
  });

  return () => {
    sse.close();
    notifySseRef.current = null;
    dispatch(setSseConnected(false));
  };
}, [dispatch, memberId]);

  // 🔹 메뉴/프로필 외부 클릭 감지
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(event.target as Node)) setMenuOpen(false);
      if (profileRef.current && !profileRef.current.contains(event.target as Node)) setProfileOpen(false);
    };
    if (menuOpen || profileOpen) document.addEventListener("mousedown", handleClickOutside);
    else document.removeEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, [menuOpen, profileOpen]);

  const toggleMenu = () => { setMenuOpen(!menuOpen); setProfileOpen(false); };
  const toggleProfile = () => { setProfileOpen(!profileOpen); setMenuOpen(false); };
  const handleLogout = () => {
    dispatch(logout());
    localStorage.removeItem("token");
    localStorage.removeItem("memberId");
    localStorage.removeItem("nickname");
    setProfileOpen(false);
    setMenuOpen(false);
    navigate("/");
  };
  const handleWrite = () => {
    if (isLoggedIn) navigate("/write");
    else { alert("로그인이 필요한 서비스입니다."); navigate("/login"); }
    setMenuOpen(false);
  };

  return (
    <div className="min-h-svh bg-gray-100">
      <div className="mx-auto w-full max-w-[430px] min-h-svh bg-white shadow-xl relative">
        <header className="sticky top-0 z-50 h-14 flex items-center justify-between px-4 bg-white shadow">
          {/* 햄버거 메뉴 */}
          <div className="relative" ref={menuRef}>
            <button className="text-[#6E56CF] text-2xl cursor-pointer" aria-label="menu" onClick={toggleMenu}>
              <FontAwesomeIcon icon={faBars} />
            </button>
            <div className={`absolute left-0 top-full mt-2 w-56 bg-white shadow-lg rounded-lg overflow-hidden ${menuOpen ? "" : "hidden"}`}>
              <div className="p-4">
                <button className="block w-full mb-4 bg-[#6E56CF] text-white py-2 rounded cursor-pointer" onClick={handleWrite}>
                  상품 등록하기
                </button>
                <h2 className="text-lg font-semibold mb-6">카테고리</h2>
                <ul className="space-y-2 text-gray-700">
                  <li className="cursor-pointer" onClick={() => { navigate("/category/all"); setMenuOpen(false); }}>전체</li>
                  {categories.map((key) => (
                    <li key={key} className="cursor-pointer" onClick={() => { navigate(`/category/${key}`); setMenuOpen(false); }}>
                      {categoryMap[key]}
                    </li>
                  ))}
                </ul>
              </div>
            </div>
          </div>

          {/* 로고 */}
          <div className="flex-1 flex justify-center">
            <img src={logo} alt="Logo" className="h-8 object-contain cursor-pointer" onClick={() => navigate("/")} />
          </div>

          {/* 프로필 */}
          <div className="relative" ref={profileRef}>
            <button className="text-[#6E56CF] text-2xl cursor-pointer" aria-label="profile" onClick={toggleProfile}>
              <FontAwesomeIcon icon={faUser} className={notifications.some(n => !n.read) ? "text-red-500" : "text-[#6E56CF]"} />
            </button>
            <div className={`absolute right-0 top-full mt-2 w-40 bg-white shadow-lg rounded-lg overflow-hidden ${profileOpen ? "" : "hidden"}`}>
              {isLoggedIn ? (
                <ul className="flex flex-col text-center text-gray-700">
                  <li className="py-2 hover:bg-gray-100 cursor-pointer flex justify-center items-center" onClick={() => { setProfileOpen(false); navigate("/notifications"); }}>
                    <span>알림함</span>
                    {notifications.length > 0 && (
                      <span className="ml-2 bg-red-500 text-white text-xs font-bold px-2 py-0.5 rounded-full">{notifications.length}</span>
                    )}
                  </li>
                  <li className="py-2 hover:bg-gray-100 cursor-pointer" onClick={() => { navigate("/search"); setProfileOpen(false); }}>검색</li>
                  <li className="py-2 hover:bg-gray-100 cursor-pointer" onClick={() => { navigate("/mypage"); setProfileOpen(false); }}>마이페이지</li>
                  <li className="py-2 hover:bg-gray-100 cursor-pointer" onClick={handleLogout}>로그아웃</li>
                </ul>
              ) : (
                <ul className="flex flex-col text-center text-gray-700">
                  <li className="py-2 hover:bg-gray-100 cursor-pointer" onClick={() => { navigate("/search"); setProfileOpen(false); }}>검색</li>
                  <li className="py-2 hover:bg-gray-100 cursor-pointer" onClick={() => { navigate("/login"); setProfileOpen(false); }}>로그인</li>
                  <li className="py-2 hover:bg-gray-100 cursor-pointer" onClick={() => { navigate("/register"); setProfileOpen(false); }}>회원가입</li>
                </ul>
              )}
            </div>
          </div>
        </header>

        <main className="px-4 pb-4">
          <Outlet />
        </main>
      </div>
    </div>
  );
};

export default BasicLayout;
