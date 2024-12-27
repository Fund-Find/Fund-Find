import { useState, useEffect, useMemo } from "react";
import 'swiper/css';
import 'swiper/css/pagination';
import 'swiper/css/navigation';
import '../assets/css/QuizShowList.css';
import QuizShowCreateModal from '../components/QuizShowCreateModal';

function QuizShowList() {
    // 상태 관리
    const [quizShowList, setQuizShowList] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [showCreateModal, setShowCreateModal] = useState(false);
    const [quizTypes, setQuizTypes] = useState([]);
    const [categories, setCategories] = useState([]);
    const [selectedCategory, setSelectedCategory] = useState('');

    // 퀴즈쇼 목록 불러오기
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
                setQuizTypes(result.data.quizTypes);
                setCategories(result.data.categories);
                setTotalPages(result.data.totalPages);
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

    const handleCreateQuizShow = async (formData) => {
        try {
            const response = await fetch('http://localhost:8080/api/v1/quizshow', {
                method: 'POST',
                body: formData,
                credentials: 'include'
            });
            const data = await response.json();
            if (data.resultCode === "200") {
                alert('퀴즈쇼가 생성되었습니다.');
                fetchQuizShows(currentPage);
            } else {
                throw new Error(data.msg || '퀴즈쇼 생성에 실패했습니다.');
            }
        } catch (error) {
            console.error('퀴즈쇼 생성 실패:', error);
            alert('퀴즈쇼 생성에 실패했습니다.');
        }
    };

    const handleCreateClick = () => {
        const token = localStorage.getItem('accessToken');
        if (!token) {
            if (window.confirm('로그인이 필요한 서비스입니다. 로그인 페이지로 이동하시겠습니까?')) {
                navigate('/user/login', { 
                    state: { from: '/quizshow/list' }
                });
            }
            return;
        }
        setShowCreateModal(true);
    };

    // 필터링된 퀴즈쇼 목록
    const filteredQuizShowList = useMemo(() => {
        return selectedCategory
            ? quizShowList.filter((quizShow) => quizShow.quizCategory === selectedCategory)
            : quizShowList;
    }, [selectedCategory, quizShowList]);

    return (
        <div className="container mx-auto p-4">
            {isLoading ? (
                <div className="flex justify-center items-center min-h-screen">
                    <div className="animate-spin rounded-full h-16 w-16 border-t-2 border-b-2 border-blue-500"></div>
                </div>
            ) : error ? (
                <div className="container mx-auto p-4 text-red-500 text-center">
                    Error: {error}
                </div>
            ) : (
                <>
                    <h1 className="text-2xl font-bold mb-6">퀴즈쇼 목록</h1>
        
                    {/* 퀴즈쇼 생성 버튼 */}
                    <button
                        onClick={handleCreateClick}
                        className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded mb-4"
                    >
                        퀴즈쇼 생성
                    </button>
        
                    {/* 카테고리 필터 */}
                    <select
                        value={selectedCategory}
                        onChange={(e) => setSelectedCategory(e.target.value)}
                        className="mb-6 p-2 border rounded"
                    >
                        <option value="">모든 카테고리</option>
                        {categories.map((category) => (
                            <option key={category.categoryEnum} value={category.categoryEnum}>
                                {category.description}
                            </option>
                        ))}
                    </select>
        
                    {/* 퀴즈쇼 목록 */}
                    {filteredQuizShowList.length === 0 ? (
                        <p className="text-center text-gray-500">등록된 퀴즈쇼가 없습니다.</p>
                    ) : (
                        <div className="quizshow-grid grid grid-cols-1 md:grid-cols-3 gap-6">
                            {filteredQuizShowList.map((quizShow) => {
                                const imagePath = quizShow.useCustomImage
                                    ? `http://localhost:8080/uploads/${quizShow.customImagePath}`
                                    : `/images/quizShow/${quizShow.quizCategory.toLowerCase()}.jpg`;
            
                                return (
                                    <div
                                        key={quizShow.id}
                                        className="quizshow-item border rounded-lg overflow-hidden hover:shadow-lg transition-shadow bg-white"
                                        onClick={() => {
                                            window.location.href = `/quizshow/${quizShow.id}`;
                                        }}
                                    >
                                        {/* 이미지 */}
                                        <div className="quiz-image-container">
                                            <img
                                                src={imagePath}
                                                alt={quizShow.showName}
                                                onError={(e) => {
                                                    e.target.src = '/images/fflogo.webp';
                                                }}
                                            />
                                        </div>
            
                                        {/* 정보 */}
                                        <div className="p-4">
                                            <h2 className="text-xl font-semibold mb-2">
                                                {quizShow.showName}
                                            </h2>
                                            <p className="text-gray-600 mb-4 line-clamp-2">
                                                {quizShow.showDescription}
                                            </p>
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
                    )}
        
                    {/* 페이지네이션 */}
                    <div className="pagination-controls flex justify-center mt-6 space-x-2">
                        <button
                            onClick={() => handlePageChange(0)}
                            disabled={currentPage === 0}
                            className={`pagination-button px-4 py-2 ${currentPage === 0 ? 'disabled' : ''}`}
                        >
                            &lt;&lt;&lt;
                        </button>
                        <button
                            onClick={() => handlePageChange(Math.max(0, currentPage - 10))}
                            disabled={currentPage < 10}
                            className={`pagination-button px-4 py-2 ${currentPage < 10 ? 'disabled' : ''}`}
                        >
                            &lt;&lt;
                        </button>
                        <button
                            onClick={() => handlePageChange(currentPage - 1)}
                            disabled={currentPage === 0}
                            className={`pagination-button px-4 py-2 ${currentPage === 0 ? 'disabled' : ''}`}
                        >
                            &lt;
                        </button>
                        <span className="pagination-info px-4 py-2">
                            {currentPage + 1} / {totalPages}
                        </span>
                        <button
                            onClick={() => handlePageChange(currentPage + 1)}
                            disabled={currentPage === totalPages - 1}
                            className={`pagination-button px-4 py-2 ${currentPage === totalPages - 1 ? 'disabled' : ''}`}
                        >
                            &gt;
                        </button>
                        <button
                            onClick={() => handlePageChange(Math.min(totalPages - 1, currentPage + 10))}
                            disabled={currentPage >= totalPages - 10}
                            className={`pagination-button px-4 py-2 ${currentPage >= totalPages - 10 ? 'disabled' : ''}`}
                        >
                            &gt;&gt;
                        </button>
                        <button
                            onClick={() => handlePageChange(totalPages - 1)}
                            disabled={currentPage === totalPages - 1}
                            className={`pagination-button px-4 py-2 ${currentPage === totalPages - 1 ? 'disabled' : ''}`}
                        >
                            &gt;&gt;&gt;
                        </button>
                    </div>
                </>
            )}

            {/* 퀴즈쇼 생성 모달 */}
            {showCreateModal && (
                <QuizShowCreateModal
                    onClose={() => setShowCreateModal(false)}
                    onSubmit={handleCreateQuizShow}
                    quizTypes={quizTypes}
                    categories={categories}
                />
            )}
        </div>
    );
}

export default QuizShowList;