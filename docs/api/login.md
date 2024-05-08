# Github Oauth 2 login

## Request
### HTTP METHOD : `GET` 
### url : `https://api.gitanimals.org/logins/oauth/github`

# Response
로그인 성공시 등록한 url로 jwt를 전달합니다.   
예시로, `https://www.naver.com`가 등록되었다면, 인증이 끝난후 다음과 같이 브라우저가 redirect 됩니다.

```http
https://www.naver.com?jwt={jwtToken}
```

## Sequence

1. `GET https://api.gitanimals.org/logins/oauth/github` 로 요청.

2. 깃허브 로그인 페이지로 리다이렉트

3. 로그인 성공하면, 등록한 url 로 임시토큰 전달.

4. 임시토큰을 서버의 `GET https://api.gitanimals.org/logins/oauth/github/tokens?code={3번에서 받은 임시토큰}` 전달

5. 서버에 등록한 url로 임시토큰 응답.
