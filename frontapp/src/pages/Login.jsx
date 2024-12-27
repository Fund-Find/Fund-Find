import React, { useState, useEffect } from 'react'
import '../assets/css/login.css'
import { useNavigate, useLocation } from 'react-router-dom'

function Login() {
    const [username, setUsername] = useState('')
    const [password, setPassword] = useState('')
    const [errorMessage, setErrorMessage] = useState('')
    const navigate = useNavigate()
    const location = useLocation()

    // 로그인 전 접근하려 했던 경로를 가져옵니다. 없으면 '/'로 기본 설정
    const from = location.state?.from || '/'
    useEffect(() => {
        // 로그인 페이지 접근 시 메시지가 있으면 alert로 표시
        if (location.state?.message) {
            alert(location.state.message) // JavaScript 경고창
        }
    }, [location.state])

    const handleLogin = async (e) => {
        e.preventDefault()
        const requestData = { username, password }

        const xsrfToken = getCookie('_xsrf')

        try {
            const response = await fetch('http://localhost:8080/api/v1/user/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-XSRF-TOKEN': xsrfToken,
                },
                body: JSON.stringify(requestData),
                credentials: 'include',
            })

            const data = await response.json()
            console.log('로그인 응답:', JSON.stringify(data, null, 2))

            if (response.ok) {
                // msg에서 accessToken 추출
                const msg = data.msg
                const accessTokenMatch = msg.match(/토큰 발급 성공: (.+)/)
                const extractedAccessToken = accessTokenMatch ? accessTokenMatch[1] : null

                if (extractedAccessToken) {
                    console.log('추출된 Access Token:', extractedAccessToken)
                    localStorage.setItem('accessToken', extractedAccessToken)
                } else {
                    console.error('Access Token이 응답에 포함되지 않았습니다.')
                    setErrorMessage(data.msg || '로그인 응답에서 토큰을 확인할 수 없습니다.')

                    return
                }

                // expirationTime 저장
                const { expirationTime } = data.data
                if (expirationTime) {
                    console.log('Expiration Time 저장:', expirationTime)
                    localStorage.setItem('expirationTime', expirationTime)
                }

                alert('로그인 성공')
                console.log('리디렉션할 페이지:', from) // 디버깅용 로그
                console.log('location.state?.from : ', location.state?.from)
                console.log('document.referrer : ', document.referrer)
                navigate(from, { replace: true }) // 항상 원래 경로로 이동

                // 인증 상태 강제로 반영 (새로고침 없이 동작)
                window.dispatchEvent(new Event('storage'))
            } else {
                setErrorMessage(data.message || '로그인에 실패했습니다.')
            }
        } catch (error) {
            console.error('로그인 요청 실패:', error)
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
                        <a href="/user/findId">아이디 찾기</a>
                        <span> | </span>
                        <a href="/user/resetpw">비밀번호 재발급</a>
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
