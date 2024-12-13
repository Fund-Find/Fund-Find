import React from 'react'
import { Navigate } from 'react-router-dom'

function PrivateRoute({ element: Element }) {
    // 로그인 여부 판단
    const expirationTimeStr = localStorage.getItem('expirationTime')
    let isLoggedIn = false
    if (expirationTimeStr) {
        const expirationTime = parseInt(expirationTimeStr, 10)
        const now = Date.now()
        const timeLeftMs = expirationTime - now
        const timeLeftSec = Math.max(Math.floor(timeLeftMs / 1000), 0)
        if (timeLeftSec > 0) {
            isLoggedIn = true
        }
    }

    return isLoggedIn ? <Element /> : <Navigate to="/auth/login" replace />
}

export default PrivateRoute
