import { useState, useEffect, useRef } from "react";
import 'swiper/css';
import 'swiper/css/pagination';
import 'swiper/css/navigation';
import '../assets/css/QuizShowList.css';

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
            const response = await fetch(`http://localhost:8080/api/v1/quizshow?page=${page}&size=9&sort=id,desc`);
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
            <button
                    onClick={() => setShowCreatePopup(true)}
                    className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
                >
                    퀴즈쇼 생성
            </button>
            {quizShowList.length === 0 ? (
                <p className="text-center text-gray-500">등록된 퀴즈쇼가 없습니다.</p>
            ) : (
                <>
                    {/* 3x3 그리드 레이아웃 */}
                    <div className="quizshow-grid">
                        {quizShowList.map((quizShow) => {
                            const imagePath = quizShow.useCustomImage
                                ? `http://localhost:8080/uploads/${quizShow.customImagePath}`
                                : `/images/quizShow/${quizShow.quizCategory.toLowerCase()}.jpg`;
    
                            return (
                                <div
                                    key={quizShow.id}
                                    className="quizshow-item border rounded-lg overflow-hidden hover:shadow-lg transition-shadow bg-white"
                                    onClick={() => {
                                        window.location.href = `/quizshow/${quizShow.id}`;
                                    }} // 퀴즈쇼 클릭 시 상세 페이지로 이동
                                >
                                    <div className="quiz-image-container">
                                        <img
                                            src={imagePath}
                                            alt={quizShow.showName}
                                            onError={(e) => {
                                                e.target.src = '/images/fflogo.webp';
                                            }}
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
                                    </div>
                                </div>
                            );
                        })}
                    </div>
    
                    {/* 페이지네이션 */}
                    <div className="pagination-controls">
                        {/* 첫 페이지로 이동 |< */}
                        <button
                            onClick={() => handlePageChange(0)}
                            disabled={currentPage === 0}
                            className={`pagination-button ${currentPage === 0 ? 'disabled' : ''}`}
                        >
                            &lt;&lt;&lt;
                        </button>

                        {/* 10페이지 뒤로 이동 << */}
                        <button
                            onClick={() => handlePageChange(Math.max(0, currentPage - 10))}
                            disabled={currentPage < 10}
                            className={`pagination-button ${currentPage < 10 ? 'disabled' : ''}`}
                        >
                            &lt;&lt;
                        </button>

                        {/* 이전 페이지로 이동 < */}
                        <button
                            onClick={() => handlePageChange(currentPage - 1)}
                            disabled={currentPage === 0}
                            className={`pagination-button ${currentPage === 0 ? 'disabled' : ''}`}
                        >
                            &lt;
                        </button>

                        {/* 현재 페이지 / 전체 페이지 표시 */}
                        <span className="pagination-info">
                            {currentPage + 1} / {totalPages}
                        </span>

                        {/* 다음 페이지로 이동 > */}
                        <button
                            onClick={() => handlePageChange(currentPage + 1)}
                            disabled={currentPage === totalPages - 1}
                            className={`pagination-button ${currentPage === totalPages - 1 ? 'disabled' : ''}`}
                        >
                            &gt;
                        </button>

                        {/* 10페이지 앞으로 이동 >> */}
                        <button
                            onClick={() => handlePageChange(Math.min(totalPages - 1, currentPage + 10))}
                            disabled={currentPage >= totalPages - 10}
                            className={`pagination-button ${currentPage >= totalPages - 10 ? 'disabled' : ''}`}
                        >
                            &gt;&gt;
                        </button>

                        {/* 마지막 페이지로 이동 >| */}
                        <button
                            onClick={() => handlePageChange(totalPages - 1)}
                            disabled={currentPage === totalPages - 1}
                            className={`pagination-button ${currentPage === totalPages - 1 ? 'disabled' : ''}`}
                        >
                            &gt;&gt;&gt;
                        </button>
                    </div>
                </>
            )}
        </div>
    );
}

export default QuizShowList;