// store.ts
import { configureStore } from "@reduxjs/toolkit";
import { persistStore, persistReducer } from "redux-persist";
import storage from "redux-persist/lib/storage";
import authReducer from "./authSlice";
import notificationReducer from "./notificationSlice";

const persistConfig = {
  key: "auth",
  storage,
  whitelist: ["memberId", "token", "nickname"],
};

const persistedReducer = persistReducer(persistConfig, authReducer);

const notificationPersistConfig = {
  key: "notifications",
  storage,
};
const persistedNotificationReducer = persistReducer(notificationPersistConfig, notificationReducer);


export const store = configureStore({
  reducer: {
    auth: persistedReducer,
    notifications: persistedNotificationReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: [
          "persist/PERSIST",
          "persist/REHYDRATE",
          "persist/PAUSE",
          "persist/FLUSH",
          "persist/PURGE",
          "persist/REGISTER",
        ],
      },
    }),
});

export const persistor = persistStore(store);

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
