import { useCallback, useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import * as StompJs from "@stomp/stompjs";
import styles from "./ChatConnect.module.css";
import ChatInput from "./ChatInput";
import { useChatMessages } from "../../hooks/useChatMessages";
import { ChatList } from "./ChatMessageList";
import { useChatMessageContext } from "../../contexts/ChatMessageContext";
import { useChatRoom } from "../../contexts/ChatRoomContext";
import ApiUtil from "../../apis/ApiUtil";

const ChatConnect = ({ lastMessageId, refetchChatRooms, lastMessage }) => {
  const [chat, setChat] = useState("");
  const [chatMessages, setChatMessages] = useState([]);
  const client = useRef({});
  const userId = ApiUtil.getUserIdFromToken();
  const { roomId } = useParams();
  const realRoomId = parseInt(roomId, 10);
  const { updateRecentMessage } = useChatRoom();
  const {
    addChatMessage,
    setRecentChatMessage: setRecentChatMessage,
    recentChatMessage: recentChatMessage,
  } = useChatMessageContext();

  const {
    data: loadedMessages,
    isLoading: messagesLoading,
    isError: messagesError,
  } = useChatMessages(lastMessageId, lastMessage, realRoomId);

  useEffect(() => {
    //첫번째 요청 100개
    if (!messagesLoading && loadedMessages && !messagesError) {
      const updatedMessages = loadedMessages.filter(
        (loadedMessage) =>
          !recentChatMessage || loadedMessage.id !== recentChatMessage.id,
      );
      if (
        recentChatMessage &&
        !updatedMessages.find(({ id }) => id === recentChatMessage.id)
      ) {
        updatedMessages.push(recentChatMessage);
      }
      setChatMessages(updatedMessages);
    }
  }, [loadedMessages, messagesLoading, messagesError, recentChatMessage]);

  const subscribe = useCallback(() => {
    client.current.subscribe("/sub/chat/room/" + realRoomId, (message) => {
      const messageBody = JSON.parse(message.body);
      if (messageBody.operationCode === 0) {
        setChatMessages((prevMessages) => [...prevMessages, messageBody]);
        updateRecentMessage(realRoomId, messageBody);
      } else if (messageBody.operationCode === 1) {
        setChatMessages((prevMessages) => {
          return prevMessages.map((msg) => {
            if (msg.createdAt >= messageBody.createdAt) {
              return { // 찾은 메시지의 unreadCount를 1 줄임
                ...msg,
                unreadCount: Math.max(0, msg.unreadCount - 1), // 최소값이 0이 되도록 함
              };
            }
            return msg;
          });
        });
      }
    });
  }, [realRoomId]);

  useEffect(() => {
    // 클라이언트 연결 및 구독 설정
    const connectAndSubscribe = () => {
      if (client.current.connected) {
        // 이미 연결된 경우, 기존 구독을 해제
        client.current.unsubscribe();
      }
      client.current = new StompJs.Client({
        brokerURL: process.env.REACT_APP_API_WS_URL,
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
        onConnect: () => {
          // 새로운 방에 대한 구독 설정
          subscribe();
        },
        onDisconnect: () => {},
        connectHeaders: {
          roomId: realRoomId.toString(),
          userId: userId.toString(),
        },
      });

      client.current.activate();
    };

    // 방 ID가 변경될 때마다 새로운 연결 및 구독을 설정
    connectAndSubscribe();

    // 컴포넌트가 언마운트되거나 방 ID가 변경될 때 클라이언트 연결 해제
    return () => {
      if (client.current && client.current.connected) {
        client.current.deactivate();
      }
    };
  }, [realRoomId]);
  //메세지 전송
  const publish = (chat) => {
    //연결 끊어지면 끝
    if (!client.current.connected) return;

    const message = {
      user_id: userId,
      room_id: realRoomId,
      message: chat,
      messageType: "TALK",
    };

    client.current.publish({
      destination: "/pub/chat",
      body: JSON.stringify(message),
      callback: function (error, messageResponse) {
        if (error) {
          console.error("메시지 전송 실패", error);
        } else {
          const receivedMessage = JSON.parse(messageResponse.body);
          addChatMessage(receivedMessage); // 실제 서버에서 생성된 ID를 포함한 메시지 객체
          updateRecentMessage(realRoomId, receivedMessage);
          refetchChatRooms();
        }
      },
    });
    setChat("");
  };

  const handleChange = (event) => {
    //입력값 state로 변경해주기
    setChat(event.target.value);
  };

  const handleSubmit = (event, chat) => {
    //보내기 누르면 publish되도록
    event.preventDefault();
    if (!chat.trim()) return;
    publish(chat);
    console.log("보내기");
  };

  if (messagesLoading) return <div>로딩중...</div>;
  if (messagesError)
    return <div>Error loading messages: {messagesError.message}</div>;

  return (
    <div className={styles.chatMsgFieldGlobal}>
      <ChatList chatList={chatMessages} userId={userId} />
      <ChatInput
        chat={chat}
        setChat={setChat}
        handleChange={handleChange}
        handleSubmit={handleSubmit}
      />
    </div>
  );
};
export default ChatConnect;
