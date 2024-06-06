## Get products

상품들을 조회합니다.

## Request

### HTTP METHOD : `GET`

### url : `https://api.gitanimals.org/auctions/products`

### Request param

- `page-number` : page-number에 해당하는 page를 조회합니다. (첫번째 page-number는 0 이며, 아무것도 입력하지 않을시, 0으로 초기화 됩니다.)      
- `persona-type` : persona-type에 해당하는 products들을 반환합니다. 입력 가능한
  persona-type들은 [확률표](https://github.com/devxb/gitanimals#%EB%93%B1%EC%9E%A5-%EA%B0%80%EB%8A%A5%ED%95%9C-%ED%8E%AB%EB%93%A4)
  의 name과 일치합니다. 어떠한, 값도 입력하지 않을경우, 모든 persona-type에 해당하는 product 들을 조회합니다.   
- `count` : product를 count개 조회합니다. 입력하지 않을 경우, 8개를 조회합니다.
- `order-type` : 정렬 타입을 입력합니다. (PRICE, CREATED_AT, LEVEL)
- `sort-by`: 내림차순, 오름차순을 입력합니다. 입력하지 않으면 내림차순 정렬이 됩니다. (ASC, DESC)

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

