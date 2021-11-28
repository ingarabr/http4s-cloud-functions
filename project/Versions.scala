object Versions {

  object scala {
    val scala2_13 = "2.13.6"
    val scala3 = "3.1.0"

    val cross = Seq(scala2_13, scala3)

    val default = scala3
  }

  val http4s = "0.23.6"

  val cloudFunctions = "1.0.4"

  def V = this

}
