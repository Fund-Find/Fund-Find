import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Modal from '../components/Modal';
import '../assets/css/QuizShowDetail.css';

const QuizShowDetail = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [quizShow, setQuizShow] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showQuizModal, setShowQuizModal] = useState(false);

    useEffect(() => {
        fetchQuizShow();
    }, [id]);

    const fetchQuizShow = async () => {
        try {
            setLoading(true);
            const token = localStorage.getItem('accessToken');
            const headers = {
                'Content-Type': 'application/json'
            };
            
            if (token) {
                headers['Authorization'] = `Bearer ${token}`;
            }
    
            const response = await fetch(`http://localhost:8080/api/v1/quizshow/${id}`, {
                headers: headers,
                credentials: 'include'  // 쿠키를 포함하여 요청
            });
    
            if (!response.ok) throw new Error('퀴즈쇼를 불러오는데 실패했습니다.');
    
            const result = await response.json();
            if (result.resultCode === "200" && result.data?.quizShow) {
                setQuizShow(result.data.quizShow);
            } else {
                throw new Error(result.msg || '데이터를 불러오는데 실패했습니다.');
            }
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    if (loading) return (
        <div className="flex justify-center items-center min-h-screen bg-gray-100">
            <div className="animate-spin rounded-full h-16 w-16 border-t-4 border-blue-500"></div>
        </div>
    );

    if (error) return (
        <div className="max-w-2xl mx-auto mt-8 bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
            <div className="font-bold">오류</div>
            <div>{error}</div>
        </div>
    );

    if (!quizShow) return (
        <div className="max-w-2xl mx-auto mt-8 bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
            <div className="font-bold">퀴즈를 찾을 수 없습니다</div>
            <div>요청하신 퀴즈쇼를 찾을 수 없습니다.</div>
        </div>
    );

    const handleVote = async (id) => {
        try {
            const token = localStorage.getItem('accessToken');
            if (!token) {
                if (window.confirm('로그인이 필요한 서비스입니다. 로그인 페이지로 이동하시겠습니까?')) {
                    navigate('/auth/login', { 
                        state: { from: `/quizshow/${id}` }
                    });
                }
                return;
            }
    
            const response = await fetch(`http://localhost:8080/api/v1/quizshow/${id}/vote`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                credentials: 'include'
            });
    
            const result = await response.json();
    
            if (result.resultCode === "401") {
                if (window.confirm('로그인이 필요한 서비스입니다. 로그인 페이지로 이동하시겠습니까?')) {
                    navigate('/user/login', { 
                        state: { from: `/quizshow/${id}` }
                    });
                }
                return;
            }
    
            if (result.resultCode === "200") {
                // 상태 업데이트 - 여기가 중요합니다!
                setQuizShow(prev => ({
                    ...prev,
                    voteCount: result.data.voteCount, // 추천 수 업데이트
                    hasVoted: result.data.hasVoted   // 추천 상태 업데이트
                }));
                
                // 상태 업데이트 이후에 alert 실행 보장
                setTimeout(() => {
                    alert(result.data.hasVoted ? '추천이 완료되었습니다.' : '추천이 취소되었습니다.');
                }, 0);
            } else {
                throw new Error(result.msg || '추천 처리 중 오류가 발생했습니다.');
            }
        } catch (err) {
            console.error('Error:', err);
            alert('추천 처리 중 문제가 발생했습니다.');
        }
    };

    return (
        <div className="quiz-detail-container bg-gray-100 min-h-screen">
            {/* 뒤로가기 버튼 */}
            <button
                className="back-button flex items-center mb-6"
                onClick={() => navigate('/quizshow/list')}
            >
                ← 목록으로
            </button>
    
            {/* 퀴즈 헤더 */}
            <div className="quiz-header">
                <div className="quiz-image-container">
                    <img
                        src={
                            quizShow.useCustomImage
                                ? `http://localhost:8080/uploads/${quizShow.customImagePath}`
                                : `/images/quizShow/${quizShow.quizCategory.toLowerCase()}.jpg`
                        }
                        alt={quizShow.showName}
                        className="quiz-image"
                    />
                    <div className="quiz-title-overlay">
                        <h1 className="quiz-title">{quizShow.showName}</h1>
                    </div>
                </div>
            </div>

            {/* 퀴즈 정보 */}
            <div className="quiz-info grid grid-cols-3 gap-6 mt-6">
                {/* 퀴즈 설명 카드 (넓은 영역) */}
                <div className="info-card col-span-2">
                    <h2 className="text-lg font-semibold mb-3">퀴즈 설명</h2>
                    <p className="text-sm text-gray-700">{quizShow.showDescription}</p>
                </div>

                {/* 통계 카드 (좁은 영역) */}
                <div className="info-card">
                    <h2 className="text-lg font-semibold mb-3">퀴즈 정보</h2>
                    <ul className="text-sm text-gray-700 space-y-2">
                        <li><strong>카테고리:</strong> {quizShow.quizCategory}</li>
                        <li><strong>총 문제 수:</strong> {quizShow.totalQuizCount} 문제</li>
                        <li><strong>총점:</strong> {quizShow.totalScore}점</li>
                        <li><strong>조회수:</strong> {quizShow.view || 0}</li>
                        <li className="flex items-center">
                            <strong>추천:</strong>
                            <span className="ml-2">{quizShow.voteCount || 0}</span>
                            <button
                                className={`recommend-button ${quizShow.hasVoted ? 'active' : ''}`}
                                onClick={() => handleVote(quizShow.id)}
                            >
                                {quizShow.hasVoted ? '❤️' : '🤍'}
                            </button>
                        </li>
                    </ul>
                </div>
            </div>
    
            {/* 퀴즈 풀기/닫기 버튼 */}
            <div className="mt-8 flex justify-end">
                <button
                    className="quiz-toggle-button"
                    onClick={() => setShowQuizModal((prev) => !prev)}
                >
                    {showQuizModal ? "퀴즈 닫기" : "퀴즈 풀기 시작"}
                </button>
            </div>
    
            {/* 퀴즈 모달 */}
            {showQuizModal && (
                <div className="quiz-section mt-6">
                    <QuizSolve quizShow={quizShow} onBack={() => setShowQuizModal(false)} />
                </div>
            )}
        </div>
    );    
};

const QuizSolve = ({ quizShow, onBack }) => {
    const [userAnswers, setUserAnswers] = useState({});
    const [submitted, setSubmitted] = useState(false);
    const [result, setResult] = useState(null);
    const [error, setError] = useState(null);

    const renderAnswerInput = (quiz) => {
        switch (quiz.quizType) {
            case 'MULTIPLE_CHOICE':
            case 'TRUE_FALSE':
                return quiz.choices.map((choice, choiceIndex) => (
                    <div key={choice.id} className="flex items-center">
                        <input
                            type="radio"
                            id={`quiz-${quiz.id}-choice-${choiceIndex}`}
                            name={`quiz-${quiz.id}`}
                            value={choiceIndex}
                            checked={userAnswers[quiz.id] === choiceIndex}
                            onChange={() => setUserAnswers({
                                ...userAnswers,
                                [quiz.id]: choiceIndex
                            })}
                            disabled={submitted}
                            className="mr-3"
                        />
                        <label htmlFor={`quiz-${quiz.id}-choice-${choiceIndex}`}>
                            {choice.choiceContent}
                        </label>
                    </div>
                ));
            case 'SUBJECTIVE':
            case 'SHORT_ANSWER':
                return (
                    <input
                        type="text"
                        value={userAnswers[quiz.id] || ''}
                        onChange={(e) => setUserAnswers({
                            ...userAnswers,
                            [quiz.id]: e.target.value
                        })}
                        disabled={submitted}
                        className="w-full p-2 border rounded"
                    />
                );
            default:
                return null;
        }
    };

    const validateAnswers = (answers, quizzes) => {
        for (const [quizId, answer] of Object.entries(answers)) {
            const quiz = quizzes.find(q => q.id === parseInt(quizId));
            if (!quiz) {
                throw new Error(`퀴즈 ID ${quizId}를 찾을 수 없습니다.`);
            }
            
            switch (quiz.quizType) {
                case 'MULTIPLE_CHOICE':
                case 'TRUE_FALSE':
                    if (typeof answer !== 'number' || answer < 0 || answer >= quiz.choices.length) {
                        throw new Error(`퀴즈 ${quizId}의 답안이 유효하지 않습니다.`);
                    }
                    break;
                case 'SUBJECTIVE':
                case 'SHORT_ANSWER':
                    if (typeof answer !== 'string' || !answer.trim()) {
                        throw new Error(`퀴즈 ${quizId}의 답안이 유효하지 않습니다.`);
                    }
                    break;
            }
        }
        return true;
    };

    const handleSubmit = async () => {
        try {
            const answersArray = Object.entries(userAnswers).map(([quizId, answer]) => {
                const quiz = quizShow.quizzes.find(q => q.id === parseInt(quizId));
                if (!quiz) {
                    throw new Error(`퀴즈 ID ${quizId}를 찾을 수 없습니다.`);
                }
    
                const quizAnswer = {
                    quizId: parseInt(quizId),
                    quizType: quiz.quizType
                };
    
                switch (quiz.quizType) {
                    case 'MULTIPLE_CHOICE':
                    case 'TRUE_FALSE':
                        // 선택한 인덱스의 선택지 ID를 전송
                        const selectedChoice = quiz.choices[answer];
                        quizAnswer.choiceId = selectedChoice.id;
                        break;
                    case 'SUBJECTIVE':
                    case 'SHORT_ANSWER':
                        quizAnswer.textAnswer = answer;
                        break;
                    default:
                        throw new Error(`지원하지 않는 퀴즈 타입입니다: ${quiz.quizType}`);
                }
    
                return quizAnswer;
            });
    
            // JWT 토큰 관련 코드 추가
            const cookies = document.cookie.split(';');
            const accessToken = cookies.find(cookie => cookie.trim().startsWith('accessToken='));
            
            const headers = {
                'Content-Type': 'application/json',
            };
    
            if (accessToken) {
                headers['Authorization'] = `Bearer ${accessToken.split('=')[1].trim()}`;
            }
    
            const response = await fetch(`http://localhost:8080/api/v1/quizshow/${quizShow.id}/submit`, {
                method: 'POST',
                headers: headers,
                body: JSON.stringify({ answers: answersArray }),
                credentials: 'include'
            });
    
            // 나머지 코드는 동일
            const data = await response.json();
            
            if (data.resultCode === "401") {
                if (window.confirm('로그인이 필요한 서비스입니다. 로그인 페이지로 이동하시겠습니까?')) {
                    window.location.href = '/auth/login';
                }
                return;
            }
    
            if (!response.ok) {
                throw new Error(data.msg || '답안 제출에 실패했습니다.');
            }
    
            if (data.resultCode === "200") {
                setResult(data.data);
                setSubmitted(true);
            } else {
                throw new Error(data.msg || '채점 중 오류가 발생했습니다.');
            }
        } catch (err) {
            setError(err.message);
            console.error('Error:', err);
        }
    };

    if (!quizShow.quizzes) {
        return (
            <div className="p-4 text-red-500">
                퀴즈 데이터를 불러올 수 없습니다.
            </div>
        );
    }

    return (
        <div className="quiz-detail-container bg-gray-100 min-h-screen">
            {/* 퀴즈 섹션 */}
            <div className="quiz-section mt-8">
                <h2 className="text-2xl font-bold mb-6">퀴즈 풀기</h2>
                <div className="space-y-6">
                    {quizShow.quizzes.map((quiz, index) => (
                        <div
                            key={quiz.id}
                            className={`quiz-question ${
                                submitted
                                    ? result.results[quiz.id]
                                        ? 'correct'
                                        : 'incorrect'
                                    : ''
                            }`}
                        >
                            <h3 className="text-lg font-semibold mb-2">
                                문제 {index + 1} <span className="text-sm text-gray-500">(배점: {quiz.quizScore}점)</span>
                            </h3>
                            <p className="text-gray-800 mb-4">{quiz.quizContent}</p>
                            <div className="quiz-choices">
                                {renderAnswerInput(quiz)}
                            </div>
    
                            {submitted && quiz.explanation && (
                                <div className="mt-4 bg-gray-50 p-4 rounded">
                                    <p className="font-semibold">해설:</p>
                                    <p className="text-gray-600">{quiz.explanation}</p>
                                </div>
                            )}
                        </div>
                    ))}
                </div>
            </div>
    
            {/* 제출 버튼 */}
            {!submitted && (
                <button
                    className="submit-button mt-8"
                    onClick={handleSubmit}
                >
                    제출하기
                </button>
            )}
    
            {/* 결과 화면 */}
            {submitted && (
                <div className="quiz-result mt-8">
                    <h2 className="text-2xl font-semibold mb-4">최종 결과</h2>
                    <p className="text-lg">
                        점수: {result.score} / {quizShow.totalScore}점
                    </p>
                </div>
            )}
        </div>
    );        
};

export default QuizShowDetail;
