package com.example.app

import org.scalatra._
import org.json4s.jackson.Serialization

import org.scalatra.json.JacksonJsonSupport
import org.json4s.jackson.JsonMethods._

import org.json4s._
import org.json4s.JsonDSL._
// JSON-related libraries
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._
import org.scalatra.CorsSupport

import org.json4s._
import org.json4s.jackson.JsonMethods._

import com.mongodb.casbah.Imports._

import com.mongodb.casbah.Imports.DBObject


class MyScalatraServlet extends ScalatraServlet  with CorsSupport  {

  options("/*") {
    response.setHeader(
      "Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers")
    )
  }

  implicit val formats = org.json4s.DefaultFormats

  val mongoClient = MongoClient("localhost", 27017)

  val db = mongoClient("local")

  val idpw = db("idpw")
  val main_kitchen = db("main_kitchen")


  get("/:name") {
    views.html.hello()
    Ok("aaaaaadddsdsdaaaa")
  }

  post("/api/login"){
    val jsonStringLogin = request.body
    println(jsonStringLogin)
    Ok("Login :)")
  }
  ///////////////////////////////////////////////////////////////////////////////////////////////////----------------------------------------------------------------
  post("/order"){
    val jsonStringOrder = request.body
    val order = MongoDBObject(jsonStringOrder)

    main_kitchen.insert(order)
    Ok("ordered")

  }
  get("/kitchen"){
    //render all everything in db
    contentType = "application/json"
//    val james_ex = (("id"->"5") ~ ("food"->"burger") ~ ("status" -> "waiting"))
//    compact(render(james_ex))
//----------------------------
//    val eiei = MongoDBObject( "id"-> 1 )
    val allDocs = main_kitchen.find()
    println( "kitchen" )
//    val eiei = for(doc <- allDocs)
//    for(doc <- allDocs) println( doc )

    val r: Seq[Map[String, AnyRef]] = allDocs.toVector.map{_.toVector}.map{_.toMap}
    Ok(Serialization.write(r))
  }

  post("/update_kitchen/:id/:food/:status"){
    //update "status" table id 1 and food burger from waiting to cooking
    val id = params("id")
    val food = params("food")
    val status = params("status")

    val update_1 = MongoDBObject( "id"-> id.toInt, "food"-> food )
    val update_2 = $set( "status" -> status )
    main_kitchen.update(update_1, update_2)

    Ok("updated from kitchen")

  }

  post("/update_table/:id/:food/:food_2"){
    val id = params("id")
    val food = params("food")
    val food_2 = params("food_2")

    val update_1 = MongoDBObject( "id" -> id.toInt, "food" -> food )
    val update_2 = $set( "food" -> food_2 )
    main_kitchen.update(update_1, update_2)

    Ok("updated from table")
  }

  get("/eachtable/:id"){
    //assume to get table id 1

    val id = params("id")
    val eiei = MongoDBObject( "id"-> id.toInt )
    val allDocss = main_kitchen.find( eiei )
    val r: Seq[Map[String, AnyRef]] = allDocss.toVector.map{_.toVector}.map{_.toMap}
    Ok(Serialization.write(r))
  }


}


