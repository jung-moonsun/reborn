import { BrowserRouter, Routes, Route } from 'react-router-dom';
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

export default function App() {
  return (
    <BrowserRouter>
      <Header />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route path="/mypage" element={
          <PrivateRoute>
            <MyPage />
          </PrivateRoute>
        } />
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

