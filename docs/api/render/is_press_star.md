## Get all pets

스타를 누른사람인지 조회합니다.

## Request
### HTTP METHOD : `GET`
### url : `https://render.gitanimals.org/stargazers/{login}/press`
- PathVariable: {login} = 현재 로그인한 사람의 이름 == 깃허브 id 을 요청합니다.

## Response

스타를 눌렀으면 true, 아니라면 false를 응답합니다.

```json
{
  "isPressStar": true
}
```
