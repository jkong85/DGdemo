package com.kj.dg.services.speed;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import com.typesafe.config.ConfigFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.CompletionStage;
import java.util.*;
import java.util.ArrayList;

/**
 * Created by jkong on 6/22/18.
 */
public class SpeedMain extends AllDirectives {

    // set up ActorSystem and other dependencies here
    private final UserRoutes userRoutes;

    public SpeedMain(ActorSystem system, ActorRef userRegistryActor) {
        userRoutes = new UserRoutes(system, userRegistryActor);
    }
    //#main-class

    public static void main(String[] args) throws Exception {
        //#server-bootstrapping
        // boot up server using the route as defined below
        ActorSystem system = ActorSystem.create("SpeedMainActor", ConfigFactory.load());

        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        //#server-bootstrapping

        ActorRef userRegistryActor = system.actorOf(UserRegistryActor.props(), "userRegistryActor");

        //#http-server
        //In order to access all directives we need an instance where the routes are define.
        SpeedMain app = new SpeedMain(system, userRegistryActor);

        String hostIPAddress = app.getHostIPaddress("/opt/dg/ipaddress");

        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = app.createRoute().flow(system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow,
                ConnectHttp.toHost(hostIPAddress, 8080), materializer);

        System.out.println("Server online at http://" + hostIPAddress + ":8080/\nPress RETURN to stop...");
        System.in.read(); // let it run until user presses return

        binding
                .thenCompose(ServerBinding::unbind) // trigger unbinding from the port
                .thenAccept(unbound -> system.terminate()); // and shutdown when done
        //#http-server
    }

    //#main-class
    /**
     * Here you can define all the different routes you want to have served by this web server
     * Note that routes might be defined in separated classes like the current case
     */
    protected Route createRoute() {
        return userRoutes.routes();
    }

    private String getHostIPaddress(String file){
        String line;
        List<String> lines = new ArrayList<>();
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(file);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            System.out.println("step 1");

            while((line = bufferedReader.readLine()) != null) {
                lines.add(line.toString());
                System.out.println(line);
            }
            bufferedReader.close();
            if(lines.size()>0){
                return lines.get(lines.size()-1); 
            }
            // Always close files.
        } catch(FileNotFoundException ex) {
            System.out.println( "Unable to open file '" + file+ "'");
            return "127.0.0.1";
        } catch(IOException ex) {
            System.out.println( "Error reading file '" + file+ "'");
            return "127.0.0.1";
        }
        return "127.0.0.1";
    }
}
