import React, { useEffect, useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import Survey from '../components/Survey'
import '../assets/css/result.css'

function Result() {
    const [mbti, setMbti] = useState('')
    const [recommendedEtfs, setRecommendedEtfs] = useState([])
    const location = useLocation()
    const navigate = useNavigate()
    const [showSurveyPopup, setShowSurveyPopup] = useState(false)

    useEffect(() => {
        const propensityId = location.state?.propensityId

        if (!propensityId) {
            alert('잘못된 접근입니다.')
            navigate('/')
            return
        }

        // MBTI 결과 가져오기
        fetch(`http://localhost:8080/api/v1/etf/propensity/${propensityId}`)
            .then((response) => response.json())
            .then((data) => {
                if (data.resultCode === '200') {
                    setMbti(data.data.surveyResult)
                }
            })

        // 추천 ETF 목록 가져오기
        fetch(`http://localhost:8080/api/v1/etf/recommend/${propensityId}`)
            .then((response) => response.json())
            .then((data) => {
                if (data.resultCode === '200') {
                    setRecommendedEtfs(data.data)
                } else {
                    alert('결과를 가져오는데 실패했습니다.')
                }
            })
            .catch((error) => {
                console.error('Error:', error)
                alert('결과를 가져오는데 실패했습니다.')
            })
    }, [location.state?.propensityId, navigate])

    // MBTI 설명을 반환하는 함수
    const getMbtiDescription = (mbti) => {
        switch (mbti) {
            // 내향적(I) + 판단형(J) 성향
            case 'ISTJ':
                return '현실적이고 신중한 성향으로, 안전한 채권형 투자를 선호합니다.'
            case 'ISFJ':
                return '안정 지향적이고 보수적인 성향으로, 안정적인 대형주 투자를 선호합니다.'
            case 'INFJ':
                return '가치 지향적이고 장기 투자를 선호하는 성향으로, 헬스케어 섹터에 관심이 있습니다.'
            case 'INTJ':
                return '전략적이고 분석적인 성향으로, IT/반도체 섹터에 대한 투자를 선호합니다.'

            // 내향적(I) + 인식형(P) 성향
            case 'ISTP':
                return '논리적이고 실용적인 성향으로, IT/반도체 섹터에 대한 투자를 선호합니다.'
            case 'ISFP':
                return '예술적이고 가치를 중시하는 성향으로, 귀금속 관련 투자를 선호합니다.'
            case 'INFP':
                return '이상주의적이고 혁신을 중시하는 성향으로, 헬스케어 섹터에 관심이 있습니다.'
            case 'INTP':
                return '분석적이고 혁신 지향적인 성향으로, 성장 가능성이 높은 중소형주 투자를 선호합니다.'

            // 외향적(E) + 인식형(P) 성향
            case 'ESTP':
                return '모험적이고 기회주의적인 성향으로, 레버리지 ETF 투자를 통한 높은 수익을 추구합니다.'
            case 'ESFP':
                return '즉흥적이고 기회 포착을 중시하는 성향으로, 중소형주 투자를 선호합니다.'
            case 'ENFP':
                return '열정적이고 새로운 기회를 추구하는 성향으로, IT/반도체 섹터에 관심이 있습니다.'
            case 'ENTP':
                return '혁신적이고 도전적인 성향으로, 레버리지 ETF를 통한 공격적인 투자를 선호합니다.'

            // 외향적(E) + 판단형(J) 성향
            case 'ESTJ':
                return '체계적이고 효율성을 중시하는 성향으로, 금융 섹터 투자를 선호합니다.'
            case 'ESFJ':
                return '조화롭고 안정을 추구하는 성향으로, 해외 채권 투자를 선호합니다.'
            case 'ENFJ':
                return '성장 지향적이고 영향력을 추구하는 성향으로, 안정적인 대형주 투자를 선호합니다.'
            case 'ENTJ':
                return '전략적이고 성취 지향적인 성향으로, IT/반도체 섹터에 대한 투자를 선호합니다.'

            default:
                return '투자 성향을 분석중입니다...'
        }
    }

    const handleETFClick = (etfCode) => {
        navigate(`/etf/${etfCode}`)
    }

    return (
        <div className="result-container">
            <h1>투자 성향 분석 결과</h1>
            <div className="result-content">
                <div className="mbti-result">
                    <h2>투자 성향 분석</h2>
                    <p>{mbti || '결과를 분석중입니다...'}</p>
                    <p className="mbti-description">{mbti && getMbtiDescription(mbti)}</p>
                </div>

                <div className="recommended-etfs">
                    <h2>추천 ETF 목록</h2>
                    {recommendedEtfs.length > 0 ? (
                        <ul>
                            {recommendedEtfs.map((etf, index) => (
                                <li key={index} onClick={() => handleETFClick(etf.code)} style={{ cursor: 'pointer' }}>
                                    <strong>{etf.name}</strong> ({etf.code})
                                </li>
                            ))}
                        </ul>
                    ) : (
                        <p>추천 ETF를 불러오는 중입니다...</p>
                    )}
                </div>
            </div>

            <div className="button-container">
                <button onClick={() => setShowSurveyPopup(true)}>다시 설문하기</button>
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

export default Result
