# Patch guild

길드 정보를 업데이트 합니다.   
(모든 필드는 null인 경우 무시됩니다.)

## Request
### Http Method: `PATCH`
### url: `https://render.gitanimals.org/guilds/{guildId}`
### Http Headers
- Authorization: 토큰을 전달합니다.

### Request body
```json
{
  "title": "변경할 길드의 제목",
  "body": "변경할 길드의 설명",
  "farmType": "변경할 길드의 배경",
  "guildIcon": "변경할 길드의 아이콘",
  "autoJoin": true // 자동가입 여부
}
```

## Response
200 OK

