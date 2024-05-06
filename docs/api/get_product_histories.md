## Get products histories

상품의 판매 내역을 조회합니다.

## Request

### HTTP METHOD : `GET`

### url : `https://api.gitanimals.org/auctions/products/histories`

### Request param

- `last-id` : last-id를 입력하면, lastId이후의 products들을 조회합니다. (lastId에 해당하는 products는 포함하지 않습니다.) 만약,
  입력하지 않을 경우, 가장 처음 products부터 count개 반환합니다.
- `persona-type` : persona-type에 해당하는 products들을 반환합니다. 입력 가능한
  persona-type들은 [확률표](https://github.com/devxb/gitanimals#%EB%93%B1%EC%9E%A5-%EA%B0%80%EB%8A%A5%ED%95%9C-%ED%8E%AB%EB%93%A4)
  의 name과 일치합니다. 어떠한, 값도 입력하지 않을경우, 모든 persona-type에 해당하는 product 들을 조회합니다.
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
      "paymentState": "SOLD_OUT",
      "receipt": {
        "buyerId": "12345677123123123123",
        "soldAt": "2024-05-06T12:30:45Z"
      }
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
      "paymentState": "SOLD_OUT",
      "receipt": {
        "buyerId": "12345677123123123123",
        "soldAt": "2024-05-06T12:30:45Z"
      }
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
      "paymentState": "SOLD_OUT",
      "receipt": {
        "buyerId": "12345677123123123123",
        "soldAt": "2024-05-06T12:30:45Z"
      }
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
| products.[].receipt.buyerId      | 구매자의 id                                    |
| products.[].receipt.soldAt       | 판매된 날짜                                                 |

