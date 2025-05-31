## Get render user info

렌더 유저 정보를 조회합니다.

## Request
### HTTP METHOD : `GET`
### url : `https://render.gitanimals.org/users`

### RequestHeader
- Authorization: `{token}`

## Response

200 OK

```json
{
  "id": "1",
  "username": "devxb", // 사용자 이름
  "contribution": 12345,
  "personaCount": 12345,
  "visitor": 30, // 방문자 수
  "contributionsForNextPersona": { // 다음펫을 얻기위한 contributions양
    "current": 15, // 현재 값
    "max": 30 // 최댓 값
  }
}
```
