// https://github.com/sbt/sbt-assembly
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.7")

// https://github.com/earldouglas/xsbt-web-plugin
addSbtPlugin("com.earldouglas" % "xsbt-web-plugin" % "4.0.2")

// https://github.com/sbt/sbt-native-packager
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.5")

// https://github.com/xerial/sbt-pack
addSbtPlugin("org.xerial.sbt" % "sbt-pack" % "0.11")

// https://github.com/rtimush/sbt-updates
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.4")

// https://github.com/sbt/sbt-scalariform
addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.8.2")

// http://www.scalastyle.org/sbt.html
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")

// https://github.com/sbt/sbt-header
addSbtPlugin("de.heikoseeberger" % "sbt-header" % "5.0.0")

resolvers += Resolver.sonatypeRepo("releases")
