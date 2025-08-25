import { createSlice, type PayloadAction } from "@reduxjs/toolkit";

interface AuthState {
  memberId: string | null;
  token: string | null;
  nickname: string | null;
}

const initialState: AuthState = {
  memberId: null,
  token: null,
  nickname: null
};

export const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    login: (state, action: PayloadAction<{ memberId: string; token: string; nickname: string; }>) => {
      state.memberId = action.payload.memberId;
      state.token = action.payload.token;
      state.nickname = action.payload.nickname;

      localStorage.setItem("memberId", action.payload.memberId);
      localStorage.setItem("token", action.payload.token);
      localStorage.setItem("nickname", action.payload.nickname);
    },
    logout: (state) => {
      state.memberId = null;
      state.token = null;
      state.nickname = null;

      localStorage.removeItem("memberId");
      localStorage.removeItem("token");
      localStorage.removeItem("nickname");
    },
  },
});

export const { login, logout } = authSlice.actions;
export default authSlice.reducer;
