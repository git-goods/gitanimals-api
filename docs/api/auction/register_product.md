# Register product

shop에 product를 등록합니다.

## Request

### HTTP METHOD : `POST`

### url : `https://api.gitanimals.org/auctions/products`

### RequestHeader

- Authorization: `{token}`

### Request body

```json
{
  "personaId": "1",
  "price": "1000"
}
```

| key       | description                                 |
|-----------|---------------------------------------------|
| personaId | 등록할 펫의 id 입니다.                              |
| price     | 등록할 펫의 가격입니다. Long 범위(2^64) 안에서 설정할 수 있습니다. |

# Response

200 OK

```json
{
  "id": "1",
  "sellerId": "1",
  "persona": {
    "personaId": "1",
    "personaType": "PENGUIN",
    "personaLevel": 1
  },
  "price": "1000",
  "paymentState": "SOLD_OUT",
  "receipt": {
    "buyerId": "1",
    "soldAt": "2022-04-29T10:15:30Z"
  }
}
```

| key                 | description                                            |
|---------------------|--------------------------------------------------------|
| id                  | 등록된 상품의 id                                             |
| sellerId            | 상품을 판매하는 유저의 id                                        |
| persona.personaId   | persona의 id                                            |
| persona.personaType | persona의 type                                          |
| persona.personaLevel | persona의 level                                         |
| price               | 등록된 상품의 가격                                             |
| paymentState        | 등록된 상품의 상태                                             |
| receipt             | 결제정보. 등록된 상품이 SOLD_OUT 상태일때만 존재하며, 아니라면, null값이 반환됩니다. |
