#!/bin/sh

dotc=/opt/scala3-3.0.0-M3/bin/scalac

rm -f *.class
javac TypeToken.java && ${dotc} Test.scala
