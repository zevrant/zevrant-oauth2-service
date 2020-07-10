FROM zevrant/zevrant-ubuntu-base:latest

EXPOSE 9001

RUN mkdir -p /usr/local/microservices/zevrant-home-services/zevrant-oauth2-service/

RUN mkdir -p /var/log/zevrant-home-services/zevrant-oauth2-service\
  && mkdir -p /storage/keys

RUN useradd -m -d /usr/local/microservices/zevrant-home-services/zevrant-oauth2-service/ -G developers  zevrant-oauth2-service

RUN chown -R zevrant-oauth2-service:developers /var/log/zevrant-home-services/zevrant-oauth2-service /usr/local/microservices/zevrant-home-services/zevrant-oauth2-service /storage/keys

USER zevrant-oauth2-service

COPY build/libs/zevrant-oauth2-service-*.jar /usr/local/microservices/zevrant-home-services/zevrant-oauth2-service/zevrant-oauth2-service.jar

RUN mkdir ~/.aws; echo "[default]" > ~/.aws/config; echo "region = us-east-1" >> ~/.aws/config; echo "output = json" >> ~/.aws/config

CMD mkdir -p ~/.aws; echo "[default]" > ~/.aws/credentials\
 && echo "aws_access_key_id = $AWS_ACCESS_KEY_ID" >> ~/.aws/credentials\
 && echo "aws_secret_access_key = $AWS_SECRET_ACCESS_KEY" >> ~/.aws/credentials\
 && openssl req -newkey rsa:4096 -nodes -keyout ~/private.pem -x509 -days 365 -out ~/public.crt -subj "/C=US/ST=New York/L=Brooklyn/O=Example Brooklyn Company/CN=examplebrooklyn.com"\
 && password=`date +%s | sha256sum | base64 | head -c 32`\
 && openssl pkcs12 -export -inkey ~/private.pem -in ~/public.crt -passout "pass:$password" -out /usr/local/microservices/zevrant-home-services/zevrant-oauth2-service/zevrant-services.p12\
 && java -jar -Dspring.profiles.active=prod -Dpassword=$password /usr/local/microservices/zevrant-home-services/zevrant-oauth2-service/zevrant-oauth2-service.jar



