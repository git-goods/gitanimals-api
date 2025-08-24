# Create refresh token

리프레시 토큰을 생성합니다.

> [!IMPORTANT]   
> 새로운 리프레시 토큰을 발급시, 이전의 리프레시 토큰은 삭제됩니다.   
> 리프레시 토큰의 유효기간은 7일 입니다.

## Request
### HTTP METHOD : `POST`
### url : `https://api.gitanimals.org/users/refresh-tokens`
### request headers
- Authorization : "bearer ..." // 유저가 현재 로그인한 jwt
- Login-Secret: 내부 로그인 토큰을 전달 하세요.

## Response

```json
{
  "refreshToken": "..."
}
```
