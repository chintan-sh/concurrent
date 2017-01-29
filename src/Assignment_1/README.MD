Goal: In this project you will accomplish two tasks;

1. Complete several classes to find the maximum Integer in a LinkedList in parallel. These classes should be thread safe.

2. Modify the code to compute an “walking average value” of the integers in a randomly generated LinkedList (as above) in parallel. Again, the classes should be thread safe. The walking average means that each of the “workers” will pull one element from the list, average it with the value it currently has, and replace the old value with the newly calculated value. This value will be initialized to zero. At the end, the main will average the values computed by all the threads and print the final result. The computation should be repeated ten times for the same LinkedList, i.e., the same randomly generated list should be used ten times, each time printing the final result.
