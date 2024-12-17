import React from 'react'
import { Navigate, useLocation } from 'react-router-dom'

function PrivateRoute({ element: Element }) {
    const location = useLocation() // 현재 경로 가져오기
    const expirationTimeStr = localStorage.getItem('expirationTime')
    let isLoggedIn = false

    if (expirationTimeStr) {
        const expirationTime = parseInt(expirationTimeStr, 10)
        const now = Date.now()
        const timeLeftMs = expirationTime - now
        if (timeLeftMs > 0) {
            isLoggedIn = true
        }
    }

    if (!isLoggedIn) {
        // 로그아웃 처리 (만료 시)
        localStorage.removeItem('accessToken')
        localStorage.removeItem('expirationTime')

        // 로그인 페이지로 리디렉션하며 원래 경로 저장
        return (
            <Navigate to="/auth/login" replace state={{ from: location.pathname, message: '로그인이 필요합니다.' }} />
        )
    }

    return <Element />
}

export default PrivateRoute
