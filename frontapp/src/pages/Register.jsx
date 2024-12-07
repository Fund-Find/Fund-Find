import React, { useState } from 'react'
import '../assets/css/register.css'

function Register() {
    const [username, setUsername] = useState('')
    const [nickname, setNickname] = useState('')
    const [password1, setPassword1] = useState('')
    const [password2, setPassword2] = useState('')
    const [intro, setIntro] = useState('')
    const [email, setEmail] = useState('')
    const [thumbnailImg, setThumbnailImg] = useState(null)
    const [agreement, setAgreement] = useState(false)
    const [errorMessage, setErrorMessage] = useState('')
    const [loading, setLoading] = useState(false)

    const handleSubmit = async (e) => {
        e.preventDefault()

        // 약관 동의 여부 확인
        if (!agreement) {
            alert('이용약관에 동의해주세요.')
            return
        }

        const formData = new FormData()
        formData.append('username', username)
        formData.append('nickname', nickname)
        formData.append('password1', password1)
        formData.append('password2', password2)
        formData.append('intro', intro)
        formData.append('email', email)
        formData.append('thumbnailImg', thumbnailImg) // 파일 처리

        // formData.append(
        //     'userRequest',
        //     JSON.stringify({
        //         username,
        //         nickname,
        //         password1,
        //         password2,
        //         intro,
        //         email,
        //     }),
        // )

        setLoading(true) // 로딩 시작

        try {
            const response = await fetch('http://localhost:8080/api/v1/user/register', {
                method: 'POST',
                /*
                headers: {
                    // 'Content-Type': 'multipart/form-data', // 파일 전송을 위한 설정
                    Authorization: 'Bearer ' + yourToken, // JWT 토큰이 필요한 경우
                }, */
                body: formData,
                mode: 'cors', // CORS를 명시적으로 설정
            })
            const data = await response.json()

            if (response.ok) {
                alert(data.msg) // 서버에서 반환한 메시지 출력
                window.location.href = '/login' // 예시: 로그인 페이지로 이동
            } else {
                setErrorMessage(data.msg || '회원가입에 실패했습니다.')
            }
        } catch (error) {
            console.log(error)
            setErrorMessage('회원가입에 실패했습니다.')
        } finally {
            setLoading(false) // 로딩 완료
        }
    }

    return (
        <div id="body">
            <div id="allbox">
                <div className="membershipBox">
                    <h1>회원가입</h1>
                    <form onSubmit={handleSubmit} id="signupForm" encType="multipart/form-data">
                        {errorMessage && <div className="alert alert-danger">{errorMessage}</div>}
                        <div className="textBox mb-4">
                            <p>
                                본인은 귀사에 이력서를 제출함에 따라 [개인정보 보호법] 제15조 및 제17조에 따라 아래의
                                내용으로 개인정보를 수집, 이용 및 제공하는데 동의합니다.
                                <br />
                                <br />
                                □ 개인정보의 수집 및 이용에 관한 사항 -<br />
                                <br />
                                수집하는 개인정보 항목 (이력서 양식 내용 일체) : 성명, 주민등록번호, 전화번호, 주소,
                                이메일, 가족관계, 학력사항, 경력사항, 자격사항 등과 그 외 이력서 기재 내용 일체
                                <br />
                                <br />- 개인정보의 이용 목적 : 수집된 개인정보를 사업장 신규 채용 서류 심사 및
                                인사서류로 활용하며, 목적 외의 용도로는 사용하지 않습니다.
                            </p>

                            <div className="form-check">
                                <input
                                    className="form-check-input"
                                    type="checkbox"
                                    id="agreement"
                                    checked={agreement}
                                    onChange={(e) => setAgreement(e.target.checked)}
                                />
                                <label className="form-check-label" htmlFor="agreement">
                                    동의
                                </label>
                                <div className="invalid-feedback" id="agreement-error"></div>
                            </div>
                        </div>

                        <div className="mb-3">
                            <label htmlFor="username" className="form-label">
                                아이디
                            </label>
                            <input
                                type="text"
                                id="username"
                                className="form-control"
                                placeholder="아이디를 입력하세요"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                required
                            />
                        </div>
                        <div className="mb-3">
                            <label htmlFor="nickname" className="form-label">
                                닉네임
                            </label>
                            <input
                                type="text"
                                id="nickname"
                                className="form-control"
                                placeholder="닉네임을 입력하세요"
                                value={nickname}
                                onChange={(e) => setNickname(e.target.value)}
                                required
                            />
                        </div>
                        <div className="mb-3">
                            <label htmlFor="password1" className="form-label">
                                비밀번호
                            </label>
                            <input
                                type="password"
                                id="password1"
                                className="form-control"
                                placeholder="비밀번호를 입력하세요"
                                value={password1}
                                onChange={(e) => setPassword1(e.target.value)}
                                required
                            />
                        </div>
                        <div className="mb-3">
                            <label htmlFor="password2" className="form-label">
                                비밀번호 확인
                            </label>
                            <input
                                type="password"
                                id="password2"
                                className="form-control"
                                placeholder="비밀번호를 확인하세요"
                                value={password2}
                                onChange={(e) => setPassword2(e.target.value)}
                                required
                            />
                        </div>

                        <div className="mb-3 file-input-container">
                            <label htmlFor="thumbnailImg" className="form-label">
                                프로필 이미지
                            </label>
                            <input
                                type="file"
                                id="thumbnailImg"
                                className="form-control"
                                accept="image/*"
                                onChange={(e) => setThumbnailImg(e.target.files[0])}
                            />
                        </div>
                        <div className="mb-3">
                            <label htmlFor="intro" className="form-label">
                                자기소개
                            </label>
                            <input
                                type="text"
                                id="intro"
                                className="form-control"
                                placeholder="간단하게 자신을 알려주세요"
                                value={intro}
                                onChange={(e) => setIntro(e.target.value)}
                                required
                            />
                        </div>
                        <div className="mb-3">
                            <label htmlFor="email" className="form-label">
                                이메일
                            </label>
                            <input
                                type="email"
                                id="email"
                                className="form-control"
                                placeholder="이메일을 입력하세요"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                required
                            />
                        </div>

                        <button type="submit" className="btn btn-primary" disabled={loading}>
                            {loading ? (
                                <span>
                                    <span
                                        className="spinner-border spinner-border-sm"
                                        role="status"
                                        aria-hidden="true"
                                    ></span>
                                    처리중...
                                </span>
                            ) : (
                                '회원가입'
                            )}
                        </button>
                    </form>
                </div>
            </div>
        </div>
    )
}

export default Register
