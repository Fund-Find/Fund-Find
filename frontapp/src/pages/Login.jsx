import React, { useState } from 'react'
import '../assets/css/login.css'

function Login() {
    const [username, setUsername] = useState('')
    const [password, setPassword] = useState('')
    const [errorMessage, setErrorMessage] = useState('')

    const handleLogin = async (e) => {
        e.preventDefault()
        const requestData = {
            username,
            password,
        }

        // _xsrf 쿠키에서 CSRF 토큰을 가져오기 (필요한 경우)
        const xsrfToken = getCookie('_xsrf')

        try {
            const response = await fetch('http://localhost:8080/api/v1/user/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-XSRF-TOKEN': xsrfToken, // CSRF 토큰 필요 시 사용
                },
                body: JSON.stringify(requestData),
                credentials: 'include', // 쿠키를 포함하여 요청
            })

            const data = await response.json()
            if (response.ok) {
                // 서버 응답에 expirationTime 포함 가정
                if (data && data.data) {
                    const { expirationTime, username, accessToken } = data.data
                    if (expirationTime) {
                        // expirationTime을 localStorage에 저장
                        localStorage.setItem('expirationTime', expirationTime)
                    }

                    if (accessToken) {
                        localStorage.setItem('accessToken', accessToken)
                    }

                    console.log('로그인 성공:', username)
                    console.log('로그인 응답:', data)
                    console.log('로그인 응답 전체:', JSON.stringify(data, null, 2))

                    alert('로그인 성공')

                    // Nav 등 다른 컴포넌트에서 localStorage의 expirationTime 사용 가능
                    // 로그인 성공 후 원하는 페이지로 이동
                    window.history.back()
                } else {
                    setErrorMessage('로그인 응답에 필요한 정보가 없습니다.')
                }
            } else {
                setErrorMessage(data.message || '로그인에 실패했습니다.')
            }
        } catch (error) {
            setErrorMessage('서버와 연결할 수 없습니다.')
        }
    }

    function getCookie(name) {
        const value = `; ${document.cookie}`
        const parts = value.split(`; ${name}=`)
        if (parts.length === 2) {
            const cookieValue = parts.pop().split(';').shift()
            console.log(`${name} Cookie Value: `, cookieValue)
            return cookieValue
        }
        return null
    }

    return (
        <div className="justLogin">
            <div className="loginContainer">
                {errorMessage && <div className="alert alert-danger">{errorMessage}</div>}
                <form onSubmit={handleLogin} className="loginBox">
                    <h1>Fund FInd</h1>
                    <div>
                        <label htmlFor="username"></label>
                        <input
                            type="text"
                            id="username"
                            name="username"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                            className="form-control"
                            placeholder="아이디를 입력하세요"
                        />
                    </div>
                    <div>
                        <label htmlFor="password"></label>
                        <input
                            type="password"
                            id="password"
                            name="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                            className="form-control"
                            placeholder="비밀번호를 입력하세요"
                        />
                    </div>
                    <div className="add">
                        <a href="/user/find-account">아이디 찾기</a>
                        <span> | </span>
                        <a href="/user/find-account">비밀번호 재설정</a>
                        <span> | </span>
                        <a href="/user/register">회원가입</a>
                    </div>
                    <div>
                        <button type="submit" className="btn-login">
                            로그인
                        </button>
                    </div>
                </form>
            </div>
        </div>
    )
}

export default Login
