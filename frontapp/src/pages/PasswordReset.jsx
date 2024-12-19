import React, { useState } from 'react'
import '../assets/css/PasswordReset.css' // CSS 파일 import

const PasswordReset = () => {
    const [username, setUsername] = useState('')
    const [email, setEmail] = useState('')
    const [message, setMessage] = useState('')
    const [error, setError] = useState('')
    const [loading, setLoading] = useState(false)

    const handleSubmit = async (e) => {
        e.preventDefault()

        if (!username || !email) {
            setError('아이디와 이메일을 모두 입력해주세요.')
            return
        }

        setLoading(true)
        setError('')
        setMessage('')

        try {
            const response = await fetch('http://localhost:8080/api/v1/user/reset-password', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ username, email }),
            })

            if (response.ok) {
                const data = await response.json()
                setMessage(data.msg || '비밀번호 재발급 이메일이 발송되었습니다.')
            } else {
                const errorData = await response.json()
                setError(errorData.msg || '비밀번호 재발급 요청에 실패했습니다.')
            }
        } catch (err) {
            setError('서버와 연결할 수 없습니다.')
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="password-reset-container">
            <h2>비밀번호 재발급 요청</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <input
                        type="text"
                        placeholder="아이디를 입력하세요"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        className="input-field"
                    />
                </div>
                <div>
                    <input
                        type="email"
                        placeholder="이메일을 입력하세요"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        className="input-field"
                    />
                </div>
                <button type="submit" className={`submit-button ${loading ? 'disabled' : ''}`} disabled={loading}>
                    {loading ? '요청 중...' : '비밀번호 재발급 요청'}
                </button>
            </form>
            {message && <p className="success-message">{message}</p>}
            {error && <p className="error-message">{error}</p>}
        </div>
    )
}

export default PasswordReset
