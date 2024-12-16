import { useState, useEffect } from "react";

function QuizShowList() {
    const [quizShowList, setQuizShowList] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);

    useEffect(() => {
        fetchQuizShows(currentPage);
    }, [currentPage]);

    const fetchQuizShows = async (page) => {
        try {
            const response = await fetch(`http://localhost:8080/api/v1/quizshow?page=${page}&size=9`);
            if (!response.ok) {
                throw new Error('퀴즈쇼 데이터를 불러오는데 실패했습니다.');
            }
            const result = await response.json();
            if (result.resultCode === "200") {
                setQuizShowList(result.data.quizShows);
                setTotalPages(result.data.totalPages);
                setTotalElements(result.data.totalElements);
            }
            setIsLoading(false);
        } catch (err) {
            setError(err.message);
            setIsLoading(false);
        }
    };

    const handlePageChange = (newPage) => {
        setCurrentPage(newPage);
    };

    if (isLoading) {
        return (
            <div className="flex justify-center items-center min-h-screen">
                <div className="animate-spin rounded-full h-16 w-16 border-t-2 border-b-2 border-blue-500"></div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="container mx-auto p-4 text-red-500 text-center">
                Error: {error}
            </div>
        );
    }

    return (
        <div className="container mx-auto p-4">
            <h1 className="text-2xl font-bold mb-6">퀴즈쇼 목록</h1>
            
            {quizShowList.length === 0 ? (
                <p className="text-center text-gray-500">등록된 퀴즈쇼가 없습니다.</p>
            ) : (
                <>
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                        {quizShowList.map((quizShow) => (
                            <div key={quizShow.id} 
                                className="border rounded-lg overflow-hidden hover:shadow-lg transition-shadow bg-white">
                                <div className="aspect-w-16 aspect-h-9">
                                    <img 
                                        src={quizShow.useCustomImage ? 
                                            `http://localhost:8080/uploads/${quizShow.customImagePath}` : 
                                            `/images/quizShow/${quizShow.quizCategory.toLowerCase()}.jpg`}
                                        alt={quizShow.showName}
                                        className="object-cover w-full h-48"
                                    />
                                </div>
                                <div className="p-4">
                                    <h2 className="text-xl font-semibold mb-2">{quizShow.showName}</h2>
                                    <p className="text-gray-600 mb-4 line-clamp-2">{quizShow.showDescription}</p>
                                    <div className="text-sm text-gray-500 space-y-1">
                                        <div>카테고리: {quizShow.quizCategory}</div>
                                        <div className="flex justify-between">
                                            <span>문제 수: {quizShow.totalQuizCount}</span>
                                            <span>총점: {quizShow.totalScore}</span>
                                        </div>
                                        <div className="flex justify-between">
                                            <span>조회수: {quizShow.view}</span>
                                            <span>추천: {quizShow.voteCount}</span>
                                        </div>
                                    </div>
                                    <div className="mt-4">
                                        <button 
                                            className="w-full bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 transition-colors"
                                            onClick={() => {
                                                window.location.href = `/quizshow/${quizShow.id}`;
                                            }}
                                        >
                                            상세보기
                                        </button>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                    
                    <div className="mt-8 flex justify-center gap-2">
                        <button
                            onClick={() => handlePageChange(currentPage - 1)}
                            disabled={currentPage === 0}
                            className={`px-4 py-2 rounded ${
                                currentPage === 0 
                                ? 'bg-gray-200 text-gray-500 cursor-not-allowed' 
                                : 'bg-blue-500 text-white hover:bg-blue-600'
                            }`}
                        >
                            이전
                        </button>
                        {[...Array(totalPages)].map((_, i) => (
                            <button
                                key={i}
                                onClick={() => handlePageChange(i)}
                                className={`px-4 py-2 rounded ${
                                    currentPage === i
                                    ? 'bg-blue-500 text-white'
                                    : 'bg-gray-200 hover:bg-gray-300'
                                }`}
                            >
                                {i + 1}
                            </button>
                        ))}
                        <button
                            onClick={() => handlePageChange(currentPage + 1)}
                            disabled={currentPage === totalPages - 1}
                            className={`px-4 py-2 rounded ${
                                currentPage === totalPages - 1
                                ? 'bg-gray-200 text-gray-500 cursor-not-allowed'
                                : 'bg-blue-500 text-white hover:bg-blue-600'
                            }`}
                        >
                            다음
                        </button>
                    </div>
                </>
            )}
        </div>
    );
}

export default QuizShowList;