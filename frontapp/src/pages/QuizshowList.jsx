import React, { useState, useEffect } from "react";
import { useNavigate } from 'react-router-dom';
import QuizShowCreateModal from '../components/QuizShowCreateModal';    
import "../assets/css/QuizShowList.css";

function QuizShowList() {
    const navigate = useNavigate();
    const [quizShowList, setQuizShowList] = useState([]); // 전체 데이터
    const [filteredQuizShowList, setFilteredQuizShowList] = useState([]); // 필터링된 데이터
    const [paginatedQuizShowList, setPaginatedQuizShowList] = useState([]); // 현재 페이지 데이터
    const [currentPage, setCurrentPage] = useState(0); // 현재 페이지
    const [totalPages, setTotalPages] = useState(0); // 총 페이지 수
    const [categories, setCategories] = useState([]); // 카테고리 목록
    const [selectedCategory, setSelectedCategory] = useState(""); // 선택된 카테고리
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showCreateModal, setShowCreateModal] = useState(false); // 생성 모달 상태
    const pageSize = 9; // 페이지당 항목 수

    // 전체 데이터를 API로 가져오기
    const fetchQuizShows = async () => {
        try {
            const response = await fetch("http://localhost:8080/api/v1/quizshow?page=0&size=9999&sort=id,desc");
            if (!response.ok) {
                throw new Error("퀴즈쇼 데이터를 불러오는데 실패했습니다.");
            }
            const result = await response.json();
            if (result.resultCode === "200") {
                setQuizShowList(result.data.quizShows); // 전체 데이터 저장
                setFilteredQuizShowList(result.data.quizShows); // 초기에는 전체 데이터를 필터링된 데이터로 설정
                setCategories(result.data.categories); // 카테고리 저장
                setTotalPages(Math.ceil(result.data.quizShows.length / pageSize)); // 총 페이지 수 계산
            }
            setIsLoading(false);
        } catch (err) {
            setError(err.message);
            setIsLoading(false);
        }
    };

    // 카테고리 변경 시 필터링
    const handleCategoryChange = (category) => {
        setSelectedCategory(category); // 선택된 카테고리 업데이트
        setCurrentPage(0); // 페이지를 첫 페이지로 초기화

        if (!category) {
            setFilteredQuizShowList(quizShowList); // 전체 데이터 사용
        } else {
            const filtered = quizShowList.filter((quizShow) => quizShow.quizCategory === category);
            setFilteredQuizShowList(filtered); // 필터링된 데이터 설정
        }
    };

    // 페이지 변경 시 데이터 슬라이싱
    useEffect(() => {
        const startIndex = currentPage * pageSize;
        const endIndex = startIndex + pageSize;
        setPaginatedQuizShowList(filteredQuizShowList.slice(startIndex, endIndex)); // 현재 페이지 데이터 설정
        setTotalPages(Math.ceil(filteredQuizShowList.length / pageSize)); // 필터링된 데이터 기준으로 총 페이지 수 계산
    }, [filteredQuizShowList, currentPage]);

    // 초기 데이터 로드
    useEffect(() => {
        fetchQuizShows();
    }, []);

    const navigateToDetail = (quizShowId) => {
        navigate(`/quizshow/${quizShowId}`); // window.location.href 대신 navigate 사용
    };

    const handleCreateClick = () => {
        const token = localStorage.getItem("accessToken");
        if (!token) {
            if (window.confirm("로그인이 필요한 서비스입니다. 로그인 페이지로 이동하시겠습니까?")) {
                navigate('/user/login');
            }
            return;
        }
        console.log("모달 열기"); // 디버깅용
        setShowCreateModal(true);
    };

    const handleCreateQuizShow = async (formData) => {
        try {
            const response = await fetch("http://localhost:8080/api/v1/quizshow", {
                method: "POST",
                body: formData,
                credentials: "include"
            });
            const data = await response.json();
            
            if (data.resultCode === "200") {
                alert("퀴즈쇼가 생성되었습니다.");
                
                // 새로운 퀴즈쇼 데이터
                const newQuizShow = data.data.quizShow;
                
                // 전체 리스트 업데이트
                setQuizShowList(prevList => [newQuizShow, ...prevList]);
                
                // 현재 선택된 카테고리에 따른 필터링 적용
                if (!selectedCategory || newQuizShow.quizCategory === selectedCategory) {
                    setFilteredQuizShowList(prevFiltered => [newQuizShow, ...prevFiltered]);
                }
                
                // 첫 페이지로 이동
                setCurrentPage(0);
                
                // 모달 닫기
                setShowCreateModal(false);
            } else {
                throw new Error(data.msg || "퀴즈쇼 생성에 실패했습니다.");
            }
        } catch (error) {
            console.error("퀴즈쇼 생성 실패:", error);
            alert("퀴즈쇼 생성에 실패했습니다.");
        }
    };

    if (isLoading) {
        return <div>로딩 중...</div>;
    }

    if (error) {
        return <div>Error: {error}</div>;
    }

    return (
        <div className="container mx-auto p-4">
            <h1 className="text-2xl font-bold mb-6">퀴즈쇼 목록</h1>

            {/* 카테고리 필터 및 생성 버튼 */}
            <div className="filter-container mb-4 flex justify-between items-center">
                <select
                    value={selectedCategory}
                    onChange={(e) => handleCategoryChange(e.target.value)}
                    className="p-2 border rounded"
                >
                    <option value="">모든 카테고리</option>
                    {categories.map((category) => (
                        <option key={category.categoryEnum} value={category.categoryEnum}>
                            {category.description}
                        </option>
                    ))}
                </select>
                <button
                    onClick={handleCreateClick}
                    className={`bg-blue-600 text-white font-bold py-2 px-4 rounded hover:bg-blue-700 ${
                        !localStorage.getItem("accessToken") ? "opacity-50 cursor-not-allowed" : ""
                    }`}
                    disabled={!localStorage.getItem("accessToken")}
                >
                    퀴즈쇼 생성
                </button>
            </div>

            {/* 퀴즈쇼 목록 */}
            <div className="quizshow-grid grid grid-cols-1 md:grid-cols-3 gap-6">
                {paginatedQuizShowList.length === 0 ? (
                    <p className="text-center text-gray-500">선택된 카테고리에 등록된 퀴즈쇼가 없습니다.</p>
                ) : (
                    paginatedQuizShowList.map((quizShow) => (
                        <div
                            key={quizShow.id}
                            className="quizshow-item border rounded-lg overflow-hidden hover:shadow-lg transition-shadow bg-white cursor-pointer"
                            onClick={() => navigateToDetail(quizShow.id)}
                        >
                            {/* 이미지 */}
                            <div className="quiz-image-container">
                                <img
                                    src={quizShow.useCustomImage ? `http://localhost:8080/uploads/${quizShow.customImagePath}` : `/images/quizShow/${quizShow.quizCategory.toLowerCase()}.jpg`}
                                    alt={quizShow.showName}
                                    onError={(e) => {
                                        e.target.src = "/images/fflogo.webp";
                                    }}
                                    className="w-full h-48 object-cover"
                                />
                            </div>
                            <div className="p-4">
                                <h2 className="text-xl font-semibold">{quizShow.showName}</h2>
                                <p>{quizShow.showDescription}</p>
                                <div className="text-sm text-gray-500">
                                    <div>카테고리: {quizShow.quizCategory}</div>
                                    <div>문제 수: {quizShow.totalQuizCount}</div>
                                    <div>총점: {quizShow.totalScore}</div>
                                </div>
                            </div>
                        </div>
                    ))
                )}
            </div>

            {/* 페이지네이션 */}
            <div className="pagination-controls flex justify-center mt-6 space-x-2">
                <button
                    onClick={() => setCurrentPage(0)}
                    disabled={currentPage === 0}
                    className={`pagination-button px-4 py-2 ${currentPage === 0 ? "disabled" : ""}`}
                >
                    &lt;&lt;&lt;
                </button>
                <button
                    onClick={() => setCurrentPage(Math.max(0, currentPage - 10))}
                    disabled={currentPage < 10}
                    className={`pagination-button px-4 py-2 ${currentPage < 10 ? "disabled" : ""}`}
                >
                    &lt;&lt;
                </button>
                <button
                    onClick={() => setCurrentPage(currentPage - 1)}
                    disabled={currentPage === 0}
                    className={`pagination-button px-4 py-2 ${currentPage === 0 ? "disabled" : ""}`}
                >
                    &lt;
                </button>
                <span className="pagination-info px-4 py-2">
                    {currentPage + 1} / {totalPages}
                </span>
                <button
                    onClick={() => setCurrentPage(currentPage + 1)}
                    disabled={currentPage >= totalPages - 1}
                    className={`pagination-button px-4 py-2 ${currentPage >= totalPages - 1 ? "disabled" : ""}`}
                >
                    &gt;
                </button>
                <button
                    onClick={() => setCurrentPage(Math.min(totalPages - 1, currentPage + 10))}
                    disabled={currentPage >= totalPages - 10}
                    className={`pagination-button px-4 py-2 ${currentPage >= totalPages - 10 ? "disabled" : ""}`}
                >
                    &gt;&gt;
                </button>
                <button
                    onClick={() => setCurrentPage(totalPages - 1)}
                    disabled={currentPage >= totalPages - 1}
                    className={`pagination-button px-4 py-2 ${currentPage >= totalPages - 1 ? "disabled" : ""}`}
                >
                    &gt;&gt;&gt;
                </button>
            </div>

            {/* 퀴즈쇼 생성 모달 */}
            {showCreateModal && (
                <QuizShowCreateModal
                    onClose={() => setShowCreateModal(false)}
                    onSubmit={handleCreateQuizShow}
                    categories={categories}
                />
            )}
        </div>
    );
}

export default QuizShowList;