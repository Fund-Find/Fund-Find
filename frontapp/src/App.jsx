import { BrowserRouter, Route, Routes } from 'react-router-dom'
import Main from './pages/Main'
import Login from './pages/Login'
import Article from './pages/Article'
import Nav from './components/Nav'
import Register from './pages/Register'
import Profile from './pages/profile'
import Survey from './pages/Survey'
import Result from './pages/Result'
import QuizShowList from './pages/QuizshowList'

function App() {
    return (
        <BrowserRouter>
            <Nav />
            <Routes>
                <Route index element={<Main />}></Route>
                <Route path="/auth/login" element={<Login />}></Route>
                <Route path="/article/lsit" element={<Article />}></Route>
                <Route path="/user/register" element={<Register />}></Route>
                <Route path="/user/profile" element={<Profile />}></Route>
                <Route path="/survey" element={<Survey />} /> {/* /survey 경로에 Survey 컴포넌트 렌더링 */}
                <Route path="/result" element={<Result />} /> {/* 결과 페이지 경로 추가 */}
                <Route path="/quizshow/list" element={<QuizShowList />}></Route>
            </Routes>
        </BrowserRouter>
    )
}

export default App
