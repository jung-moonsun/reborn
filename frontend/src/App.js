import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { useEffect, useState } from 'react';
import Header from './components/Header';
import Home from './pages/Home';
import Login from './pages/Login';
import Signup from './pages/Signup';
import CreateProduct from './pages/CreateProduct';
import EditProduct from './pages/EditProduct';
import ProductDetail from './pages/ProductDetail';
import ChatList from './pages/ChatList';
import ChatRoom from './pages/ChatRoom';
import NotFound from './pages/NotFound';
import Search from './pages/Search';
import MyPage from './pages/MyPage';
import PrivateRoute from './components/PrivateRoute';
import OAuthCallback from './pages/OAuthCallback';

export default function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem('token'));

  useEffect(() => {
    const handleStorage = () => {
      setIsLoggedIn(!!localStorage.getItem('token'));
    };
    window.addEventListener('storage', handleStorage);
    return () => window.removeEventListener('storage', handleStorage);
  }, []);

  return (
    <BrowserRouter>
      <Header isLoggedIn={isLoggedIn} setIsLoggedIn={setIsLoggedIn} />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login setIsLoggedIn={setIsLoggedIn} />} /> {/* ✅ 수정 */}
        <Route path="/signup" element={<Signup />} />
        <Route path="/mypage" element={
          <PrivateRoute>
            <MyPage />
          </PrivateRoute>
        } />
        <Route path="/oauth/callback" element={<OAuthCallback setIsLoggedIn={setIsLoggedIn} />} /> {/* ✅ 수정 */}
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
