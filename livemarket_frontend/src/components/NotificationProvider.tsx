import React, { useEffect, useRef } from "react";
import { useDispatch } from "react-redux";
import { addNotification } from "../redux/notificationSlice";

const NotificationProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const dispatch = useDispatch();
  const ws = useRef<WebSocket | null>(null);

  const memberId = localStorage.getItem("memberId") || "";

  useEffect(() => {
    if (!memberId) return;

    let isMounted = true;

    const connect = () => {
      const token = localStorage.getItem("token");
      ws.current = new WebSocket(`ws://localhost:9008/notify?token=${token}`);

      ws.current.onopen = () => console.log("Notification WS 연결 완료");

      ws.current.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data);
          dispatch(addNotification({
            id: data.id,
            type: data.type,
            message: data.content,
            roomId: data.roomId,
            createdAt: new Date().toISOString(),
            read: false,
            fromMemberId: data.senderId,
            toMemberId: data.receiverId,
          }));
        } catch (err) {
          console.error("Notification 메시지 처리 실패", err);
        }
      };

      ws.current.onclose = () => {
        console.warn("Notification WS 종료, 3초 후 재연결");
        if (isMounted) setTimeout(connect, 3000); // 자동 재연결
      };

      ws.current.onerror = (err) => console.error("Notification WS 오류", err);
    };

    connect();

    return () => {
      isMounted = false;
      ws.current?.close();
    };
  }, [dispatch, memberId]);

  return <>{children}</>;
};

export default NotificationProvider;
