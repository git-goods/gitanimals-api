# APPLE login

## Request
### HTTP METHOD : `POST`
### url : `https://api.gitanimals.org/logins/oauth/apple`
### Header
- Login-Secret: 내부 로그인 토큰을 전달 하세요.

### Request Body
```json
{
  "name": "유저의 이름",
  "profileImage": "유저의 프로필 이미지"
}
```

# Response

```json
{
  "token": "bearer ..."
}
```
