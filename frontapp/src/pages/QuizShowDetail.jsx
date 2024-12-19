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
            const response = await fetch(`http://localhost:8080/api/v1/quizshow/${id}`);
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

    return (
        <div className="bg-gray-100 min-h-screen py-8">
            <div className="container mx-auto px-4">
                <button 
                    className="mb-4 flex items-center text-gray-600 hover:text-gray-900"
                    onClick={() => navigate('/quizshow/list')}>
                    <span className="mr-2">←</span>
                    목록으로
                </button>

                <div className="max-w-4xl mx-auto bg-white rounded-lg shadow-lg overflow-hidden">
                    <div className="relative h-64">
                        <img 
                            src={quizShow.useCustomImage ? 
                                `http://localhost:8080/uploads/${quizShow.customImagePath}` : 
                                `/images/quizShow/${quizShow.quizCategory.toLowerCase()}.jpg`}
                            alt={quizShow.showName}
                            className="w-full h-full object-cover"/>
                        <div className="absolute inset-0 bg-gradient-to-t from-black via-transparent to-transparent flex items-end p-6">
                            <h1 className="text-4xl font-bold text-white">{quizShow.showName}</h1>
                        </div>
                    </div>

                    <div className="p-6">
                        <div className="mb-6">
                            <h2 className="text-2xl font-semibold mb-4">퀴즈 설명</h2>
                            <p className="text-gray-700">{quizShow.showDescription}</p>
                        </div>
                        <div className="grid grid-cols-2 gap-4 mb-6">
                            <div className="bg-gray-50 p-4 rounded shadow">
                                <h3 className="font-semibold mb-2">퀴즈 정보</h3>
                                <ul className="space-y-2 text-gray-600">
                                    <li>카테고리: {quizShow.quizCategory}</li>
                                    <li>총 문제 수: {quizShow.totalQuizCount}문제</li>
                                    <li>총점: {quizShow.totalScore}점</li>
                                </ul>
                            </div>
                            <div className="bg-gray-50 p-4 rounded shadow">
                                <h3 className="font-semibold mb-2">통계</h3>
                                <ul className="space-y-2 text-gray-600">
                                    <li>조회수: {quizShow.view || 0}</li>
                                    <li>추천수: {quizShow.voteCount || 0}</li>
                                </ul>
                            </div>
                        </div>
                        <button 
                            className="w-full bg-blue-500 text-white py-3 rounded-lg hover:bg-blue-600 transition-transform transform hover:scale-105"
                            onClick={() => setShowQuizModal(true)}>
                            퀴즈 풀기 시작
                        </button>
                    </div>
                </div>

                <Modal 
                    isOpen={showQuizModal} 
                    onClose={() => setShowQuizModal(false)}
                >
                    <QuizSolve 
                        quizShow={quizShow} 
                        onBack={() => setShowQuizModal(false)} 
                    />
                </Modal>
            </div>
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
                        quizAnswer.choiceIndex = answer;
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
            <button
                className="back-button flex items-center"
                onClick={() => navigate('/quizshow/list')}
            >
                ← 목록으로
            </button>
    
            {/* 헤더 영역 */}
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
                        <h1 className="text-3xl font-bold">{quizShow.showName}</h1>
                    </div>
                </div>
            </div>
    
            {/* 퀴즈 정보 */}
            <div className="quiz-info">
                <div className="info-card">
                    <h2 className="text-xl font-semibold mb-4">퀴즈 정보</h2>
                    <ul className="text-gray-600 space-y-2">
                        <li>카테고리: {quizShow.quizCategory}</li>
                        <li>총 문제 수: {quizShow.totalQuizCount} 문제</li>
                        <li>총점: {quizShow.totalScore}점</li>
                    </ul>
                </div>
                <div className="info-card">
                    <h2 className="text-xl font-semibold mb-4">통계</h2>
                    <ul className="text-gray-600 space-y-2">
                        <li>조회수: {quizShow.view || 0}</li>
                        <li>추천수: {quizShow.voteCount || 0}</li>
                    </ul>
                </div>
            </div>
    
            {/* 퀴즈 섹션 */}
            <div className="quiz-section">
                <h2 className="text-2xl font-semibold mb-6">퀴즈 풀기</h2>
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
                                문제 {index + 1}
                                <span className="text-sm text-gray-500 ml-2">(배점: {quiz.quizScore}점)</span>
                            </h3>
                            <p className="text-gray-800 mb-4">{quiz.quizContent}</p>
    
                            <div className="quiz-choices">
                                {renderAnswerInput(quiz)}
                            </div>
    
                            {submitted && quiz.explanation && (
                                <div className="mt-4 p-4 bg-gray-50 rounded">
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
                    className="submit-button"
                    onClick={handleSubmit}
                >
                    제출하기
                </button>
            )}
    
            {/* 결과 화면 */}
            {submitted && (
                <div className="quiz-result">
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
