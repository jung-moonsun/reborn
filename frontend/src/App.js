// src/App.jsx
import React, { useEffect, useState } from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';

import Header from './components/Header';
import PrivateRoute from './components/PrivateRoute';

import Home from './pages/Home';
import Login from './pages/Login';
import Signup from './pages/Signup';
import MyPage from './pages/MyPage';
import CreateProduct from './pages/CreateProduct';
import EditProduct from './pages/EditProduct';
import ProductDetail from './pages/ProductDetail';
import Search from './pages/Search';
import ChatList from './pages/ChatList';
import ChatRoom from './pages/ChatRoom';
import NotFound from './pages/NotFound';
import KakaoCallback from './pages/KakaoCallback';

export default function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem('token'));

  useEffect(() => {
    const handleStorage = () => {
      setIsLoggedIn(!!localStorage.getItem('token'));
    };
    window.addEventListener('storage', handleStorage);
    return () => {
      window.removeEventListener('storage', handleStorage);
    };
  }, []);

  return (
    <BrowserRouter>
      <Header
        isLoggedIn={isLoggedIn}
        setIsLoggedIn={setIsLoggedIn}
      />

      <Routes>
        <Route path="/" element={<Home />} />

        <Route
          path="/login"
          element={<Login setIsLoggedIn={setIsLoggedIn} />}
        />

        <Route path="/signup" element={<Signup />} />

        <Route
          path="/mypage"
          element={
            <PrivateRoute isLoggedIn={isLoggedIn}>
              <MyPage />
            </PrivateRoute>
          }
        />

        <Route
          path="/oauth/kakao/callback"
          element={<KakaoCallback setIsLoggedIn={setIsLoggedIn} />}
        />

        <Route path="/create" element={<CreateProduct />} />
        <Route path="/product/edit/:id" element={<EditProduct />} />
        <Route path="/product/:id" element={<ProductDetail />} />
        <Route path="/search" element={<Search />} />

        <Route path="/chat" element={<ChatList />} />
        <Route path="/chat/:roomId" element={<ChatRoom />} />

        <Route path="*" element={<NotFound />} />
      </Routes>
    </BrowserRouter>
  );
}
