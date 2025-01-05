# Deny join guild

{guildId}와 {userId}를 입력받아 가입 요청을 거절합니다.

## Request
### Http Method: `POST`
### url: `https://render.gitanimals.org/guilds/{guildId}/deny`
### Request param
- `user-id`: 가입을 거절할 유저의 id
### Http Headers
- Authorization: 토큰을 전달합니다.

## Response
200 OK
