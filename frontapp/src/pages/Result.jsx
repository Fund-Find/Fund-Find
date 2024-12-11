import React, { useEffect, useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import '../assets/css/result.css'

function Result() {
    const [mbti, setMbti] = useState('')
    const [recommendedEtfs, setRecommendedEtfs] = useState([])
    const location = useLocation()
    const navigate = useNavigate()

    useEffect(() => {
        const propensityId = location.state?.propensityId

        if (!propensityId) {
            alert('잘못된 접근입니다.')
            navigate('/survey')
            return
        }

        // MBTI 결과 가져오기
        fetch(`http://localhost:8080/api/v1/etf/propensity/${propensityId}`)
            .then((response) => response.json())
            .then((data) => {
                if (data.resultCode === '200') {
                    setMbti(data.data.surveyResult) // MBTI 결과 저장
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
    }, [navigate])

    // MBTI 설명을 반환하는 함수
    const getMbtiDescription = (category, subCategory) => {
        let description = ''

        // 카테고리별 설명
        switch (category) {
            case 'STOCK':
                description = '안정적이고 장기 투자를 선호하는 성향입니다.'
                break
            case 'BOND':
                description = '안전을 중시하며 꾸준한 수익을 추구하는 성향입니다.'
                break
            case 'COMMODITY':
                description = '실물 자산에 대한 투자를 선호하는 성향입니다.'
                break
            case 'SECTOR':
                description = '특정 산업에 대한 전문적인 투자를 선호하는 성향입니다.'
                break
            case 'HighLisk':
                description = '높은 위험을 감수하고 높은 수익을 추구하는 성향입니다.'
                break
            default:
                description = '투자 성향을 분석중입니다...'
        }

        return description
    }

    return (
        <div className="result-container">
            <h1>투자 성향 분석 결과</h1>
            <div className="result-content">
                <div className="mbti-result">
                    <h2>투자 성향 분석</h2>
                    <p>{mbti || '결과를 분석중입니다...'}</p>
                </div>

                <div className="recommended-etfs">
                    <h2>추천 ETF 목록</h2>
                    {recommendedEtfs.length > 0 ? (
                        <ul>
                            {recommendedEtfs.map((etf, index) => (
                                <li key={index}>
                                    <strong>{etf.name}</strong> ({etf.code})
                                    <br />
                                    카테고리: {etf.category} / {etf.subCategory}
                                </li>
                            ))}
                        </ul>
                    ) : (
                        <p>추천 ETF를 불러오는 중입니다...</p>
                    )}
                </div>
            </div>

            <div className="button-container">
                <button onClick={() => navigate('/survey')}>다시 설문하기</button>
            </div>
        </div>
    )
}

export default Result
