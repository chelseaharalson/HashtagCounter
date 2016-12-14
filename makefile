JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
        HashtagObj.java \
        FibonacciNode.java \
        FibonacciHeap.java \
        hashtagcounter.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class
