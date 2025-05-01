FROM arm64v8/openjdk:21

ARG DB_URL
ARG DB_USERNAME
ARG DB_PASSWORD
ARG GITHUB_TOKEN
ARG REDIS_HOST
ARG REDIS_PORT
ARG GH_OAUTH_ID
ARG GH_OAUTH_SECRET
ARG INTERNAL_SECRET
ARG SLACK_TOKEN
ARG JWT_KEY
ARG TEST_SECRET
ARG OPENAI_KEY
ARG OPENAI_PROJECT
ARG OPENAI_ORGANIZATION
ARG TOKENIZER_API_KEY
ARG ELASTIC_SEARCH_URIS
ARG ELASTIC_SEARCH_USER_NAME
ARG ELASTIC_SEARCH_PASSWORD
ARG QUIZ_APPROVE_TOKEN
ARG RELAY_APPROVE_TOKEN
ARG INTERNAL_AUTH_SECRET
ARG LOGIN_SECRET

ARG JAR_FILE=./*.jar
COPY ${JAR_FILE} gitanimals-api.jar

ENV db_url=${DB_URL} \
  db_username=${DB_USERNAME} \
  db_password=${DB_PASSWORD} \
  github_token=${GITHUB_TOKEN} \
  redis_host=${REDIS_HOST} \
  redis_port=${REDIS_PORT} \
  oauth_client_id_github=${GH_OAUTH_ID} \
  oauth_client_secret_github=${GH_OAUTH_SECRET} \
  internal_secret=${INTERNAL_SECRET} \
  slack_token=${SLACK_TOKEN} \
  jwt_key=${JWT_KEY} \
  test_secret=${TEST_SECRET} \
  openai_key=${OPENAI_KEY} \
  openai_project=${OPENAI_PROJECT} \
  openai_organization=${OPENAI_ORGANIZATION} \
  tokenizer_api_key=${TOKENIZER_API_KEY} \
  elastic_search_uris=${ELASTIC_SEARCH_URIS} \
  elastic_search_user_name=${ELASTIC_SEARCH_USER_NAME} \
  elastic_search_password=${ELASTIC_SEARCH_PASSWORD} \
  quiz_approve_token=${QUIZ_APPROVE_TOKEN} \
  relay_approve_token=${RELAY_APPROVE_TOKEN} \
  internal_auth_secret=${INTERNAL_AUTH_SECRET} \
  login_secret=${LOGIN_SECRET}

ENTRYPOINT java -Djava.net.preferIPv4Stack=true -jar gitanimals-api.jar \
  --spring.datasource.url=${db_url} \
  --spring.datasource.username=${db_username} \
  --spring.datasource.password=${db_password} \
  --netx.host=${redis_host} \
  --netx.port=${redis_port} \
  --github.token=${github_token} \
  --oauth.client.id.github=${oauth_client_id_github} \
  --oauth.client.secret.github=${oauth_client_secret_github} \
  --internal.secret=${internal_secret} \
  --slack.token=${slack_token} \
  --jwt.key=${jwt_key} \
  --test.secret=${test_secret} \
  --openai.key=${openai_key} \
  --openai.project=${openai_project} \
  --openai.organization-id=${openai_organization} \
  --tokenizer.api.key=${tokenizer_api_key} \
  --spring.elasticsearch.uris=${elastic_search_uris} \
  --spring.elasticsearch.username=${elastic_search_user_name} \
  --spring.elasticsearch.password=${elastic_search_password} \
  --quiz.approve.token=${quiz_approve_token} \
  --relay.approve.token=${relay_approve_token} \
  --internal.auth.secret=${internal_auth_secret} \
  --login.secret=${login_secret}
