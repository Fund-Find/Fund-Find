import React, { useState, useEffect } from 'react';
import '../assets/css/quizshow.css';

const QuizShowCreateModal = ({ onClose, onSubmit, quizTypes, categories }) => {
    const [step, setStep] = useState(1); // 1: 퀴즈쇼 기본정보, 2: 퀴즈 추가 단계
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
    
    const [previewImage, setPreviewImage] = useState(null);

    const handleImageUpload = (e) => {
        const file = e.target.files[0];
        setImageFile(file);
        if (file) {
            const reader = new FileReader();
            reader.onload = () => setPreviewImage(reader.result);
            reader.readAsDataURL(file);
        }
    };


    const [imageFile, setImageFile] = useState(null);
    const [loading, setLoading] = useState(false);

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
        if (!quizData.quizContent.trim()) {
            alert('퀴즈 내용을 입력해주세요.');
            return;
        }
        if (quizData.quizScore <= 0) {
            alert('퀴즈 점수는 0 이상이어야 합니다.');
            return;
        }
        if (!quizData.quizTypeId) {
            alert('퀴즈 유형을 선택해주세요.');
            return;
        }
        if (quizData.choices.length < 2) {
            alert('선택지는 최소 2개 이상이어야 합니다.');
            return;
        }
    
        setFormData((prev) => ({
            ...prev,
            quizzes: [...prev.quizzes, { ...quizData }],
        }));
        setQuizData({ quizContent: '', quizScore: 0, quizTypeId: '', choices: [] });
    };
    

    const handleSubmit = async () => {
        // 데이터 유효성 검사
        if (!formData.showName || !formData.category || !formData.showDescription) {
            alert('필수 항목을 모두 입력해주세요.');
            return;
        }

        const token = localStorage.getItem('accessToken'); // 토큰 가져오기
        if (!token) {
            alert('로그인이 필요한 서비스입니다.');
            return;
        }
        
        setLoading(true);
        try {
            const formDataToSend = new FormData();
            
            // 퀴즈 데이터 준비
            const quizData = {
                showName: formData.showName,
                category: formData.category,
                showDescription: formData.showDescription,
                totalQuizCount: formData.quizzes?.length || 0,
                totalScore: formData.quizzes?.reduce((sum, quiz) => sum + (quiz.quizScore || 0), 0) || 0,
                useCustomImage: !!imageFile,
                quizzes: formData.quizzes || [],
                selectedImagePath: ''
            };
    
            // JSON 데이터를 'data'라는 키로 추가
            formDataToSend.append('data', new Blob([JSON.stringify(quizData)], { 
                type: 'application/json' 
            }));
    
            // 이미지 파일이 있다면 추가
            if (imageFile) {
                formDataToSend.append('imageFile', imageFile);
            }
    
            const response = await fetch('http://localhost:8080/api/v1/quizshow', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`
                },
                body: formDataToSend,
                credentials: 'include'
            });
    
            if (!response.ok) {
                const contentType = response.headers.get("content-type");
                let errorMessage;
                if (contentType?.includes("application/json")) {
                    const errorData = await response.json();
                    errorMessage = errorData.msg || '퀴즈쇼 생성 중 오류가 발생했습니다.';
                } else {
                    errorMessage = await response.text();
                }
                throw new Error(errorMessage);
            }
    
            const result = await response.json();
            alert('퀴즈쇼가 성공적으로 생성되었습니다!');
            onClose();
            
        } catch (error) {
            console.error('Error:', error);
            alert(error.message || '퀴즈쇼 생성 중 오류가 발생했습니다.');
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
                        <section className="quizinfo">
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
                                    <option value="">카테고리 선택</option>
                                    {categories.map((category) => (
                                        <option key={category.categoryEnum} value={category.categoryEnum}>
                                            {category.description}
                                        </option>
                                    ))}
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
                            <div className="quiz-button-container">
                                <button className="quiz-button" onClick={() => setStep(2)}>다음</button>
                            </div>
                        </section>
                    </>
                )}
                {step === 2 && (
                    <>
                        <header className="header">
                            <h1>퀴즈 추가</h1>
                        </header>
                        <section className="quizinfo">
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
                                <button className="quiz-button" type="button" onClick={addChoice}>
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
                            <div className="quiz-button-container">
                                <button className="quiz-button" type="button" onClick={addQuiz}>
                                    퀴즈 추가
                                </button>
                                <button className="quiz-button" type="button" onClick={() => setStep(1)}>
                                    이전
                                </button>
                                <button className="quiz-button" type="button" onClick={handleSubmit} disabled={loading}>
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
