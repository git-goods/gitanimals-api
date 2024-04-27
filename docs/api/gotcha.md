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
성공시, 응답으로 뽑힌 펫의 이름과 비율을 반환합니다.

``` json
{
    "name" : "CAT",
    "ratio" : "0.2"
}
```
