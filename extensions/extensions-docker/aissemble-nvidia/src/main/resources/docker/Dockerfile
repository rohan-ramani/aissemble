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
