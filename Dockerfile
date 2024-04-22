FROM arm64v8/openjdk:21

ARG DB_URL
ARG DB_USERNAME
ARG DB_PASSWORD
ARG GITHUB_TOKEN
ARG REDIS_HOST
ARG REDIS_PORT
ARG GH_OAUTH_ID
ARG GH_OAUTH_SECRET

ARG JAR_FILE=./build/libs/*.jar
COPY ${JAR_FILE} gitanimals-api.jar

ENV db_url=${DB_URL} \
  db_username=${DB_USERNAME} \
  db_password=${DB_PASSWORD} \
  github_token=${GITHUB_TOKEN} \
  redis_host=${REDIS_HOST} \
  redis_port=${REDIS_PORT} \
  oauth_client_id_github=${GH_OAUTH_ID} \
  oauth_client_secret_github=${GH_OAUTH_SECRET}

ENTRYPOINT java -jar gitanimals-api.jar \
  --spring.datasource.url=${db_url} \
  --spring.datasource.username=${db_username} \
  --spring.datasource.password=${db_password} \
  --netx.host=${redis_host} \
  --netx.port=${redis_port} \
  --github.token=${github_token} \
  --oauth.client.id.github=${oauth_client_id_github} \
  --oauth.client.secret.github=${oauth_client_secret_github}
