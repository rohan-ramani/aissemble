FROM docker.io/jenkins/jenkins:lts-jdk17

LABEL org.opencontainers.image.source="https://github.com/boozallen/aissemble"

RUN jenkins-plugin-cli --plugins git:5.7.0
RUN jenkins-plugin-cli --plugins github:1.42.0
RUN jenkins-plugin-cli --plugins templating-engine:2.5.3
RUN jenkins-plugin-cli --plugins adoptopenjdk:1.7
RUN jenkins-plugin-cli --plugins pipeline-maven:1508.v347c4b_692202
RUN jenkins-plugin-cli --plugins slack:761.v2a_8770f0d169

COPY ./src/main/resources/jenkins/ "$REF"

ENV TZ="EST"
