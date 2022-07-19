package fr.antoninruan.cellarmanager.utils.mobile_sync;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import fr.antoninruan.cellarmanager.MainApp;
import fr.antoninruan.cellarmanager.model.*;
import fr.antoninruan.cellarmanager.utils.DialogUtils;
import fr.antoninruan.cellarmanager.utils.Saver;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Antonin Ruan
 */
class WebServer implements HttpHandler {

    private static final Pattern pagePattern = Pattern.compile("^(bottles|compartments)(\\/(([0-9])*))?", Pattern.CASE_INSENSITIVE);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {

            HttpMethod method = HttpMethod.valueOf(exchange.getRequestMethod());

            String requestURI = exchange.getRequestURI().toString().replaceFirst("/", "");

            Headers responseHeaders = exchange.getResponseHeaders();
            responseHeaders.add("Server", "CaveManager MobileSync/" + MobileSyncManager.VERSION);

            Matcher matcher = pagePattern.matcher(requestURI);

            if(!matcher.find()) {

                JsonObject object = new JsonObject();
                object.addProperty("message", "Not found");

                sendResponse(method, exchange, 404, object);

                return;
            }

            String path = matcher.group(1).toLowerCase();
            String group3 = matcher.group(3);

            Headers requestHeaders = exchange.getRequestHeaders();
            switch (path) {
                case "bottles" -> {
                    if(group3 != null) {
                        if (method == HttpMethod.GET || method == HttpMethod.HEAD) {
                            int id = Integer.parseInt(group3.toLowerCase());

                            Bottle target = MainApp.getBottles().get(id);

                            if(target == null) {
                                JsonObject response = new JsonObject();
                                response.addProperty("message", "Bottle with 'id'=" + id + " not found");

                                sendResponse(method, exchange, 404, response);
                            } else
                                sendResponse(method, exchange, 200, target.toJson());

                        } else {
                            responseHeaders.add("Allow", "GET, HEAD");
                            JsonObject response = new JsonObject();
                            response.addProperty("message", "Method not allowed");
                            response.add("method_allowed", JsonParser.parseString("[GET, HEAD]"));

                            sendResponse(method, exchange, 405, response);
                        }
                    }else if (method == HttpMethod.GET || method == HttpMethod.HEAD) {
                        MainApp.saveFiles();

                        sendResponse(method, exchange, 200, JsonParser.parseReader(new FileReader(MainApp.getBottleFile())));
                    } else if (method == HttpMethod.POST) {

                        if(!(requestHeaders.containsKey("Content-type") && requestHeaders.getFirst("Content-type").equals("application/json"))) {
                            responseHeaders.add("Accept", "application/json");

                            JsonObject response = new JsonObject();
                            response.addProperty("message", "Content type not accepted");

                            sendResponse(method, exchange, 415, response);
                        } else {
                            JsonObject bottle = JsonParser.parseReader(new InputStreamReader(exchange.getRequestBody())).getAsJsonObject();

                            String name = bottle.has("name") ? bottle.get("name").getAsString() : "";
                            String region = bottle.has("region") ? bottle.get("region").getAsString() : "";
                            String edition = bottle.has("edition") ? bottle.get("edition").getAsString() : "";
                            String domain = bottle.has("domain") ? bottle.get("domain").getAsString() : "";
                            String comment = bottle.has("comment") ? bottle.get("comment").getAsString() : "";
                            int year = bottle.has("year") ? bottle.get("year").getAsInt() : 1950;
                            int consumerYear = bottle.has("consume_year") ? bottle.get("consume_year").getAsInt() : year;
                            String wineType = bottle.has("wine_type") ? bottle.get("wine_type").getAsString() : WineType.AUTRES.toString();

                            BottleInfo bottleInfo = new BottleInfo(name, region, domain, edition, comment, year, consumerYear, WineType.valueOf(wineType));

                            sendResponse(method, exchange, 201, bottleInfo.createBottle().toJson());
                        }
                    } else if (method == HttpMethod.PUT) {

                        if(!(requestHeaders.containsKey("Content-type") && requestHeaders.getFirst("Content-type").equals("application/json"))) {
                            responseHeaders.add("Accept", "application/json");

                            JsonObject response = new JsonObject();
                            response.addProperty("message", "Content type not accepted");
                            response.add("content_type_accepted", JsonParser.parseString("[application/json]"));

                            sendResponse(method, exchange, 415, response);
                        } else {

                            JsonObject request = JsonParser.parseReader(new InputStreamReader(exchange.getRequestBody())).getAsJsonObject();

                            if(request.has("id")) {

                                int id = request.get("id").getAsInt();

                                Bottle modifying = MainApp.getBottles().get(id);

                                if (modifying == null) {
                                    JsonObject response = new JsonObject();
                                    response.addProperty("message", "Bottle with 'id'=" + id + " not found");

                                    sendResponse(method, exchange, 404, response);
                                    return;
                                }

                                if(request.has("name"))
                                    modifying.setName(request.get("name").getAsString());

                                if(request.has("region"))
                                    modifying.setRegion(request.get("region").getAsString());

                                if(request.has("edition"))
                                    modifying.setEdition(request.get("edition").getAsString());

                                if(request.has("domain"))
                                    modifying.setDomain(request.get("domain").getAsString());

                                if(request.has("comment"))
                                    modifying.setComment(request.get("comment").getAsString());

                                if(request.has("year"))
                                    modifying.setYear(request.get("year").getAsInt());

                                if(request.has("consume_year"))
                                    modifying.setConsumeYear(request.get("consume_year").getAsInt());

                                if(request.has("wine_type"))
                                    modifying.setType(WineType.valueOf(request.get("wine_type").getAsString()));

                                Saver.doChange();

                                sendResponse(method, exchange, 200, modifying.toJson());


                            }  else {

                                JsonObject response = new JsonObject();
                                response.addProperty("message", "Missing key 'id' in request");

                                sendResponse(method, exchange, 400, response);

                            }

                        }

                    } else if (method == HttpMethod.DELETE) {

                        if(!(requestHeaders.containsKey("Content-type") && requestHeaders.getFirst("Content-type").equals("application/json"))) {
                            responseHeaders.add("Accept", "application/json");

                            JsonObject response = new JsonObject();
                            response.addProperty("message", "Content type not accepted");
                            response.add("content_type_accepted", JsonParser.parseString("[application/json]"));

                            sendResponse(method, exchange, 415, response);
                        } else {

                            JsonObject request = JsonParser.parseReader(new InputStreamReader(exchange.getRequestBody())).getAsJsonObject();

                            if(request.has("id")) {

                                Bottle removed = MainApp.getBottles().remove(request.get("id").getAsInt());
                                Saver.doChange();

                                sendResponse(method, exchange, 200, removed == null ? new JsonObject() : removed.toJson());

                            }  else {

                                JsonObject response = new JsonObject();
                                response.addProperty("message", "Missing key 'id' in request");

                                sendResponse(method, exchange, 400, response);

                            }

                        }

                    } else {

                        responseHeaders.add("Allow", "GET, HEAD, POST, PUT, DELETE");
                        JsonObject response = new JsonObject();
                        response.addProperty("message", "Method not allowed");
                        response.add("method_allowed", JsonParser.parseString("[GET, HEAD, POST, PUT, DELETE]"));

                        sendResponse(method, exchange, 405, response);

                    }
                }
                case "compartments" -> {
                    if (group3 != null) {
                        if(method == HttpMethod.GET || method == HttpMethod.HEAD) {
                            int id = Integer.parseInt(group3);

                            Compartment target = MainApp.getCompartements().get(id);
                            if(target == null) {
                                JsonObject response = new JsonObject();
                                response.addProperty("message", "Bottle with 'id'=" + id + " not found");

                                sendResponse(method, exchange, 404, response);
                            } else {
                                sendResponse(method, exchange, 200, target.toJson());
                            }

                        } else {
                            responseHeaders.add("Allow", "GET, HEAD");
                            JsonObject response = new JsonObject();
                            response.addProperty("message", "Method not allowed");
                            response.add("method_allowed", JsonParser.parseString("[GET, HEAD]"));

                            sendResponse(method, exchange, 405, response);
                        }

                    } else if (method == HttpMethod.GET || method == HttpMethod.HEAD) {
                        MainApp.saveFiles();

                        sendResponse(method, exchange, 200, JsonParser.parseReader(new FileReader(MainApp.getOpenedFile())));
                    } else if (method == HttpMethod.POST) {

                        if(!(requestHeaders.containsKey("Content-type") && requestHeaders.getFirst("Content-type").equals("application/json"))) {
                            responseHeaders.add("Accept", "application/json");

                            JsonObject response = new JsonObject();
                            response.addProperty("message", "Content type not accepted");
                            response.add("content_type_accepted", JsonParser.parseString("[application/json]"));

                            sendResponse(method, exchange, 415, response);
                        } else {

                            JsonObject request = JsonParser.parseReader(new InputStreamReader(exchange.getRequestBody())).getAsJsonObject();
                            if (!request.has("row")) {
                                JsonObject response = new JsonObject();
                                response.addProperty("message", "Missing key 'row' in request");
                                sendResponse(method, exchange, 400, response);
                                return;
                            }

                            if (!request.has("column")) {
                                JsonObject response = new JsonObject();
                                response.addProperty("message", "Missing key 'column' in request");
                                sendResponse(method, exchange, 400, response);
                                return;
                            }

                            int row = request.get("row").getAsInt(), column = request.get("column").getAsInt();
                            String name = request.has("name") ? request.get("name").getAsString() : "Etagère";
                            int index = request.has("index") ? request.get("index").getAsInt() : MainApp.getCompartements().size();
                            if(index > MainApp.getCompartements().size() || index < 0)
                                index = MainApp.getCompartements().size();

                            CompartmentInfo compartmentInfo = new CompartmentInfo(name, row, column, index);
                            sendResponse(method, exchange, 201, compartmentInfo.createCompartment().toJson());

                        }

                    } /*else if (method == HttpMethod.PUT) {

                        if(!(requestHeaders.containsKey("Content-type") && requestHeaders.getFirst("Content-type").equals("application/json"))) {
                            responseHeaders.add("Accept", "application/json");

                            JsonObject response = new JsonObject();
                            response.addProperty("message", "Content type not accepted");
                            response.add("content_type_accepted", JsonParser.parseString("[application/json]"));

                            sendResponse(method, exchange, 415, response);
                        } else {

                            //TODO Modification des étagères

                        }
                    }*/ else if (method == HttpMethod.DELETE) {
                        if(!(requestHeaders.containsKey("Content-type") && requestHeaders.getFirst("Content-type").equals("application/json"))) {
                            responseHeaders.add("Accept", "application/json");

                            JsonObject response = new JsonObject();
                            response.addProperty("message", "Content type not accepted");
                            response.add("content_type_accepted", JsonParser.parseString("[application/json]"));

                            sendResponse(method, exchange, 415, response);
                        } else {

                            JsonObject request = JsonParser.parseReader(new InputStreamReader(exchange.getRequestBody())).getAsJsonObject();
                            if(!request.has("id")) {
                                JsonObject response = new JsonObject();
                                response.addProperty("message", "Missing key 'id' in request");
                                sendResponse(method, exchange, 400, response);
                                return;
                            }

                            sendResponse(method, exchange, 200, MainApp.removeCompartement(request.get("id").getAsInt()).toJson());

                        }
                    } else {
                        responseHeaders.add("Allow", "GET, HEAD, POST, DELETE");
                        JsonObject response = new JsonObject();
                        response.addProperty("message", "Method not allowed");
                        response.add("method_allowed", JsonParser.parseString("[GET, HEAD, POST, DELETE]"));

                        sendResponse(method, exchange, 405, response);
                    }
                }
            }

        }  catch (Exception e) {

            JsonObject response = new JsonObject();
            response.addProperty("class", e.getClass().toString());
            response.addProperty("message", e.getMessage());
            response.addProperty("stacktrace", Arrays.toString(e.getStackTrace()));

            sendResponse(HttpMethod.valueOf(exchange.getRequestMethod()), exchange, 500, response);

            DialogUtils.sendErrorWindow(e);
        }
    }

    private void sendResponse(HttpMethod method, HttpExchange exchange, int statusCode, JsonElement element) throws IOException {
        byte[] response = element.toString().getBytes(StandardCharsets.UTF_8);
        int length = response.length;

        exchange.getResponseHeaders().set("Content-type", "application/json");
        exchange.getResponseHeaders().set("Content-length", String.valueOf(length));

        exchange.sendResponseHeaders(statusCode, method == HttpMethod.HEAD ? -1 : length);

        if (method != HttpMethod.HEAD) {
            exchange.getResponseBody().write(response);
            exchange.getResponseBody().flush();
        }

        exchange.close();
    }

}
