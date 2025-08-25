import { createSlice, type PayloadAction } from "@reduxjs/toolkit";

export interface Notification {
  id: string; // sessionId 등
  type: "chat" | "video" | "other";
  message: string;
  roomId?: string;
  createdAt: string;
  read?: boolean;
  chatType?: "video-request" | "video-created" | "normal";
  fromMemberId?: string;
  toMemberId?: string;
}

interface NotificationState {
  list: Notification[];
  sseConnected: boolean; // SSE 연결 상태 추가
}

const initialState: NotificationState = {
  list: [],
  sseConnected: false, 
};

export const notificationSlice = createSlice({
  name: "notifications",
  initialState,
  reducers: {
    addNotification: (state, action: PayloadAction<Notification>) => {
      state.list.push(action.payload);
    },
    markAsRead: (state, action: PayloadAction<string>) => {
      state.list = state.list.map(n =>
        n.id === action.payload ? { ...n, read: true } : n
      );
    },
    clearNotifications: (state) => {
      state.list = [];
    },
    setSseConnected: (state, action: PayloadAction<boolean>) => {
      state.sseConnected = action.payload;
    },
  },
});

export const { addNotification, markAsRead, clearNotifications, setSseConnected } = notificationSlice.actions;
export default notificationSlice.reducer;
