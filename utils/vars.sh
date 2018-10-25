#!/usr/bin/env bash

OC_USER=$(oc whoami)
OC_TOKEN=$(oc whoami -t)
OC_DOCKER_REGISTRY_INFO=$(oc get svc docker-registry -n default | tr -s " " | sed -n 2p)
OC_DOCKER_REGISTRY_IP=$(echo ${OC_DOCKER_REGISTRY_INFO} | cut -d" " -f3)
OC_DOCKER_REGISTRY_PORT=$(echo ${OC_DOCKER_REGISTRY_INFO} | cut -d" " -f5 | cut -d/ -f1)
OC_DOCKER_REGISTRY_SOCKET=${OC_DOCKER_REGISTRY_IP}:${OC_DOCKER_REGISTRY_PORT}
OC_NAMESPACE=timescale-demo
OC_ANY_UID_SA_NAME=roooooty

KAFKA_CLUSTER_NAME=cluster-for-readings

STRIMZI_INSTALL_DIR=$(realpath ../strimzi-0.7.0/examples/install)
STRIMZI_TEMPLATES_DIR=$(realpath ../strimzi-0.7.0/examples/templates)
