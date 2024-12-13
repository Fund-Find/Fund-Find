import React, { useState, useEffect } from 'react'
import { Link, useNavigate } from 'react-router-dom'

function Nav() {
    const [isLoggedIn, setIsLoggedIn] = useState(false)
    const [remainingTime, setRemainingTime] = useState(0)
    const [showRefreshButton, setShowRefreshButton] = useState(false)
    const navigate = useNavigate()

    // 남은 시간을 "분:초" 형태의 문자열로 변환하는 함수
    const getFormattedTime = (seconds) => {
        const m = Math.floor(seconds / 60)
            .toString()
            .padStart(2, '0')
        const s = (seconds % 60).toString().padStart(2, '0')
        return `${m}:${s}`
    }

    useEffect(() => {
        const expirationTimeStr = localStorage.getItem('expirationTime')
        if (expirationTimeStr) {
            const expirationTime = parseInt(expirationTimeStr, 10)
            const now = Date.now()
            const timeLeftMs = expirationTime - now
            const timeLeftSec = Math.max(Math.floor(timeLeftMs / 1000), 0)

            if (timeLeftSec > 0) {
                setIsLoggedIn(true)
                setRemainingTime(timeLeftSec)
            } else {
                setIsLoggedIn(false)
                localStorage.removeItem('expirationTime')
                localStorage.removeItem('accessToken')
            }
        } else {
            setIsLoggedIn(false)
        }
    }, [])

    // 매초 남은 시간을 1초씩 감소
    useEffect(() => {
        if (isLoggedIn && remainingTime > 0) {
            const interval = setInterval(() => {
                setRemainingTime((prev) => {
                    const nextVal = prev - 1
                    if (nextVal <= 0) {
                        // 만료 시 처리
                        setIsLoggedIn(false)
                        localStorage.removeItem('expirationTime')
                        localStorage.removeItem('accessToken')
                        return 0
                    }
                    return nextVal
                })
            }, 1000)

            return () => clearInterval(interval)
        }
    }, [isLoggedIn, remainingTime])

    // 남은 시간이 300초 이하로 내려가면 갱신 버튼 표시
    useEffect(() => {
        if (isLoggedIn && remainingTime <= 2980 && remainingTime > 0) {
            setShowRefreshButton(true)
        } else {
            setShowRefreshButton(false)
        }
    }, [isLoggedIn, remainingTime])

    const handleLogout = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/v1/user/logout', {
                method: 'POST',
                credentials: 'include',
            })

            if (response.ok) {
                setIsLoggedIn(false)
                alert('로그아웃 성공!')
                localStorage.removeItem('expirationTime')
                localStorage.removeItem('accessToken')
                setIsLoggedIn(false)
                window.history.back()
            } else {
                alert('로그아웃 실패!')
            }
        } catch (error) {
            console.error('로그아웃 중 오류 발생:', error)
            alert('로그아웃 중 오류가 발생했습니다.')
        }
    }

    const handleRefresh = async () => {
        // refresh 토큰은 서버에서 HttpOnly 쿠키로 관리 중이라 가정
        // /refresh 요청시 서버가 새로운 accessToken, expirationTime을 반환해야 함
        try {
            const response = await fetch('http://localhost:8080/api/v1/user/refresh', {
                method: 'POST',
                credentials: 'include',
            })
            if (response.ok) {
                // 서버에서 새로운 expirationTime 제공한다고 가정
                // {"expirationTime": 1734056000000, ...}
                const resData = await response.json()
                // 응답 구조에 따라 조정 필요
                // 예: { "resultCode":"200", "data":{"expirationTime":1734056000000}}
                const newExpirationTime = resData.data?.expirationTime
                if (newExpirationTime) {
                    localStorage.setItem('expirationTime', newExpirationTime)
                    const now = Date.now()
                    const timeLeftMs = newExpirationTime - now
                    const timeLeftSec = Math.max(Math.floor(timeLeftMs / 1000), 0)
                    if (timeLeftSec > 0) {
                        setRemainingTime(timeLeftSec)
                        setIsLoggedIn(true)
                    }
                    alert('로그인 시간 갱신 성공!')
                } else {
                    alert('새로운 만료 시간을 가져올 수 없습니다.')
                }
            } else {
                alert('로그인 시간 갱신 실패!')
            }
        } catch (error) {
            console.error('갱신 중 오류:', error)
            alert('로그인 시간 갱신 중 오류가 발생했습니다.')
        }
    }

    return (
        <ul>
            <li>
                <Link to="/">홈</Link>
            </li>
            {!isLoggedIn ? (
                <li>
                    <Link to="/auth/login">로그인</Link>
                </li>
            ) : (
                <>
                    <li>
                        <button
                            onClick={handleLogout}
                            style={{
                                background: 'none',
                                border: 'none',
                                color: 'inherit',
                                cursor: 'pointer',
                                padding: '0',
                            }}
                        >
                            로그아웃
                        </button>
                    </li>
                    {isLoggedIn && (
                        <>
                            <li>
                                <span style={{ marginLeft: '10px', color: 'gray' }}>
                                    남은 시간: {remainingTime > 0 ? getFormattedTime(remainingTime) : '만료됨'}
                                </span>
                            </li>
                            <li>
                                <Link to="/user/profile">내 프로필</Link>
                            </li>
                        </>
                    )}
                    {showRefreshButton && (
                        <li>
                            <button
                                onClick={handleRefresh}
                                style={{
                                    marginLeft: '10px',
                                    background: '#4CAF50',
                                    color: '#fff',
                                    border: 'none',
                                    padding: '5px 10px',
                                    cursor: 'pointer',
                                    borderRadius: '3px',
                                }}
                            >
                                로그인 시간 갱신
                            </button>
                        </li>
                    )}
                </>
            )}
            <li>
                <Link to="/article/list">게시글 목록</Link>
            </li>
        </ul>
    )
}

export default Nav
