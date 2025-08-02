# APPLE login

## Request
### HTTP METHOD : `POST`
### url : `https://api.gitanimals.org/logins/oauth/apple`
### Header
- Login-Secret: 내부 로그인 토큰을 전달 하세요.

### Request Body
```json
{
  "accessToken": "..." // accessToken을 전달합니다. 
}
```

# Response

```json
{
  "token": "bearer ..."
}
```
