# Join guild

guildId에 해당하는 길드에 가입합니다.

## Request
### HTTP METHOD : `POST`
### url : `https://render.gitanimals.org/guilds/{guildId}`
### Request Header
- Authorization: 토큰에 해당하는 유저가 가입됩니다.

### Request body
```json
{
  "personaId": "12345" // 길드에 보여질 대표펫의 id를 넘깁니다.
}
```

## Response

200 OK
