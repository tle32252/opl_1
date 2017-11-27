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
import scala.util.parsing.json._
import com.mongodb.casbah.Imports.DBObject

//import scalate.ScalateSupport
//import net.liftweb.json._
case class someModel(id: Int, food: String, status: String);
case class someModel_1(food: String,price:Int,id: String, status: String);
case class All(list: List[someModel])
case class whole()

case class User(name: String, emails: List[String])

case class UserList(users: List[User]) {
  override def toString(): String = {
    this.users.foldLeft("")((a, b) => a + b.toString)
  }
}

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
    response.addHeader("sdsd","sdsd")
  }
  ///////////////////////////////////////////////////////////////////////////////////////////////////


  post("/order"){

    val json = request.body
//    val json = """
//      [
//        {"name": "Foo", "emails": ["Foo@gmail.com", "foo2@gmail.com"]},
//        {"name": "Bar", "emails": ["Bar@gmail.com", "bar@gmail.com"]}
//      ]
//    """
//    println(json)
    val obj = parse(json).extract[(List[someModel_1])]
//    val obj = parse(json).extract[someModel]
    println(obj.size)



//    val jsonStringOrder = request.body
//
//    val json = parse(request.body)
//    val ip = json.extract[All]
//    println(jsonStringOrder)
//    println(obj)


    for (a <- 0 to obj.size-1 ){
      val eiei = MongoDBObject("id"->obj(a).id,"food"->obj(a).food,"status"->obj(a).status)
      main_kitchen.insert(eiei)
    }
//    println(obj(1).id)
//    main_kitchen.insert(MongoDBObject(jso))

//    println(jsonStringOrder)
//    val ee = parse(jsonStringOrder).extract[(String,String,String)]
//    println(ee)
//    val ip = jsonStringOrder.extract[(String,String,String)]
//    println(ip)
//    val jsonStringOrder_1 = MongoDBObject(parse(request.body).children)

//    println(jsonStringOrder_1.getClass)

//    println(jsonStringOrder.getClass)
//    println(jsonStringOrder)
//    println("hi"+jsonStringOrder)
//    println("YO "+request.body)
//    for(doc <- json) {
//      println(doc)
////      for (echdoc <- doc.children){
////        println(echdoc.)
////      }
////      val doc2 = MongoDBObject(doc.toString)
//      main_kitchen.insert(doc)
//
//
////      println("DOC "+doc.toString.getClass)
////      println(doc.id)
//
////      val eiei2 = MongoDBObject(eiei)
////          println(eiei2.getClass)
////      main_kitchen.insert(MongoDB)
////      println(eiei2)
//    }

//    val list = List(eiei_1)
//    val order = MongoDBList(eiei_1:_*)

//    main_kitchen.insert(MongoDBObject(doc))
//    println(jsonStringOrder_1)
    Ok("ordered")
  }



  post("/update_kitchen/:id/:food/:status"){
    //update "status"
    val id = params("id")
    val food = params("food")
    val status = params("status")

    val update_1 = MongoDBObject( "id"-> id.toInt, "food"-> food )
    val update_2 = $set( "status" -> status )
    main_kitchen.update(update_1, update_2)

    Ok("updated from kitchen")

  }

  post("/update_table/:id/:food/:food_2"){
    //update "food"
    val id = params("id")
    val food = params("food")
    val food_2 = params("food_2")

    val update_1 = MongoDBObject( "id" -> id.toInt, "food" -> food )
    val update_2 = $set( "food" -> food_2 )
    main_kitchen.update(update_1, update_2)
    Ok("updated from table")
  }
  ///////////////////////////////////////////////////////////////////////////////////////////////////

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

  get("/eachtable/:id"){
    //assume to get table id 1

    val id = params("id")
    val eiei = MongoDBObject( "id"-> id.toInt )
    val allDocss = main_kitchen.find( eiei )
    val r: Seq[Map[String, AnyRef]] = allDocss.toVector.map{_.toVector}.map{_.toMap}
    Ok(Serialization.write(r))
  }

  get("/check_id/:id"){
    val id = params("id")
    val eiei = MongoDBObject( "id"-> id.toInt )
//    println(idpw.findOne(eiei))
    if (idpw.findOne(eiei) == None){
      idpw.insert(eiei)
    }
    else{
      response.addHeader("status","error")
//      response.
    }
  }


}


