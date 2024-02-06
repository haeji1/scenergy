import { useRef, useState, useEffect, useCallback } from "react";
import { useLocation, useParams } from "react-router-dom";
import * as StompJs from "@stomp/stompjs";
import styles from "./ChatConnect.module.css";
import ChatInput from "./ChatInput";
import { useChatMessages } from "../../hooks/useChatMessages";
import { ChatList } from "./ChatMessageList";
import { useChatMessageContext } from "../../contexts/ChatMessageContext";

const ChatConnect = ({ roomId, lastMessageId }) => {
  const [chat, setChat] = useState("");
  const [chatMessages, setChatMessages] = useState([]);
  const [nowLastMessageId, setNowLastMessageId] = useState(lastMessageId);
  const client = useRef({});
  const location = useLocation();
  const userId = 1;

  const { addChatMessage, setFirstChatMessage, firstChatMessage } =
    useChatMessageContext();

  const {
    data: chatMessage,
    isLoading,
    isError,
    error,
  } = useChatMessages(lastMessageId);

  useEffect(() => {
    if (!isLoading) {
      setChatMessages(chatMessage);
    }
  }, [chatMessage, isLoading]);

  const subscribe = useCallback(() => {
    client.current.subscribe("/sub/chat/room/" + roomId, (body) => {
      const messageBody = JSON.parse(body.body);
      setChatMessages((prevMessages) => {
        const updatedMessages = Array.isArray(prevMessages)
          ? [...prevMessages, messageBody]
          : [messageBody];
        console.log(messageBody);
        console.log(updatedMessages);
        return updatedMessages;
      });
      console.log("subsub하네요");
      console.log(chatMessages);
      setNowLastMessageId(messageBody.id);
      setFirstChatMessage(messageBody);
      console.log(messageBody.id);
    });
  }, [roomId, firstChatMessage, setFirstChatMessage]);

  const connect = useCallback(() => {
    client.current = new StompJs.Client({
      brokerURL: "ws://localhost:8080/ws",
      reconnectDelay: 5000,
      heartbeatIncoming: 2000,
      heartbeatOutgoing: 2000,
      onConnect: () => {
        console.log("yesyes");
        console.log(lastMessageId); //null
        console.log(roomId);
        console.log(chatMessages);
        subscribe();
      },
      onError: (error) => {
        console.log(error);
      },
    });
    client.current.activate();
  }, [subscribe]);

  const publish = (chat) => {
    //연결 끊어지면 끝
    if (!client.current.connected) return;

    //test용
    const message = {
      //TODO:나중에 header로 user정보 담아왔을때 user_id 받자받자
      user_id: 2,
      room_id: roomId,
      message: chat,
      messageType: "TALK",
    };

    client.current.publish({
      destination: "/pub/chat",
      body: JSON.stringify(message),
    });

    console.log(chat);
    setChat("");
    console.log("pubpub");
  };
  const disconnect = () => {
    client.current.deactivate();
  };
  const handleChange = (event) => {
    //입력값 state로 변경해주기
    setChat(event.target.value);
  };

  const handleSubmit = (event, chat) => {
    //보내기 누르면 publish되도록
    event.preventDefault();
    console.log("보내기");
    publish(chat);
  };

  useEffect(() => {
    connect();
    return () => disconnect();
  }, [connect, roomId]);

  if (isLoading) return <div>Loading messages...</div>;
  if (isError) return <div>Error loading messages: {error.message}</div>;

  return (
    <div className={styles.chatMsgFieldGlobal}>
      <ChatList chatList={chatMessages || []} userId={userId} />
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
