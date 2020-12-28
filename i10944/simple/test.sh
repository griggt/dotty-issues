#!/bin/sh

dotc=/src/dotty/bin/scalac
#dotc=/opt/scala3-3.0.0-M3/bin/scalac
#dotc=scalac

java -version; javac -version ; ${dotc} -version

rm -f *.class *.tasty
javac TypeToken.java && ${dotc} Test.scala
