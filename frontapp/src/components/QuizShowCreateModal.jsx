import React, { useState, useEffect } from 'react';
import { X, Plus, Trash2 } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

const QuizShowCreateModal = ({ onClose, onSubmit, categories }) => {
    const navigate = useNavigate();
    const [step, setStep] = useState(1);
    const [basicInfo, setBasicInfo] = useState({
        showName: '',
        category: '',
        showDescription: '',
        useCustomImage: false
    });
    const [imageFile, setImageFile] = useState(null);
    const [previewUrl, setPreviewUrl] = useState('');
    const [quizzes, setQuizzes] = useState([]);
    const [currentQuiz, setCurrentQuiz] = useState({
        quizTypeId: "MULTIPLE_CHOICE",
        quizContent: '',
        quizScore: 10,
        choices: [
            { choiceContent: '', isCorrect: false },
            { choiceContent: '', isCorrect: false }
        ]
    });

    // 로그인 체크
    useEffect(() => {
        const token = localStorage.getItem('accessToken');
        if (!token) {
            alert('로그인이 필요한 서비스입니다.');
            navigate('/auth/login', { state: { from: location.pathname } });
            onClose();
        }
    }, []);

    const handleBasicInfoChange = (e) => {
        const { name, value, type, checked } = e.target;
        setBasicInfo(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
    };

    const handleImageChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            setImageFile(file);
            setPreviewUrl(URL.createObjectURL(file));
            setBasicInfo(prev => ({
                ...prev,
                useCustomImage: true
            }));
        }
    };

    const handleQuizChange = (e) => {
        const { name, value } = e.target;
        setCurrentQuiz(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleChoiceChange = (index, e) => {
        const { name, value, type, checked } = e.target;
        setCurrentQuiz(prev => ({
            ...prev,
            choices: prev.choices.map((choice, i) => {
                if (i === index) {
                    return { 
                        ...choice, 
                        [name]: type === 'radio' || type === 'checkbox' ? checked : value 
                    };
                }
                // radio 타입인 경우 다른 모든 선택지의 isCorrect를 false로 설정
                if (type === 'radio' && name === 'isCorrect' && checked) {
                    return { ...choice, isCorrect: false };
                }
                return choice;
            })
        }));
    };

    const addChoice = () => {
        if (currentQuiz.choices.length < 5) {
            setCurrentQuiz(prev => ({
                ...prev,
                choices: [...prev.choices, { choiceContent: '', isCorrect: false }]
            }));
        }
    };

    const removeChoice = (index) => {
        if (currentQuiz.choices.length > 2) {
            setCurrentQuiz(prev => ({
                ...prev,
                choices: prev.choices.filter((_, i) => i !== index)
            }));
        }
    };

    const validateQuiz = () => {
        if (!currentQuiz.quizContent.trim()) {
            alert('문제 내용을 입력해주세요.');
            return false;
        }

        switch (currentQuiz.quizTypeId) {
            case 'MULTIPLE_CHOICE':
                if (!currentQuiz.choices.some(choice => choice.isCorrect)) {
                    alert('정답을 선택해주세요.');
                    return false;
                }
                if (currentQuiz.choices.some(choice => !choice.choiceContent.trim())) {
                    alert('모든 선택지를 입력해주세요.');
                    return false;
                }
                break;

            case 'TRUE_FALSE':
                if (!currentQuiz.choices.some(choice => choice.isCorrect)) {
                    alert('O 또는 X를 선택해주세요.');
                    return false;
                }
                break;

            case 'SUBJECTIVE':
            case 'SHORT_ANSWER':
                if (!currentQuiz.choices[0].choiceContent.trim()) {
                    alert('정답을 입력해주세요.');
                    return false;
                }
                break;
        }

        return true;
    };

    const addQuiz = () => {
        if (validateQuiz()) {
            setQuizzes(prev => [...prev, { ...currentQuiz }]);
            // 새로운 퀴즈 폼 초기화
            setCurrentQuiz({
                quizTypeId: currentQuiz.quizTypeId, // 현재 선택된 타입 유지
                quizContent: '',
                quizScore: 10,
                choices: currentQuiz.quizTypeId === "TRUE_FALSE" 
                    ? [
                        { choiceContent: "T", isCorrect: false },
                        { choiceContent: "F", isCorrect: false }
                    ]
                    : currentQuiz.quizTypeId === "MULTIPLE_CHOICE" 
                        ? [
                            { choiceContent: "", isCorrect: false },
                            { choiceContent: "", isCorrect: false }
                        ]
                        : [{ choiceContent: "", isCorrect: true }]
            });
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
    
        if (step === 1) {
            if (!basicInfo.showName || !basicInfo.category || !basicInfo.showDescription) {
                alert('모든 필수 항목을 입력해주세요.');
                return;
            }
            setStep(2);
            return;
        }
    
        // 퀴즈 추가 확인
        if (quizzes.length === 0) {
            if (!validateQuiz()) return;
            quizzes.push({...currentQuiz});
        }
    
        try {
            const token = localStorage.getItem('accessToken');
            if (!token) {
                throw new Error('로그인이 필요합니다.');
            }
    
            const totalScore = quizzes.reduce((sum, quiz) => sum + parseInt(quiz.quizScore), 0);
    
            const submitData = {
                showName: basicInfo.showName,
                category: basicInfo.category,
                showDescription: basicInfo.showDescription,
                useCustomImage: basicInfo.useCustomImage,
                totalQuizCount: quizzes.length,
                totalScore: quizzes.reduce((sum, quiz) => sum + parseInt(quiz.quizScore), 0),
                quizzes: quizzes.map(quiz => ({
                    quizTypeId: quiz.quizTypeId,
                    quizContent: quiz.quizContent,
                    quizScore: quiz.quizScore,
                    choices: quiz.choices
                }))
            };
        
            const formData = new FormData();
            formData.append('data', new Blob([JSON.stringify(submitData)], {
                type: 'application/json'  // 여기를 명시적으로 지정
            }));
    
            // 이미지 파일이 있는 경우에만 추가
            if (imageFile) {
                submitData.append('imageFile', imageFile);
            }
    
            const response = await fetch('http://localhost:8080/api/v1/quizshow', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`
                },
                body: submitData
            });
    
            const result = await response.json();
    
            if (result.resultCode === "200") {
                alert('퀴즈쇼가 성공적으로 생성되었습니다.');
                onClose();
            } else {
                throw new Error(result.msg || '퀴즈쇼 생성에 실패했습니다.');
            }
        } catch (error) {
            if (error.message === '로그인이 필요합니다.') {
                navigate('/auth/login', { 
                    state: { from: location.pathname }
                });
            } else {
                alert(error.message);
            }
        }
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
            <div className="bg-white rounded-lg w-full max-w-2xl max-h-[90vh] overflow-y-auto">
                <div className="sticky top-0 bg-white p-4 border-b flex justify-between items-center">
                    <h2 className="text-xl font-bold">
                        {step === 1 ? '퀴즈쇼 정보 입력' : '퀴즈 추가'}
                    </h2>
                    <button onClick={onClose} className="text-gray-500 hover:text-gray-700">
                        <X size={24} />
                    </button>
                </div>

                <form onSubmit={handleSubmit} className="p-6 space-y-6">
                    {step === 1 ? (
                        // 기본 정보 입력 단계
                        <div className="space-y-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">
                                    퀴즈쇼 제목 *
                                </label>
                                <input
                                    type="text"
                                    name="showName"
                                    value={basicInfo.showName}
                                    onChange={handleBasicInfoChange}
                                    required
                                    className="w-full p-2 border rounded focus:ring-2 focus:ring-blue-500"
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">
                                    카테고리 *
                                </label>
                                <select
                                    name="category"
                                    value={basicInfo.category}
                                    onChange={handleBasicInfoChange}
                                    required
                                    className="w-full p-2 border rounded focus:ring-2 focus:ring-blue-500"
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
                                <label className="block text-sm font-medium text-gray-700 mb-1">
                                    퀴즈쇼 설명 *
                                </label>
                                <textarea
                                    name="showDescription"
                                    value={basicInfo.showDescription}
                                    onChange={handleBasicInfoChange}
                                    required
                                    className="w-full p-2 border rounded focus:ring-2 focus:ring-blue-500 h-32"
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">
                                    대표 이미지
                                </label>
                                <input
                                    type="file"
                                    accept="image/*"
                                    onChange={handleImageChange}
                                    className="w-full p-2 border rounded focus:ring-2 focus:ring-blue-500"
                                />
                                {previewUrl && (
                                    <div className="mt-2">
                                        <img
                                            src={previewUrl}
                                            alt="Preview"
                                            className="w-full max-h-48 object-cover rounded"
                                        />
                                    </div>
                                )}
                            </div>
                        </div>
                    ) : (
                        // 퀴즈 추가 단계
                        <div className="space-y-6">
                            {/* 기존 퀴즈 목록 */}
                            {quizzes.length > 0 && (
                                <div className="space-y-2">
                                    <h3 className="font-medium">추가된 퀴즈 ({quizzes.length}개)</h3>
                                    <div className="bg-gray-50 p-4 rounded-lg">
                                        {quizzes.map((quiz, index) => (
                                            <div key={index} className="mb-2 last:mb-0">
                                                {index + 1}. {quiz.quizContent} ({quiz.quizScore}점)
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            )}

                            {/* 새 퀴즈 입력 폼 */}
                            <div className="border p-4 rounded-lg">
                                <div className="space-y-4">
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-1">
                                            퀴즈 타입 *
                                        </label>
                                        <select
                                            name="quizTypeId"
                                            value={currentQuiz.quizTypeId}
                                            onChange={(e) => {
                                                const newType = e.target.value;
                                                setCurrentQuiz(prev => ({
                                                    ...prev,
                                                    quizTypeId: newType,
                                                    choices: newType === "TRUE_FALSE" 
                                                        ? [
                                                            { choiceContent: "T", isCorrect: false },
                                                            { choiceContent: "F", isCorrect: false }
                                                        ]
                                                        : newType === "MULTIPLE_CHOICE" 
                                                            ? [
                                                                { choiceContent: "", isCorrect: false },
                                                                { choiceContent: "", isCorrect: false }
                                                            ]
                                                            : [{ choiceContent: "", isCorrect: true }]
                                                }));
                                            }}
                                            className="w-full p-2 border rounded focus:ring-2 focus:ring-blue-500"
                                        >
                                            <option value="MULTIPLE_CHOICE">객관식</option>
                                            <option value="SUBJECTIVE">주관식</option>
                                            <option value="TRUE_FALSE">OX</option>
                                            <option value="SHORT_ANSWER">단답형</option>
                                        </select>
                                    </div>
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-1">
                                            문제 *
                                        </label>
                                        <textarea
                                            name="quizContent"
                                            value={currentQuiz.quizContent}
                                            onChange={handleQuizChange}
                                            className="w-full p-2 border rounded focus:ring-2 focus:ring-blue-500"
                                        />
                                    </div>

                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-1">
                                            배점 *
                                        </label>
                                        <input
                                            type="number"
                                            name="quizScore"
                                            value={currentQuiz.quizScore}
                                            onChange={handleQuizChange}
                                            min="1"
                                            className="w-full p-2 border rounded focus:ring-2 focus:ring-blue-500"
                                        />
                                    </div>

                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-1">
                                            {currentQuiz.quizTypeId === "MULTIPLE_CHOICE" && "선택지 *"}
                                            {currentQuiz.quizTypeId === "TRUE_FALSE" && "OX 선택 *"}
                                            {currentQuiz.quizTypeId === "SUBJECTIVE" && "정답 *"}
                                            {currentQuiz.quizTypeId === "SHORT_ANSWER" && "정답 *"}
                                        </label>
                                        <div className="space-y-2">
                                            {currentQuiz.quizTypeId === "MULTIPLE_CHOICE" && (
                                                <>
                                                    {currentQuiz.choices.map((choice, index) => (
                                                        <div key={index} className="flex gap-2 items-center">
                                                            <input
                                                                type="radio"
                                                                name="isCorrect"
                                                                checked={choice.isCorrect}
                                                                onChange={(e) => handleChoiceChange(index, e)}
                                                                className="w-4 h-4"
                                                            />
                                                            <input
                                                                type="text"
                                                                name="choiceContent"
                                                                value={choice.choiceContent}
                                                                onChange={(e) => handleChoiceChange(index, e)}
                                                                placeholder={`선택지 ${index + 1}`}
                                                                className="flex-1 p-2 border rounded"
                                                            />
                                                            {currentQuiz.choices.length > 2 && (
                                                                <button
                                                                    type="button"
                                                                    onClick={() => removeChoice(index)}
                                                                    className="text-red-500 hover:text-red-700"
                                                                >
                                                                    <Trash2 size={20} />
                                                                </button>
                                                            )}
                                                        </div>
                                                    ))}
                                                    {currentQuiz.choices.length < 5 && (
                                                        <button
                                                            type="button"
                                                            onClick={addChoice}
                                                            className="mt-2 flex items-center gap-1 text-blue-600 hover:text-blue-800"
                                                        >
                                                            <Plus size={16} />
                                                            선택지 추가
                                                        </button>
                                                    )}
                                                </>
                                            )}
                                            {currentQuiz.quizTypeId === "TRUE_FALSE" && (
                                                <div className="flex gap-4">
                                                    <label className="flex items-center gap-2">
                                                        <input
                                                            type="radio"
                                                            name="tfAnswer"
                                                            checked={currentQuiz.choices[0].isCorrect}
                                                            onChange={() => handleChoiceChange(0, { target: { name: 'isCorrect', type: 'radio', checked: true } })}
                                                            className="w-4 h-4"
                                                        />
                                                        O
                                                    </label>
                                                    <label className="flex items-center gap-2">
                                                        <input
                                                            type="radio"
                                                            name="tfAnswer"
                                                            checked={currentQuiz.choices[1].isCorrect}
                                                            onChange={() => handleChoiceChange(1, { target: { name: 'isCorrect', type: 'radio', checked: true } })}
                                                            className="w-4 h-4"
                                                        />
                                                        X
                                                    </label>
                                                </div>
                                            )}
                                            {(currentQuiz.quizTypeId === "SUBJECTIVE" || currentQuiz.quizTypeId === "SHORT_ANSWER") && (
                                                <input
                                                    type="text"
                                                    name="choiceContent"
                                                    value={currentQuiz.choices[0].choiceContent}
                                                    onChange={(e) => handleChoiceChange(0, e)}
                                                    placeholder="정답을 입력하세요"
                                                    className="w-full p-2 border rounded"
                                                />
                                            )}
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    )}

                    <div className="flex justify-end gap-4 pt-4">
                        {step === 2 && (
                            <button
                                type="button"
                                onClick={addQuiz}
                                className="px-4 py-2 text-blue-600 bg-blue-50 rounded hover:bg-blue-100"
                            >
                                퀴즈 추가
                            </button>
                        )}
                        <button
                            type="submit"
                            className="px-4 py-2 text-white bg-blue-600 rounded hover:bg-blue-700"
                        >
                            {step === 1 ? '다음' : '생성 완료'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default QuizShowCreateModal;