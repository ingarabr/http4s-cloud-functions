object Versions {

  object scala {
    val scala2_13 = "2.13.16"
    val scala3 = "3.3.5"

    val cross = Seq(scala2_13, scala3)

    val default = scala3
  }

  val http4s = "0.23.30"


  val cloudFunctions = "1.1.4"

  def V = this

}
