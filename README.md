Before running your tests, ensure your KUBECONFIG is defined (if not using `~/.kube/config`) and your context is set.

To run the tests, simply run `make`, or `make run`. If you want to specify a namespace other than `default`, run `NAMESPACE=foo make`

After running your test, you'll need to `make clean`. This isn't handled automatically because doing so would nuke the pod logs.

The images are built and pushed using `make build`. They go to my Docker Hub namespace by default, but this can be overridden with the `IMAGE_NAME` environment variable.

There are two main tests:

- thread-creation-teardown
  - Test source code: `ThreadCreationTeardownTest.java`
  - This test creates an increasing number of threads at varying levels of priority on a single core
  - Each thread infinitely loops with a small sleep
  - Results are generated as a JSON list of objects
  - Performance overview:
    - CRI-O with runc (AWS m5.metal or Azure D32s_v3): The control, this test runs relatively quickly. Thread creation time is fairly constant
    - CRI-O with Kata (AWS m5.metal): Runs with basically no performance degredation vs runc
    - CRI-O with kata (Azure D32s_v3): Catastrophically bad performance when starting threads. The time to start 1024 threads at priority 10 with a 2 millisecond sleep is two orders of magnitude worse than runc
- thread-yield-test
  - Test source code: `ThreadTest.java`
  - This test creates an increasing number threads across multiple cores
  - Each thread infinitely loops, doing a bunch of busywork and then yielding
  - Results are generated as CSV
  - Performance overview:
    - CRI-O with runc (AWS m5.metal or Azure D32s_v3): The control. The time to create new threads increases somewhat linearly
    - CRI-O with Kata (Azure D32s_v3): The time to create new threads is relatively comparable when there are more CPU cores than threads, but increases catastrophically once there are more threads than cores. The worker threads do more work on Kata, it seems like the main thread is starved of resources and takes longer to create new threads (in some cases, this is an order of magnitude worse)
    - CRI-O with Kata (AWS m5.metal): Like Kata on Azure, but seemingly worse. I've seen Kata perform two orders of magnitude worse in this case


Each test also spits out a bunch of extra information, including ulimits, sysctls, virtualization platform, and the Java version.

