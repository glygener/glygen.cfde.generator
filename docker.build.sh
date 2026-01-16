#!/bin/bash

# modify the version number as needed
version_number=1.0.0

# other variables automatically generated
datestamp=$(date -u +'%Y%m%d')
timestamp=$(date -u +'%Y-%m-%dT%H:%M:%SZ')

docker build \
  --build-arg IMAGE_VERSION=${version_number}.${datestamp} \
  --build-arg IMAGE_CREATED=${timestamp} \
  -t glygen/c2m2.generator:${version_number}.${datestamp} -t glygen/c2m2.generator:latest .
