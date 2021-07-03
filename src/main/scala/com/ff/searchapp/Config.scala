package com.ff.searchapp

import cats.Show

import scala.util.Try

final case class Config(usersFile: String, ticketsFile: String, verboseErrors: Boolean, maxSearchResults: Option[Int])

object Config {
  def loadConfig: Config = Config(
    usersFile = sys.env.getOrElse("USERS_JSON", "./data/users.json"),
    ticketsFile = sys.env.getOrElse("TICKETS_JSON", "./data/tickets.json"),
    verboseErrors = true,
    maxSearchResults = sys.env.get("MAX_SEARCH_RESULTS").flatMap(str => Try(str.toInt).toOption)
  )

  implicit val configShow: Show[Config] =
    c => s"""
            |Config:
            | users file: ${c.usersFile}
            | tickets file: ${c.ticketsFile}
            | max results: ${c.maxSearchResults.map(_.toString).getOrElse("none")}
            |""".stripMargin
}
