CSE291-project3
=====TEAM======
Name: Qingyu Zhou
ID: A53093706 email:qyzhou@ucsd.edu
Name: jiaxiang chen
ID:A53100265        email:jic286@ucsd.edu

===HOW TO RUN=====
1. cd to project folder, make sure you are in docker environment

2. run ./start-container.sh
   after it finishes running, you will be in the interactive command line of root@master.

3. run ./start-hadoop.sh in the docker container
   NOTICE: check the output of this command. If some slaves fail at start or other error, 
   it might be your host does not have enough availabe memory. Clean your memory and retry 

4. after hadoop is started, run ./jar/run-ngram.sh

5. check the raw key-value parit result after "bigram output----" 
   a pretty print version is after "pretty print-----"

=====How to change input======
1. in your host machine, delete the input.txt in ./jar/input, 
   place  your test file or files 
2. rerun  ./jar/run-ngram.sh 
   NOTICE:  If thre is None space error, pls redo "HOW TO RUN" part to relase space

=====How to exit container =========
1. type "exit" 

=====Project structure=========
1. jar filr is in ./jar/291p_3.jar
1. source file is in ./jar/source_code
