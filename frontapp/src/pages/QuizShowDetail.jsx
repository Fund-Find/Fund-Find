import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';

const QuizShowDetail = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [quizShow, setQuizShow] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [currentQuizIndex, setCurrentQuizIndex] = useState(0);
    const [userAnswers, setUserAnswers] = useState({});
    const [showResults, setShowResults] = useState(false);
    const [totalScore, setTotalScore] = useState(0);

    useEffect(() => {
        fetchQuizShow();
    }, [id]);

    const fetchQuizShow = async () => {
        try {
            setLoading(true);
            const response = await fetch(`http://localhost:8080/api/v1/quizshow/${id}`);
            if (!response.ok) {
                throw new Error('퀴즈쇼를 불러오는데 실패했습니다.');
            }
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

    const handleAnswer = (answer) => {
        setUserAnswers(prev => ({
            ...prev,
            [currentQuizIndex]: answer
        }));
    };

    const handleNext = () => {
        if (!quizShow?.quizzes) return;
        
        if (currentQuizIndex < quizShow.quizzes.length - 1) {
            setCurrentQuizIndex(currentQuizIndex + 1);
        } else {
            calculateResults();
        }
    };

    const calculateResults = () => {
        if (!quizShow?.quizzes) return;

        let score = 0;
        quizShow.quizzes.forEach((quiz, index) => {
            const userAnswer = userAnswers[index];
            if (userAnswer !== undefined && quiz.choices[userAnswer]?.isCorrect) {
                score += quiz.quizScore || 0;
            }
        });
        setTotalScore(score);
        setShowResults(true);
    };

    const handleRetry = () => {
        setCurrentQuizIndex(0);
        setUserAnswers({});
        setShowResults(false);
        setTotalScore(0);
    };

    if (loading) {
        return (
            <div className="flex justify-center items-center min-h-screen">
                <div className="animate-spin rounded-full h-16 w-16 border-t-2 border-b-2 border-blue-500"></div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="max-w-2xl mx-auto mt-8 bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
                <div className="font-bold">오류</div>
                <div>{error}</div>
            </div>
        );
    }

    if (!quizShow) {
        return (
            <div className="max-w-2xl mx-auto mt-8 bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
                <div className="font-bold">퀴즈를 찾을 수 없습니다</div>
                <div>요청하신 퀴즈쇼를 찾을 수 없습니다.</div>
            </div>
        );
    }

    const currentQuiz = quizShow.quizzes?.[currentQuizIndex];

    return (
        <div className="container mx-auto px-4 py-8">
            <button 
                className="mb-4 flex items-center text-gray-600 hover:text-gray-900"
                onClick={() => navigate('/quizshow/list')}
            >
                <span className="mr-2">←</span>
                목록으로 돌아가기
            </button>

            <div className="max-w-4xl mx-auto bg-white rounded-lg shadow-md overflow-hidden">
                <div className="p-6 border-b">
                    <div className="flex justify-between items-center">
                        <div>
                            <h1 className="text-2xl font-bold">{quizShow.showName}</h1>
                            <p className="text-gray-600">{quizShow.showDescription}</p>
                        </div>
                        <div className="flex gap-4 text-sm text-gray-500">
                            <span className="flex items-center">
                                👁️ {quizShow.view || 0}
                            </span>
                            <span className="flex items-center">
                                👍 {quizShow.voteCount || 0}
                            </span>
                        </div>
                    </div>
                </div>

                <div className="p-6">
                    {showResults ? (
                        <div className="text-center py-8">
                            <h2 className="text-2xl font-bold mb-4">퀴즈 결과</h2>
                            <p className="text-xl mb-4">총점: {totalScore} / {quizShow.totalScore}</p>
                            <button 
                                className="bg-blue-500 text-white px-6 py-2 rounded hover:bg-blue-600"
                                onClick={handleRetry}
                            >
                                다시 풀기
                            </button>
                        </div>
                    ) : (
                        <div className="space-y-6">
                            {currentQuiz && (
                                <>
                                    <div className="flex justify-between text-sm text-gray-500 mb-4">
                                        <span>문제 {currentQuizIndex + 1} / {quizShow.totalQuizCount}</span>
                                        <span>배점: {currentQuiz.quizScore}점</span>
                                    </div>

                                    <div className="p-4 bg-gray-50 rounded-lg">
                                        <p className="text-lg font-medium mb-4">
                                            {currentQuiz.quizContent}
                                        </p>

                                        <div className="space-y-2">
                                            {currentQuiz.choices?.map((choice, idx) => (
                                                <button
                                                    key={idx}
                                                    className={`w-full text-left px-4 py-2 rounded ${
                                                        userAnswers[currentQuizIndex] === idx 
                                                            ? 'bg-blue-500 text-white' 
                                                            : 'bg-white border hover:bg-gray-50'
                                                    }`}
                                                    onClick={() => handleAnswer(idx)}
                                                >
                                                    {choice.choiceContent}
                                                </button>
                                            ))}
                                        </div>
                                    </div>
                                </>
                            )}
                        </div>
                    )}
                </div>

                <div className="p-6 border-t flex justify-between items-center">
                    <div className="text-sm text-gray-500">
                        카테고리: {quizShow.quizCategory}
                    </div>
                    {!showResults && currentQuiz && (
                        <button 
                            className={`px-6 py-2 rounded ${
                                userAnswers[currentQuizIndex] === undefined
                                    ? 'bg-gray-300 cursor-not-allowed'
                                    : 'bg-blue-500 text-white hover:bg-blue-600'
                            }`}
                            onClick={handleNext}
                            disabled={userAnswers[currentQuizIndex] === undefined}
                        >
                            {currentQuizIndex < quizShow.totalQuizCount - 1 ? '다음 문제' : '결과 보기'}
                        </button>
                    )}
                </div>
            </div>
        </div>
    );
};

export default QuizShowDetail;