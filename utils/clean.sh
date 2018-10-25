#!/usr/bin/env bash

source vars.sh

oc project myproject
oc delete project $OC_NAMESPACE

git checkout -- ../strimzi-0.7.0
