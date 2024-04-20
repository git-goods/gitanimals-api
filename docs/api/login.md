# Github Oauth 2 login

## Request
### HTTP METHOD : `GET` 
### url : `https://api.gitanimals.org/logins/oauth/github`
### RequestHeader
- Response-url: jwt 토큰을 받을 url   
    example : `Response-url: "https://www.naver.com"`

# Response
로그인 성공시 요청에 입력한 response로 jwt를 전달합니다.   
예시로, response-url에 `https://www.naver.com` 을 입력했을시, 인증이 끝난후 다음과 같이 브라우저가 redirect 됩니다.

```http
https://www.naver.com?jwt={jwtToken}
```
