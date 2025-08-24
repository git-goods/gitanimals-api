# Create refresh token

리프레시 토큰을 이용해 로그인 합니다.

> [!IMPORTANT]   
> 리프레시 토큰의 유효기간은 7일 입니다.

## Request
### HTTP METHOD : `POST`
### url : `https://api.gitanimals.org/logins/refresh-tokens`
### request headers
- Login-Secret: 내부 로그인 토큰을 전달 하세요.

### request body
```json
{
  "refreshToken": "..."
}
```

## Response

```json
{
  "token": "bearer ..."
}
```
