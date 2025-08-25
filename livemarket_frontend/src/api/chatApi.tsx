import axios from "axios";

const API_SERVER = `http://localhost:9008`
const VIDEO_CALL_API_SERVER = `http://localhost:9008/v1/video-calls`

export interface ChatMessage {
  roomId: string;
  senderId: string;
  receiverId: string;
  senderNickname: string;
  message: string;
  sentAt?: string;
  type: "ENTER" | "TALK" | "QUIT";
}

interface VideoCallSessionCreateDto {
  sessionId: string;
  requesterId: string;
  receiverId: string;
}

export const sendVideoCallRequest = async (
  fromMemberId: string,
  toMemberId: string
) => {
  await axios.post("http://localhost:9008/v1/video-calls/request", {
    sessionId: `${fromMemberId}_${toMemberId}`,
    fromMemberId,
    toMemberId,
    message: "화상 요청",
    token: "", // 필요 시 OpenVidu 토큰
    createdAt: Date.now()
  });
};

export const joinVideoCallSession = async (dto: VideoCallSessionCreateDto) => {
  const token = localStorage.getItem("token");
  const res = await axios.post(
    `${VIDEO_CALL_API_SERVER}/sessions/join`,
    dto,
    {
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json"
      }
    }
  );
  return res.data;
};

// 화상채팅 세션 생성
export const createVideoCallSession = async (roomId: string, publisherId: string, receiverId: string) => {
  const token = localStorage.getItem("token");
  const res = await axios.post(
    `${VIDEO_CALL_API_SERVER}/sessions`,
    {
      roomId,
      publisherId,
      receiverId,
      sessionId: null,
      token: null,
    },
    {
      headers: { Authorization: `Bearer ${token}` },
    }
  );
  return res.data; // { sessionId, token } 같은 정보가 반환된다고 가정
};

// 특정 채팅방 메시지 조회
export const getMessages = async (roomId: string): Promise<ChatMessage[]> => {
    const token = localStorage.getItem("token")
  const res = await axios.get(`${API_SERVER}/v1/chat/rooms/${roomId}/messages`, {
    headers: {
        Authorization : `Bearer ${token}`
    }
  });
  return res.data;
};

// 새 채팅 메시지 전송
export const sendMessage = async (msg: ChatMessage) => {
    const token = localStorage.getItem("token")
  const res = await axios.post(`${API_SERVER}/v1/chat/rooms/${msg.roomId}/messages`, msg, {
    headers: {
        Authorization : `Bearer ${token}`
    }
  });
  return res.data;
};