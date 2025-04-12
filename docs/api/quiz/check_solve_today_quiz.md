# Check solve today quiz

오늘의 푼 퀴즈 정보를 조회합니다.

## Request
### Http method: `GET`
### URL: `https://api.gitanimals.org/quizs/context/today`
### Request header
- Authorization: `{token}`

## Response

오늘 풀었다면 아래와 같이 응답됩니다.

Response Status 200 

```json
{
  "isSolved": true,
  "contextId": "12345", 
  "prize": 12345, // 오늘 획득한 prize가 응답된다. 획득한 돈이 없다면 (ex. FAIL) 0
  "result": "FAIL" // FAIL, SOLVING, SUCCESS, FAIL, DONE 
}
```

오늘의 퀴즈를 아직 풀지 않았다면 아래와 같이 응답됩니다.

Response Status 200

```json
{
  "isSolved": false
}
```
