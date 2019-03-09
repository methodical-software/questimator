name := """questimator"""
organization := "com.methodical.software"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.8"

resolvers += "Spring Plugins Repository" at "http://repo.spring.io/plugins-release/"

libraryDependencies += guice

libraryDependencies += "fastily" % "jwiki" % "1.7.0"
