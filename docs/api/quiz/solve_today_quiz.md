# Solve today quiz

오늘의 퀴즈를 풉니다.   
퀴즈 풀기는 아래와 같은 단계로 진행됩니다.   
   
1. 퀴즈 Context 생성하기 -> contextId를 발급합니다.
2. contextId로 퀴즈를 조회합니다.
3. contextId로 현재 조회한 퀴즈의 정답을 응답합니다.
4. contextId로 현재 조회한 퀴즈의 결과를 조회합니다.
5. 정답을 맞추면 go or stop을 할 수 있습니다.
6. 퀴즈 Context가 종료되면, 결과에 따라 포인트가 지급됩니다.


# 1. Create quiz context
## Request
### HTTP method: `POST`
### URL: `https://api.gitanimals.org/quizs/context`
### Request header
- Authorization: `{token}`
- Locale: `{language}` // 해당 하는 언어의 퀴즈를 응답합니다. (없다면 EN_US default, value는 대소문자 상관없음)

### Request body
```json
{
  "category": "FRONTEND" // FRONTEND OR BACKEND
}
```

## Response
```json
{
  "contextId": "12345"
}
```

# 2. Get quiz by contextId
## Request
### HTTP method: `GET`
### URL: `https://api.gitanimals.org/quizs/context/{contextId}`
### Request header
- Authorization: `{token}`
- Locale: `{language}`

## Response

```json
{
  "round": {
    "total": 5,
    "current": 1,
    "timeoutAt": "2024-10-14 12:34:56" // 서버에서 계산해서 응답 타임아웃안에 풀지 못하면 틀림 처리됨
  },
  "level": "EASY", // EASY, MEDIUM, DIFFICULT
  "category": "FRONTEND", // FRONTEND, BACKEND
  "problem": "linux는 리눅스 토발즈가 개발했다.",
  "prize": 0, // 현재 라운드 기준 유저가 획득한 포인트를 응답한다.
  "status": SOLVING // NOT_STARTED, SOLVING, SUCCESS, FAIL, DONE
}
```

응답의 status 대한 설명   
**클라이언트는 SOLVING, FAIL, DONE 상태중 하나만 보게될 것 이다.**

- `NOT_STARTED`: 생성만 하고 시작하지 않은 상태이다. 
- `SOLVING`: context 조회와 동시에 timeout이 시작되며 SOLVING 상태가 된다. 
           클라이언트가 GET요청을 하는순간 SOLVING상태가 되며 timeout이 시작되므로, 클라이언트에게 응답될일은 없다.   
- `SUCCESS`: 현재 라운드를 맞췄을 경우 SUCCESS 상태가 된다.
- `FAIL`: 현재 라운드를 실패했을 경우 FAIL 상태가 된다.
- `DONE`: 퀴즈 Context가 성공적으로 끝난경우 DONE 상태가 된다. (ex. stop을 하거나 모든 문제를 맞춘경우)

# 3. Answer quiz by contextId
## Request
### HTTP method: `POST`
### URL: `https://api.gitanimals.org/quizs/context/{contextId}/answers`
### Request header
- Authorization: `{token}`
- Locale: `{language}`
### Request body
```json
{
  "answer": "YES" // YES, NO
}
```

## Response
200 OK 

# 4. Get round result by contextId
## Request
### HTTP method: `GET`
### URL: `https://api.gitanimals.org/quizs/context/{contextId}/results`
### Request header
- Authorization: `{token}`
- Locale: `{language}`

## Response
```json
{
  "result": "FAIL" // SUCCESS, FAIL, NOT_STARTED (NOT_STARTED같은 경우 아직 풀지 않았다면 응답한다.)
}
```

# 5. Stop Context by contextId
## Request
### HTTP method: `POST`
### URL: `https://api.gitanimals.org/quizs/context/{contextId}/stops`
### Request header
- Authorization: `{token}`
- Locale: `{language}`
