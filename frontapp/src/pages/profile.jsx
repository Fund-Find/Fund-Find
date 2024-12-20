import React, { useEffect, useState } from 'react'
import '../assets/css/profile.css'

const API_URL = 'http://localhost:8080/api/v1/user/profile'
const UPDATE_API_URL = 'http://localhost:8080/api/v1/user/profile'
const UPDATE_EMAIL_URL = 'http://localhost:8080/api/v1/user/profile/email'

const Profile = () => {
    const [user, setUser] = useState(null)
    const [previewImage, setPreviewImage] = useState(null) // 이미지 미리보기 상태
    const [originalUser, setOriginalUser] = useState(null) // 원본 사용자 정보
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)
    const [isEditing, setIsEditing] = useState(false)

    useEffect(() => {
        fetch(API_URL, {
            method: 'GET',
            credentials: 'include',
        })
            .then((response) => response.json())
            .then((data) => {
                if (data.resultCode === '200') {
                    setUser(data.data)
                    setOriginalUser(data.data) // 원본 상태 저장
                    setPreviewImage(data.data.thumbnailImg) // 기존 이미지 미리보기 설정
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
                    setOriginalUser(data.data) // 저장된 상태를 원본으로 업데이트
                    setPreviewImage(data.data.thumbnailImg) // 저장된 이미지 미리보기로 업데이트
                    setIsEditing(false)
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
        setUser(originalUser) // 원본 상태로 되돌림
        setPreviewImage(originalUser.thumbnailImg) // 원본 이미지로 복구
    }

    const handleEmailUpdate = () => {
        const newEmail = prompt('수정할 이메일을 입력하세요:', user.email)
        if (newEmail) {
            fetch(UPDATE_EMAIL_URL, {
                method: 'PATCH',
                credentials: 'include',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams({ newEmail }),
            })
                .then((response) => response.json())
                .then((data) => {
                    if (data.resultCode === '200') {
                        alert('이메일이 성공적으로 수정되었습니다.')
                        setUser((prev) => ({ ...prev, email: newEmail }))
                    } else {
                        throw new Error(data.msg || '이메일 수정 실패')
                    }
                })
                .catch(() => {
                    alert('이메일 수정에 실패했습니다.')
                })
        }
    }

    if (loading) {
        return <div>로딩 중...</div>
    }

    if (error) {
        return <div>{error}</div>
    }

    return (
        <div className="profile-container">
            <h2>프로필</h2>
            <div className="profile-info">
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
                        <input
                            type="text"
                            value={user.nickname}
                            onChange={(e) => setUser({ ...user, nickname: e.target.value })}
                        />
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
                    <button onClick={handleEmailUpdate}>이메일 수정</button>
                </div>
                <div>
                    <strong>소개:</strong>
                    {isEditing ? (
                        <textarea value={user.intro} onChange={(e) => setUser({ ...user, intro: e.target.value })} />
                    ) : (
                        user.intro
                    )}
                </div>
                <div>
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
            </div>
            {isEditing ? (
                <>
                    <button onClick={handleSave}>저장</button>
                    <button onClick={handleCancel}>취소</button>
                </>
            ) : (
                <button onClick={handleEdit}>수정</button>
            )}
        </div>
    )
}

export default Profile
