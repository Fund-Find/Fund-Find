import { useState, useEffect } from 'react'

function QuizShowList() {
    const [quizShowList, setQuizShowList] = useState([])
    const [isLoading, setIsLoading] = useState(true)
    const [error, setError] = useState(null)

    useEffect(() => {
        fetchQuizShows()
    }, [])

    const fetchQuizShows = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/v1/quizshow')
            if (!response.ok) {
                throw new Error('퀴즈쇼 데이터를 불러오는데 실패했습니다.')
            }
            const result = await response.json()
            console.log('API Response:', result) // 데이터 구조 확인용
            if (result.resultCode === '200') {
                setQuizShowList(result.data.quizShows)
            }
            setIsLoading(false)
        } catch (err) {
            setError(err.message)
            setIsLoading(false)
        }
    }

    if (isLoading) {
        return <div>로딩 중...</div>
    }

    if (error) {
        return <div>Error: {error}</div>
    }

    return (
        <div className="container mx-auto p-4">
            <h1 className="text-2xl font-bold mb-6">퀴즈쇼 목록</h1>

            {quizShowList.length === 0 ? (
                <p>등록된 퀴즈쇼가 없습니다.</p>
            ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                    {quizShowList.map((quizShow) => (
                        <div key={quizShow.id} className="border rounded-lg p-4 hover:shadow-lg transition-shadow">
                            <h2 className="text-xl font-semibold">{quizShow.showName}</h2>
                            <p className="text-gray-600 mt-2">{quizShow.showDescription}</p>
                            <div className="mt-4 text-sm text-gray-500">
                                <div>카테고리: {quizShow.quizCategory}</div>
                                <div>문제 수: {quizShow.totalQuizCount}</div>
                                <div>총점: {quizShow.totalScore}</div>
                                <div>조회수: {quizShow.view}</div>
                            </div>
                            <div className="mt-4">
                                <button
                                    className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
                                    onClick={() => {
                                        /* TODO: 퀴즈쇼 상세 페이지로 이동 */
                                    }}
                                >
                                    상세보기
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    )
}

export default QuizShowList
