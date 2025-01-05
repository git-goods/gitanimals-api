# Create guild

길드를 생성합니다.
길드의 title은 중복될 수 없습니다.

## Request
### HTTP METHOD : `POST`
### url : `https://render.gitanimals.org/guilds`
### Request Header
- Authorization: 생성 요청자의 토큰 전달 이 토큰으로 leader가 결정됨

### Request body

```json
{
  "title": "길드의 이름",
  "body": "길드의 본문",
  "guildIcon": "guild의 아이콘",
  "autoJoin": true, // 자동가입 여부
  "farmType": "FARM_TYPE", // 그리는 팜의 타입
  "personaId": "1234567890" // 대표의 보여질 펫 아이디 
}
```

## Response 

200 OK
