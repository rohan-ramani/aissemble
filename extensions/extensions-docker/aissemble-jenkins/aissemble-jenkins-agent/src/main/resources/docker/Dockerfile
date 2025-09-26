FROM docker.io/jenkins/ssh-agent:latest-debian-jdk21

LABEL org.opencontainers.image.source="https://github.com/boozallen/aissemble"
ARG TARGETARCH
ARG TARGETOS

RUN --mount=type=cache,target=/var/cache/apt,sharing=locked \
    --mount=type=cache,target=/var/lib/apt,sharing=locked \
    apt --yes update && apt install --yes curl pipx && pipx ensurepath
RUN --mount=type=cache,target=/root/.cache/pip \
    pipx install poetry

RUN mkdir /var/pyenv
RUN ls -l -a /var/pyenv
RUN git clone https://github.com/pyenv/pyenv.git /var/pyenv

RUN echo 'export PYENV_ROOT=$(echo "/var/pyenv")' >> /etc/bash.bashrc
RUN echo 'command -v pyenv >/dev/null || export PATH=$(echo "$PYENV_ROOT/bin:$PATH")' >> /etc/bash.bashrc
RUN echo 'eval "$(pyenv init -)"'  >> /etc/bash.bashrc

RUN echo 'export PYENV_ROOT=$(echo "/var/pyenv")' >> /etc/profile
RUN echo 'command -v pyenv >/dev/null || export PATH=$(echo "$PYENV_ROOT/bin:$PATH")' >> /etc/profile
RUN echo 'eval "$(pyenv init -)"' >> /etc/profile

RUN chown -R jenkins:jenkins /var/pyenv

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
