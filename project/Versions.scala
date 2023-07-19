object Versions {

  object scala {
    val scala2_13 = "2.13.11"
    val scala3 = "3.3.0"

    val cross = Seq(scala2_13, scala3)

    val default = scala3
  }

  val http4s = "0.23.22"

  val cloudFunctions = "1.1.0"

  def V = this

}
