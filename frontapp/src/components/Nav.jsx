import React, { useState, useEffect } from 'react'
import { Link, useNavigate, useLocation } from 'react-router-dom'
import '@fortawesome/fontawesome-free/css/all.min.css'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faBars, faUser, faTimes } from '@fortawesome/free-solid-svg-icons'
import { faFacebook } from '@fortawesome/free-brands-svg-icons'
import { faSyncAlt } from '@fortawesome/free-solid-svg-icons'
import '../assets/css/Nav.css'

function Nav() {
    const [isLoggedIn, setIsLoggedIn] = useState(false)
    const [remainingTime, setRemainingTime] = useState(0)
    const [showRefreshButton, setShowRefreshButton] = useState(false)
    const [isToggled, setIsToggled] = useState(false)
    const [userToggled, setUserToggled] = useState(false)

    const navigate = useNavigate()
    const location = useLocation()

    const handleToggle = (menu) => {
        if (menu === 'nav') {
            setIsToggled(!isToggled)
            if (!isToggled) setUserToggled(false) // Nav가 열리면 User는 닫음
        } else if (menu === 'user') {
            setUserToggled(!userToggled)
            if (!userToggled) setIsToggled(false) // User가 열리면 Nav는 닫음
        }
    }

    // 남은 시간을 계산하는 함수
    const calculateRemainingTime = () => {
        const expirationTimeStr = localStorage.getItem('expirationTime')
        if (expirationTimeStr) {
            const expirationTime = parseInt(expirationTimeStr, 10)
            const now = Date.now()
            const timeLeftMs = expirationTime - now
            return Math.max(Math.floor(timeLeftMs / 1000), 0) // 초 단위 반환
        }
        return 0 // 만료된 경우 0 반환
    }

    // 스토리지 변경 감지 (다른 탭에서 로그인/로그아웃 시 상태 업데이트)
    useEffect(() => {
        const handleStorageChange = () => {
            const token = localStorage.getItem('accessToken')
            const timeLeft = calculateRemainingTime()

            if (token && timeLeft > 0) {
                setIsLoggedIn(true)
                setRemainingTime(timeLeft)
            } else {
                setIsLoggedIn(false)
            }
        }

        window.addEventListener('storage', handleStorageChange)

        return () => {
            window.removeEventListener('storage', handleStorageChange)
        }
    }, [])

    // 초기 상태 설정
    useEffect(() => {
        const token = localStorage.getItem('accessToken')
        const timeLeft = calculateRemainingTime()

        if (token && timeLeft > 0) {
            setIsLoggedIn(true)
            setRemainingTime(timeLeft)
        }
    }, [])

    // 남은 시간 감소 처리
    useEffect(() => {
        if (isLoggedIn && remainingTime > 0) {
            const interval = setInterval(() => {
                setRemainingTime((prevTime) => {
                    if (prevTime <= 1) {
                        handleLogout()
                        return 0
                    }
                    return prevTime - 1
                })
            }, 1000)

            return () => clearInterval(interval)
        }
    }, [isLoggedIn, remainingTime])

    // 갱신 버튼 표시 로직
    useEffect(() => {
        if (isLoggedIn && remainingTime <= 2980 && remainingTime > 0) {
            setShowRefreshButton(true)
        } else {
            setShowRefreshButton(false)
        }
    }, [isLoggedIn, remainingTime])

    const handleLogout = async () => {
        try {
            const currentPath = location.pathname

            // URL 패턴으로 보호된 경로 판단
            const isProtectedRoute = currentPath.startsWith('/user/') || currentPath.startsWith('/survey')

            const response = await fetch('http://localhost:8080/api/v1/user/logout', {
                method: 'POST',
                credentials: 'include', // HttpOnly 쿠키 포함
            })

            if (response.ok) {
                // 로컬 스토리지 정리
                localStorage.removeItem('expirationTime')
                localStorage.removeItem('accessToken')
                setIsLoggedIn(false)
                setRemainingTime(0)
                alert('로그아웃되었습니다.')

                // 보호된 경로라면 홈으로 이동, 아니라면 현재 경로 유지
                if (isProtectedRoute) {
                    navigate('/') // 홈으로 이동
                } else {
                    navigate(currentPath) // 기존 페이지로 이동
                }
            } else {
                alert('로그아웃 실패! 서버 응답을 확인하세요.')
            }
        } catch (error) {
            console.error('로그아웃 중 오류 발생:', error)
            alert('로그아웃 요청 중 오류가 발생했습니다.')
        }
    }

    const handleRefresh = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/v1/user/refresh', {
                method: 'POST',
                credentials: 'include',
            })
            if (response.ok) {
                const resData = await response.json()
                const newExpirationTime = resData.data?.expirationTime
                if (newExpirationTime) {
                    localStorage.setItem('expirationTime', newExpirationTime)
                    const now = Date.now()
                    const timeLeftMs = newExpirationTime - now
                    const timeLeftSec = Math.max(Math.floor(timeLeftMs / 1000), 0)
                    if (timeLeftSec > 0) {
                        setRemainingTime(timeLeftSec)
                        setIsLoggedIn(true)
                        alert('로그인 시간이 갱신되었습니다.')
                    }
                } else {
                    alert('갱신에 실패했습니다. 다시 시도해주세요.')
                }
            } else {
                alert('갱신 요청이 실패했습니다.')
            }
        } catch (error) {
            console.error('갱신 중 오류:', error)
            alert('갱신 중 문제가 발생했습니다.')
        }
    }

    const getFormattedTime = (seconds) => {
        const m = Math.floor(seconds / 60)
            .toString()
            .padStart(2, '0')
        const s = (seconds % 60).toString().padStart(2, '0')
        return `${m}:${s}`
    }

    return (
        <div id="navi" className={`${isToggled ? 'toggled' : ''} ${userToggled ? 'user-toggled' : ''}`}>
            {/* 햄버거 버튼(bar) */}
            <div className="toggle" onClick={() => handleToggle('nav')}>
                <FontAwesomeIcon icon={!isToggled ? faBars : faTimes} />
            </div>

            {/* faFacebook 로고  */}
            <div className="logo">
                <Link to="/">
                    <img src="/images/fflogo.webp" alt="FF Logo" />
                </Link>
            </div>

            {/* User 버튼 */}
            <div className="user" onClick={() => handleToggle('user')}>
                <FontAwesomeIcon icon={!userToggled ? faUser : faTimes} />
            </div>
            <ul className="Nav__menulist">
                <li>
                    <Link to="/">홈</Link>
                </li>
                <li>
                    <Link to="/etf/list">ETF 목록</Link>
                </li>
            </ul>

            <ul className="User__menulist">
                {!isLoggedIn ? (
                    <>
                        <li>
                            <Link to="/user/login" state={{ from: location.pathname }}>
                                로그인
                            </Link>
                        </li>
                        <li>
                            <Link to="/user/register" state={{ from: location.pathname }}>
                                회원가입
                            </Link>
                        </li>
                    </>
                ) : (
                    <>
                        <li>
                            <Link to="/user/profile">내 프로필</Link>
                        </li>
                        <li>
                            <button onClick={handleLogout}>로그아웃</button>
                        </li>
                        {showRefreshButton && (
                            <li className="refresh-button-container">
                                <span className="remaining-time">
                                    남은 시간: {remainingTime > 0 ? getFormattedTime(remainingTime) : '만료됨'}
                                </span>
                                <button className="refresh-button" onClick={handleRefresh}>
                                    <FontAwesomeIcon icon={faSyncAlt} />
                                    <span className="tooltip-text">로그인 시간 갱신</span>
                                </button>
                            </li>
                        )}
                    </>
                )}
            </ul>
        </div>
    )
}

export default Nav
