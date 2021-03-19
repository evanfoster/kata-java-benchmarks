#!/bin/bash
tests=($(basename -s .yaml ./*.yaml))
# this is really hacky but I am lazy
kubectl -n "$NAMESPACE" get pods | grep -q -E "($(tr -s ' ' '|' <<<"${tests[@]}"))" && { echo "Pods already exist, run a make clean first"; exit 1; }

for test in "${tests[@]}"; do
    kubectl -n "$NAMESPACE" create -f "$test.yaml" || { echo "Failed to create test $test"; exit 1; }
    echo "$test"
    # TODO I'm sure there's a nicer way to wait for a pod. kubectl wait has some issues, but this is silly.
    until kubectl -n "$NAMESPACE" logs -f "$test" --tail 1 &> /dev/null; do
        sleep 1
    done
    kubectl -n "$NAMESPACE" logs -f "$test"
done
