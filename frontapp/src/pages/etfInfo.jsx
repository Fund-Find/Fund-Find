import React, { useState, useEffect } from 'react'
import '../assets/css/etfInfo.css'

const ETFInfo = () => {
    // State to hold ETF info and error message
    const [etfInfo, setEtfInfo] = useState(null)
    const [error, setError] = useState(null)

    // Fetch ETF data (you can replace this with an actual API call)
    useEffect(() => {
        // Example: Simulate fetching data
        fetch('/api/etf-info') // Replace with your API endpoint
            .then((response) => {
                if (!response.ok) {
                    throw new Error('Failed to fetch ETF information')
                }
                return response.json()
            })
            .then((data) => setEtfInfo(data))
            .catch((err) => setError(err.message))
    }, [])

    return (
        <div>
            <h1>ETF 정보</h1>
            {etfInfo ? (
                <div className="info-box">
                    <pre>{JSON.stringify(etfInfo, null, 2)}</pre>
                </div>
            ) : error ? (
                <div className="error">
                    <p>{error}</p>
                </div>
            ) : (
                <p>Loading...</p>
            )}
        </div>
    )
}

export default ETFInfo
