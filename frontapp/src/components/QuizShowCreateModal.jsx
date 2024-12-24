import React, { useState, useEffect } from 'react';
import '../assets/css/quizshow.css';

const QuizShowCreateModal = ({ onClose, onSubmit, fetchQuizTypes }) => {
    const [step, setStep] = useState(1); // 1: 퀴즈쇼 기본정보, 2: 퀴즈 추가 단계
    const [quizTypes, setQuizTypes] = useState([]);
    const [formData, setFormData] = useState({
        showName: '',
        category: 'INVESTMENT',
        showDescription: '',
        totalQuizCount: 0,
        totalScore: 0,
        selectedImagePath: '',
        useCustomImage: false,
        quizzes: [], // 퀴즈 목록
    });

    const [quizData, setQuizData] = useState({
        quizContent: '',
        quizScore: 0,
        quizTypeId: '',
        choices: [],
    });

    const [choiceData, setChoiceData] = useState({
        choiceContent: '',
        isCorrect: false,
    });

    const [imageFile, setImageFile] = useState(null);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        const loadQuizTypes = async () => {
            try {
                const types = await fetchQuizTypes(); // 퀴즈 타입을 가져오는 API 호출
                setQuizTypes(types);
            } catch (error) {
                console.error('Failed to fetch quiz types:', error);
            }
        };

        loadQuizTypes();
    }, [fetchQuizTypes]);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleQuizChange = (e) => {
        const { name, value } = e.target;
        setQuizData((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleChoiceChange = (e) => {
        const { name, value, type } = e.target;
        setChoiceData((prev) => ({
            ...prev,
            [name]: type === 'checkbox' ? e.target.checked : value,
        }));
    };

    const addChoice = () => {
        if (!choiceData.choiceContent.trim()) {
            alert('선택지 내용을 입력해주세요.');
            return;
        }
        setQuizData((prev) => ({
            ...prev,
            choices: [...prev.choices, { ...choiceData }],
        }));
        setChoiceData({ choiceContent: '', isCorrect: false }); // 초기화
    };

    const addQuiz = () => {
        if (!quizData.quizContent.trim() || quizData.quizScore <= 0 || !quizData.quizTypeId) {
            alert('퀴즈 내용을 모두 입력해주세요.');
            return;
        }
        setFormData((prev) => ({
            ...prev,
            quizzes: [...prev.quizzes, { ...quizData }],
        }));
        setQuizData({ quizContent: '', quizScore: 0, quizTypeId: '', choices: [] }); // 초기화
    };

    const handleSubmit = async () => {
        setLoading(true);
        try {
            const formDataToSend = new FormData();
            formDataToSend.append('data', JSON.stringify(formData));
            if (imageFile) {
                formDataToSend.append('imageFile', imageFile);
            }

            await onSubmit(formDataToSend);
            alert('퀴즈쇼가 성공적으로 생성되었습니다!');
            onClose();
        } catch (error) {
            console.error('퀴즈쇼 생성 실패:', error);
            alert('퀴즈쇼 생성 중 오류가 발생했습니다.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="quiz-popup-overlay">
            <div className="quiz-popup">
                <button className="close-button" onClick={onClose}>
                    ×
                </button>
                {step === 1 && (
                    <>
                        <header className="header">
                            <h1>퀴즈쇼 생성</h1>
                        </header>
                        <section className="questions">
                            <div>
                                <h2>1. 퀴즈쇼 제목을 입력해주세요.</h2>
                                <input
                                    type="text"
                                    name="showName"
                                    value={formData.showName}
                                    onChange={handleInputChange}
                                    required
                                    placeholder="퀴즈쇼 제목을 입력하세요"
                                />
                            </div>
                            <div>
                                <h2>2. 카테고리를 선택해주세요.</h2>
                                <select
                                    name="category"
                                    value={formData.category}
                                    onChange={handleInputChange}
                                    required
                                >
                                    <option value="INVESTMENT">투자</option>
                                    <option value="SCIENCE">과학</option>
                                    <option value="ENTERTAINMENT">오락</option>
                                </select>
                            </div>
                            <div>
                                <h2>3. 퀴즈쇼 설명을 작성해주세요.</h2>
                                <textarea
                                    name="showDescription"
                                    value={formData.showDescription}
                                    onChange={handleInputChange}
                                    required
                                    placeholder="퀴즈쇼에 대한 간단한 설명을 입력하세요"
                                    rows={4}
                                />
                            </div>
                            <div>
                                <h2>4. 대표 이미지를 선택해주세요.</h2>
                                <input
                                    type="file"
                                    accept="image/*"
                                    onChange={(e) => setImageFile(e.target.files[0])}
                                />
                            </div>
                            <div className="button-container">
                                <button onClick={() => setStep(2)}>다음</button>
                            </div>
                        </section>
                    </>
                )}
                {step === 2 && (
                    <>
                        <header className="header">
                            <h1>퀴즈 추가</h1>
                        </header>
                        <section className="questions">
                            <div>
                                <h2>퀴즈 내용을 입력해주세요.</h2>
                                <textarea
                                    name="quizContent"
                                    value={quizData.quizContent}
                                    onChange={handleQuizChange}
                                    required
                                    rows={3}
                                />
                            </div>
                            <div>
                                <h2>퀴즈 점수를 입력해주세요.</h2>
                                <input
                                    type="number"
                                    name="quizScore"
                                    value={quizData.quizScore}
                                    onChange={handleQuizChange}
                                    required
                                />
                            </div>
                            <div>
                                <h2>퀴즈 유형을 선택해주세요.</h2>
                                <select
                                    name="quizTypeId"
                                    value={quizData.quizTypeId}
                                    onChange={handleQuizChange}
                                    required
                                >
                                    <option value="">유형 선택</option>
                                    {quizTypes.map((type) => (
                                        <option key={type.id} value={type.id}>
                                            {type.typeName}
                                        </option>
                                    ))}
                                </select>
                            </div>
                            <div>
                                <h2>선택지를 추가해주세요.</h2>
                                <input
                                    type="text"
                                    name="choiceContent"
                                    value={choiceData.choiceContent}
                                    onChange={handleChoiceChange}
                                    placeholder="선택지 내용을 입력하세요"
                                />
                                <label>
                                    <input
                                        type="checkbox"
                                        name="isCorrect"
                                        checked={choiceData.isCorrect}
                                        onChange={handleChoiceChange}
                                    />
                                    정답 여부
                                </label>
                                <button type="button" onClick={addChoice}>
                                    선택지 추가
                                </button>
                            </div>
                            <div>
                                <h3>추가된 선택지:</h3>
                                <ul>
                                    {quizData.choices.map((choice, index) => (
                                        <li key={index}>
                                            {choice.choiceContent} {choice.isCorrect ? '(정답)' : ''}
                                        </li>
                                    ))}
                                </ul>
                            </div>
                            <div className="button-container">
                                <button type="button" onClick={addQuiz}>
                                    퀴즈 추가
                                </button>
                                <button type="button" onClick={() => setStep(1)}>
                                    이전
                                </button>
                                <button type="button" onClick={handleSubmit} disabled={loading}>
                                    {loading ? '생성 중...' : '퀴즈쇼 생성하기'}
                                </button>
                            </div>
                        </section>
                    </>
                )}
            </div>
        </div>
    );
};

export default QuizShowCreateModal;
