## Get my products

토큰에 해당하는 유저가 올린 상품들을 조회합니다.

## Request

### HTTP METHOD : `GET`

### url : `https://api.gitanimals.org/auctions/products/users`

### RequestHeader

- Authorization: `{token}`

### Request param

- `last-id` : last-id를 입력하면, lastId이후의 products들을 조회합니다. (lastId에 해당하는 products는 포함하지 않습니다.) 만약,
  입력하지 않을 경우, 가장 처음 products부터 count개 반환합니다.
- `count` : `last-id` 이후의 product를 count개 조회합니다. 입력하지 않을 경우, 8개를 조회합니다.

## Response

```json
{
  "products": [
    {
      "id": "1",
      "sellerId": "1",
      "persona": {
        "personaId": "1",
        "personaType": "PENGUIN",
        "personaLevel": 1
      },
      "price": "1000",
      "paymentState": "ON_SALE"
    },
    {
      "id": "2",
      "sellerId": "1",
      "persona": {
        "personaId": "1",
        "personaType": "CAT",
        "personaLevel": 1
      },
      "price": "1000",
      "paymentState": "ON_SALE"
    },
    {
      "id": "3",
      "sellerId": "1",
      "persona": {
        "personaId": "1",
        "personaType": "WHITE_CAT",
        "personaLevel": 1
      },
      "price": "1000",
      "paymentState": "ON_SALE"
    }
  ]
}
```

| key                              | description                                            |
|----------------------------------|--------------------------------------------------------|
| products.[].id                   | 등록된 상품의 id                                             |
| products.[].sellerId             | 상품을 판매하는 유저의 id                                        |
| products.[].persona.personaId    | persona의 id                                            |
| products.[].persona.personaType  | persona의 type                                          |
| products.[].persona.personaLevel | persona의 level                                         |
| products.[].price                | 등록된 상품의 가격                                             |
| products.[].paymentState         | 등록된 상품의 상태                                             |
| products.[].receipt              | 결제정보. 등록된 상품이 SOLD_OUT 상태일때만 존재하며, 아니라면, null값이 반환됩니다. |

