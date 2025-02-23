# Create quiz

퀴즈를 생성합니다.

## Request
### Http method: `POST`
### URL: `https://api.gitanimals.org/quizs`
### Request header
- Authorization: `{token}`
- Location: `{location}` // 요청 위치를 같이 주세용 퀴즈 내용 국제화 할때 참고해서 사용 할 예정입니다.

### Request body

```json
{
  "level": "EASY", // EASY, MEDIUM, DIFFICULT
  "category": "FRONTEND", // FRONTEND, BACKEND
  "problem": "linux는 리눅스 토발즈가 개발했다.",
  "expectedAnswer": "YES" // "YES" "NO"
}
```

## Response

200 OK

```json
{
  "result": "FAIL", // SUCCESS, FAIL
  "point": 0, // 지급된 포인트
  "message": "아쉽지만 중복 문제가 등록되어 있어서 포인트 지급이 되지 않았어요."
}
```
