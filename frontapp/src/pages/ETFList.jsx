import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { Virtual, Navigation, Pagination } from 'swiper/modules'
import { Swiper, SwiperSlide } from 'swiper/react'
import Survey from '../components/Survey'
import 'swiper/css'
import 'swiper/css/pagination'
import 'swiper/css/navigation'
import '../assets/css/etfList.css'

const ComparePopup = ({ etfCodes = [], onClose = () => {}, removeETF = () => {} }) => {
    if (!etfCodes || !Array.isArray(etfCodes)) {
        return null
    }

    const handleClose = (e) => {
        e.preventDefault()
        if (typeof onClose === 'function') {
            onClose()
        }
    }

    return (
        <div className="compare-popup-overlay">
            <div className="compare-popup">
                <button className="popup-close-btn" onClick={handleClose}>
                    <span>×</span>
                </button>
                <div className="compare-container">
                    {etfCodes.map((code) => (
                        <div key={code} className="compare-item">
                            <div className="compare-item-header">
                                <h3>ETF {code}</h3>
                                <button className="item-close-btn" onClick={() => removeETF(code)}>
                                    ×
                                </button>
                            </div>
                            <iframe src={`/etf/${code}`} title={`ETF ${code}`} />
                        </div>
                    ))}
                </div>
            </div>
        </div>
    )
}

export default function ETFList() {
    const [etfs, setEtfs] = useState([])
    const [searchTerm, setSearchTerm] = useState('')
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)
    const [sortedETFs, setSortedETFs] = useState([])
    const [swiperRef, setSwiperRef] = useState(null)
    const [currentPage, setCurrentPage] = useState(1)
    const [favorites, setFavorites] = useState(() => {
        const saved = localStorage.getItem('etfFavorites')
        return saved ? JSON.parse(saved) : []
    })
    const [compareList, setCompareList] = useState([])
    const [showComparePopup, setShowComparePopup] = useState(false)
    const [showFavoritesOnly, setShowFavoritesOnly] = useState(false)
    const itemsPerPage = 10
    const navigate = useNavigate()
    const [showSurveyPopup, setShowSurveyPopup] = useState(false)

    useEffect(() => {
        fetchETFs()
    }, [])

    useEffect(() => {
        let filtered = etfs.filter((etf) =>
            searchTerm ? etf.name.toLowerCase().includes(searchTerm.toLowerCase()) : true,
        )
        if (showFavoritesOnly) {
            filtered = filtered.filter((etf) => favorites.includes(etf.code))
        }
        if (filtered.length > 0) {
            const sorted = sortByPriceChangeRate(filtered)
            setSortedETFs(sorted)
            setCurrentPage(1)
        } else {
            setSortedETFs([])
        }
    }, [etfs, searchTerm, showFavoritesOnly, favorites])

    const sortByPriceChangeRate = (etfs) => {
        return [...etfs].sort((a, b) => {
            const rateA = parseFloat(a.priceChangeRate || 0)
            const rateB = parseFloat(b.priceChangeRate || 0)
            return rateB - rateA
        })
    }

    const fetchETFs = async () => {
        try {
            setLoading(true)
            const response = await fetch('http://localhost:8080/api/v1/etf/list')
            const data = await response.json()
            if (response.ok && data.resultCode === '200') {
                const etfList = Array.isArray(data.data) ? data.data : []
                setEtfs(etfList)
                setError(null)
            } else {
                throw new Error(data.msg)
            }
        } catch (error) {
            console.error('Error fetching ETF data:', error)
            setError(error.message || '데이터를 불러오는데 실패했습니다')
            setEtfs([])
        } finally {
            setLoading(false)
        }
    }

    const handleETFClick = (etfCode) => {
        navigate(`/etf/${etfCode}`)
    }

    const toggleFavorite = (event, etfCode) => {
        event.stopPropagation()
        setFavorites((prev) => {
            const newFavorites = prev.includes(etfCode) ? prev.filter((code) => code !== etfCode) : [...prev, etfCode]
            localStorage.setItem('etfFavorites', JSON.stringify(newFavorites))
            return newFavorites
        })
    }

    const toggleCompare = (event, etfCode) => {
        event.stopPropagation()
        setCompareList((prev) => {
            if (prev.includes(etfCode)) {
                return prev.filter((code) => code !== etfCode)
            }
            if (prev.length >= 3) {
                alert('최대 3개까지만 비교할 수 있습니다.')
                return prev
            }
            return [...prev, etfCode]
        })
    }

    const removeFromCompare = (etfCode) => {
        setCompareList((prev) => prev.filter((code) => code !== etfCode))
    }

    const indexOfLastItem = currentPage * itemsPerPage
    const indexOfFirstItem = indexOfLastItem - itemsPerPage
    const currentItems = sortedETFs.slice(indexOfFirstItem, indexOfLastItem)
    const totalPages = Math.ceil(sortedETFs.length / itemsPerPage)

    const handleSurveyClick = (e) => {
        e.preventDefault()
        setShowSurveyPopup(true)
    }

    return (
        <div className="container mx-auto px-4 py-8">
            <div className="mbti-banner" onClick={handleSurveyClick}>
                <span className="mbti-banner-text">투자성향 MBTI 분석하러 가기 →</span>
            </div>
            <h1 className="text-3xl font-bold mb-8">ETF 찾기</h1>
            {error && (
                <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
                    <p>{error}</p>
                    <button
                        onClick={fetchETFs}
                        className="mt-2 bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600"
                    >
                        다시 시도
                    </button>
                </div>
            )}
            <div className="search-controls">
                <div className="search-box">
                    <input
                        type="text"
                        placeholder="ETF를 검색해보세요"
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                </div>
                <div className="filter-controls">
                    <label>
                        <input
                            type="checkbox"
                            checked={showFavoritesOnly}
                            onChange={(e) => setShowFavoritesOnly(e.target.checked)}
                        />
                        즐겨찾기만 보기
                    </label>
                </div>
            </div>
            <div className="etf-list mb-4">
                <div className="list-header">
                    <div>회원사 명</div>
                    <div>이름</div>
                    <div>가격</div>
                    <div>ETF 구성종목 수</div>
                    <div>ETF 순자산 총액</div>
                    <div className="nav-column">
                        NAV
                        <div className="nav-tooltip">
                            NAV = (총 자산 - 총 부채) / 발행 주식수
                            <br />
                            <br />
                            프리미엄: 시장가격 &gt; NAV
                            <br />
                            디스카운트: 시장가격 &lt; NAV
                        </div>
                    </div>
                    <div>전일 최종 NAV</div>
                    <div>NAV 변동액</div>
                    <div>ETF 배당주기</div>
                    <div>
                        전일 대비
                        <br />
                        가격 변화
                    </div>
                    <div>
                        전일 대비
                        <br />
                        등락률
                    </div>
                    <div>즐겨찾기</div>
                    <div>비교</div>
                </div>
                {loading ? (
                    <div className="text-center py-8">로딩 중...</div>
                ) : sortedETFs.length === 0 ? (
                    <div className="text-center py-8">{error ? '' : '검색 결과가 없습니다'}</div>
                ) : (
                    currentItems.map((etf) => (
                        <div
                            key={etf.code}
                            className="etf-item"
                            onClick={() => handleETFClick(etf.code)}
                            style={{ cursor: 'pointer' }}
                        >
                            <div className="company-name">{etf.company || 'N/A'}</div>
                            <div className="text-left">{etf.name}</div>
                            <div className={`${Number(etf.currentPrice) >= 0 ? 'up' : 'down'}`}>
                                {etf.currentPrice || '0'}원
                            </div>
                            {/* <div>{etf.currentPrice || '0'}원</div> */}
                            <div>{etf.componentCount || '0'}개</div>
                            <div>{etf.netAsset || '0'}억원</div>
                            <div>{etf.nav || '0'}원</div>
                            <div>{etf.prevNav || '0'}원</div>
                            <div className={`${Number(etf.navChange) >= 0 ? 'up' : 'down'}`}>
                                {etf.navChange || '0'}원
                            </div>
                            <div>{etf.dividendCycle || '0'}개월</div>
                            <div>{etf.priceChange || '0'}원</div>
                            <div className={`${Number(etf.priceChangeRate) >= 0 ? 'up' : 'down'}`}>
                                {etf.priceChangeRate || '0'}%
                            </div>
                            <div>
                                <button
                                    onClick={(e) => toggleFavorite(e, etf.code)}
                                    className={`favorite-btn ${favorites.includes(etf.code) ? 'active' : ''}`}
                                >
                                    {favorites.includes(etf.code) ? '★' : '☆'}
                                </button>
                            </div>
                            <div>
                                <input
                                    type="checkbox"
                                    checked={compareList.includes(etf.code)}
                                    onChange={(e) => toggleCompare(e, etf.code)}
                                    onClick={(e) => e.stopPropagation()}
                                />
                            </div>
                        </div>
                    ))
                )}
            </div>
            <div className="compare-controls">
                <button
                    onClick={() => setShowComparePopup(true)}
                    disabled={compareList.length === 0}
                    className="compare-btn"
                >
                    선택한 ETF 비교하기 ({compareList.length}/3)
                </button>
            </div>
            {showComparePopup && (
                <ComparePopup
                    etfCodes={compareList}
                    onClose={() => setShowComparePopup(false)}
                    removeETF={removeFromCompare}
                />
            )}
            {!loading && sortedETFs.length > 0 && (
                <div className="pagination-controls">
                    <button onClick={() => setCurrentPage(1)} disabled={currentPage === 1}>
                        &lt;&lt;
                    </button>
                    <button onClick={() => setCurrentPage(currentPage - 1)} disabled={currentPage === 1}>
                        &lt;
                    </button>
                    <span>
                        {currentPage} / {totalPages}
                    </span>
                    <button onClick={() => setCurrentPage(currentPage + 1)} disabled={currentPage === totalPages}>
                        &gt;
                    </button>
                    <button onClick={() => setCurrentPage(totalPages)} disabled={currentPage === totalPages}>
                        &gt;&gt;
                    </button>
                </div>
            )}
            <div className="mt-12">
                <h2 className="text-2xl font-bold mb-6">등락률 Best 펀드</h2>
                <Swiper
                    modules={[Virtual, Navigation, Pagination]}
                    onSwiper={setSwiperRef}
                    slidesPerView={5}
                    centeredSlides={false}
                    spaceBetween={28}
                    navigation={true}
                    pagination={{
                        type: 'fraction',
                        clickable: true,
                    }}
                    breakpoints={{
                        320: {
                            slidesPerView: 1,
                            spaceBetween: 10,
                        },
                        640: {
                            slidesPerView: 2,
                            spaceBetween: 15,
                        },
                        768: {
                            slidesPerView: 3,
                            spaceBetween: 15,
                        },
                        1024: {
                            slidesPerView: 4,
                            spaceBetween: 20,
                        },
                        1280: {
                            slidesPerView: 5,
                            spaceBetween: 20,
                        },
                    }}
                    virtual
                    className="bestETFSwiper"
                >
                    {sortedETFs.map((etf, index) => (
                        <SwiperSlide
                            key={etf.code}
                            virtualIndex={index}
                            onClick={() => handleETFClick(etf.code)}
                            style={{ cursor: 'pointer' }}
                        >
                            <div className="bg-white rounded-2xl shadow-md p-4 relative h-full">
                                <div className="flex justify-between items-center mb-2">
                                    <span className="text-lg font-medium text-gray-900">{index + 1}</span>
                                    <div className="flex gap-2">
                                        <button
                                            onClick={(e) => toggleFavorite(e, etf.code)}
                                            className={`favorite-btn ${favorites.includes(etf.code) ? 'active' : ''}`}
                                        >
                                            {favorites.includes(etf.code) ? '★' : '☆'}
                                        </button>
                                        <input
                                            type="checkbox"
                                            checked={compareList.includes(etf.code)}
                                            onChange={(e) => toggleCompare(e, etf.code)}
                                            onClick={(e) => e.stopPropagation()}
                                        />
                                    </div>
                                </div>
                                <div className="flex items-start gap-2 mb-6">
                                    <div className="w-10 h-10 rounded-full bg-yellow-100 flex items-center justify-center"></div>
                                    <div className="flex-1">
                                        <h3 className="font-bold text-sm leading-tight">{etf.name}</h3>
                                        <p className="text-xs text-gray-500 mt-1">{etf.code}</p>
                                    </div>
                                </div>
                                <div className={`priceChangeRate ${Number(etf.priceChangeRate) >= 0 ? 'up' : 'down'}`}>
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
                <p className="text-xs text-gray-400 mt-2">
                    · 기준일 전일 대비 등락률
                    <br />· 등락률 데이터는 펀드의 과거 성과이며 향후 수익을 보장하지 않습니다
                </p>
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
        </div>
    )
}
