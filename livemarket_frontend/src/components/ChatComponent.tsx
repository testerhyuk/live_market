import React, { useEffect, useRef, useState } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPaperPlane } from "@fortawesome/free-solid-svg-icons";
import { useDispatch } from "react-redux";
import { addNotification } from "../redux/notificationSlice";
import { type ChatMessage, getMessages } from "../api/chatApi";

interface Props {
  roomId: string;
  senderId: string;
  receiverId: string;
}

const WS_URL = "ws://localhost:9008/ws/chat";
const NOTIFY_WS_URL = "ws://localhost:9008/notify";

const ChatComponent: React.FC<Props> = ({ roomId, senderId, receiverId }) => {
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [input, setInput] = useState("");
  const [chatConnected, setChatConnected] = useState(false);

  const chatWs = useRef<WebSocket | null>(null);
  const notifyWs = useRef<WebSocket | null>(null);
  const dispatch = useDispatch();

  const memberId = localStorage.getItem("memberId")

  // -------------------------------
  // 1️⃣ 기존 채팅 메시지 불러오기
  // -------------------------------
  useEffect(() => {
    getMessages(roomId).then(setMessages);
  }, [roomId]);

  // -------------------------------
  // 2️⃣ 채팅 WebSocket 연결
  // -------------------------------
  useEffect(() => {
    const token = localStorage.getItem("token");
    chatWs.current = new WebSocket(`${WS_URL}?roomId=${roomId}&senderId=${memberId}&token=${token}`);

    chatWs.current.onopen = () => setChatConnected(true);

    chatWs.current.onmessage = (event) => {
      const msg: any = JSON.parse(event.data);

      if (msg.roomId === roomId) {
        setMessages((prev) => [...prev, msg]);
      }
    };

    chatWs.current.onclose = () => setChatConnected(false);
    chatWs.current.onerror = (err) => console.error("Chat WS Error", err);

    return () => chatWs.current?.close();
  }, [roomId, senderId]);

  // -------------------------------
  // 3️⃣ 알림 WebSocket 연결
  // -------------------------------
  useEffect(() => {
    const token = localStorage.getItem("token");
    notifyWs.current = new WebSocket(`${NOTIFY_WS_URL}?memberId=${receiverId}&token=${token}`);

    notifyWs.current.onmessage = (event) => {
      console.log("event : ", event.data)
      const data = JSON.parse(event.data);
      dispatch(addNotification({
        id: data.id,
        type: data.type,
        message: data.content,
        roomId: data.roomId,
        createdAt: new Date().toISOString(),
        read: false,
        fromMemberId: data.senderId,  // 필요하면
        toMemberId: data.receiverId,      // 여기 추가
      }));
    };

    return () => notifyWs.current?.close();
  }, [senderId]);

  // -------------------------------
  // 4️⃣ 메시지 전송
  // -------------------------------
  const handleSend = () => {
    if (!input.trim() || !chatWs.current || chatWs.current.readyState !== WebSocket.OPEN) return;

    const nickname = localStorage.getItem("nickname");
    const msg: ChatMessage = {
      roomId,
      senderId,
      senderNickname: nickname || "",
      receiverId,
      message: input,
      type: "TALK",
    };

    chatWs.current.send(JSON.stringify(msg));
    setInput("");
  };

  // -------------------------------
  // 5️⃣ JSX
  // -------------------------------
  return (
    <div className="max-w-md mx-auto border p-4 rounded shadow">
      {/* 채팅 영역 */}
      <div className="h-64 overflow-y-auto border-b mb-2 p-2">
        {messages.map((msg, idx) => (
          <div key={idx} className={`mb-1 p-1 rounded ${msg.senderId === senderId ? "bg-blue-200 text-right" : "bg-gray-200"}`}>
            <span className="text-sm font-semibold">{msg.senderNickname}: </span>{msg.message}
          </div>
        ))}
      </div>

      {/* 입력창 & 전송 버튼 */}
      <div className="flex gap-2">
        <input
          className="flex-1 border rounded p-1"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && handleSend()}
        />
        <button
          className={`px-3 rounded ${chatConnected ? "bg-blue-500 text-white" : "bg-gray-300 text-gray-500"}`}
          onClick={handleSend}
          disabled={!chatConnected}
        >
          <FontAwesomeIcon icon={faPaperPlane} />
        </button>
      </div>
    </div>
  );
};

export default ChatComponent;
