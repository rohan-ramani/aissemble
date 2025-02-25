#!/bin/sh
cat > resources.yaml
kubectl kustomize
rm resources.yaml
