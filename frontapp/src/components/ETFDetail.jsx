import React, { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import styles from '../assets/css/module/ETFDetail.module.css'

function ETFDetail() {
    const [etfInfo, setEtfInfo] = useState(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)
    const { code } = useParams()
    const navigate = useNavigate()

    useEffect(() => {
        fetchETFInfo()
    }, [code])

    const fetchETFInfo = async () => {
        try {
            const response = await fetch(`http://localhost:8080/api/v1/etf/${code}`)
            const data = await response.json()
            console.log('API Response:', data)
            if (data.resultCode === '200') {
                const etfData = parseETFData(data.data)
                setEtfInfo(etfData)
            } else {
                throw new Error(data.msg || 'ETF 정보를 불러오는데 실패했습니다.')
            }
        } catch (error) {
            console.error('ETF 정보 조회 실패:', error)
            setError(error.message)
        } finally {
            setLoading(false)
        }
    }

    const parseETFData = (dataString) => {
        const sections = dataString.split('\n\n')
        const etfData = {
            basicInfo: {},
            componentInfo: [],
        }

        // 기본 정보 파싱
        const basicInfoLines = sections[0].split('\n')
        basicInfoLines.forEach((line) => {
            if (line.includes(':')) {
                const [key, value] = line.split(':').map((item) => item.trim())
                etfData.basicInfo[key] = value
            }
        })

        // 구성종목 정보 파싱
        if (sections.length > 1) {
            const componentLines = sections[1].split('\n')
            let isHeader = true
            componentLines.forEach((line) => {
                if (line.includes('구성종목 정보가 없습니다.')) {
                    etfData.componentInfo = []
                    return
                }
                if (!isHeader && line.trim() !== '' && !line.startsWith('=')) {
                    const values = line.split(/\s+/).filter(Boolean)
                    if (values.length >= 5) {
                        etfData.componentInfo.push({
                            stockName: values[0],
                            stockCode: values[1],
                            marketCap: values[2],
                            weightedMarketCap: values[3],
                            weight: values[4],
                        })
                    }
                }
                isHeader = false
            })
        }

        return etfData
    }

    const handleGoBack = () => {
        navigate(-1)
    }

    return (
        <div className={styles.etfDetailContainer}>
            <button onClick={handleGoBack} className={styles.backButton}>
                뒤로 가기
            </button>
            {loading ? (
                <div>로딩 중...</div>
            ) : error ? (
                <div>에러 발생: {error}</div>
            ) : etfInfo ? (
                <div className={styles.etfInfo}>
                    <h2>ETF 상세 정보</h2>
                    <div className={styles.infoTableWrapper}>
                        <table className={styles.infoTable}>
                            <thead>
                                <tr>
                                    <th>종목명</th>
                                    <th>회원사명</th>
                                    <th>구성종목 수</th>
                                    <th>순자산 총액</th>
                                    <th>NAV</th>
                                    <th>전일 최종 NAV</th>
                                    <th>전일 대비 NAV</th>
                                    <th>배당주기</th>
                                    <th>현재가</th>
                                    <th>전일대비</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td>{etfInfo.basicInfo['종목명']}</td>
                                    <td>{etfInfo.basicInfo['회원사명']}</td>
                                    <td>{etfInfo.basicInfo['ETF 구성종목 수(최대 100개)']}</td>
                                    <td>{etfInfo.basicInfo['ETF 순자산 총액']}</td>
                                    <td>{etfInfo.basicInfo['NAV']}</td>
                                    <td>{etfInfo.basicInfo['전일 최종 NAV']}</td>
                                    <td>{etfInfo.basicInfo['전일 대비 NAV 변동액']}</td>
                                    <td>{etfInfo.basicInfo['ETF 배당주기']}</td>
                                    <td>{etfInfo.basicInfo['현재가']}</td>
                                    <td
                                        className={
                                            Number(etfInfo.basicInfo['전일대비'].replace(/[^-\d.]/g, '')) > 0
                                                ? styles.positive
                                                : styles.negative
                                        }
                                    >
                                        {etfInfo.basicInfo['전일대비']} ({etfInfo.basicInfo['등락률']})
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                        <h3 className={styles.componentTitle}>구성종목 정보</h3>
                        <table className={styles.componentTable}>
                            <thead>
                                <tr>
                                    <th>종목명</th>
                                    <th>종목코드</th>
                                    <th>구성종목 시가총액(억 원)</th>
                                    <th>비중에 따른 시가총액(억 원)</th>
                                    <th>비중(%)</th>
                                </tr>
                            </thead>
                            <tbody>
                                {etfInfo.componentInfo.length > 0 ? (
                                    etfInfo.componentInfo.map((item, index) => (
                                        <tr key={index}>
                                            <td>{item.stockName}</td>
                                            <td>{item.stockCode}</td>
                                            <td>{item.marketCap}</td>
                                            <td>{item.weightedMarketCap}</td>
                                            <td>{item.weight}</td>
                                        </tr>
                                    ))
                                ) : (
                                    <tr>
                                        <td colSpan="5" className={styles.noData}>
                                            해당 ETF는 주식 종목을 갖고 있지 않습니다.
                                        </td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                </div>
            ) : (
                <div>ETF 정보를 찾을 수 없습니다.</div>
            )}
        </div>
    )
}

export default ETFDetail
