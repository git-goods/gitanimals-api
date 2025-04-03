# Get rank

이전 회차 랭크 위너를 조회합니다.

## Request
### Http method: `GET`
### URL: `https://render.gitanimals.org/ranks/histories`
### Parameter
- rankType: WEEKLY_GUILD_CONTRIBUTIONS // 혹은 WEEKLY_USER_CONTRIBUTIONS

## Response

```json
{
  "winner": [
    {
      "id": "123", // user면 user의 id guild면 guild의 id
      "rank": 0,
      "prize": 12345,
      "name": "dog",
      "rankType": "WEEKLY_GUILD_CONTRIBUTIONS" // WEEKLY_USER_CONTRIBUTIONS
    },
    ...
  ]
}
```
