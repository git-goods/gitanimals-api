# Get total user count

총 유저수를 조회합니다.

> [!IMPORTANT]   
> render와 identity서버 각각 조회해야합니다.   
> 이 문서는 render 서버의 사용자 수 조회 문서입니다. identity 서버 사용자 수 조회 문서는 `identity/` 를 참조헤주세요.
   
실시간 업데이트 되지 않으며, 최소 1시간에 한번 업데이트 됩니다.    

## Request
### HTTP METHOD : `GET`
### url : `https://render.gitanimals.org/users/statistics/total`

## Response

```json
{
  "userCount": "12345678910"
}
```
