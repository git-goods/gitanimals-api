# Search guild

길드를 검색합니다.

## Request
### Http Method: `GET`
### url: `https://render.gitanimals.org/guilds/search/{text}`
### Request param
- `page-number`: page-number에 해당하는 page를 조회합니다. (첫번째 page-number는 0 이며, 아무것도 입력하지 않을시, 0으로 초기화 됩니다.)
- `filter`: filter 조건에 의해서 정렬합니다. 기본값은 random 입니다.
  - `RANDOM`: 랜덤하게 정렬합니다.
  - `PEOPLE_ASC`: 사람 오름차순
  - `PEOPLE_DESC`: 사람 내림차순
  - `CONTRIBUTION_ASC`: 기여 오름차순
  - `CONTRIBUTION_DESC`: 기여 내림차순
- `text`: 검색어 입니다.
- `key`: 랜덤의 변인이 되는 키 입니다. 하나의 유저는 페이지를 이탈하기 전까지 같은 key로 요청해야 중복된 상품을 보지 않습니다. key는 음수가 아닌 int 입니다.


### Response
200 OK

```json
{
  "guilds": [
    {
      "title": "제목",
      "body": "설명",
      "guildIcon": "길드의 아이콘",
      "leader": {
        "userId": "1", // 리더의 아이디
        "name": "리더의 이름",
        "contributions": "12345", // 리더의 컨트리뷰션
        "personaId": "12345"
      },
      "farmType": "길드 팜의 종류",
      "totalContributions": "99999999", // 길드 모든 멤버와 리더의 contributions 총합
      "members": [
        {
          "id": "2", // 멤버의 고유 아이디
          "userId": "3", // 유저의 아이디
          "name": "유저의 이름",
          "contributions": "12345", // 각 멤버의 contributions 
          "personaId": "4"// 길드에 보여질 대표펫의 아이디
        },
        ...
      ],
      "joinWaitList": ["5", "6", "7"], // 길드에 가입대기중인 유저들의 id
      "createdAt": "2022-04-29T10:15:30Z"
    },
  ],
  "pagination": {
    "totalRecords": 100,
    "currentPage": 0,
    "totalPages": 10,
    "nextPage": 1,
    "prevPage": null
  }
}
```
