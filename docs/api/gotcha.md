# Gotcha

pet을 뽑기 할 수 있습니다.

## Request
### HTTP METHOD : `POST`
### url : `https://api.gitanimals.org/gotchas?type={gotcha-type}`
### RequestHeader
- Authorization: `{token}`
### Request param 
- gotcha-type: gotcha에 사용할 뽑기 기계의 type입니다. 입력 가능한 type은 아래와 같습니다.   
    입력 가능한 type : `DEFAULT`

# Response
성공시, 응답으로 뽑힌 펫의 id, 이름, 비율(뽑히는 확률)을 반환합니다.

``` json
{
    "id" : "1",
    "name" : "CAT",
    "ratio" : "0.2"
}
```

펫의 id는 `https://render.gitanimals.org/lines/{username}?pet-id={id}` 와 같이 요청하면서, pet의 정보를 가져올 수 있습니다.
