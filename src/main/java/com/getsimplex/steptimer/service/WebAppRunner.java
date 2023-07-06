package com.getsimplex.steptimer.service;

/**
 © 2021 Sean Murdock
 * Created by sean on 8/10/2016 based on https://github.com/tipsy/spark-websocket/tree/master/src/main/java
 */


import com.getsimplex.steptimer.model.*;
import com.getsimplex.steptimer.utils.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.codec.digest.DigestUtils;
import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static spark.Spark.*;

public class WebAppRunner {
    private static Gson gson = new Gson();
    private static Logger logger = Logger.getLogger(WebAppRunner.class.getName());
    public static void main(String[] args){

        Spark.port(getHerokuAssignedPort());
        staticFileLocation("/public");
        webSocket("/socket", DeviceWebSocketHandler.class);
        webSocket("/timeruiwebsocket", TimerUIWebSocket.class);

        after((Filter) (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, PATCH, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,");
            response.header("Access-Control-Allow-Credentials", "true");
        });
        post("/contact", (req, res)->{
            try {
                EmailMessage emailMessage = gson.fromJson(req.body(), EmailMessage.class);
                SendGmail.send(emailMessage.getToAddress(), emailMessage.getMessageText(), emailMessage.getSubject(), emailMessage.getName());

                System.out.println(req.body());
                res.status(200);

            }catch(Exception e){
                return null;
            }
            return "sent";
        });
		//secure("/Applications/steptimerwebsocket/keystore.jks","password","/Applications/steptimerwebsocket/keystore.jks","password");

        //post("/sensorUpdates", (req, res)-> WebServiceHandler.routeDeviceRequest(req));
        //post("/generateHistoricalGraph", (req, res)->routePdfRequest(req, res));
        //get("/readPdf", (req, res)->routePdfRequest(req, res));
        post("/user", (req, res)-> {
            String response="Error creating user";
            try {
                response = callUserDatabase(req);
            } catch (AlreadyExistsException ae){
                res.status(409);
                System.out.println("User already exists");
            } catch (Exception e){
                res.status(500);
                System.out.println("Error creating user");
            }
            return response;
        }
            );
        patch("/user/:username",(req,res)->{//update password
            userFilter(req,res);
            User existingUser = (User) JedisData.getFromRedisMap(req.params("username"), User.class);
            String response = "";
            if (existingUser!=null){
                User userUpdate = gson.fromJson(req.body(),User.class);
                if (CreateNewUser.validatePassword(userUpdate.getPassword())){
                    existingUser.setPassword(DigestUtils.sha256Hex(userUpdate.getPassword()));
                    JedisData.updateRedisMap(existingUser,existingUser.getUserName());
                    res.status(200);
                    response = "Updated password";
                    res.body(response);
                } else{
                    res.status(400);
                    response="Password doesn't meet requirements";
                    res.body(response);
                }
            } else{
                res.status(404);//user not found
                response = "User "+req.params("username")+" not found.";
                res.body(response);
            }

            return response;
        });
        get("/validate/:token", (req,res)->SessionValidator.emailFromToken(req.params(":token")));
        get("/simulation", (req, res) -> SimulationDataDriver.getSimulationActive());
        post("/simulation", (req, res)-> MessageIntake.route(new StartSimulation(30)));
        delete("/simulation", (req, res)-> MessageIntake.route(new StopSimulation()));
        post("/complexity", (req,res)->{
            Gson gson = new Gson();
            Boolean validPassword = CreateNewUser.validatePassword(gson.fromJson(req.body(), User.class).getPassword());

            if (validPassword){
                res.status(200);
            } else{
                res.status(400);
            }
            return validPassword;
        });
        delete("/user/:username", (req,res)->{
           userFilter(req,res);
           CreateNewUser.deleteUser(req.params("username"));
           return "deleted user";
        });
        get ("/stephistory/:customer", (req, res)-> {
            try{
                userFilter(req, res);
            } catch (Exception e){
                res.redirect("/");
            }
            return StepHistory.getAllTests(req.params(":customer"));
        });
        post("/customer", (req, res)-> {
            String response;
            try {
                createNewCustomer(req, res);
                response="Successfully created customer";
            }

            catch (AlreadyExistsException ae){
                logger.info("User already exists");
                System.out.println("User already exists");
                res.status(409);
                logger.info("Error creating customer");
                response="Error creating customer";
            }

            catch (Exception e){
                logger.warning("*** Error Creating Customer: "+e.getMessage());
                System.out.println("*** Error Creating Customer: "+e.getMessage());
                res.status(500);
                response="Error creating customer";
            }
            return response;
        });

        put("/customer", (req, res)-> {
            String response;
            try {
                updateCustomer(req, res);
                response="Successfully created customer";
            }

            catch (AlreadyExistsException ae){
                logger.info("User already exists");
                System.out.println("User already exists");
                res.status(409);
                logger.info("Error creating customer");
                response="Error creating customer";
            }

            catch (Exception e){
                logger.warning("*** Error Creating Customer: "+e.getMessage());
                System.out.println("*** Error Creating Customer: "+e.getMessage());
                res.status(500);
                response="Error creating customer";
            }
            return response;
        });

        get("/customer/:phone", (req, res)-> {
          String phone =  req.params(":phone");
            Optional<User> optionalUser = Optional.empty();
            try {
              optionalUser = userFilter(req, res);
            } catch (Exception e){
                res.status(401);
                logger.warning("*** Error Finding Customer: "+e.getMessage());
                System.out.println("*** Error Finding Customer: "+e.getMessage());
                return null;
            }
            if(optionalUser.isPresent() && optionalUser.get().getPhone().equals( SendText.getFormattedPhone(phone))){
                return gson.toJson(CustomerService.getCustomerByPhone(phone));
            }
            return  null;
        });

        post("/login", (req, res)->loginUser(req, res));
        post("/twofactorlogin/:phoneNumber",(req, res) -> twoFactorLogin(req, res));
        post("/twofactorlogin", (req, res) ->{
            String response = "";
           try{
               response=OneTimePasswordService.handleRequest(req);
           } catch (NotFoundException nfe){
               res.status(404);
               response= nfe.getMessage();
           } catch (ExpiredException ee){
               res.status(401);
               response= ee.getMessage();
           } catch (Exception e){
                res.status(500);
                response = "Unexpected error";
           }
            return response;
        });
        post("/rapidsteptest", (req, res)->{
            try{
                userFilter(req, res);
            } catch (Exception e){
                res.status(401);
            }

            saveStepSession(req);
            return "Saved";
        });
        get("/riskscore/:customer",((req,res) -> {
            try{
                Optional<User> user = userFilter(req, res);
               String customer = req.params(":customer");
                if (user.isPresent() && user.get().getEmail().equals(customer)) {
                    return riskScore(req.params(":customer"));
                }
            } catch (Exception e){
                res.status(401);
                logger.info("*** Error Finding Risk Score: "+e.getMessage());
                System.out.println("*** Error Finding Risk Score: "+e.getMessage());
                throw e;
            }
           res.status(404);
            return "User not found, or user doesn't match customer";
        }));

        options("/*",
                (request, response) -> {

                    String accessControlRequestHeaders = request
                            .headers("Access-Control-Request-Headers");
                    if (accessControlRequestHeaders != null) {
                        response.header("Access-Control-Allow-Headers",
                                accessControlRequestHeaders);
                    }

                    String accessControlRequestMethod = request
                            .headers("Access-Control-Request-Method");
                    if (accessControlRequestMethod != null) {
                        response.header("Access-Control-Allow-Methods",
                                accessControlRequestMethod);
                    }

                    return "OK";
        });
        init();
    }
    private static String twoFactorLogin(Request request, Response response){
        String phoneNumber =  request.params(":phoneNumber");
        int randomNum = ThreadLocalRandom.current().nextInt(1111, 10000);
        User user=null;
        try {
            phoneNumber = SendText.getFormattedPhone(phoneNumber);
            user = FindUser.getUserByPhone(phoneNumber);
            if (user!=null){
                Long expiration = new Date().getTime()+100l * 365l * 24l *60l * 60l *1000l;//100 years
                String loginToken=TokenService.createUserTokenSpecificTimeout(user.getUserName(), expiration);
                OneTimePassword oneTimePassword = new OneTimePassword();
                oneTimePassword.setOneTimePassword(randomNum);
                if (user.getEmail().equals("scmurdock@gmail,.com")){
                    oneTimePassword.setExpirationDate(new Date(expiration));

                } else{
                    oneTimePassword.setExpirationDate(new Date(System.currentTimeMillis()+60l*30l*1000l));

                }
                oneTimePassword.setLoginToken(loginToken);
                oneTimePassword.setPhoneNumber(phoneNumber);
                OneTimePasswordService.saveOneTimePassword(oneTimePassword);

                SendText.send(phoneNumber, "STEDI OTP: "+String.valueOf(randomNum));
                response.status(200);

            } else{
                response.status(400);
                logger.info("Unable to find user with phone number: "+phoneNumber);
                System.out.println("Unable to find user with phone number: "+phoneNumber);

            }
        } catch (Exception e){
            response.status(500);
            logger.info("Error while looking up user "+phoneNumber+" "+e.getMessage());
            System.out.println("Error while looking up user "+phoneNumber+" "+e.getMessage());
        }

        if (user==null){
            return "Unable to find user with phone number: "+phoneNumber;
        } else{
            return "Ok";
        }
    }

    private static Optional<User> userFilter(Request request, Response response) throws Exception{
        String tokenString = request.headers("suresteps.session.token");

            Optional<User> user = TokenService.getUserFromToken(tokenString);//

            Boolean tokenExpired = SessionValidator.validateToken(tokenString);

            if (user.isPresent() && tokenExpired && !user.get().isLocked()){//if a user is locked, we won't renew tokens until they are unlocked
                TokenService.renewToken(tokenString);
                return user;
            }

            if (!user.isPresent()) { //Check to see if session expired
                logger.info("Invalid user token: user not found using token: "+tokenString);
                throw new Exception("Invalid user token: user not found using token: "+tokenString);
            }

            if (tokenExpired.equals(true)){
                logger.info("Invalid user token: "+tokenString+" expired");
                throw new Exception("Invalid user token: "+tokenString+" expired");
            }
        return user;
    }




    public static void createNewCustomer(Request request, Response response) throws Exception{
            CustomerService.handleRequest(request, false);
    }

    public static void updateCustomer(Request request, Response response) throws Exception{
        CustomerService.handleRequest(request, true);
    }

    private static String callUserDatabase(Request request)throws Exception{
        return CreateNewUser.handleRequest(request);
    }

    private static String loginUser(Request request, Response response) throws Exception{
        String responseText="";

        try{

            String token = responseText=LoginController.handleRequest(request);
            response.cookie("stedi-token",token);
        } catch(InvalidLoginException ile){
            response.status(401);
        }

        return responseText;

    }

    private static String riskScore(String email) throws Exception{
        return StepHistory.riskScore(email);
    }

    private static void saveStepSession(Request request) throws Exception{
        SaveRapidStepTest.save(request.body());
    }

	
    private static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return Configuration.getConfiguration().getInt("suresteps.port"); //return default port if heroku-port isn't set (i.e. on localhost)
    }

}
