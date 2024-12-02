# Get all unread inbox

읽지 않은 inbox를 모두 조회합니다.

## Request
### HTTP METHOD : `GET` 
### url : `https://api.gitanimals.org/inboxes`
### RequestHeader
- Authorization: `{token}`

## Response
```json
inboxes = [
  {
    "id": "1234678128323414", 
    "image": "https://static.gitanimals.org/inbox/images/guild-join.png",
    "title": "길드 가입요청이 왔어요.", 
    "body": "devxb에게 git-goods 길드가입 요청이 왔어요.",
    "redirectTo": "/auctions/",
    "type": "INBOX" // (INBOX, NOTICE...)
    "status": "UNREAD", // READ or UNREAD
    "publishedAt": "2024-10-14 12:34:56"
  }
  ...
]
```
