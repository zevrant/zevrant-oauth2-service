FROM zevrant/zevrant-ubuntu-base:latest

EXPOSE 9001

RUN mkdir -p /usr/local/microservices/zevrant-home-services/zevrant-oauth2-service/

RUN mkdir -p /var/log/zevrant-home-services/zevrant-oauth2-service\
  && mkdir -p /storage/keys

RUN useradd -m -d /usr/local/microservices/zevrant-home-services/zevrant-oauth2-service/ -G developers  zevrant-oauth2-service

RUN chown -R zevrant-oauth2-service:developers /var/log/zevrant-home-services/zevrant-oauth2-service /usr/local/microservices/zevrant-home-services/zevrant-oauth2-service /storage/keys

USER zevrant-oauth2-service

COPY build/libs/zevrant-oauth2-service-*.jar /usr/local/microservices/zevrant-home-services/zevrant-oauth2-service/zevrant-oauth2-service.jar

CMD export ROLE_ARN="arn:aws:iam::725235728275:role/OauthServiceRole" \
 && echo $POD_IP
 && openssl req -new -key rsa:4096 -nodes -keyout ~/private.pem -x509 -days 365 -out ~/public.crt -subj "/C=US/ST=New York/L=Brooklyn/O=Example Brooklyn Company/CN=$POD_IP"\
 && password=`date +%s | sha256sum | base64 | head -c 32`\
 && openssl pkcs12 -export -inkey ~/private.pem -in ~/public.crt -passout "pass:$password" -out /usr/local/microservices/zevrant-home-services/zevrant-oauth2-service/zevrant-services.p12\
 && java -jar -Dspring.profiles.active=$ENVIRONMENT -Dpassword=$password /usr/local/microservices/zevrant-home-services/zevrant-oauth2-service/zevrant-oauth2-service.jar
