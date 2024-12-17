# Get all joined guilds

토큰에 해당하는 유저가 참여중인 모든 길드를 조회합니다.

## Request
### HTTP METHOD : `GET`
### url : `https://render.gitanimals.org/guilds`
### Http Headers
- Authorization: 토큰을 전달합니다.

## Response

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
        "contributions": "12345"// 리더의 컨트리뷰션
      },
      "farmType": "길드 팜의 종류",
      "totalContributions": "99999999", // 길드 모든 멤버와 리더의 contributions 총합
      "members": [
        {
          "id": "2", // 멤버의 고유 아이디
          "userId": "3", // 유저의 아이디
          "name": "유저의 이름",
          "contributions": "12345", // 각 멤버의 contributions 
          "personaId": "4" // 길드에 보여질 대표펫의 아이디
        },
        ...
      ],
      "joinWaitList": ["5", "6", "7"], // 길드에 가입대기중인 유저들의 id
      "createdAt": "2022-04-29T10:15:30Z"
    },
    ...
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