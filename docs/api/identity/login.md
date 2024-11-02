# Github Oauth 2 login

## Request
### HTTP METHOD : `GET` 
### url : `https://api.gitanimals.org/logins/oauth/github`
### Header
- Redirect-When-Success : `HOME`, `ADMIN`, `LOCAL`, `LOCAL_ADMIN` 중 하나를 입력해주세요. HOME은 로그인 성공시 홈페이지로 로그인, ADMIN은 로그인 성공시 어드민 페이지로 리다이렉트 됩니다. LOCAL은 `http://localhost:3000?jwt={jwt}` 로 리다이렉트 됩니다. 

# Response
로그인 성공시 등록한 url로 jwt를 전달합니다.   
ADMIN : `https://admin.gitanimals.org?jwt={jwtToken}`   
HOME : `https://www.gitanimals.org?jwt={jwtToken}`
LOCAL : `http://localhost:3000?jwt={jwtToken}`
LOCAL_ADMIN : `http://localhost:5173?jwt={jwtToken}`


## Sequence

1. `GET https://api.gitanimals.org/logins/oauth/github` 로 요청.

2. 깃허브 로그인 페이지로 리다이렉트.

3. 로그인 성공하면, 등록한 url 로 임시토큰 전달.

4. 임시토큰을 서버의 `GET https://api.gitanimals.org/logins/oauth/github/tokens?code={3번에서 받은 임시토큰}` 전달.

5. 서버에 등록한 url로 임시토큰 응답.
