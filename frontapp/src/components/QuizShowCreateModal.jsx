import React, { useState, useEffect } from 'react'
import { X, Plus, Trash2 } from 'lucide-react'
import { useNavigate } from 'react-router-dom'
import '../assets/css/quizshow.css'

const QuizShowCreateModal = ({ onClose, onSubmit, categories }) => {
    const navigate = useNavigate()
    const [step, setStep] = useState(1)
    const [basicInfo, setBasicInfo] = useState({
        showName: '',
        category: '',
        showDescription: '',
        useCustomImage: false,
    })
    const [imageFile, setImageFile] = useState(null)
    const [previewUrl, setPreviewUrl] = useState('')
    const [quizzes, setQuizzes] = useState([])
    const [currentQuiz, setCurrentQuiz] = useState({
        quizType: 'MULTIPLE_CHOICE',
        quizContent: '',
        quizScore: 10,
        choices: [
            { choiceContent: '', isCorrect: false },
            { choiceContent: '', isCorrect: false },
        ],
    })

    // 로그인 체크
    useEffect(() => {
        const token = localStorage.getItem('accessToken')
        if (!token) {
            alert('로그인이 필요한 서비스입니다.')
            navigate('/auth/login', { state: { from: location.pathname } })
            onClose()
        }
    }, [])

    const handleBasicInfoChange = (e) => {
        const { name, value, type, checked } = e.target
        setBasicInfo((prev) => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value,
        }))
    }

    const handleImageChange = (e) => {
        const file = e.target.files[0]
        if (file) {
            setImageFile(file)
            setPreviewUrl(URL.createObjectURL(file))
            setBasicInfo((prev) => ({
                ...prev,
                useCustomImage: true,
            }))
        }
    }

    const handleQuizChange = (e) => {
        const { name, value } = e.target
        setCurrentQuiz((prev) => ({
            ...prev,
            [name]: value,
        }))
    }

    const handleChoiceChange = (index, e) => {
        const { name, value, type, checked } = e.target
        setCurrentQuiz((prev) => ({
            ...prev,
            choices: prev.choices.map((choice, i) => {
                if (i === index) {
                    return {
                        ...choice,
                        [name]: type === 'radio' || type === 'checkbox' ? checked : value,
                    }
                }
                // radio 타입인 경우 다른 모든 선택지의 isCorrect를 false로 설정
                if (type === 'radio' && name === 'isCorrect' && checked) {
                    return { ...choice, isCorrect: false }
                }
                return choice
            }),
        }))
    }

    const addChoice = () => {
        if (currentQuiz.choices.length < 5) {
            setCurrentQuiz((prev) => ({
                ...prev,
                choices: [...prev.choices, { choiceContent: '', isCorrect: false }],
            }))
        }
    }

    const removeChoice = (index) => {
        if (currentQuiz.choices.length > 2) {
            setCurrentQuiz((prev) => ({
                ...prev,
                choices: prev.choices.filter((_, i) => i !== index),
            }))
        }
    }

    const validateQuiz = () => {
        if (!currentQuiz.quizContent.trim()) {
            alert('문제 내용을 입력해주세요.')
            return false
        }

        switch (currentQuiz.quizType) {
            case 'MULTIPLE_CHOICE':
                if (!currentQuiz.choices.some((choice) => choice.isCorrect)) {
                    alert('정답을 선택해주세요.')
                    return false
                }
                if (currentQuiz.choices.some((choice) => !choice.choiceContent.trim())) {
                    alert('모든 선택지를 입력해주세요.')
                    return false
                }
                break

            case 'TRUE_FALSE':
                if (!currentQuiz.choices.some((choice) => choice.isCorrect)) {
                    alert('O 또는 X를 선택해주세요.')
                    return false
                }
                break

            case 'SUBJECTIVE':
            case 'SHORT_ANSWER':
                if (!currentQuiz.choices[0].choiceContent.trim()) {
                    alert('정답을 입력해주세요.')
                    return false
                }
                break
        }

        return true
    }

    const addQuiz = () => {
        if (validateQuiz()) {
            setQuizzes((prev) => [...prev, { ...currentQuiz }])
            // 새로운 퀴즈 폼 초기화
            setCurrentQuiz({
                quizType: currentQuiz.quizType, // 현재 선택된 타입 유지
                quizContent: '',
                quizScore: 10,
                choices:
                    currentQuiz.quizType === 'TRUE_FALSE'
                        ? [
                              { choiceContent: 'T', isCorrect: false },
                              { choiceContent: 'F', isCorrect: false },
                          ]
                        : currentQuiz.quizType === 'MULTIPLE_CHOICE'
                        ? [
                              { choiceContent: '', isCorrect: false },
                              { choiceContent: '', isCorrect: false },
                          ]
                        : [{ choiceContent: '', isCorrect: true }],
            })
        }
    }

    const handleSubmit = async (e) => {
        e.preventDefault()

        if (step === 1) {
            if (!basicInfo.showName || !basicInfo.category || !basicInfo.showDescription) {
                alert('모든 필수 항목을 입력해주세요.')
                return
            }
            setStep(2)
            return
        }

        // 퀴즈 추가 확인
        if (quizzes.length === 0) {
            if (!validateQuiz()) return
            quizzes.push({ ...currentQuiz })
        }

        try {
            const formData = new FormData()
            
            // 이미지 파일이 있는 경우에만 추가 
            if (imageFile) {
                formData.append('imageFile', imageFile)
            }

            const submitData = {
                showName: basicInfo.showName,
                category: basicInfo.category,
                showDescription: basicInfo.showDescription,
                useCustomImage: basicInfo.useCustomImage,
                totalQuizCount: quizzes.length,
                totalScore: quizzes.reduce((sum, quiz) => sum + parseInt(quiz.quizScore), 0),
                quizzes: quizzes.map((quiz) => ({
                    quizType: quiz.quizType,
                    quizContent: quiz.quizContent,
                    quizScore: quiz.quizScore,
                    choices: quiz.choices,
                })),
            }

            formData.append(
                'data',
                new Blob([JSON.stringify(submitData)], {
                    type: 'application/json',
                }),
            )

            const response = await fetch('http://localhost:8080/api/v1/quizshow', {
                method: 'POST',
                credentials: 'include', // 쿠키를 전송하기 위해 추가
                body: formData,
            })

            // 응답 상태 코드 확인
            if (!response.ok) {
                if (response.status === 403 || response.status === 401) {
                    // 인증 실패 시 로그인 페이지로 이동
                    navigate('/auth/login', {
                        state: { from: location.pathname },
                    })
                    return
                }
                throw new Error(`HTTP error! status: ${response.status}`)
            }

            const result = await response.json()

            if (result.resultCode === '200') {
                alert('퀴즈쇼가 성공적으로 생성되었습니다.')
                onClose()
            } else {
                throw new Error(result.msg || '퀴즈쇼 생성에 실패했습니다.')
            }
        } catch (error) {
            alert(error.message)
        }
    }

    return (
        <div className="quiz-popup-overlay">
            <div className="quiz-popup">
                <button className="close-button" onClick={onClose}>
                    <X size={24} />
                </button>
                <form onSubmit={handleSubmit}>
                    {step === 1 ? (
                        <div className="quizinfo">
                            <h2>퀴즈쇼 정보 입력</h2>
                            <label>
                                퀴즈쇼 제목 *
                                <input
                                    type="text"
                                    name="showName"
                                    value={basicInfo.showName}
                                    onChange={handleBasicInfoChange}
                                    required
                                />
                            </label>
                            <label>
                                카테고리 *
                                <select
                                    name="category"
                                    value={basicInfo.category}
                                    onChange={handleBasicInfoChange}
                                    required
                                >
                                    <option value="">카테고리 선택</option>
                                    {categories.map((category) => (
                                        <option key={category.categoryEnum} value={category.categoryEnum}>
                                            {category.description}
                                        </option>
                                    ))}
                                </select>
                            </label>
                            <label>
                                퀴즈쇼 설명 *
                                <textarea
                                    name="showDescription"
                                    value={basicInfo.showDescription}
                                    onChange={handleBasicInfoChange}
                                    required
                                />
                            </label>
                            <label>
                                대표 이미지
                                <input type="file" accept="image/*" onChange={handleImageChange} />
                            </label>
                            {previewUrl && <img src={previewUrl} alt="Preview" className="quiz-preview" />}
                        </div>
                    ) : (
                        // 퀴즈 추가 단계
                        <div className="space-y-6">
                            {/* 기존 퀴즈 목록 */}
                            {quizzes.length > 0 && (
                                <div className="quizinfo">
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
                                <div className="quizinfo">
                                    <div className="quiz-form">
                                        <label className="block text-sm font-medium text-gray-700 mb-1">
                                            퀴즈 타입
                                        </label>
                                        <select
                                            name="quizType"
                                            value={currentQuiz.quizType}
                                            onChange={(e) => {
                                                const newType = e.target.value
                                                setCurrentQuiz((prev) => ({
                                                    ...prev,
                                                    quizType: newType,
                                                    choices:
                                                        newType === 'TRUE_FALSE'
                                                            ? [
                                                                  { choiceContent: 'T', isCorrect: false },
                                                                  { choiceContent: 'F', isCorrect: false },
                                                              ]
                                                            : newType === 'MULTIPLE_CHOICE'
                                                            ? [
                                                                  { choiceContent: '', isCorrect: false },
                                                                  { choiceContent: '', isCorrect: false },
                                                              ]
                                                            : [{ choiceContent: '', isCorrect: true }],
                                                }))
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
                                        <label className="block text-sm font-medium text-gray-700 mb-1">문제</label>
                                        <textarea
                                            name="quizContent"
                                            value={currentQuiz.quizContent}
                                            onChange={handleQuizChange}
                                            className="w-full p-2 border rounded focus:ring-2 focus:ring-blue-500"
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-1">배점</label>
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
                                            {currentQuiz.quizType === 'MULTIPLE_CHOICE' && '선택지 *'}
                                            {currentQuiz.quizType === 'TRUE_FALSE' && 'OX 선택 *'}
                                            {currentQuiz.quizType === 'SUBJECTIVE' && '정답 *'}
                                            {currentQuiz.quizType === 'SHORT_ANSWER' && '정답 *'}
                                        </label>
                                        <div className="space-y-2">
                                            {currentQuiz.quizType === 'MULTIPLE_CHOICE' && (
                                                <>
                                                    {currentQuiz.choices.map((choice, index) => (
                                                        <div key={index} className="quiz-choice-container">
                                                            <input
                                                                type="radio"
                                                                name="isCorrect"
                                                                checked={choice.isCorrect}
                                                                onChange={(e) => handleChoiceChange(index, e)}
                                                                className="quiz-radio"
                                                            />
                                                            <input
                                                                type="text"
                                                                name="choiceContent"
                                                                value={choice.choiceContent}
                                                                onChange={(e) => handleChoiceChange(index, e)}
                                                                placeholder={`선택지 ${index + 1}`}
                                                                className="quiz-choice-input"
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
                                            {currentQuiz.quizType === 'TRUE_FALSE' && (
                                                <div className="flex gap-4">
                                                    <label className="quiz-ox flex items-center gap-2">
                                                        <input
                                                            type="radio"
                                                            name="tfAnswer"
                                                            checked={currentQuiz.choices[0].isCorrect}
                                                            onChange={() =>
                                                                handleChoiceChange(0, {
                                                                    target: {
                                                                        name: 'isCorrect',
                                                                        type: 'radio',
                                                                        checked: true,
                                                                    },
                                                                })
                                                            }
                                                            className="quiz-radio"
                                                        />
                                                        <div className="quiz-ox-text">O</div>
                                                    </label>
                                                    <label className="quiz-ox flex items-center gap-2">
                                                        <input
                                                            type="radio"
                                                            name="tfAnswer"
                                                            checked={currentQuiz.choices[1].isCorrect}
                                                            onChange={() =>
                                                                handleChoiceChange(1, {
                                                                    target: {
                                                                        name: 'isCorrect',
                                                                        type: 'radio',
                                                                        checked: true,
                                                                    },
                                                                })
                                                            }
                                                            className="quiz-radio"
                                                        />
                                                        <div className="quiz-ox-text">X</div>
                                                    </label>
                                                </div>
                                            )}
                                            {(currentQuiz.quizType === 'SUBJECTIVE' ||
                                                currentQuiz.quizType === 'SHORT_ANSWER') && (
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
                    <div className="quiz-button-container">
                        <button
                            type="button"
                            onClick={() => (step === 2 ? setStep(1) : onClose())}
                            className="quiz-button"
                        >
                            {step === 2 ? '이전' : '취소'}
                        </button>
                        {step === 2 && (
                            <button type="button" onClick={addQuiz} className="quiz-button">
                                퀴즈 추가
                            </button>
                        )}
                        <button type="submit" className="quiz-button">
                            {step === 1 ? '다음' : '생성 완료'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    )
}

export default QuizShowCreateModal
