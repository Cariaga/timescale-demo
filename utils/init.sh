#!/usr/bin/env bash

source vars.sh

oc new-project ${OC_NAMESPACE}



######################################################################
### strimzi
######################################################################
# http://strimzi.io/docs/0.7.0/#deploying-cluster-operator-openshift-str
sed -i "s/namespace: .*/namespace: $OC_NAMESPACE/" \
    ${STRIMZI_INSTALL_DIR}/cluster-operator/*RoleBinding*.yaml

oc apply -n ${OC_NAMESPACE} -f ${STRIMZI_INSTALL_DIR}/cluster-operator

oc process -f ${STRIMZI_TEMPLATES_DIR}/cluster-operator/ephemeral-template.yaml \
    -p CLUSTER_NAME=${KAFKA_CLUSTER_NAME} \
    | oc create -n ${OC_NAMESPACE} -f -



######################################################################
### timescale DB container requires root
######################################################################
# https://blog.openshift.com/understanding-service-accounts-sccs/
oc create sa ${OC_ANY_UID_SA_NAME} -n ${OC_NAMESPACE}
oc adm policy add-scc-to-user anyuid -z ${OC_ANY_UID_SA_NAME} -n ${OC_NAMESPACE}



######################################################################
### timescale DB
######################################################################
oc process -f $(realpath ../timescaledb/template.yaml) \
    -p OC_NAMESPACE=${OC_NAMESPACE} \
    -p OC_SA=${OC_ANY_UID_SA_NAME} \
    | oc create -n ${OC_NAMESPACE} -f -



######################################################################
### build project
######################################################################
pushd ..
mvn clean install
popd



######################################################################
### producer
######################################################################
oc new-build --name=producer --strategy=docker --binary=true
oc start-build producer --from-dir=$(realpath ../producer) --follow

for person_id in $(seq 1 10);
do
    oc process -f $(realpath ../producer/template.yaml) \
        -p OC_NAMESPACE=${OC_NAMESPACE} \
        -p OC_NAME=producer-${person_id} \
        -p PRODUCER_PERSON_ID=${person_id} \
        | oc create -n ${OC_NAMESPACE} -f -
done



######################################################################
### consumer
######################################################################
oc new-build --name=consumer --strategy=docker --binary=true
oc start-build consumer --from-dir=$(realpath ../consumer) --follow
oc create -f $(realpath ../consumer/dc.yaml)
oc create -f $(realpath ../consumer/svc.yaml)
oc expose svc consumer
oc describe route consumer
