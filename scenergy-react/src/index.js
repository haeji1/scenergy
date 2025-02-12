import React from "react";
import ReactDOM from "react-dom/client";
import "./index.css";
import store from "./store/store";
import {Provider} from "react-redux";
import App from "./App";
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import {routes} from "./router/router";
import {ChatRoomProvider} from "./contexts/ChatRoomContext";
import {ChatMessageProvider} from "./contexts/ChatMessageContext";
import {QueryClient, QueryClientProvider} from "react-query";
import axios from "axios";
import {ScenergyPostProvider} from "./contexts/ScenergyPostContext";
import {NotificationProvider} from "./contexts/NotificationContext";
/*danny 추가 (네이버 유저정보 가져올떄)*/
axios.defaults.baseURL = "http://localhost:3000/";
axios.defaults.withCredentials = true;

const router = createBrowserRouter(routes);
const queryClient = new QueryClient();
const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
  <Provider store={store}>
    <ScenergyPostProvider>
      <ChatRoomProvider>
        <ChatMessageProvider>
          <QueryClientProvider client={queryClient}>
            <NotificationProvider>
              <RouterProvider router={router}>
                <App/>
              </RouterProvider>
            </NotificationProvider>
          </QueryClientProvider>
        </ChatMessageProvider>
      </ChatRoomProvider>
    </ScenergyPostProvider>
  </Provider>,
);
