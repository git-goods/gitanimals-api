## Get all pets

내가 갖고있는 모든 펫들을 조회합니다.

## Request
### HTTP METHOD : `GET`
### url : `https://render.gitanimals.org/users/{username}`
### PathVariable
- {username}: 사용자의 이름

## Response

```json
{
  "id": "1",
  "name": "devxb",
  "personas": [
    {
      "id": "1",
      "type": "LITTLE_CHICK_SUNGLASSES",
      "level": "8",
      "visible": true,
      "dropRate": "10%"
    },
    {
      "id": "2",
      "type": "LITTLE_CHICK_SUNGLASSES",
      "level": "7",
      "visible": true,
      "dropRate": "0.7%"
    },
    {
      "id": "3",
      "type": "LITTLE_CHICK_SUNGLASSES",
      "level": "11",
      "visible": true,
      "dropRate": "33%"
    },
    {
      "id": "4",
      "type": "LITTLE_CHICK",
      "level": "11",
      "visible": true,
      "dropRate": "24%"
    },
    {
      "id": "5",
      "type": "GOBLIN_BAG",
      "level": "15",
      "visible": true,
      "dropRate": "10%"
    },
    {
      "id": "6",
      "type": "LITTLE_CHICK_SUNGLASSES",
      "level": "14",
      "visible": true,
      "dropRate": "10%"
    },
    {
      "id": "7",
      "type": "GOOSE",
      "level": "11",
      "visible": true,
      "dropRate": "10%"
    },
    {
      "id": "8",
      "type": "FISH_MAN",
      "level": "14",
      "visible": true,
      "dropRate": "10%"
    },
    {
      "id": "9",
      "type": "GOBLIN",
      "level": "16",
      "visible": true,
      "dropRate": "10%"
    },
    {
      "id": "10",
      "type": "LITTLE_CHICK_SUNGLASSES",
      "level": "20",
      "visible": true,
      "dropRate": "10%"
    },
    {
      "id": "11",
      "type": "LITTLE_CHICK",
      "level": "13",
      "visible": true,
      "dropRate": "10%"
    },
    {
      "id": "12",
      "type": "LITTLE_CHICK",
      "level": "12",
      "visible": true,
      "dropRate": "10%"
    }
  ]
}
```
