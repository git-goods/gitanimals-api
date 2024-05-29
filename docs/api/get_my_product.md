## Get my products

토큰에 해당하는 유저가 올린 상품들을 조회합니다.

## Request

### HTTP METHOD : `GET`

### url : `https://api.gitanimals.org/auctions/products/users`

### RequestHeader

- Authorization: `{token}`

### Request param

- `page-number` : page-number에 해당하는 page를 조회합니다. (첫번째 page-number는 0 이며, 아무것도 입력하지 않을시, 0으로 초기화 됩니다.)   
- `count` : product를 count개 조회합니다. 입력하지 않을 경우, 8개를 조회합니다.

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
  ],
  "pagination": {
    "totalRecords": 100,
    "currentPage": 0,
    "totalPages": 10,
    "nextPage": 1,
    "prevPage": null
  }
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
| pagination.totalRecords          | 상품의 총 수량                                               |
| pagination.currentPage           | 현재 페이지                                                 |
| pagination.totalPages            | 총 페이지                                                  |
| pagination.nextPage              | 다음 페이지 번호 (없으면 null)                                   |
| pagination.prevPage              | 이전 페이지 번호 (없으면 null)                                   |

