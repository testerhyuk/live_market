import { createRoot } from 'react-dom/client'
import './index.css'
import { RouterProvider } from 'react-router'
import router from './router/router.tsx'
import { Provider } from 'react-redux'
import { PersistGate } from 'redux-persist/integration/react'
import { persistor, store } from './redux/store.tsx'
import NotificationProvider from './components/NotificationProvider'

createRoot(document.getElementById('root')!).render(
  <Provider store={store}>
    <PersistGate loading={null} persistor={persistor}>
      <NotificationProvider>
        <RouterProvider router={router} />
      </NotificationProvider>
    </PersistGate>
  </Provider>
)
