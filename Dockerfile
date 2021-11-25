FROM docker.io/zevrant/zevrant-ubuntu-base:latest

EXPOSE 9001

RUN mkdir -p /usr/local/microservices/zevrant-home-services/zevrant-oauth2-service/

RUN mkdir -p /var/log/zevrant-home-services/zevrant-oauth2-service\
  && mkdir -p /storage/keys

RUN useradd -m -d /usr/local/microservices/zevrant-home-services/zevrant-oauth2-service/ -G developers  zevrant-oauth2-service

RUN chown -R zevrant-oauth2-service:developers /var/log/zevrant-home-services/zevrant-oauth2-service /usr/local/microservices/zevrant-home-services/zevrant-oauth2-service /storage/keys

USER zevrant-oauth2-service

COPY build/libs/zevrant-oauth2-service-*.jar /usr/local/microservices/zevrant-home-services/zevrant-oauth2-service/zevrant-oauth2-service.jar

RUN mkdir ~/.aws; echo "[default]" > ~/.aws/config; echo "region = us-east-1" >> ~/.aws/config; echo "output = json" >> ~/.aws/config

RUN curl https://raw.githubusercontent.com/zevrant/zevrant-services-pipeline/master/bash/zevrant-services-start.sh > ~/startup.sh \
  && curl https://raw.githubusercontent.com/zevrant/zevrant-services-pipeline/master/bash/openssl.conf > ~/openssl.conf

CMD password=`date +%s | sha256sum | base64 | head -c 32` \
 && bash ~/startup.sh zevrant-oauth2-service $password \
 && java -jar -Dspring.profiles.active=$ENVIRONMENT -Dpassword=$password /usr/local/microservices/zevrant-home-services/zevrant-oauth2-service/zevrant-oauth2-service.jar
