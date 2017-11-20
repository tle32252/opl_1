package com.example.app

import org.scalatra._

import org.scalatra.json.JacksonJsonSupport
import org.json4s.jackson.JsonMethods._

import org.json4s._
import org.json4s.JsonDSL._
// JSON-related libraries
import org.json4s.{DefaultFormats, Formats}

import com.mongodb.casbah.Imports._

class MyScalatraServlet extends ScalatraServlet {
  val mongoClient = MongoClient("localhost", 27017)

  val db = mongoClient("local")

  val idpw = db("idpw")
  val main_kitchen = db("main_kitchen")


  get("/") {
    views.html.hello()
    Ok("aaaaaaaaaa")
  }

  post("/api/login"){
    val jsonStringLogin = request.body
    println(jsonStringLogin)
    Ok("Login :)")
  }

  post("/order"){
    val jsonStringOrder = request.body
    println(jsonStringOrder)
    Ok("Ordered..")
//    val example = MongoDBObject(List("id" -> 223, "food" -> "burger", "status" -> "waiting"),("id" -> 356, "food" -> "burger", "status" -> "waiting"),("id" -> 357, "food" -> "burger", "status" -> "waiting"))
    val example_2 = MongoDBObject( "hello" -> "world", "language" -> "scala" )
    main_kitchen.insert( example_2 )
  }
  get("/fromdbtokit"){
    //render all everything in db
    contentType = "application/json"
    val james_ex = (("id"->"5") ~ ("food"->"burger") ~ ("status" -> "waiting"))
    compact(render(james_ex))
//----------------------------
    val allDocs = main_kitchen.find()
    for(doc <- allDocs) println( doc )

  }
  post("/fromkittodb"){
    //update "status" table id 1 and food burger from waiting to cooking
    val update_1 = MongoDBObject( "id"-> 1, "food"-> "sandwich")
    val update_2 = $set("status" -> "cooking")
    main_kitchen.update(update_1, update_2)

    Ok("updated cooking..")

  }
  get("/eachtable"){
    //assume to get table id 1
    val eiei = MongoDBObject( "id"-> 2 )
    val allDocss = main_kitchen.find(eiei)
    for(doc <- allDocss) println( doc )
  }
}
