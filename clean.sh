#!/bin/bash
kubectl -n "$NAMESPACE" delete pod $(basename -s .yaml ./*.yaml)
