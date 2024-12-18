// PasswordChange.jsx

import React, { useState } from 'react'
import '../assets/css/PasswordChange.css'
import { useNavigate } from 'react-router-dom'

const CHANGE_PASSWORD_API_URL = 'http://localhost:8080/api/v1/user/change-password'

const PasswordChange = () => {
    const [currentPassword, setCurrentPassword] = useState('')
    const [newPassword, setNewPassword] = useState('')
    const [confirmPassword, setConfirmPassword] = useState('')
    const [error, setError] = useState(null)
    const [success, setSuccess] = useState(null)
    const [loading, setLoading] = useState(false)
    const navigate = useNavigate()

    const handleSubmit = async (e) => {
        e.preventDefault()
        setError(null)
        setSuccess(null)

        // 클라이언트 측 유효성 검사
        if (newPassword !== confirmPassword) {
            setError('새 비밀번호와 비밀번호 확인이 일치하지 않습니다.')
            return
        }

        if (newPassword.length < 8) {
            setError('새 비밀번호는 최소 8자 이상이어야 합니다.')
            return
        }

        // 추가적인 비밀번호 복잡성 검사 가능
        // 예: 대문자, 소문자, 숫자, 특수문자 포함 여부 등

        setLoading(true)

        try {
            const response = await fetch(CHANGE_PASSWORD_API_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include', // 쿠키를 포함하여 요청
                body: JSON.stringify({
                    currentPassword,
                    newPassword,
                    confirmPassword,
                }),
            })

            const data = await response.json()

            if (response.ok) {
                setSuccess('비밀번호가 성공적으로 변경되었습니다.')
                setCurrentPassword('')
                setNewPassword('')
                setConfirmPassword('')
            } else {
                setError(data.message || '비밀번호 변경에 실패했습니다.')
            }
        } catch (err) {
            setError('서버와의 연결에 문제가 있습니다. 나중에 다시 시도해주세요.')
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="password-change-container">
            <h2>비밀번호 변경</h2>
            <form onSubmit={handleSubmit} className="password-change-form">
                {error && <div className="error-message">{error}</div>}
                {success && <div className="success-message">{success}</div>}
                <div className="form-group">
                    <label htmlFor="currentPassword">현재 비밀번호</label>
                    <input
                        type="password"
                        id="currentPassword"
                        value={currentPassword}
                        onChange={(e) => setCurrentPassword(e.target.value)}
                        required
                        placeholder="현재 비밀번호를 입력하세요"
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="newPassword">새 비밀번호</label>
                    <input
                        type="password"
                        id="newPassword"
                        value={newPassword}
                        onChange={(e) => setNewPassword(e.target.value)}
                        required
                        placeholder="새 비밀번호를 입력하세요"
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="confirmPassword">비밀번호 확인</label>
                    <input
                        type="password"
                        id="confirmPassword"
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        required
                        placeholder="비밀번호를 다시 입력하세요"
                    />
                </div>
                <button type="submit" disabled={loading}>
                    {loading ? '변경 중...' : '비밀번호 변경'}
                </button>
            </form>
            <button className="back-button" onClick={() => navigate(-1)}>
                뒤로가기
            </button>
        </div>
    )
}

export default PasswordChange
