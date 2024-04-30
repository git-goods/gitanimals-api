# Buy_product

productId에 해당하는 product를 삭제합니다.

## Request

### HTTP METHOD : `DELETE`

### url : `https://api.gitanimals.org/auctions/products/{product-id}`

### RequestHeader

- Authorization: `{token}`

### RequestBody

## Response

200 OK

```json
{
  "id": "1"
}
```

| key | description     |
|-----|-----------------|
| id  | 삭제된 product의 id |
