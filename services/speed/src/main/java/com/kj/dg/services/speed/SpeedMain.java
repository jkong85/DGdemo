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

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.concurrent.CompletionStage;
import java.util.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        //String hostIPAddress = app.getHostIPaddress("/opt/dg/ipaddress");
        //String hostIPAddress = app.getHostIP();
        String hostIPAddress = app.getHostIPbyShell();

        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = app.createRoute().flow(system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow,
                ConnectHttp.toHost(hostIPAddress, 8080), materializer);

        System.out.println("Server online at http://" + hostIPAddress + ":8080/\nPress RETURN to stop...");
        System.in.read(); // let it run until user presses return

        int flag = 2;
        while( flag-- > 0 ){
            Thread.sleep(5000);
            //#http-server
            System.out.println("Speed is still running ...");
            flag = 2;
        }

        binding
                .thenCompose(ServerBinding::unbind) // trigger unbinding from the port
                .thenAccept(unbound -> system.terminate()); // and shutdown when done
        //#http-server
        System.out.println("SpeedMain exited successfully");
    }

    //#main-class
    /**
     * Here you can define all the different routes you want to have served by this web server
     * Note that routes might be defined in separated classes like the current case
     */
    protected Route createRoute() {
        return userRoutes.routes();
    }

    private String getHostIPbyShell(){
        try {
            Thread.currentThread().sleep(1000);//毫秒
        } catch(Exception e){}

        String s = null;
        //String cmd = " ifconfig eth0 | awk '/inet addr/{print substr($2,6)}' >> /opt/dg/ipaddress";
        String cmd = " ifconfig eth0 ";
        try {
            // run the Unix "ps -ef" command
            Process p = Runtime.getRuntime().exec(cmd);

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");

            s = stdInput.readLine();
            s = stdInput.readLine();

            String regEx="((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)";
            Pattern pt = Pattern.compile(regEx);
            Matcher m = pt.matcher(s);

            while (m.find()) {
                String result=m.group();
                System.out.println(result);
                return result;
            }
        }
        catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
            return  "127.0.0.1";
        }

        return "127.0.0.1";
    }

    private String getHostIP()
    {
        NetworkInterface iface = null;
        String ethr;
        String myip = "";
        String regex = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +	"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        try
        {
            for(Enumeration ifaces = NetworkInterface.getNetworkInterfaces();ifaces.hasMoreElements();)
            {
                iface = (NetworkInterface)ifaces.nextElement();
                ethr = iface.getDisplayName();
                System.out.println("Interface:" + ethr.toString());

                //if (Pattern.matches("eth[0-9]", ethr))
                if (Pattern.matches("eth0", ethr))
                {
                    InetAddress ia = null;
                    for(Enumeration ips = iface.getInetAddresses();ips.hasMoreElements();)
                    {
                        ia = (InetAddress)ips.nextElement();
                        if (Pattern.matches(regex, ia.getCanonicalHostName()))
                        {
                            myip = ia.getCanonicalHostName();
                            return myip;
                        }
                    }
                }
            }
        } catch (SocketException e){}
        System.out.println("IP:" + myip.toString());
        return myip;
    }

    private String getHostIPaddress(String file){
        String line;
        List<String> lines = new ArrayList<>();
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(file);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);

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
