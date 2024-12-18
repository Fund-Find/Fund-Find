import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Modal from '../components/Modal';

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
        <div className="flex justify-center items-center min-h-screen">
            <div className="animate-spin rounded-full h-16 w-16 border-t-2 border-b-2 border-blue-500"></div>
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
        <div className="container mx-auto px-4 py-8">
            <button 
                className="mb-4 flex items-center text-gray-600 hover:text-gray-900"
                onClick={() => navigate('/quizshow/list')}
            >
                <span className="mr-2">←</span>
                목록으로 돌아가기
            </button>

            <div className="max-w-4xl mx-auto bg-white rounded-lg shadow-md overflow-hidden">
                <div className="relative h-64">
                    <img 
                        src={quizShow.useCustomImage ? 
                            `http://localhost:8080/uploads/${quizShow.customImagePath}` : 
                            `/images/quizShow/${quizShow.quizCategory.toLowerCase()}.jpg`}
                        alt={quizShow.showName}
                        className="w-full h-full object-cover"
                    />
                    <div className="absolute inset-0 bg-black bg-opacity-40 flex items-center justify-center">
                        <h1 className="text-4xl font-bold text-white">{quizShow.showName}</h1>
                    </div>
                </div>

                <div className="p-6">
                    <div className="mb-6">
                        <h2 className="text-2xl font-bold mb-2">퀴즈 설명</h2>
                        <p className="text-gray-600">{quizShow.showDescription}</p>
                    </div>

                    <div className="grid grid-cols-2 gap-4 mb-6">
                        <div className="bg-gray-50 p-4 rounded">
                            <h3 className="font-semibold mb-2">퀴즈 정보</h3>
                            <ul className="space-y-2 text-gray-600">
                                <li>카테고리: {quizShow.quizCategory}</li>
                                <li>총 문제 수: {quizShow.totalQuizCount}문제</li>
                                <li>총점: {quizShow.totalScore}점</li>
                            </ul>
                        </div>
                        <div className="bg-gray-50 p-4 rounded">
                            <h3 className="font-semibold mb-2">통계</h3>
                            <ul className="space-y-2 text-gray-600">
                                <li>조회수: {quizShow.view || 0}</li>
                                <li>추천수: {quizShow.voteCount || 0}</li>
                            </ul>
                        </div>
                    </div>

                    <button 
                        className="w-full bg-blue-500 text-white py-3 rounded-lg hover:bg-blue-600 transition-colors"
                        onClick={() => setShowQuizModal(true)}
                    >
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
    );
};

const QuizSolve = ({ quizShow, onBack }) => {
    const [userAnswers, setUserAnswers] = useState({});
    const [submitted, setSubmitted] = useState(false);
    const [result, setResult] = useState(null);
    const [error, setError] = useState(null);

    const handleAnswer = (quizId, answer) => {
        setUserAnswers(prev => ({
            ...prev,
            [quizId]: answer
        }));
    };

    const handleSubmit = async () => {
        if (!quizShow.quizzes || quizShow.quizzes.length === 0) {
            setError("퀴즈 데이터가 없습니다.");
            return;
        }

        try {
            const unansweredQuestions = quizShow.quizzes.filter(quiz => 
                userAnswers[quiz.id] === undefined
            );

            if (unansweredQuestions.length > 0) {
                alert(`아직 ${unansweredQuestions.length}개의 문제를 풀지 않았습니다.`);
                return;
            }

            const response = await fetch(`http://localhost:8080/api/v1/quizshow/${quizShow.id}/submit`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    answers: Object.entries(userAnswers).map(([quizId, answer]) => ({
                        quizId: parseInt(quizId),
                        answer: answer
                    }))
                }),
                credentials: 'include'
            });

            if (!response.ok) throw new Error('답안 제출에 실패했습니다.');
            
            const resultData = await response.json();
            if (resultData.resultCode === "200") {
                setResult(resultData.data);
                setSubmitted(true);
            } else {
                throw new Error(resultData.msg || '채점 중 오류가 발생했습니다.');
            }
        } catch (err) {
            setError(err.message);
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
        <div className="max-w-4xl mx-auto p-4">
            <div className="flex justify-between items-center mb-6">
                <button 
                    className="flex items-center text-gray-600 hover:text-gray-900"
                    onClick={onBack}
                >
                    <span className="mr-2">←</span>
                    퀴즈 설명으로 돌아가기
                </button>
                {submitted && (
                    <div className="text-xl font-bold">
                        최종 점수: {result.score} / {quizShow.totalScore}점
                    </div>
                )}
            </div>

            {error && (
                <div className="mb-6 p-4 bg-red-100 border border-red-400 text-red-700 rounded">
                    {error}
                </div>
            )}

            <div className="space-y-6">
                {quizShow.quizzes.map((quiz, index) => (
                    <div 
                        key={quiz.id}
                        className={`p-6 rounded-lg mb-4 ${
                            submitted ? (result.results[quiz.id] ? 'bg-green-50' : 'bg-red-50') : 'bg-gray-50'
                        }`}
                    >
                        <div className="flex justify-between items-start mb-4">
                            <h3 className="text-lg font-semibold">
                                문제 {index + 1}
                                <span className="text-sm text-gray-500 ml-2">
                                    (배점: {quiz.quizScore}점)
                                </span>
                            </h3>
                            {submitted && (
                                <span className={`px-3 py-1 rounded ${
                                    result.results[quiz.id] ? 'bg-green-500 text-white' : 'bg-red-500 text-white'
                                }`}>
                                    {result.results[quiz.id] ? '정답' : '오답'}
                                </span>
                            )}
                        </div>

                        <div className="mb-4">
                            <p className="text-gray-800">{quiz.quizContent}</p>
                        </div>

                        <div className="space-y-2">
                            {quiz.choices.map((choice, choiceIndex) => (
                                <div key={choice.id} className="flex items-center">
                                    <input
                                        type="radio"
                                        id={`quiz-${quiz.id}-choice-${choiceIndex}`}
                                        name={`quiz-${quiz.id}`}
                                        value={choiceIndex}
                                        checked={userAnswers[quiz.id] === choiceIndex}
                                        onChange={() => handleAnswer(quiz.id, choiceIndex)}
                                        disabled={submitted}
                                        className="mr-3"
                                    />
                                    <label 
                                        htmlFor={`quiz-${quiz.id}-choice-${choiceIndex}`}
                                        className={`flex-1 p-2 rounded ${
                                            submitted && choice.isCorrect ? 'bg-green-100' : ''
                                        }`}
                                    >
                                        {choice.choiceContent}
                                    </label>
                                </div>
                            ))}
                        </div>

                        {submitted && quiz.explanation && (
                            <div className="mt-4 p-4 bg-white rounded">
                                <p className="font-semibold">해설:</p>
                                <p className="text-gray-600">{quiz.explanation}</p>
                            </div>
                        )}
                    </div>
                ))}
            </div>

            {!submitted && (
                <div className="mt-8 flex justify-center">
                    <button 
                        className="bg-blue-500 text-white px-8 py-3 rounded-lg hover:bg-blue-600"
                        onClick={handleSubmit}
                    >
                        제출하기
                    </button>
                </div>
            )}
        </div>
    );
};

export default QuizShowDetail;