import { useParams, useLocation } from "react-router";
import ChatComponent from "../components/ChatComponent";

const ChatPage = () => {
  const { userId } = useParams();
  const location = useLocation();
  const { senderId, roomId } = location.state as { senderId: string; roomId: string };

  return <ChatComponent roomId={roomId} senderId={senderId} receiverId={userId} />;
};

export default ChatPage;