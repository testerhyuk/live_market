import { useState } from "react";
import { useNavigate } from "react-router";
import { useDispatch } from "react-redux";
import { login as loginAction } from "../redux/authSlice";
import { login, type LoginRequest } from "../api/memberApi";

export default function LoginComponent() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const handleLogin = async () => {
    if (!email || !password) {
      alert("이메일과 비밀번호를 입력해주세요");
      return;
    }

    const data: LoginRequest = { email, password };

    try {
      const res = await login(data);

      // Redux 상태 업데이트
      dispatch(
        loginAction({
          memberId: res.memberId,
          token: res.token,
          nickname: res.nickname
        })
      );

      alert("로그인 성공");
      navigate("/");
    } catch (err: any) {
      alert(err?.response?.data || "로그인 실패");
    }
  };

  return (
    <div className="max-w-md mx-auto mt-10 p-6 border rounded shadow">
      <h2 className="text-xl font-bold mb-4">로그인</h2>
      <input
        type="email"
        placeholder="이메일"
        className="w-full p-2 mb-3 border rounded"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
      />
      <input
        type="password"
        placeholder="비밀번호"
        className="w-full p-2 mb-3 border rounded"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />
      <button
        className="w-full bg-blue-500 text-white p-2 rounded cursor-pointer"
        onClick={handleLogin}
      >
        로그인
      </button>
    </div>
  );
}
