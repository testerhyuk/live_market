import React, { useEffect, useRef, useState } from "react";
import { useLocation, useParams } from "react-router";
import {
  createVideoCallSession,
  getMessages,
  joinVideoCallSession, // 🔹 추가
  type ChatMessage,
} from "../api/chatApi";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faVideo, faPaperPlane } from "@fortawesome/free-solid-svg-icons";
import { useDispatch } from "react-redux";
import { addNotification } from "../redux/notificationSlice";
import { Client, type IMessage } from "@stomp/stompjs";
import { OpenVidu, Publisher, Session } from "openvidu-browser";
import SockJS from "sockjs-client";

interface LocationState {
  senderId: string;
  receiverId?: string;
}

const WS_URL = "ws://localhost:9008/ws/chat";
const NOTIFY_WS_URL = "ws://localhost:9008/notify";

const VideoCallComponent: React.FC = () => {
  const { roomId } = useParams<{ roomId: string }>();
  const location = useLocation();
  const { senderId, receiverId: locationReceiverId } = location.state as LocationState;

  const localVideoRef = useRef<HTMLVideoElement>(null);
  const remoteVideoRef = useRef<HTMLVideoElement>(null);
  const [joined, setJoined] = useState(false);

  // OpenVidu 상태
  const [ov, setOv] = useState<OpenVidu | null>(null);
  const [session, setSession] = useState<Session | null>(null);
  const [publisher, setPublisher] = useState<Publisher | null>(null);

  // 채팅 관련 state
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [input, setInput] = useState("");
  const [chatConnected, setChatConnected] = useState(false);

  const [isHost, setIsHost] = useState(false);
  const chatWs = useRef<WebSocket | null>(null);
  const notifyWs = useRef<WebSocket | null>(null);

  const dispatch = useDispatch();
  const memberId = localStorage.getItem("memberId")!;
  const token = localStorage.getItem("token")!;

  // -------------------------------
  // 1️⃣ 기존 채팅 메시지 로드
  // -------------------------------
  useEffect(() => {
    if (!roomId) return;
    getMessages(roomId).then(setMessages);
  }, [roomId]);

  // -------------------------------
  // 2️⃣ 채팅 WebSocket 연결
  // -------------------------------
  useEffect(() => {
    if (!roomId) return;

    chatWs.current?.close();
    chatWs.current = new WebSocket(`${WS_URL}?roomId=${roomId}&senderId=${memberId}&token=${token}`);

    chatWs.current.onopen = () => setChatConnected(true);
    chatWs.current.onmessage = (event) => {
      const msg: ChatMessage = JSON.parse(event.data);
      if (msg.roomId === roomId) setMessages((prev) => [...prev, msg]);
    };
    chatWs.current.onclose = () => setChatConnected(false);
    chatWs.current.onerror = (err) => console.error("Chat WS Error", err);

    return () => chatWs.current?.close();
  }, [roomId, memberId, token]);

  // -------------------------------
  // 3️⃣ 알림 WebSocket 연결
  // -------------------------------
  useEffect(() => {
    notifyWs.current = new WebSocket(`${NOTIFY_WS_URL}?memberId=${memberId}&token=${token}`);

    notifyWs.current.onmessage = (event) => {
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
    };

    notifyWs.current.onclose = () => console.warn("Notification WS 종료");
    notifyWs.current.onerror = (err) => console.error("Notification WS 오류", err);

    return () => notifyWs.current?.close();
  }, [memberId, token]);

  // -------------------------------
  // 4️⃣ 화상채팅 시작 버튼 클릭 (방송자)
  // -------------------------------
  const handleStartVideoCall = async () => {
    if (!roomId) return;
    setIsHost(true);

    try {
      let receiverId = locationReceiverId;
      if (!receiverId && roomId) {
        const ids = roomId.split("_");
        receiverId = ids.find((id) => id !== memberId) || "";
      }

      const sessionData = await createVideoCallSession(roomId, memberId, receiverId);

      // OpenVidu 세션 초기화
      const OV = new OpenVidu();
      const session = OV.initSession();

      session.on("streamCreated", (event) => {
        const subscriber = session.subscribe(event.stream, undefined);
        subscriber.addVideoElement(remoteVideoRef.current!);
      });

      await session.connect(sessionData.token, { clientData: memberId });

      const publisher = OV.initPublisher(undefined, {
        audioSource: undefined,
        videoSource: undefined,
        publishAudio: true,
        publishVideo: true,
        resolution: "640x480",
        frameRate: 30,
      });

      // 로컬 비디오 DOM에 붙이기
      publisher.addVideoElement(localVideoRef.current!);

      session.publish(publisher);

      setOv(OV);
      setSession(session);
      setPublisher(publisher);
      setJoined(true);
    } catch (err) {
      console.error("Video call start failed:", err);
    }
  };

  // -------------------------------
  // 5️⃣ 채팅 메시지 전송
  // -------------------------------
  const handleSend = () => {
    if (!input.trim() || !chatWs.current || chatWs.current.readyState !== WebSocket.OPEN)
      return;

    const nickname = localStorage.getItem("nickname") || "";

    let receiverId = locationReceiverId || "";
    if (!receiverId && roomId) {
      const ids = roomId.split("_");
      receiverId = ids.find((id) => id !== memberId) || "";
    }

    const msg: ChatMessage = {
      roomId: roomId!,
      senderId: memberId,
      senderNickname: nickname,
      receiverId,
      message: input,
      type: "TALK",
      source: "VIDEO_CALL",
    };

    chatWs.current.send(JSON.stringify(msg));
    setInput("");
  };

  // -------------------------------
  // 6️⃣ 시청자(B)가 VIDEO_CALL_SESSION_CREATED 받으면 자동 join
  // -------------------------------
  useEffect(() => {
    if (!memberId || !token) return;

    const stompClient = new Client({
      webSocketFactory: () => new SockJS('http://localhost:9021/ws/video-calls'),
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      reconnectDelay: 5000,
      debug: console.log,
    });

    // 연결 성공 시 실행
    stompClient.onConnect = () => {
      console.log("STOMP connected!");

      // 구독
      stompClient.subscribe(`/topic/video-calls/${memberId}`, async (message: IMessage) => {
        const data = JSON.parse(message.body);
        
        if (data.sessionId && !joined) {

          const receiverId = data.receiverId

          if (memberId === receiverId) {
            try {
              // 🔹 joinVideoCallSession API 호출
              const sessionData = await joinVideoCallSession({
                sessionId: data.sessionId,
                receiverId: memberId
              });

              const OV = new OpenVidu();
              const session = OV.initSession();

              session.on("streamCreated", (event) => {
                const subscriber = session.subscribe(event.stream, undefined);
                subscriber.addVideoElement(remoteVideoRef.current!);
              });

              await session.connect(sessionData.token, { clientData: memberId });

              setOv(OV);
              setSession(session);
              setJoined(true);
            } catch (err) {
              console.error("B join failed:", err);
            }
          }
        }
      });
    };

    // STOMP 에러
    stompClient.onStompError = (err) => console.error("STOMP error", err);

    // 활성화
    stompClient.activate();

    // cleanup
    return () => {
      stompClient.deactivate();
    };
  }, [memberId, token, joined]);

  // -------------------------------
  // 7️⃣ JSX
  // -------------------------------
  return (
    <div className="min-h-screen flex flex-col items-center bg-gray-100 p-4">
      {!joined && (
        <button
          className="mb-4 px-4 py-2 bg-purple-600 text-white rounded"
          onClick={handleStartVideoCall}
        >
          <FontAwesomeIcon icon={faVideo} /> 화상채팅 시작
        </button>
      )}

      <div className="flex gap-4 mb-4">
        {isHost ? (
          <video
            ref={localVideoRef}
            className="w-96 h-72 bg-black"
            autoPlay
            muted
          />
        ) : (
          <video
            ref={remoteVideoRef}
            className="w-96 h-72 bg-black"
            autoPlay
          />
        )
      }
        
      </div>

      {/* 🔽 채팅 UI 그대로 유지 */}
      <div className="max-w-md w-full border p-2 rounded shadow flex flex-col">
        <div className="h-64 overflow-y-auto mb-2">
          {messages.map((msg, idx) => (
            <div
              key={idx}
              className={`mb-1 p-1 rounded ${msg.senderId === memberId ? "bg-blue-200 text-right" : "bg-gray-200"}`}
            >
              <span className="text-sm font-semibold">{msg.senderNickname}: </span>
              {msg.message}
            </div>
          ))}
        </div>
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
    </div>
  );
};

export default VideoCallComponent;
