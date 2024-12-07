import { Link } from 'react-router-dom'

function Nav() {
    return (
        <ul>
            <li>
                <Link to="/">홈</Link>
            </li>
            <li>
                <Link to="/auth/login">로그인</Link>
            </li>
            <li>
                <Link to="/article/lsit">게시글 목록</Link>
            </li>
        </ul>
    )
}

export default Nav
