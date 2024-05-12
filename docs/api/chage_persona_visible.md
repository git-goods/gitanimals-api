# Change_persona_visible

토큰에 해당하는 유저가 personaId에 해당하는 persona를 갖고있다면 해당 persona의 visible을 변경합니다.   
만약, 갖고있지 않거나, 이미 visible 상태인 persona의 수가 30개 이상이라면, 예외를 던집니다.

## Request
### HTTP METHOD : `PATCH`
### url : `https://render.gitanimals.org/personas`
### RequestHeader
- Authorization: `{token}`
### RequestBody
``` json
{
  "personaId": "123",
  "visible": true
}
```

## Response

200 OK

```json
{
  "id": "5",
  "type": "GOBLIN_BAG",
  "level": "15",
  "visible": true
}
```
