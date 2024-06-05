# Gotcha

pet을 뽑기 할 수 있습니다.

> [!IMPORTANT]   
> 기존 단일 뽑기 API는 RequestHeader에 아무값도 넣지 마세요.   
> count가 추가된 뽑기는 RequestHeader에 ApiVersion=2 를 입력해야합니다.

## Request

### HTTP METHOD : `POST`

### url : `https://api.gitanimals.org/gotchas?type={gotcha-type}?count={count}`

### RequestHeader

- Authorization: `{token}`
- ApiVersion: 2 // 아무값도 넣지 않을시 버전 1 api로 매핑됩니다.

### Request param

- gotcha-type: gotcha에 사용할 뽑기 기계의 type입니다. 입력 가능한 type은 아래와 같습니다.   
  입력 가능한 type : `DEFAULT`
- count: 뽑을 횟수 입니다. 아무것도 입력하지 않을시, 1로 매핑됩니다.

# Response

성공시, 응답으로 뽑힌 펫의 id, 이름, 비율(뽑히는 확률)을 반환합니다.

``` json
{
  "gotchaResults": [
    {
      "id" : "1",
      "name" : "CAT",
      "ratio" : "0.2"
    },
    {
      "id" : "2",
      "name" : "LITTLE_CHICK",
      "ratio" : "0.9"
    }
  ]
}
```

펫의 id는 `https://render.gitanimals.org/lines/{username}?pet-id={id}` 와 같이 요청하면서, pet의 정보를 가져올 수 있습니다.
