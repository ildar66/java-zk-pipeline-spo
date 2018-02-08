#!/bin/sh
mvn -U clean package install source:jar javadoc:jar deploy -P dev
