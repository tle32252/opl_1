package com.example.app

import org.scalatra._
import org.json4s.jackson.Serialization
import java.util.UUID
import java.time.Instant

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
//case class someModel(id: Int, food: String, status: String);
case class someModel_1(kind: String, food: String, price:Int, id: Int, status: String);
case class model_2(UUID: String, food: String, id: Int, price: Int, status: String, trash: ObjectId)
//case class All(list: List[someModel])
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
  val cashier = db("backup")



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
    def generateUniqueId = UUID.randomUUID().toString

    val json = request.body
    val e = request.body.getClass
//    println(e)


    val obj = parse(json).extract[(List[someModel_1])]

    for (a <- 0 to obj.size-1 ){
      val d = Instant.now.getEpochSecond
      val eiei = MongoDBObject("id"->obj(a).id, "food"->obj(a).food, "status"->obj(a).status, "price"->obj(a).price, "UUID"->generateUniqueId, "kind"->obj(a).kind, "time"->d )
      main_kitchen.insert(eiei)
    }
//    val obj = parse(json).extract[someModel]
//    println(obj.size)



//    val jsonStringOrder = request.body
//
//    val json = parse(request.body)
//    val ip = json.extract[All]
//    println(jsonStringOrder)
//    println(obj)



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

  put("/upkit"){
    val json = request.body
    val obj = parse(json).extract[(List[model_2])]
    val uuid_1 = obj(0).UUID
    val status_1 = obj(0).status

  }



  put("/update_kitchen/:id/:status"){
    //update "status"
    val id = params("id")
//    val food = params("food")
    val status = params("status")

    val update_1 = MongoDBObject( "UUID"-> id )
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

  delete("/delfood/:id"){
    val id = params("id")
    val update_1 = MongoDBObject( "UUID"-> id )
    main_kitchen.remove(update_1)

  }
  ///////////////////////////////////////////////////////////////////////////////////////////////////

  get("/kitchen"){
    //render all everything in db
    contentType = "application/json"
    //    val james_ex = (("id"->"5") ~ ("food"->"burger") ~ ("status" -> "waiting"))
    //    compact(render(james_ex))
    //----------------------------
    //    val eiei = MongoDBObject( "id"-> 1 )

    val eiei = MongoDBObject("kind"->"food")
    val sorttt = MongoDBObject("time"-> 1)
    val allDocs = main_kitchen.find(eiei).sort( sorttt)
//    println( "kitchen" )
    //    val eiei = for(doc <- allDocs)
    //    for(doc <- allDocs) println( doc )

    val r: Seq[Map[String, AnyRef]] = allDocs.toVector.map{_.toVector}.map{_.toMap}
    Ok(Serialization.write(r))
  }

  get("/dessert"){
    //render all everything in db
    contentType = "application/json"
    //    val james_ex = (("id"->"5") ~ ("food"->"burger") ~ ("status" -> "waiting"))
    //    compact(render(james_ex))
    //----------------------------
    //    val eiei = MongoDBObject( "id"-> 1 )
    val sorttt = MongoDBObject("time"-> 1)
    val eiei = MongoDBObject("kind"->"dessert")
    val allDocs = main_kitchen.find(eiei).sort(sorttt)
    //    println( "kitchen" )
    //    val eiei = for(doc <- allDocs)
    //    for(doc <- allDocs) println( doc )

    val r: Seq[Map[String, AnyRef]] = allDocs.toVector.map{_.toVector}.map{_.toMap}
    Ok(Serialization.write(r))
  }



  get("/eachtable/:id"){
    //assume to get table id 1

    val id = params("id")
    val eiei = MongoDBObject( "id"-> id.toInt )
    val sort = MongoDBObject("time" -> 1)
    val allDocss = main_kitchen.find( eiei ).sort(sort)
    val r: Seq[Map[String, AnyRef]] = allDocss.toVector.map{_.toVector}.map{_.toMap}
    Ok(Serialization.write(r))
  }

  get("/check_id/:id"){
    val id = params("id")
    val eiei = MongoDBObject( "id"-> id.toInt )
//    println(idpw.findOne(eiei))
    if (idpw.findOne(eiei) == None){
      idpw.insert(eiei)
      Ok("true")
    }
    else{
//      response.addHeader("status","error")
      Ok("false")
//      response.sendError(400, "error")
//      response.
    }
  }



  get("/check_out/:id"){
    val id = params("id")
    val eiei = MongoDBObject( "id"-> id.toInt )
    val search = main_kitchen.find(eiei)

    var ans = 0
    var count = 0
    for(doc <- search) {
      var e = doc.toList(4)._2
//      println(e.getClass())
      ans = ans + e.asInstanceOf[Int]
      count += 1
    }

    val amount = MongoDBObject("id"-> id.toInt , "status"->"Unpaid","amount"->ans)
//    cashier.insert(amount)

    Ok(ans)

  }
  post("/check_out_1/:id"){
    def generateUniqueId = UUID.randomUUID().toString
    val id = params("id")
    val eiei = MongoDBObject( "id"-> id.toInt )
    val search = main_kitchen.find(eiei)

    var ans = 0
    var count = 0
    for(doc <- search) {
      var e = doc.toList(4)._2
      //      println(e.getClass())
      ans = ans + e.asInstanceOf[Int]
      count += 1
    }

    val amount = MongoDBObject("id"-> id.toInt ,"amount"->ans, "status"->"Unpaid","UUID"->generateUniqueId)
    cashier.insert(amount)


  }
  delete("/check_out_2/:id"){
    val id = params("id")
    val eiei = MongoDBObject( "id"-> id.toInt )
    main_kitchen.remove(eiei)
  }

  get("/cashier"){
    contentType = "application/json"
    val allDocs = cashier.find()

    val r: Seq[Map[String, AnyRef]] = allDocs.toVector.map{_.toVector}.map{_.toMap}
    Ok(Serialization.write(r))
  }

  post("/cashier_update/:id/:status"){
    val id = params("id")
    val status = params("status")

    val update_1 = MongoDBObject( "UUID"-> id )
    val update_2 = $set( "status" -> status )
    cashier.update(update_1, update_2)

    Ok("updated cashier")

  }





}


