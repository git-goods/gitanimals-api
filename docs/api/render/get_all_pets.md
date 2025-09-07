## Get all pets

모든 펫들의 정보를 조회합니다.

## Request
### HTTP METHOD : `GET`
### url : `https://render.gitanimals.org/personas/infos`

## Response

- grade >> 
  - DEFAULT: 기본 (뽑기 혹은 커밋으로 획득 가능)
  - EVOLUTION: 진화 (진화로만 획득가능)

```json
{
  "personas": [
    {
      "type": "LITTLE_CHICK_SUNGLASSES",
      "dropRate": "10%",
      "grade": "DEFAULT" // DEFAULT, EVOLUTION, MANAGER
    },
    {
      "type": "LITTLE_CHICK_SUNGLASSES",
      "dropRate": "0.7%",
      "grade": "DEFAULT"
    },
    {
      "type": "LITTLE_CHICK_SUNGLASSES",
      "dropRate": "33%",
      "grade": "DEFAULT"
    },
    {
      "type": "LITTLE_CHICK",
      "dropRate": "24%",
      "grade": "DEFAULT"
    },
    {
      "type": "GOBLIN_BAG",
      "dropRate": "10%",
      "grade": "DEFAULT"
    },
    {
      "type": "LITTLE_CHICK_SUNGLASSES",
      "dropRate": "10%",
      "grade": "DEFAULT"
    },
    {
      "type": "GOOSE",
      "dropRate": "10%",
      "grade": "DEFAULT"
    },
    {
      "type": "FISH_MAN",
      "dropRate": "10%",
      "grade": "DEFAULT"
    },
    {
      "type": "GOBLIN",
      "dropRate": "10%",
      "grade": "DEFAULT"
    },
    {
      "type": "LITTLE_CHICK_SUNGLASSES",
      "dropRate": "10%",
      "grade": "DEFAULT"
    },
    {
      "type": "LITTLE_CHICK",
      "dropRate": "10%",
      "grade": "DEFAULT"
    },
    {
      "type": "LITTLE_CHICK",
      "dropRate": "10%",
      "grade": "DEFAULT"
    }
  ]
}
```
