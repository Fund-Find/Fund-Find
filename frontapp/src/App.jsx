import { BrowserRouter, Route, Routes } from 'react-router-dom'
import Main from './pages/Main'
import Login from './pages/Login'
import Article from './pages/Article'
import Nav from './components/Nav'
import Register from './pages/Register'

function App() {
    return (
        <BrowserRouter>
            <Nav />
            <Routes>
                <Route index element={<Main />}></Route>
                <Route path="/auth/login" element={<Login />}></Route>
                <Route path="/article/lsit" element={<Article />}></Route>
                <Route path="/user/register" element={<Register />}></Route>
            </Routes>
        </BrowserRouter>
    )
}

export default App
