import io.restassured.RestAssured;
import org.json.simple.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

import java.io.IOException;

@Listeners(TestOrderRandomizer.class)
public class Test_Todos {

    /////////////////////////////
    //// SETUP AND TEARDOWN /////
    /////////////////////////////

    private Process proc;
    private int randomNum[];
    //This boolean will turn exec on and off, can use to see if tests work when application not running
    private static boolean executeBool = true;

    //This method will run before continuing with the rest of the tests
    //it will turn on the program, and initialise the base uri for rest assured
    @BeforeClass
    public void setUp() throws InterruptedException {
        if (executeBool) {
            try {

                proc = Runtime.getRuntime().exec("java -jar runTodoManagerRestAPI-1.5.5.jar");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //Make sure application is running
        System.out.println("Starting tests in...\n");
        for (int i = 3; i > 0; i--) {
            System.out.println(i);
            Thread.sleep(1000);
        }
        randomNum = new int[27];

        RestAssured.baseURI = "http://localhost:4567";
    }

    //This will run at the end of all tests in this class, basically closes the program to reset it back to initial state
    @AfterClass
    public void tearDown() {
        proc.destroy();
    }

    ///////////////////////////
    // TODOS ENDPOINTS ////////
    ///////////////////////////

    //Gets all todos
    @Test
    public void t1_getAllTodos() {
        RestAssured.get("/todos")
                .then()
                .assertThat()
                .statusCode(equalTo(200));
    }

    //Get headers for all todos
    @Test
    public void t2_headersForAllTodos() {

        RestAssured.head("/todos")
                .then()
                .assertThat()
                .statusCode(equalTo(200));
    }

    //Create a todo with title
    @Test
    public void t3_createTodo_title() {

        JSONObject todo = createTodoRequest("title1", null, null);
        System.out.println("JSON From test 3 \n" + todo + "\n");

        given()
                .body(todo.toJSONString())
                .when()
                .post("/todos")
                .then()
                .assertThat()
                .body("title", equalTo("title1"))
                .and()
                .statusCode(equalTo(201));
    }

    //Create a todo with no title
    @Test
    public void t4_createTodo_noTitle() {

        JSONObject todo = createTodoRequest(null, null, null);
        System.out.println("JSON From test 4 \n" + todo + "\n");

        given()
                .body(todo.toJSONString())
                .when()
                .post("/todos")
                .then()
                .assertThat()
                .statusCode(equalTo(400));
    }

    //Create a todo with description (and title)
    @Test
    public void t5_createTodo_description() {

        JSONObject todo = createTodoRequest("testTitle", "test", null);
        System.out.println("JSON From test 5 \n" + todo + "\n");

        given()
                .body(todo.toJSONString())
                .when()
                .post("/todos")
                .then()
                .assertThat()
                .body("description", equalTo("test"))
                .and()
                .statusCode(equalTo(201));
    }

    //Create todo with doneStatus set to false
    @Test
    public void t6_createTodo_falseDoneStatus() {

        JSONObject todo = createTodoRequest("testTitle", null, false);
        System.out.println("JSON From test 6 \n" + todo + "\n");

        given()
                .body(todo.toJSONString())
                .when()
                .post("/todos")
                .then()
                .assertThat()
                .body("doneStatus", equalTo("false"))
                .and()
                .statusCode(equalTo(201));
    }

    //Create todo with doneStatus set to true
    @Test
    public void t7_createTodo_trueDoneStatus() {

        JSONObject todo = createTodoRequest("testTitle", null, true);
        System.out.println("JSON From test 7 \n" + todo + "\n");

        given()
                .body(todo.toJSONString())
                .when()
                .post("/todos")
                .then()
                .assertThat()
                .body("doneStatus", equalTo("true"))
                .and()
                .statusCode(equalTo(201));
    }

    /////////////////////////////
    //// TODOS/:ID ENDPOINTS ////
    /////////////////////////////

    //Get todo with id 1
    @Test
    public void t8_getTodoWithId_simple() {

        RestAssured.get("/todos/1")
                .then()
                .assertThat()
                .body("todos[0].id", equalTo("1"))
                .and()
                .statusCode(equalTo(200));
    }

    //Get todo header with id 1
    @Test
    public void t9_headTodoWithId() {

        RestAssured.head("/todos/1")
                .then()
                .assertThat()
                .and()
                .statusCode(equalTo(200));
    }

    //Amend todo using post, with a title
    @Test
    public void t10_amendTodoWithId_postTitle() {

        JSONObject todo = createTodoRequest("testPostTitle", null, null);
        System.out.println("JSON From test 10 \n" + todo + "\n");

        given()
                .body(todo.toJSONString())
                .when()
                .post("/todos/1")
                .then()
                .assertThat()
                .body("title", equalTo("testPostTitle"))
                .and()
                .body("id", equalTo("1"))
                .and()
                .statusCode(equalTo(200));
    }

    //Amend todo using post, with a description
    @Test
    public void t11_amendTodoWithId_postDescription() {

        JSONObject todo = createTodoRequest(null, "testPostDescription", null);
        System.out.println("JSON From test 11 \n" + todo + "\n");

        given()
                .body(todo.toJSONString())
                .when()
                .post("/todos/1")
                .then()
                .assertThat()
                .body("description", equalTo("testPostDescription"))
                .and()
                .body("id", equalTo("1"))
                .and()
                .statusCode(equalTo(200));
    }

    //Amend todo using post, with a doneStatus of true
    @Test
    public void t12_amendTodoWithId_postDoneStatusTrue() {

        JSONObject todo = createTodoRequest(null, null, true);
        System.out.println("JSON From test 12 \n" + todo + "\n");

        given()
                .body(todo.toJSONString())
                .when()
                .post("/todos/1")
                .then()
                .assertThat()
                .body("doneStatus", equalTo("true"))
                .and()
                .body("id", equalTo("1"))
                .and()
                .statusCode(equalTo(200));
    }

    //Amend todo using put, with a title
    @Test
    public void t13_amendTodoWithId_putTitle() {

        JSONObject todo = createTodoRequest("testPutTitle", null, null);
        System.out.println("JSON From test 13 \n" + todo + "\n");

        given()
                .body(todo.toJSONString())
                .when()
                .put("/todos/1")
                .then()
                .assertThat()
                .body("title", equalTo("testPutTitle"))
                .and()
                .body("id", equalTo("1"))
                .and()
                .statusCode(equalTo(200));
    }

    //BUGBEHAVIOR
    //Amend todo using post, with a description
    //THIS TEST CONFIRMS THAT THE FUNCTIONALITY DOES NOT WORK CORRECTLY
    //The intended capability is the the PUT call with a description should modify id=1 witht he input description
    //Instead, a 400 error is returned, this is a bug
    @Test
    public void t14_amendTodoWithId_putDescription() {

        JSONObject todo = createTodoRequest(null, "testPutDescription", null);
        System.out.println("JSON From test 14 \n" + todo + "\n");

        given()
                .body(todo.toJSONString())
                .when()
                .put("/todos/1")
                .then()
                .assertThat()
                .statusCode(equalTo(400));
    }

    //Amend todo using put, with a doneStatus of true
    @Test
    public void t15_amendTodoWithId_putDoneStatusTrue() {

        JSONObject todo = createTodoRequest(null, null, true);
        System.out.println("JSON From test 15 \n" + todo + "\n");

        given()
                .body(todo.toJSONString())
                .when()
                .post("/todos/1")
                .then()
                .assertThat()
                .body("doneStatus", equalTo("true"))
                .and()
                .body("id", equalTo("1"))
                .and()
                .statusCode(equalTo(200));
    }

    //Delete todo with id 2
    @Test
    public void t16_deleteTodoWithId() {

        RestAssured
                .when()
                .delete("/todos/2")
                .then()
                .assertThat()
                .statusCode(equalTo(200));
    }

    ///////////////////////////
    //// JSON/XML PAYLOADS ////
    ///////////////////////////

    //Check if content type and accept can be changed to json
    @Test
    public void t17_jsonHeaders() {

        RestAssured
                .given()
                .header("Content-Type", "application/json", "Accept", "application/json")
                .when()
                .get("/todos")
                .then()
                .assertThat()
                .statusCode(equalTo(200));
    }

    //Check if content type and accept can be changed to xml
    @Test
    public void t18_xmlHeaders() {

        RestAssured
                .given()
                .header("Content-Type", "application/xml", "Accept", "application/xml")
                .when()
                .get("/todos")
                .then()
                .assertThat()
                .statusCode(equalTo(200));
    }

    /////////////////////////////////
    //// ADDITION CONSIDERATIONS ////
    /////////////////////////////////

    //Make a call with a bad path
    @Test
    public void t19_getAllTodos_BadPath() {
        RestAssured.get("/todoss")
                .then()
                .assertThat()
                .statusCode(equalTo(404));
    }

    //Attempting to post with an ID in json
    @Test
    public void t20_post_withID_fail() {
        JSONObject todo = createTodoRequest("title1", null, null);
        todo.put("id", "100");
        System.out.println("JSON From test 20 \n" + todo + "\n");

        given()
                .body(todo.toJSONString())
                .when()
                .post("/todos")
                .then()
                .assertThat()
                .statusCode(equalTo(400));
    }

    //Get with a non existent id
    @Test
    public void t21_get_badID() {
        RestAssured.get("/todos/10000")
                .then()
                .assertThat()
                .statusCode(equalTo(404));
    }

    //Amend with post with a non existent id
    @Test
    public void t22_post_badID() {
        JSONObject todo = createTodoRequest("title1", null, null);
        System.out.println("JSON From test 22 \n" + todo + "\n");

        given()
                .body(todo.toJSONString())
                .when()
                .post("/todos/1000")
                .then()
                .assertThat()
                .statusCode(equalTo(404));
    }

    //Amend with put with a non existent id
    @Test
    public void t23_put_badID() {
        JSONObject todo = createTodoRequest("title1", null, null);
        System.out.println("JSON From test 23 \n" + todo + "\n");

        given()
                .body(todo.toJSONString())
                .when()
                .put("/todos/1000")
                .then()
                .assertThat()
                .statusCode(equalTo(404));
    }

    //Delete with non existent id
    @Test
    public void t24_delete_badID() {

        RestAssured
                .when()
                .delete("/todos/200")
                .then()
                .assertThat()
                .statusCode(equalTo(404));
    }

    //Head with non existent id
    @Test
    public void t25_head_badID() {

        RestAssured.head("/todos/700")
                .then()
                .assertThat()
                .statusCode(equalTo(404));

    }

    //Test will wrongly formed json
    @Test
    public void t26_malformed_json() {

        JSONObject badRequest = new JSONObject();
        badRequest.put("titttle", "badAttribute");
        System.out.println("JSON From test 26 \n" + badRequest + "\n");

        given()
                .body(badRequest.toJSONString())
                .when()
                .post("/todos")
                .then()
                .assertThat()
                .statusCode(equalTo(400));
    }

    //Test with wrongly formed xml
    @Test
    public void t27_malformed_xml() {

        given()
                .body("<xmls><tittle><//xml>")
                .when()
                .post("/todos")
                .then()
                .assertThat()
                .statusCode(equalTo(400));
    }

    /**
     * Description:
     * This method will create and format a JSONObject that will be used to create todos
     *
     * @param title
     * @param description
     * @param doneStatus
     * @return reques
     */
    private JSONObject createTodoRequest(String title, String description, Boolean doneStatus) {

        JSONObject request = new JSONObject();
        if (title != null) {
            request.put("title", title);
        }
        if (description != null) {
            request.put("description", description);
        }
        if (doneStatus != null) {
            request.put("doneStatus", doneStatus);
        }
        return request;
    }
}
