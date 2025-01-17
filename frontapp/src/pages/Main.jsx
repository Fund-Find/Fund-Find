// Main.jsx
import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { Swiper, SwiperSlide } from 'swiper/react'
import { Virtual, Navigation, Pagination } from 'swiper/modules'
import Survey from '../components/Survey'
import 'swiper/css'
import 'swiper/css/pagination'
import 'swiper/css/navigation'
import '../assets/css/main.css'

function Home() {
    const navigate = useNavigate()
    const [showSurveyPopup, setShowSurveyPopup] = useState(false)
    const [sortedETFs, setSortedETFs] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)

    const [username, setUsername] = useState('')
    const [password, setPassword] = useState('')
    const [errorMessage, setErrorMessage] = useState('')

    const [isLoggedIn, setIsLoggedIn] = useState(false)
    const [profileData, setProfileData] = useState(null)

    const fetchProfile = async () => {
        try {
            const token = localStorage.getItem('accessToken')
            const expirationTimeStr = localStorage.getItem('expirationTime')
            const now = Date.now()

            if (token && expirationTimeStr) {
                const expirationTime = parseInt(expirationTimeStr, 10)
                if (now < expirationTime) {
                    const response = await fetch('http://localhost:8080/api/v1/user/profile', {
                        method: 'GET',
                        credentials: 'include',
                    })
                    const data = await response.json()
                    if (data.resultCode === '200') {
                        setProfileData(data.data)
                        setIsLoggedIn(true)
                        return
                    }
                }
            }
            // 토큰이 없거나 만료되었거나 프로필 fetch 실패 시
            setProfileData(null)
            setIsLoggedIn(false)
        } catch (error) {
            console.error('프로필 정보를 가져오는데 실패했습니다: ', error)
            setProfileData(null)
            setIsLoggedIn(false)
        }
    }

    // 페이지 로딩 시 한 번 상태 체크
    useEffect(() => {
        fetchProfile()
    }, [])

    // storage 이벤트를 통해 localStorage 변경 감지
    useEffect(() => {
        const handleStorageChange = () => {
            fetchProfile()
        }

        window.addEventListener('storage', handleStorageChange)
        return () => {
            window.removeEventListener('storage', handleStorageChange)
        }
    }, [])

    const handleLogin = async (e) => {
        e.preventDefault()
        const requestData = { username, password }

        try {
            const response = await fetch('http://localhost:8080/api/v1/user/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(requestData),
                credentials: 'include',
            })

            const data = await response.json()

            if (response.ok) {
                // 로그인 성공 처리
                if (data.msg && data.msg.includes('토큰 발급 성공')) {
                    const accessTokenMatch = data.msg.match(/토큰 발급 성공: (.+)/)
                    const extractedAccessToken = accessTokenMatch ? accessTokenMatch[1] : null

                    if (extractedAccessToken) {
                        localStorage.setItem('accessToken', extractedAccessToken)
                    }

                    if (data.data?.expirationTime) {
                        localStorage.setItem('expirationTime', data.data.expirationTime)
                    }

                    alert('로그인 성공')
                    // storage 이벤트 발생 -> Main에서 감지
                    window.dispatchEvent(new Event('storage'))
                }
            } else {
                setErrorMessage(data.message || '로그인에 실패했습니다.')
            }
        } catch (error) {
            console.error('로그인 요청 실패:', error)
            setErrorMessage('서버와 연결할 수 없습니다.')
        }
    }

    const quizImages = [
        { id: 384, title: '인기 퀴즈1' },
        { id: 385, title: '인기 퀴즈2' },
        { id: 386, title: '인기 퀴즈3' },
    ]

    useEffect(() => {
        fetchETFs()
    }, [])

    const fetchETFs = async () => {
        try {
            setLoading(true)
            const response = await fetch('http://localhost:8080/api/v1/etf/list')
            const data = await response.json()
            if (response.ok && data.resultCode === '200') {
                const etfList = Array.isArray(data.data) ? data.data : []
                const sorted = sortByPriceChangeRate(etfList)
                setSortedETFs(sorted)
                setError(null)
            } else {
                throw new Error(data.msg)
            }
        } catch (error) {
            console.error('Error fetching ETF data:', error)
            setError(error.message || '데이터를 불러오는데 실패했습니다')
            setSortedETFs([])
        } finally {
            setLoading(false)
        }
    }

    const sortByPriceChangeRate = (etfs) => {
        return [...etfs].sort((a, b) => {
            const rateA = parseFloat(a.priceChangeRate || 0)
            const rateB = parseFloat(b.priceChangeRate || 0)
            return rateB - rateA
        })
    }

    const handleSurveyClick = () => {
        setShowSurveyPopup(true)
    }

    const handleETFClick = (etfCode) => {
        navigate(`/etf/${etfCode}`)
    }

    return (
        <div className="home-container">
            <div className="top-banner-section">
                {/* 투자성향 분석 배너 */}
                <div className="mbti-banner" onClick={handleSurveyClick}>
                    <div className="banner-content">
                        <div className="banner-text">
                            <span className="mbti-banner-text">투자성향 분석하러 가기 →</span>
                        </div>
                        <div className="propensity-types">
                            <img src="images/투자성향분석 배너.png" alt="투자성향 유형" className="propensity-image" />
                        </div>
                    </div>
                </div>

                {/* 로그인 섹션 */}
                <div className="login-banner">
                    {!isLoggedIn ? (
                        <form onSubmit={handleLogin} className="login-form">
                            <input
                                type="text"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                placeholder="아이디를 입력하세요"
                                className="login-input"
                            />
                            <input
                                type="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                placeholder="비밀번호를 입력하세요"
                                className="login-input"
                            />
                            {errorMessage && <div className="error-message">{errorMessage}</div>}
                            <button type="submit" className="login-btn">
                                로그인
                            </button>
                            <div className="login-links">
                                <a href="/user/findId">아이디 찾기</a>
                                <span> | </span>
                                <a href="/user/resetpw">비밀번호 재발급</a>
                                <span> | </span>
                                <a href="/user/register">회원가입</a>
                            </div>
                        </form>
                    ) : (
                        <div className="logged-in-section">
                            {profileData && profileData.thumbnailImg ? (
                                <img
                                    src={profileData.thumbnailImg}
                                    alt="프로필 이미지"
                                    className="profile-thumbnail"
                                    style={{ width: '80px', height: '80px', borderRadius: '50%' }}
                                />
                            ) : (
                                <div className="no-profile-image">프로필 이미지 없음</div>
                            )}
                            <div className="welcome-message">
                                <span>{profileData?.nickname || profileData?.username}님 환영합니다!</span>
                            </div>
                        </div>
                    )}
                </div>
            </div>

            {showSurveyPopup && (
                <div className="survey-popup-overlay">
                    <div className="survey-popup">
                        <button className="popup-close-btn" onClick={() => setShowSurveyPopup(false)}>
                            <span>×</span>
                        </button>
                        <Survey onClose={() => setShowSurveyPopup(false)} />
                    </div>
                </div>
            )}

            <div className="quiz-section">
                <h2>인기 퀴즈</h2>
                <div className="quiz-grid">
                    {quizImages.map((quiz) => (
                        <div key={quiz.id} className="quiz-card" onClick={() => navigate(`/quizshow/${quiz.id}`)}>
                            <div className="quiz-image-placeholder">
                                <img src="/images/quizShow/cryptocurrency.jpg" alt="" />
                            </div>
                            <h3>{quiz.title}</h3>
                        </div>
                    ))}
                </div>
                <button className="gotoquizlists" onClick={() => navigate('/quizshow/list')}>
                    퀴즈 전체 목록 보기
                </button>
            </div>

            <div className="best-etf-section">
                <h2 className="text-2xl font-bold mb-6">등락률 Best 펀드</h2>
                {loading ? (
                    <div className="text-center py-8">로딩 중...</div>
                ) : error ? (
                    <div className="text-center py-8 text-red-500">{error}</div>
                ) : (
                    <Swiper
                        modules={[Virtual, Navigation, Pagination]}
                        slidesPerView="auto"
                        spaceBetween={20}
                        centeredSlides={false}
                        navigation={true}
                        pagination={{
                            type: 'fraction',
                            clickable: true,
                        }}
                        breakpoints={{
                            320: { slidesPerView: 1, spaceBetween: 10 },
                            640: { slidesPerView: 2, spaceBetween: 15 },
                            768: { slidesPerView: 3, spaceBetween: 15 },
                            1024: { slidesPerView: 4, spaceBetween: 20 },
                            1280: { slidesPerView: 5, spaceBetween: 20 },
                        }}
                        className="bestETFSwiper"
                    >
                        {sortedETFs.map((etf, index) => (
                            <SwiperSlide
                                key={etf.code}
                                onClick={() => handleETFClick(etf.code)}
                                style={{ cursor: 'pointer' }}
                            >
                                <div className="bg-white rounded-2xl shadow-md p-4 relative h-full">
                                    <div className="flex justify-between items-center mb-2">
                                        <span className="text-lg font-medium text-gray-900">{index + 1}</span>
                                    </div>
                                    <div className="flex items-start gap-2 mb-6">
                                        <div className="w-10 h-10 rounded-full bg-yellow-100 flex items-center justify-center"></div>
                                        <div className="flex-1">
                                            <h3 className="font-bold text-sm leading-tight">{etf.name}</h3>
                                            <p className="text-xs text-gray-500 mt-1">{etf.code}</p>
                                        </div>
                                    </div>
                                    <div
                                        className={`priceChangeRate ${
                                            Number(etf.priceChangeRate) >= 0 ? 'up' : 'down'
                                        }`}
                                    >
                                        {etf.priceChangeRate || '0'}%
                                    </div>
                                    <div className="flex justify-between items-center">
                                        <span className="text-sm text-gray-500">현재가</span>
                                        <span className="text-sm font-medium">
                                            {Number(etf.currentPrice).toLocaleString()} 원
                                        </span>
                                    </div>
                                    <div className="text-xs text-gray-400 text-right mt-1">
                                        기준일 {new Date().toLocaleDateString('ko-KR').slice(0, -1)}
                                    </div>
                                </div>
                            </SwiperSlide>
                        ))}
                    </Swiper>
                )}
                <p className="text-xs text-gray-400 mt-2">
                    · 기준일 전일 대비 등락률
                    <br />· 등락률 데이터는 펀드의 과거 성과이며 향후 수익을 보장하지 않습니다
                </p>
            </div>
        </div>
    )
}

export default Home
