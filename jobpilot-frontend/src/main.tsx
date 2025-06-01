import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import {Provider } from 'react-redux'
import { store } from './store.ts'
import ThemeWrapper from './components/theme/ThemeWrapper.tsx'
const rootElement = document.getElementById('root');

if (rootElement) {
  createRoot(rootElement).render(

  <StrictMode>
    <Provider store={store}>
    <ThemeWrapper>
      <App />
    </ThemeWrapper>
    </Provider>
  </StrictMode>,
)
}

