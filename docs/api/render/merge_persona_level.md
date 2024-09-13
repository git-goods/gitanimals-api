# Merge persona level

펫의 레벨을 합칩니다.
재물이 된 펫은 영구적으로 삭제되며 (복구 불가능)
재물 펫이 갖고있는 레벨만큼 합성된 펫의 레벨이 증가합니다.
ex. A펫, B펫이 있고, B펫이 재물일때, B펫의 레벨만큼 A펫 레벨이 증가함.

## Request
### HTTP METHOD : `PUT`
### url : `https://render.gitanimals.org/personas/merges`
### RequestHeader
- Authorization: `{token}`
### RequestBody
``` json
{
    "increasePersonaId": "1",
    "decreasePersonaId": "2"
}
```

## Response

200 OK
