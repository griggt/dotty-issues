#!/bin/sh

dotc=/opt/scala3-3.0.0-M3/bin/scalac

rm -f *.class *.tasty
javac TypeToken.java && ${dotc} Test.scala
