import { BrowserRouter, Route, Routes } from 'react-router-dom'
import { useState, useEffect } from 'react'
import Main from './pages/Main'
import Login from './pages/Login'
import Article from './pages/Article'
import Nav from './components/Nav'
import Register from './pages/Register'
import '@fortawesome/fontawesome-free/css/all.min.css'
import Profile from './pages/profile'
import Survey from './components/Survey'
import Result from './pages/Result'
import QuizShowList from './pages/QuizshowList'
import ETFList from './pages/ETFList'
import ETFDetail from './components/ETFDetail'
import PrivateRoute from './components/PrivateRoute'

function App() {
    const [isAuthenticated, setIsAuthenticated] = useState(false)

    useEffect(() => {
        // 로컬 스토리지나 쿠키를 확인하여 인증 상태 설정
        const accessToken = localStorage.getItem('accessToken')
        console.log(localStorage)
        setIsAuthenticated(!!accessToken)
    }, [])

    return (
        <BrowserRouter>
            <Nav />
            <Routes>
                <Route index element={<Main />}></Route>
                <Route path="/auth/login" element={<Login />}></Route>
                <Route path="/article/list" element={<Article />}></Route>
                <Route path="/user/register" element={<Register />}></Route>
                <Route path="/user/profile" element={<PrivateRoute element={Profile} />} />
                <Route path="/survey" element={<PrivateRoute element={Survey} />} />
                {/* /survey 경로에 Survey 컴포넌트 렌더링 */}
                <Route path="/result" element={<Result />} /> {/* 결과 페이지 경로 추가 */}
                <Route path="/quizshow/list" element={<QuizShowList />}></Route>
                <Route path="/etf/list" element={<ETFList />}></Route>
                <Route path="/etf/:code" element={<ETFDetail />} />
            </Routes>
        </BrowserRouter>
    )
}

export default App
