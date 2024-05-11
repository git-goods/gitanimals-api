# Get used coupons

userId에 해당하는 user가 사용한 coupon들을 조회합니다.

## Request

### HTTP METHOD : `GET`

### url : `https://api.gitanimals.org/coupons/users`

### RequestHeader

- Authorization: `{token}`

# Response

```json
{
  "id": "987654321",
  "userId": "user789",
  "code": "ABC123",
  "usedAt": "2024-05-06 15:45:30"
}
```

| key    | description   |
|--------|---------------|
| id     | 쿠폰의 id        |
| userId | 쿠폰을 사용한 유저의 id |
| code   | 쿠폰의 code      |
| usedAt | 쿠폰을 사용한 날짜    |


