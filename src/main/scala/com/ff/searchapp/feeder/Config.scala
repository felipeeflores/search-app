package com.ff.searchapp.feeder

final case class Config(usersFile: String, ticketsFile: String, verboseErrors: Boolean)

object Config {
  //ideally this should be loaded from environment or as execution params
  val defaultConfig: Config = Config(
    usersFile = "./data/users.json",
    ticketsFile = "./data/tickets.json",
    verboseErrors = true
  )
}
