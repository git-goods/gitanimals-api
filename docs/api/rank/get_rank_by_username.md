# Get rank by user name

특정 유저의 랭킹을 조회합니다.

## Request
### Http method: `GET`
### URL: `https://render.gitanimals.org/ranks/by-username/{username}`

## Response
```json
{
  "id": "123", // user면 user의 id guild면 guild의 id
  "rank": 100,
  "image": "https://static.gitanimals.org/...",
  "name": "dog",
  "contributions": 3,
}
```
