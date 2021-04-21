In this project, an optimization of multithreading using Reentrant Read-Write lock was deployed to maximize the run time performance of Java.

• Nearly 14x faster of the reading speed than that of the ordinary Reentrant lock method running on a 16GB, 4-core, 8 thread processor(6571ms/444ms)
• Allocated heap size changed from 2048MB to 4096MB to have a better performance
• Reading threads and writing threads are assigned to do 100,000 times of access running in parallel on the locally created database object
