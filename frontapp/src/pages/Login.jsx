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

        try {
            const response = await fetch('http://localhost:8080/api/v1/user/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(requestData),
            })

            const data = await response.json()
            if (response.ok) {
                alert('로그인 성공')
                // 로그인 성공 후 리다이렉트 또는 토큰 저장 로직 추가
                window.location.href = '/home' // 예시: 홈 페이지로 리다이렉트
            } else {
                setErrorMessage(data.message || '로그인에 실패했습니다.')
            }
        } catch (error) {
            setErrorMessage('서버와 연결할 수 없습니다.')
        }
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
                            placeholder="아이디를 입력하세요" // placeholder 추가
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
                            placeholder="비밀번호를 입력하세요" // placeholder 추가
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
