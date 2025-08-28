FROM docker.io/nvidia/cuda:12.6.2-base-ubuntu22.04

LABEL org.opencontainers.image.source="https://github.com/boozallen/aissemble"

# Workaround for ubuntu fix start
# https://bugs.launchpad.net/ubuntu/+source/glibc/+bug/1962606
RUN mv /bin/uname /bin/uname.orig
RUN printf '#!/bin/bash\n\nif [[ "$1" == "-r" ]] ;then\n echo '4.9.250'\n exit\nelse\n uname.orig "$@"\nfi' > /bin/uname
RUN chmod 755 /bin/uname
# Workaround for ubuntu fix end

RUN --mount=type=cache,target=/var/cache/apt,sharing=locked \
    --mount=type=cache,target=/var/lib/apt,sharing=locked \
    apt-get update \
    && apt-get upgrade -y \
    && apt-get install -y openjdk-17-jdk \
    && update-ca-certificates

ARG PYTHON_VERSION=3.11
ENV DEBIAN_FRONTEND=noninteractive

# Update repositories and add necessary ones
RUN --mount=type=cache,target=/var/cache/apt,sharing=locked \
    --mount=type=cache,target=/var/lib/apt,sharing=locked \
    apt-get -y update \
    && apt-get install -y software-properties-common \
    && add-apt-repository universe \
    && add-apt-repository ppa:deadsnakes/ppa

# Install Python and related packages, and perform cleanup
RUN --mount=type=cache,target=/var/cache/apt,sharing=locked \
    --mount=type=cache,target=/var/lib/apt,sharing=locked \
    apt-get update -y && apt-get install --assume-yes \
    python${PYTHON_VERSION} \
    python${PYTHON_VERSION}-distutils \
    python${PYTHON_VERSION}-dev \
    python${PYTHON_VERSION}-venv \
    gcc \
    curl \
    && rm -rf /var/lib/apt/lists/* \
    && ln -s /usr/bin/python${PYTHON_VERSION} /usr/bin/python \
    #Habushu containerization uses `python3` to setup virtual env. This links out latest python install to that command.
    && ln -sf /usr/bin/python${PYTHON_VERSION} /usr/bin/python3

RUN python -m ensurepip --upgrade

ENV PATH=/root/.local/bin:$PATH
