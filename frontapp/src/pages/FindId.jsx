import React, { useState } from 'react'
import '../assets/css/findId.css' // CSS 파일 임포트
import { Link } from 'react-router-dom'

const FindId = () => {
    const [email, setEmail] = useState('')
    const [username, setUsername] = useState(null)
    const [error, setError] = useState(null)
    const [loading, setLoading] = useState(false)

    const handleFindId = async () => {
        if (!email) {
            setError('이메일을 입력해주세요.')
            return
        }

        setLoading(true)
        setError(null)
        setUsername(null)

        try {
            const response = await fetch('http://localhost:8080/api/v1/user/find-id', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ email }),
            })

            const data = await response.json()

            if (response.ok) {
                setUsername(data.data.username)
            } else {
                setError(data.msg || '아이디를 찾을 수 없습니다.')
            }
        } catch (err) {
            setError('서버와 연결할 수 없습니다.')
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="find-id-container">
            <h2>아이디 찾기</h2>
            <div>
                <input
                    type="email"
                    placeholder="이메일을 입력하세요"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                />
                <button onClick={handleFindId} disabled={loading}>
                    {loading ? '로딩 중...' : '아이디 찾기'}
                </button>
            </div>
            {error && <p className="error-message">{error}</p>}
            {username && (
                <>
                    <p className="success-message">
                        찾으신 아이디: <strong>{username}</strong>
                    </p>
                    <Link className="gotologin" to="/user/login">
                        {' '}
                        로그인 페이지 돌아가기기
                    </Link>
                </>
            )}
        </div>
    )
}

export default FindId
