package fr.antoninruan.cellarmanager.utils.mobile_sync;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.antoninruan.cellarmanager.MainApp;
import fr.antoninruan.cellarmanager.model.Bottle;
import fr.antoninruan.cellarmanager.model.Compartment;
import fr.antoninruan.cellarmanager.model.Spot;
import fr.antoninruan.cellarmanager.model.WineType;
import fr.antoninruan.cellarmanager.utils.change.Change;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * @author Antonin Ruan
 */
class WebServer {

    private static ServerSocket serverSocket;
    protected static boolean run;
    private static final ArrayList<Bottle> bottlesChanged = new ArrayList <>();
    private static final ArrayList<Change> changes = new ArrayList <>();


    public static void startWebServer() {
        run = true;

        Thread serverThread = new Thread(() -> {
            try {
                serverSocket = new ServerSocket(MobileSyncManager.PORT);
                serverSocket.setSoTimeout(1000);
                while (run) {
                    handleConnection(serverSocket);
                }
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


        });
        serverThread.start();

    }

    private static void handleConnection(ServerSocket serverSocket) {
        try (Socket socket = serverSocket.accept();
             PrintWriter out = new PrintWriter(socket.getOutputStream());
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))){

            StringTokenizer tokenizer = new StringTokenizer(reader.readLine());

            String method = tokenizer.nextToken().toUpperCase();

            if(method.equals("GET") || method.equals("HEAD")) {

                String request = tokenizer.nextToken().replace("%22", "\"").replace("%20", " ");
                String[] parsedRequest = request.split("\\?");

                String fileName = parsedRequest[0];
                String param = parsedRequest.length >= 2 ? parsedRequest[1] : "";

                try {
                    if(fileName.endsWith("bottle")) {
                        if(!param.isEmpty()) {

                            String[] params = param.split("&");
                            for(String s : params) {

                                String type = s.split("=")[0];
                                String arg = s.split("=").length > 1 ? s.split("=")[1] : "{}";

                                System.out.println("type=" + type + ", arg=" + arg);

                                if(type.equalsIgnoreCase("change")) {
                                    JsonObject change = JsonParser.parseString(arg).getAsJsonObject();
                                    if(change.has("id")) {
                                        int id = change.get("id").getAsInt();
                                        Bottle toChange = MainApp.getBottles().get(id).clone();
                                        for(String key : change.keySet()) {
                                            if(!key.equalsIgnoreCase("id")) {
                                                switch (key.toLowerCase()) {
                                                    case "name":
                                                        toChange.setName(change.get(key).getAsString());
                                                        break;

                                                    case "region":
                                                        toChange.setRegion(change.get(key).getAsString());
                                                        break;

                                                    case "edition":
                                                        toChange.setEdition(change.get(key).getAsString());
                                                        break;

                                                    case "domain":
                                                        toChange.setDomain(change.get(key).getAsString());
                                                        break;

                                                    case "year":
                                                        toChange.setYear(change.get(key).getAsInt());
                                                        break;

                                                    case "consume_year":
                                                        toChange.setConsumeYear(change.get(key).getAsInt());
                                                        break;

                                                    case "comment":
                                                        toChange.setComment(change.get(key).getAsString());
                                                        break;

                                                    default:
                                                        break;
                                                }
                                            }
                                        }
                                        System.out.println("id=" + toChange.getId() + ", bottle=" + toChange);
                                        bottlesChanged.add(toChange);
                                    }
                                } else if(type.equalsIgnoreCase("add")) {
                                    JsonObject add = JsonParser.parseString(arg).getAsJsonObject();
                                    if(!add.has("id")) {
                                        String name = add.has("name") ? add.get("name").getAsString() : "";
                                        String region = add.has("region") ? add.get("region").getAsString() : "";
                                        String domain = add.has("domain") ? add.get("domain").getAsString() : "";
                                        String edition = add.has("edition") ? add.get("edition").getAsString() : "";
                                        String comment = add.has("comment") ? add.get("comment").getAsString() : "";
                                        int year = add.has("year") ? add.get("year").getAsInt() : 1980;
                                        int consumeYear = add.has("consumer_year") ? add.get("consumer_year").getAsInt() : year;
                                        WineType type1 = add.has("type") ? WineType.valueOf(add.get("type").getAsString()) : WineType.ROUGE;

                                        Bottle bottle = new Bottle(name, region, edition, domain, comment, year, consumeYear, type1);
                                        bottlesChanged.add(bottle);
                                    }
                                } else if(type.equalsIgnoreCase("save")) {
                                    for(Bottle bottle : bottlesChanged) {
                                        System.out.println("id=" + bottle.getId() + ", bottle=" + bottle.toString());
                                        MainApp.getBottles().put(bottle.getId(), bottle);
                                    }
                                    bottlesChanged.clear();
                                } else if(type.equalsIgnoreCase("cancel")) {
                                    bottlesChanged.clear();
                                }
                            }

                        }

                        MainApp.saveFiles();

                        out.println("HTTP/1.1 200 OK");
                        out.println("Server: CaveManager MobileSync: " + MobileSyncManager.VERSION);
                        out.println("Date: " + new Date());
                        out.println("Content-type: text/plain");
                        out.println("Content-length: " + MainApp.getBottleFile().length());
                        out.println();
                        out.flush();

                        BufferedReader reader1 = new BufferedReader(new FileReader(MainApp.getBottleFile()));
                        String line = reader1.readLine();
                        out.println(line);
                        out.flush();
                    } else if(fileName.endsWith("cave")) {

                        if(!param.isEmpty()) {
                            String[] params = param.split("&");
                            for(String s : params) {

                                String type = s.split("=")[0];
                                String arg = s.split("=").length > 1 ? s.split("=")[1] : "{}";

                                System.out.println("type=" + type + ", arg=" + arg);

                                if(type.equalsIgnoreCase("remove") || type.equalsIgnoreCase("add")) {

                                    //arg de la forme = {"compartement_id":id, "spot_id":id, ("bottle_id":id)} (dans le cas du add)

                                    JsonObject jsonArg = JsonParser.parseString(arg).getAsJsonObject();

                                    if(jsonArg.has("compartement_id") && jsonArg.has("spot_id")) {

                                        Compartment compartment = MainApp.getCompartements().get(jsonArg.get("compartement_id").getAsInt());
                                        int spotId = jsonArg.get("spot_id").getAsInt();
                                        int row = spotId / 100;
                                        int column = spotId - (row * 100);
                                        Spot spot = compartment.getSpots()[row][column];

                                        if(type.equalsIgnoreCase("remove")) {
                                            Change change = new Change(Change.ChangeType.SPOT_EMPTIED, spot, spot, MainApp.getBottles().get(spot.getBottle().getId()));
                                            changes.add(change);
                                            spot.setBottle(null);
                                        } else if(jsonArg.has("bottle_id")) {
                                            Change change = new Change(Change.ChangeType.SPOT_FILLED, spot, spot, null);
                                            changes.add(change);
                                            spot.setBottle(MainApp.getBottles().get(jsonArg.get("bottle_id").getAsInt()));
                                        }
                                    }

                                } else if(type.equalsIgnoreCase("save")) {
                                    changes.clear();
                                } else if(type.equalsIgnoreCase("cancel")) {
                                    for(Change change : changes) {
                                        change.undo();
                                    }
                                }

                            }
                        }

                        MainApp.saveFiles();

                        writeHttpHeader(out, "200 OK", "application/json", MainApp.getOpenedFile().length());

                        BufferedReader reader1 = new BufferedReader(new FileReader(MainApp.getOpenedFile()));
                        socket.getOutputStream().write(reader1.readLine().getBytes(StandardCharsets.UTF_8));
                        socket.getOutputStream().flush();


                    } else {
                        out.println("HTTP/1.1 200 OK");
                        out.println("Server: CaveManager MobilySync: " + MobileSyncManager.VERSION);
                        out.println("Date: " + new Date());
                        out.println("Content-type: text/plain");

                        String response = InetAddress.getLocalHost().getHostName();

                        out.println("Content-length: " + response.length());
                        out.println();
                        out.flush();

                        socket.getOutputStream().write(response.getBytes(StandardCharsets.UTF_8));
                        socket.getOutputStream().flush();
                    }
                } catch (Exception e) {
                    out.println("HTTP/1.1 500 Internal Server Error");
                    out.println("Server: CaveManager MobileSync: " + MobileSyncManager.VERSION);
                    out.println("Date: " + new Date());
                    out.println("Content-type: text/plain");

                    String response = "Une erreur s'est produite lors du traitement de la requete";
                    out.println("Content-length: " + response.length());
                    out.println();
                    out.flush();

                    socket.getOutputStream().write(response.getBytes(StandardCharsets.UTF_8));
                    socket.getOutputStream().flush();
                    e.printStackTrace();
                }


            } else {

                out.println("HTTP/1.1 501 Not Implemented");
                out.println("Server: CaveManger MobileSync: " + MobileSyncManager.VERSION);
                out.println("Date: " + new Date());
                out.println("Content-type: text/plain");

                String response = "Méthode non supportée";

                out.println("Content-length: " + response.length());
                out.println();
                out.flush();

                socket.getOutputStream().write(response.getBytes(StandardCharsets.UTF_8));
                socket.getOutputStream().flush();

            }


        } catch (SocketTimeoutException ignored){

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stopWebServer() {
        run = false;
    }

    private static void writeHttpHeader(PrintWriter out, String status, String contentType, long contentLength) {
        out.println("HTTP/1.1 " + status);
        out.println("Server: CaveManager MobileSync: " + MobileSyncManager.VERSION);
        out.println("Date: " + new Date());
        out.println("Content-type: " + contentType);
        out.println("Content-length: " + contentLength);
        out.println();
        out.flush();
    }

}
