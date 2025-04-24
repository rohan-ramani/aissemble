FROM docker.io/jenkins/jenkins:lts-jdk17

LABEL org.opencontainers.image.source="https://github.com/boozallen/aissemble"
ARG TARGETARCH
ARG TARGETOS

USER root

# install helm
RUN curl -fsSL -o get_helm.sh https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3
RUN chmod 700 get_helm.sh
RUN ./get_helm.sh

# install helmfile
RUN mkdir "/tmp/helmfile"
RUN curl -L https://github.com/helmfile/helmfile/releases/download/v0.171.0/helmfile_0.171.0_${TARGETOS}_${TARGETARCH}.tar.gz | tar \
    -xzv -C /tmp/helmfile
RUN chmod +x /tmp/helmfile/helmfile
RUN mv /tmp/helmfile/helmfile /usr/local/bin/
RUN helmfile --version
RUN rm -rf /tmp/helmfile

USER jenkins

# install helm-diff plugin
RUN helm plugin install https://github.com/databus23/helm-diff

RUN jenkins-plugin-cli --plugins git:5.7.0
RUN jenkins-plugin-cli --plugins github:1.42.0
RUN jenkins-plugin-cli --plugins templating-engine:2.5.3
RUN jenkins-plugin-cli --plugins adoptopenjdk:1.7
RUN jenkins-plugin-cli --plugins pipeline-maven:1508.v347c4b_692202
RUN jenkins-plugin-cli --plugins slack:761.v2a_8770f0d169

COPY ./src/main/resources/jenkins/ "$REF"

ENV TZ="EST"
