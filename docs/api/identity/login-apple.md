# APPLE login

## Request
### HTTP METHOD : `POST`
### url : `https://api.gitanimals.org/logins/oauth/apple`
### Header
- Redirect-When-Success : `HOME`, `ADMIN`, `LOCAL`, `LOCAL_ADMIN` 중 하나를 입력해주세요. HOME은 로그인 성공시 홈페이지로 로그인, ADMIN은 로그인 성공시 어드민 페이지로 리다이렉트 됩니다. LOCAL은 `http://localhost:3000?jwt={jwt}` 로 리다이렉트 됩니다.
- Login-Secret: 내부 로그인 토큰을 전달 하세요.

### Request Body
```json
{
  "name": "유저의 이름",
  "profileImage": "유저의 프로필 이미지"
}
```

# Response
로그인 성공시 등록한 url로 jwt를 전달합니다.   
ADMIN : `https://admin.gitanimals.org?jwt={jwtToken}`   
HOME : `https://www.gitanimals.org?jwt={jwtToken}`
LOCAL : `http://localhost:3000?jwt={jwtToken}`
LOCAL_ADMIN : `http://localhost:5173?jwt={jwtToken}`
