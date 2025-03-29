# Get rank

전체 랭킹을 조회합니다.

## Request
### Http method: `GET`
### URL: `https://render.gitanimals.org/ranks`
### Parameter
- rank: 0 //특정 랭크를 입력하면 해당 랭크부터 {size} 수 만큼 응답합니다.
- size: 10 // 조회할 랭크의 수 입니다. default: 10 
- type: WEEKLY_GUILD_CONTRIBUTIONS, WEEKLY_USER_CONTRIBUTIONS // 조회할 랭크의 타입을 입력합니다.

## Response

```json
{
  "ranks": [
    {
      "id": "123", // user면 user의 id guild면 guild의 id
      "rank": 1,
      "image": "https://static.gitanimals.org/...",
      "name": "dog",
      "contributions": 3,
    },
    {
      "id": "124", 
      "rank": 2,
      "image": "https://static.gitanimals.org/...",
      "name": "cat",
      "contributions": 2
    },
    ...
  ]
}
```
