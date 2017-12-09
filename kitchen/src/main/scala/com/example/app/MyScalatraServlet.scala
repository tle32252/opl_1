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


case class someModel_1(kind: String, food: String, price:Int, id: Int, status: String);
//case class model_2(UUID: String, food: String, id: Int, price: Int, status: String, trash: ObjectId)

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
//  val copy_1 = db("cashier")

  ///////////////////////////////////////////////////////////////////////////////////////////////////
  get("/:name") {
    views.html.hello()
    Ok("aaaaaadddsdsdaaaa")
  }
  ///////////////////////////////////////////////////////////////////////////////////////////////////


  /////////////////////////////////////////////////TABLE/////////////////////////////////////////////

  //check whether that number has already been used
  get("/check_id/:id"){
    val id = params("id")
    val table_no = MongoDBObject( "id"-> id.toInt )
    if (idpw.findOne(table_no) == None){
      idpw.insert(table_no)
      Ok("true")
    }
    else{
      Ok("false")
    }
  }

  // post the order from the table
  post("/order"){
    def generateUniqueId = UUID.randomUUID().toString

    val json = request.body
    val obj = parse(json).extract[(List[someModel_1])]


    for (a <- 0 to obj.size-1 ){
      val d = Instant.now.getEpochSecond
      val info = MongoDBObject("id"->obj(a).id, "food"->obj(a).food, "status"->obj(a).status, "price"->obj(a).price, "UUID"->generateUniqueId, "kind"->obj(a).kind, "time"->d)
      main_kitchen.insert(info)

    }
    Ok("ordered")
  }

  //each table can monitor their food
  get("/eachtable/:id"){
    contentType = "application/json"

    val id = params("id")
    val table_id = MongoDBObject( "id"-> id.toInt )
    val sort_by_time = MongoDBObject("time" -> 1)
    val allDocss = main_kitchen.find( table_id ).sort(sort_by_time)

    val r: Seq[Map[String, AnyRef]] = allDocss.toVector.map{_.toVector}.map{_.toMap}
    Ok(Serialization.write(r))
  }



  //check waitaing by uuid
  get("/check_waiting/:id"){
    val id = params("id")
    val that_food = MongoDBObject( "UUID"-> id )
    val search = main_kitchen.find(that_food)
//    val ans = search.toList.toVector(3)
    var ans = ""
    for(doc <- search){
      var bbb = doc.toList(3)._2
      ans = bbb.toString
    }
    println(ans)

  }


  //customers want to remove that food and check the status of that food as well whether it is still waiting
  //since we did not refresh every single sec.
  delete("/delfood/:id"){
    val id = params("id")
    val update_1 = MongoDBObject( "UUID"-> id )
    val search = main_kitchen.find(update_1)
    var ans = ""
    for(doc <- search){
      var status = doc.toList(3)._2
      ans = status.toString
    }

    if (ans == "Waiting"){
      main_kitchen.remove(update_1)
      Ok("Your food has been cancelled.")
    }
    else{
      Ok("false")
    }
    //main_kitchen.remove(update_1)

  }


  //calculate amount customer needs to pay
  get("/check_out/:id"){
    val id = params("id")
    val table_no = MongoDBObject( "id"-> id.toInt )
    val search = main_kitchen.find(table_no)

    var ans = 0
    for(doc <- search) {
      var e = doc.toList(4)._2
      ans = ans + e.asInstanceOf[Int]
    }
    Ok(ans)
  }

  ///////////////////////////////////////////////////////////////////////////////////////////////////


  //////////////////////////////////KITCHEN/////////////////////////////////////////////////////////

  //kitchen see allll
  get("/kitchen"){
    contentType = "application/json"

    val kind = MongoDBObject("kind"->"food")
    val sort_by_time = MongoDBObject("time"-> 1)
    val allDocs = main_kitchen.find(kind).sort( sort_by_time )

    val r: Seq[Map[String, AnyRef]] = allDocs.toVector.map{_.toVector}.map{_.toMap}
    Ok(Serialization.write(r))
  }

  //kitchen can update the status of each food by looking at UUID
  put("/update_kitchen/:id/:status"){
    val id = params("id")
    val status = params("status")

    val update_1 = MongoDBObject( "UUID"-> id )
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


  ////////////////////////////////////KITCHEN-DESERT///////////////////////////////////////////////////

  // all information of every table in the kitchen-desert side
  get("/dessert"){
    contentType = "application/json"

    val kind = MongoDBObject("kind"->"dessert")
    val sort_by_time = MongoDBObject("time"-> 1)
    val allDocs = main_kitchen.find(kind).sort(sort_by_time)

    val r: Seq[Map[String, AnyRef]] = allDocs.toVector.map{_.toVector}.map{_.toMap}
    Ok(Serialization.write(r))
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////////


  //////////////////////////////////////////CASHIER///////////////////////////////////////////////////

  //all information of every table in the cashier side
  get("/cashier"){
    contentType = "application/json"
    val allDocs = cashier.find()
    val r: Seq[Map[String, AnyRef]] = allDocs.toVector.map{_.toVector}.map{_.toMap}
    Ok(Serialization.write(r))
  }


  //when
  post("/check_out_1/:id"){
    def generateUniqueId = UUID.randomUUID().toString
    val id = params("id")
    val table_no = MongoDBObject( "id"-> id.toInt )
    val search = main_kitchen.find(table_no)
//    db.clone()

    var ans = 0
    var count = 0
    for(doc <- search) {
      var e = doc.toList(4)._2
      ans = ans + e.asInstanceOf[Int]
      count += 1
    }

    val amount = MongoDBObject("id"-> id.toInt ,"amount"->ans, "status"->"Unpaid","UUID"->generateUniqueId)
    cashier.insert(amount)


  }

  //for cashier to view the menu of that table
  get("/info_cashier/:id"){
    val id = params("id")
    val table_no = MongoDBObject( "id"-> id.toInt )
    val sort_by_time = MongoDBObject("time" -> 1)
    val allDocss = cashier.find( table_no ).sort(sort_by_time)
    val r: Seq[Map[String, AnyRef]] = allDocss.toVector.map{_.toVector}.map{_.toMap}
    Ok(Serialization.write(r))

  }

  delete("/check_out_2/:id"){
    val id = params("id")
    val table_no = MongoDBObject( "id"-> id.toInt )
    main_kitchen.remove(table_no)
//    main_kitchen.cop
  }


  //cashier update status from unpaid to paid
  post("/cashier_update/:id/:status"){
    val id = params("id")
    val status = params("status")

    val update_1 = MongoDBObject( "UUID"-> id )
    val update_2 = $set( "status" -> status )
    cashier.update(update_1, update_2)

    Ok("updated cashier")

  }
}


