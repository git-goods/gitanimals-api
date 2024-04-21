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
