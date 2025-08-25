import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { markAsRead } from "../redux/notificationSlice";
import type { RootState } from "../redux/store";
import { useNavigate } from "react-router";

const NotificationComponent: React.FC = () => {
  const dispatch = useDispatch();
  const notifications = useSelector((state: RootState) => state.notifications.list);
  const memberId = localStorage.getItem("memberId"); // 로그인한 회원 ID
  const navigate = useNavigate();

  const handleClick = (notif: any) => {
    dispatch(markAsRead(notif.id));
    if (!notif.roomId) return;

    if (notif.message === "화상 요청" || notif.type === "VIDEO") {
      const roomId = [memberId, notif.fromMemberId].sort().join("_"); // generateRoomId 동일
      navigate(`/video-call/${roomId}`, {
        state: { senderId: memberId, roomId, receiverId: notif.fromMemberId },
      });
    } else {
      navigate(`/chat/${notif.fromMemberId}`, {
        state: { senderId: memberId, roomId: notif.roomId },
      });
    }
  };

  return (
    <div className="p-2">
      <ul className="flex flex-col gap-2">
        {notifications.map((notif, index) => (
          <li
            key={`${notif.id}-${index}`}
            className="p-2 border rounded hover:bg-gray-100 cursor-pointer flex justify-between items-center"
            onClick={() => handleClick(notif)}
          >
            <span>{notif.message}</span>
            {!notif.read && <span className="w-3 h-3 bg-green-500 rounded-full" />}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default NotificationComponent;
