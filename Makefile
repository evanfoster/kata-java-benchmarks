.PHONY: build run clean
IMAGE_NAME?="evanfoster/kata-java-benchmarks"
NAMESPACE?="default"

default: run

build:
	docker build -f Dockerfile.thread-yield-test -t $(IMAGE_NAME):thread-yield-test .
	docker push $(IMAGE_NAME):thread-yield-test
	docker build -f Dockerfile.thread-creation-teardown-test -t $(IMAGE_NAME):thread-creation-teardown-test .
	docker push $(IMAGE_NAME):thread-creation-teardown-test

run:
	./run.sh $(NAMESPACE)

clean:
	./clean.sh $(NAMESPACE)
