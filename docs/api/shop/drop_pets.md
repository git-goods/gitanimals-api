## Drop pets

나의 펫을 상점에 판매합니다.

## Request

### HTTP METHOD : `POST`

### url : `https://api.gitanimals.org/shops/drop/{persona-id}`

### RequestHeader

- Authorization: `{token}`

### PathVariable

- {persona-id}: 펫의 아이디

## Response

200 OK

```json
{
  "id": "1",
  "personaId": "2",
  // 판매한 펫의 아이디
  "droppedUserId": "3",
  // 펫을 판매한 유저의 아이디
  "givenPoint": 300
  // 유저에게 지급한 포인트
}
```
