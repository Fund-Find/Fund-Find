import React, { useEffect, useState } from 'react'
import '../assets/css/profile.css'
import { useNavigate } from 'react-router-dom'
import PasswordChange from '../components/PsswordChange'
import Survey from '../components/Survey'

const API_URL = 'http://localhost:8080/api/v1/user/profile'
const UPDATE_API_URL = 'http://localhost:8080/api/v1/user/profile'
const UPDATE_EMAIL_URL = 'http://localhost:8080/api/v1/user/profile/email'

const Profile = () => {
    const [user, setUser] = useState(null)
    const [previewImage, setPreviewImage] = useState(null)
    const [originalUser, setOriginalUser] = useState(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)
    const [isEditing, setIsEditing] = useState(false)
    const [nicknameError, setNicknameError] = useState('')
    const [introError, setIntroError] = useState('')
    const navigate = useNavigate()
    const [showPasswordChange, setShowPasswordChange] = useState(false)
    const [showSurveyPopup, setShowSurveyPopup] = useState(false)

    useEffect(() => {
        fetch(API_URL, {
            method: 'GET',
            credentials: 'include',
        })
            .then((response) => response.json())
            .then((data) => {
                if (data.resultCode === '200') {
                    setUser(data.data)
                    setOriginalUser(data.data)
                    setPreviewImage(data.data.thumbnailImg)
                } else {
                    setError(data.msg || '사용자 정보를 가져오는 데 실패했습니다.')
                }
                setLoading(false)
            })
            .catch(() => {
                setError('서버와 연결할 수 없습니다.')
                setLoading(false)
            })
    }, [])

    const handleEdit = () => {
        setIsEditing(true)
    }

    const handleSave = () => {
        if (user.nickname.length > 12) {
            setNicknameError('닉네임은 최대 12자까지 가능합니다.')
            return
        }

        if (user.intro.length > 500) {
            setIntroError('자기소개는 최대 500자까지 가능합니다.')
            return
        }

        const formData = new FormData()
        formData.append('nickname', user.nickname)
        formData.append('intro', user.intro)
        if (user.thumbnailImg instanceof File) {
            formData.append('thumbnailImg', user.thumbnailImg)
        }

        fetch(UPDATE_API_URL, {
            method: 'PATCH',
            credentials: 'include',
            body: formData,
        })
            .then((response) => response.json())
            .then((data) => {
                if (data.resultCode === '200') {
                    setUser(data.data)
                    setOriginalUser(data.data)
                    setPreviewImage(data.data.thumbnailImg)
                    setIsEditing(false)
                    setNicknameError('')
                    setIntroError('')
                } else {
                    throw new Error(data.msg || '저장 실패')
                }
            })
            .catch(() => {
                setError('프로필을 저장하는 데 실패했습니다.')
            })
    }

    const handleCancel = () => {
        setIsEditing(false)
        setUser(originalUser)
        setPreviewImage(originalUser.thumbnailImg)
        setNicknameError('')
        setIntroError('')
    }

    const handleSurveyClick = () => {
        setShowSurveyPopup(true)
    }

    const handleNicknameChange = (e) => {
        const value = e.target.value
        if (value.length > 12) {
            setNicknameError('닉네임은 최대 12자까지 가능합니다.')
        } else {
            setNicknameError('')
        }
        setUser({ ...user, nickname: value })
    }

    const handleIntroChange = (e) => {
        const value = e.target.value
        if (value.length > 500) {
            setIntroError('자기소개는 최대 500자까지 가능합니다.')
        } else {
            setIntroError('')
        }
        setUser({ ...user, intro: value })
    }

    if (loading) {
        return <div>로딩 중...</div>
    }

    if (error) {
        return <div>{error}</div>
    }
    // 비밀번호 변경 화면 렌더링
    if (showPasswordChange) {
        return <PasswordChange onBack={() => setShowPasswordChange(false)} />
    }

    return (
        <div className="profile-container">
            <h2>프로필</h2>
            <div className="profile-info">
                <div>
                    <div className="profileupdate">
                        {isEditing ? (
                            <>
                                <button onClick={handleSave}>저장</button>
                                <button onClick={handleCancel}>취소</button>
                            </>
                        ) : (
                            <>
                                <button onClick={handleEdit}>프로필 변경</button>
                            </>
                        )}
                    </div>

                    <strong>프로필 이미지:</strong>
                    {isEditing ? (
                        <>
                            <input
                                type="file"
                                accept="image/*"
                                onChange={(e) => {
                                    const file = e.target.files[0]
                                    setUser({ ...user, thumbnailImg: file })
                                    setPreviewImage(URL.createObjectURL(file))
                                }}
                            />
                            {previewImage && <img src={previewImage} alt="미리보기" />}
                        </>
                    ) : (
                        user.thumbnailImg && <img src={user.thumbnailImg} alt="프로필 이미지" width="100" />
                    )}
                </div>
                <div>
                    <strong>사용자 ID:</strong>
                    <input
                        type="text"
                        value={user.username}
                        readOnly
                        style={{ backgroundColor: '#f0f0f0', border: 'none' }}
                    />
                </div>
                <div>
                    <strong>닉네임:</strong>
                    {isEditing ? (
                        <>
                            <input type="text" value={user.nickname} onChange={handleNicknameChange} />
                            {nicknameError && <div style={{ color: 'red' }}>{nicknameError}</div>}
                        </>
                    ) : (
                        user.nickname
                    )}
                </div>
                <div>
                    <strong>이메일:</strong>
                    <input
                        type="email"
                        value={user.email}
                        readOnly
                        style={{ backgroundColor: '#f0f0f0', border: 'red' }}
                    />
                </div>
                <div>
                    <strong>소개:</strong>
                    {isEditing ? (
                        <>
                            <textarea value={user.intro} onChange={handleIntroChange} />
                            {introError && <div style={{ color: 'red' }}>{introError}</div>}
                        </>
                    ) : (
                        user.intro
                    )}
                </div>
                <div>
                    <strong>투자성향 MBTI:</strong>
                    {user.propensity ? (
                        <div>
                            <input
                                type="text"
                                value={user.propensity.surveyResult}
                                readOnly
                                style={{ backgroundColor: '#f0f0f0', border: 'none', marginRight: '10px' }}
                            />
                            {!isEditing && (
                                <button
                                    className="myfundlist"
                                    onClick={() => {
                                        navigate('/result', {
                                            state: { propensityId: user.propensity.propensityId },
                                        })
                                    }}
                                >
                                    내 성향에 맞는 펀드 목록 보기 !
                                </button>
                            )}
                        </div>
                    ) : (
                        <span>
                            <div className="surveyanchor" onClick={handleSurveyClick}>
                                설문조사를 통해 투자성향을 알아보세요!
                            </div>
                        </span>
                    )}
                </div>
            </div>
            <button onClick={() => setShowPasswordChange(true)}>비밀번호 변경</button>
            {showSurveyPopup && (
                <div className="survey-popup-overlay">
                    <div className="survey-popup">
                        <button className="popup-close-btn" onClick={() => setShowSurveyPopup(false)}>
                            <span>×</span>
                        </button>
                        <Survey onClose={() => setShowSurveyPopup(false)} />
                    </div>
                </div>
            )}
        </div>
    )
}

export default Profile
