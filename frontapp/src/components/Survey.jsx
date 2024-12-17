import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import '../assets/css/survey.css'

function Survey() {
    const navigate = useNavigate()
    const [answers, setAnswers] = useState({
        q1: '',
        q2: '',
        q3: '',
        q4: '',
    })

    // 라디오 버튼 변경 핸들러
    const handleRadioChange = (event) => {
        const { name, value } = event.target
        setAnswers((prev) => ({
            ...prev,
            [name]: value,
        }))
    }

    // 제출 핸들러
    const handleSubmit = async () => {
        // 모든 질문에 답변했는지 확인
        if (!answers.q1 || !answers.q2 || !answers.q3 || !answers.q4) {
            alert('모든 질문에 답변해주세요.')
            return
        }

        try {
            const response = await fetch('http://localhost:8080/api/v1/etf/survey/submit', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(answers),
                credentials: 'include',
            })

            const result = await response.json()

            if (result.resultCode === '200') {
                alert('설문 제출 완료!')
                navigate('/result', {
                    state: { propensityId: result.data },
                })
            } else {
                alert(result.msg || '제출 중 오류가 발생했습니다.')
            }
        } catch (error) {
            console.error('Error:', error)
            alert('제출 중 오류가 발생했습니다.')
        }
    }

    return (
        <div className="container">
            <header className="header">
                <h1>투자성향 분석</h1>
            </header>
            <section className="instructions">
                <p>
                    고객님에게 가장 적합한 상품을 제공하기 위해서는 고객님의 정확한 답변이 필요합니다.
                    <br />
                    최대한 고객님의 상황에 부합하거나 가장 가까운 항목을 선택하여 주시기 바랍니다.
                    <br />
                    투자성향 분석은 <strong>1일 2회만 분석</strong> 가능하니 신중히 답변해주시기 바랍니다.
                </p>
            </section>
            <section className="questions">
                <h2>1. 예상 수익률이 높은 상품이 있지만 손실 위험도 있습니다. 어떤 선택을 하시겠습니까?</h2>
                <ul>
                    <li>
                        <input
                            type="radio"
                            id="q1_option1"
                            name="q1"
                            value="option1"
                            checked={answers.q1 === 'option1'}
                            onChange={handleRadioChange}
                        />
                        <label htmlFor="q1_option1">안정적인 자산으로 손실을 최소화합니다.</label>
                    </li>
                    <li>
                        <input
                            type="radio"
                            id="q1_option2"
                            name="q1"
                            value="option2"
                            checked={answers.q1 === 'option2'}
                            onChange={handleRadioChange}
                        />
                        <label htmlFor="q1_option2">수익을 극대화하기 위해 도전해봅니다.</label>
                    </li>
                </ul>

                <h2>2. 투자를 결정할 때, 어떤 점을 더 중요하게 보시나요?</h2>
                <ul>
                    <li>
                        <input
                            type="radio"
                            id="q2_option1"
                            name="q2"
                            value="option1"
                            checked={answers.q2 === 'option1'}
                            onChange={handleRadioChange}
                        />
                        <label htmlFor="q2_option1">현재 가치를 판단할 수 있는 재무 지표</label>
                    </li>
                    <li>
                        <input
                            type="radio"
                            id="q2_option2"
                            name="q2"
                            value="option2"
                            checked={answers.q2 === 'option2'}
                            onChange={handleRadioChange}
                        />
                        <label htmlFor="q2_option2">회사의 성장 가능성</label>
                    </li>
                </ul>

                <h2>3. 원금 손실 가능성이 높은 대신 높은 수익을 기대할 수 있는 상품에 대해 어떻게 생각하시나요?</h2>
                <ul>
                    <li>
                        <input
                            type="radio"
                            id="q3_option1"
                            name="q3"
                            value="option1"
                            checked={answers.q3 === 'option1'}
                            onChange={handleRadioChange}
                        />
                        <label htmlFor="q3_option1">손실 위험이 낮은 안정적인 상품을 선택합니다.</label>
                    </li>
                    <li>
                        <input
                            type="radio"
                            id="q3_option2"
                            name="q3"
                            value="option2"
                            checked={answers.q3 === 'option2'}
                            onChange={handleRadioChange}
                        />
                        <label htmlFor="q3_option2">높은 수익을 기대하며 위험을 감수합니다.</label>
                    </li>
                </ul>

                <h2>4. 시장 변동성이 큰 상황에서 투자 계획은 어떻게 하시겠습니까?</h2>
                <ul>
                    <li>
                        <input
                            type="radio"
                            id="q4_option1"
                            name="q4"
                            value="option1"
                            checked={answers.q4 === 'option1'}
                            onChange={handleRadioChange}
                        />
                        <label htmlFor="q4_option1">변동성이 낮은 자산으로 갈아탑니다.</label>
                    </li>
                    <li>
                        <input
                            type="radio"
                            id="q4_option2"
                            name="q4"
                            value="option2"
                            checked={answers.q4 === 'option2'}
                            onChange={handleRadioChange}
                        />
                        <label htmlFor="q4_option2">변동성이 크더라도 장기적인 수익을 기대하며 투자합니다.</label>
                    </li>
                </ul>
            </section>
            <div className="button-container">
                <button onClick={handleSubmit}>제출하기</button>
            </div>
        </div>
    )
}

export default Survey
