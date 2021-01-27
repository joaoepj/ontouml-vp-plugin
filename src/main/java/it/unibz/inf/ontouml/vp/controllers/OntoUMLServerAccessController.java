package it.unibz.inf.ontouml.vp.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vp.plugin.view.IDialogHandler;
import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.model.ProjectConfigurations;
import it.unibz.inf.ontouml.vp.utils.ViewManagerUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.stream.Collectors;

/**
 * Class responsible for making requests to the OntoUML Server based on standard end points and
 * configured server URL.
 *
 * @author Claudenir Fonseca
 * @author Victor Viola
 */
public class OntoUMLServerAccessController {

  private static final String TRANSFORM_GUFO_SERVICE_ENDPOINT = "/v1/transform/gufo";
  private static final String TRANSFORM_DB_SERVICE_ENDPOINT = "/v1/transform/db";
  private static final String TRANSFORM_OBDA_SERVICE_ENDPOINT = "/v1/transform/obda";
  private static final String VERIFICATION_SERVICE_ENDPOINT = "/v1/verify";
  private static final String USER_MESSAGE_BAD_REQUEST =
      "There was a internal plugin error and the verification could not be completed.";
  private static final String USER_MESSAGE_NOT_FOUND = "Unable to reach the server.";
  private static final String USER_MESSAGE_INTERNAL_ERROR = "Internal server error.";
  private static final String USER_MESSAGE_UNKNOWN_ERROR_REQUEST =
      "Error sending model verification to the server.";
  private static final String USER_MESSAGE_UNKNOWN_ERROR_RESPONSE =
      "Error receiving model verification response.";

  public static BufferedReader transformToGUFO(
      String model,
      String baseIRI,
      String format,
      String uriFormatBy,
      String inverse,
      String object,
      String analysis,
      String packages,
      String elementMapping,
      String packageMapping,
      IDialogHandler loading)
      throws Exception {
    final JsonObject optionsObj = new JsonObject();

    boolean createObjectProperty = !Boolean.parseBoolean(object);
    boolean createInverses = Boolean.parseBoolean(inverse);
    boolean preAnalysis = Boolean.parseBoolean(analysis);
    boolean prefixPackages = Boolean.parseBoolean(packages);

    optionsObj.addProperty("baseIRI", baseIRI);
    optionsObj.addProperty("format", format);
    optionsObj.addProperty("uriFormatBy", uriFormatBy);
    optionsObj.addProperty("createInverses", createInverses);
    optionsObj.addProperty("createObjectProperty", createObjectProperty);
    optionsObj.addProperty("preAnalysis", preAnalysis);
    optionsObj.addProperty("prefixPackages", prefixPackages);
    optionsObj.add("customElementMapping", new Gson().fromJson(elementMapping, JsonObject.class));
    optionsObj.add("customPackageMapping", new Gson().fromJson(packageMapping, JsonObject.class));

    final JsonObject bodyObj = new JsonObject();
    bodyObj.add("options", optionsObj);
    bodyObj.add("model", new JsonParser().parse(model).getAsJsonObject());

    final GsonBuilder builder = new GsonBuilder();
    final Gson gson = builder.serializeNulls().setPrettyPrinting().create();
    final String body = gson.toJson(bodyObj);

    final ProjectConfigurations configurations =
        Configurations.getInstance().getProjectConfigurations();
    final String url;

    if (configurations.isCustomServerEnabled()) {
      url = configurations.getServerURL() + TRANSFORM_GUFO_SERVICE_ENDPOINT;
    } else {
      url = ProjectConfigurations.DEFAULT_SERVER_URL + TRANSFORM_GUFO_SERVICE_ENDPOINT;
    }

    loading.shown();

    try {

      final HttpURLConnection request = request(url, body);
      final BufferedReader responseReader =
          request.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST
              ? new BufferedReader(new InputStreamReader(request.getInputStream()))
              : new BufferedReader(new InputStreamReader(request.getErrorStream()));

      loading.canClosed();

      switch (request.getResponseCode()) {
        case HttpURLConnection.HTTP_OK:
          if (!request.getContentType().equals("text/html")) {
            return responseReader;
          } else {
            if (ViewManagerUtils.exportToGUFOIssueDialogWithOption(
                "Server not found.", HttpURLConnection.HTTP_NOT_FOUND))
              return transformToGUFO(
                  model,
                  baseIRI,
                  format,
                  uriFormatBy,
                  inverse,
                  object,
                  analysis,
                  packages,
                  elementMapping,
                  packageMapping,
                  loading);

            System.out.println(responseReader.lines().collect(Collectors.joining()));
            new Exception("Server not found.").printStackTrace();
            return null;
          }
        case HttpURLConnection.HTTP_BAD_REQUEST:
          ViewManagerUtils.exportToGUFOIssueDialog(
              "Unable to transform the model due to an unexpected error.\n"
                  + "Please check the model for any syntactical errors.\n\n"
                  + "Warning: partially exporting models to gUFO may introduce syntactical"
                  + " errors.");
          System.out.println(responseReader.lines().collect(Collectors.joining()));
          new Exception(
                  "Unable to transform the model due to an unexpected error.\n"
                      + "Please check the model for any syntactical errors.\n\n"
                      + "Warning: partially exporting models to gUFO may introduce syntactical"
                      + " errors.")
              .printStackTrace();
          return null;
        case HttpURLConnection.HTTP_NOT_FOUND:
          if (ViewManagerUtils.exportToGUFOIssueDialogWithOption(
              "Server not found.", HttpURLConnection.HTTP_NOT_FOUND))
            return transformToGUFO(
                model,
                baseIRI,
                format,
                uriFormatBy,
                inverse,
                object,
                analysis,
                packages,
                elementMapping,
                packageMapping,
                loading);

          System.out.println(responseReader.lines().collect(Collectors.joining()));
          new Exception("Server not found.").printStackTrace();
          return null;
        case HttpURLConnection.HTTP_INTERNAL_ERROR:
          if (ViewManagerUtils.exportToGUFOIssueDialogWithOption(
              "Server error.", HttpURLConnection.HTTP_INTERNAL_ERROR))
            return transformToGUFO(
                model,
                baseIRI,
                format,
                uriFormatBy,
                inverse,
                object,
                analysis,
                packages,
                elementMapping,
                packageMapping,
                loading);

          System.out.println(responseReader.lines().collect(Collectors.joining()));
          new Exception("Server error.").printStackTrace();
          return null;
        default:
          ViewManagerUtils.exportToGUFOIssueDialog("Unexpected error.");
          throw new Exception("Unknown error");
      }
    } catch (MalformedURLException e) {
      ViewManagerUtils.exportToGUFOIssueDialog("Server error.");
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  public static String requestModelVerification(String serializedModel, IDialogHandler loading) {
    final ProjectConfigurations configurations =
        Configurations.getInstance().getProjectConfigurations();
    final String url;

    if (configurations.isCustomServerEnabled()) {
      url = configurations.getServerURL() + VERIFICATION_SERVICE_ENDPOINT;
    } else {
      url = ProjectConfigurations.DEFAULT_SERVER_URL + VERIFICATION_SERVICE_ENDPOINT;
    }

    loading.shown();

    try {

      final HttpURLConnection request = request(url, serializedModel);
      final StringBuilder response = new StringBuilder();
      final BufferedReader reader =
          request.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST
              ? new BufferedReader(new InputStreamReader(request.getInputStream()))
              : new BufferedReader(new InputStreamReader(request.getErrorStream()));

      String line = null;

      while ((line = reader.readLine()) != null) {
        response.append(line.trim());
      }

      reader.close();

      loading.canClosed();

      switch (request.getResponseCode()) {
        case HttpURLConnection.HTTP_OK:
          if (!request.getContentType().equals("text/html")) {
            return response.toString();
          } else {
            if (ViewManagerUtils.verificationFailedDialogWithOption(
                USER_MESSAGE_NOT_FOUND, HttpURLConnection.HTTP_NOT_FOUND))
              return requestModelVerification(serializedModel, loading);
          }
        case HttpURLConnection.HTTP_BAD_REQUEST:
          ViewManagerUtils.verificationFailedDialog(USER_MESSAGE_BAD_REQUEST);
          return null;
        case HttpURLConnection.HTTP_NOT_FOUND:
          if (ViewManagerUtils.verificationFailedDialogWithOption(
              USER_MESSAGE_NOT_FOUND, HttpURLConnection.HTTP_NOT_FOUND))
            return requestModelVerification(serializedModel, loading);

          return null;
        case HttpURLConnection.HTTP_INTERNAL_ERROR:
          if (ViewManagerUtils.verificationFailedDialogWithOption(
              USER_MESSAGE_INTERNAL_ERROR, HttpURLConnection.HTTP_INTERNAL_ERROR))
            return requestModelVerification(serializedModel, loading);

          return null;
        default:
          ViewManagerUtils.verificationFailedDialog(USER_MESSAGE_UNKNOWN_ERROR_RESPONSE);
          return null;
      }

    } catch (SocketException e) {
      loading.canClosed();
      ViewManagerUtils.verificationFailedDialog(USER_MESSAGE_NOT_FOUND);
      e.printStackTrace();
    } catch (IOException e) {
      loading.canClosed();
      ViewManagerUtils.verificationFailedDialog(USER_MESSAGE_UNKNOWN_ERROR_RESPONSE);
      e.printStackTrace();
    } catch (Exception e) {
      loading.canClosed();
      ViewManagerUtils.verificationFailedDialog(USER_MESSAGE_UNKNOWN_ERROR_REQUEST);
      e.printStackTrace();
    }

    return null;
  }

  private static HttpURLConnection request(String urlString, String body)
      throws MalformedURLException, IOException {
    final URL url = new URL(urlString);
    final HttpURLConnection request = (HttpURLConnection) url.openConnection();

    request.setRequestMethod("POST");
    request.setRequestProperty("Content-Type", "application/json");
    request.setReadTimeout(60000);
    request.setDoOutput(true);

    final OutputStream requestStream = request.getOutputStream();
    final byte[] requestBody = body.getBytes();

    requestStream.write(requestBody, 0, requestBody.length);
    requestStream.flush();
    requestStream.close();

    return request;
  }

  public static BufferedReader transformToDB(
      String model, String mappingStrategy, String targetDBMS, boolean isStandardizeNames)
      throws Exception {

    final JsonObject optionsObj = new JsonObject();

    optionsObj.addProperty("mappingStrategy", mappingStrategy);
    optionsObj.addProperty("targetDBMS", targetDBMS);
    optionsObj.addProperty("isStandardizeNames", isStandardizeNames);

    final JsonObject bodyObj = new JsonObject();
    bodyObj.add("options", optionsObj);
    bodyObj.add("model", new JsonParser().parse(model).getAsJsonObject());

    final GsonBuilder builder = new GsonBuilder();
    final Gson gson = builder.serializeNulls().setPrettyPrinting().create();
    final String body = gson.toJson(bodyObj);

    final ProjectConfigurations configurations =
        Configurations.getInstance().getProjectConfigurations();
    final String url;

    if (configurations.isCustomServerEnabled()) {
      url = configurations.getServerURL() + TRANSFORM_DB_SERVICE_ENDPOINT;
    } else {
      url = ProjectConfigurations.DEFAULT_SERVER_URL + TRANSFORM_DB_SERVICE_ENDPOINT;
    }

    try {
      final HttpURLConnection request = request(url, body);
      final BufferedReader responseReader =
          request.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST
              ? new BufferedReader(new InputStreamReader(request.getInputStream()))
              : new BufferedReader(new InputStreamReader(request.getErrorStream()));

      switch (request.getResponseCode()) {
        case HttpURLConnection.HTTP_OK:
          if (!request.getContentType().equals("text/html")) {
            return responseReader;
          } else {
            System.out.println(responseReader.lines().collect(Collectors.joining()));
            new Exception("Server not found.").printStackTrace();
            return null;
          }
        case HttpURLConnection.HTTP_BAD_REQUEST:
          ViewManagerUtils.exportToGUFOIssueDialog(
              "Unable to transform the model due to an unexpected error.\n"
                  + "Please check the model for any syntactical errors.\n\n"
                  + "Warning: partially exporting models to gUFO may introduce syntactical"
                  + " errors.");
          System.out.println(responseReader.lines().collect(Collectors.joining()));
          new Exception(
                  "Unable to transform the model due to an unexpected error.\n"
                      + "Please check the model for any syntactical errors.\n\n"
                      + "Warning: partially exporting models to gUFO may introduce syntactical"
                      + " errors.")
              .printStackTrace();
          return null;
        case HttpURLConnection.HTTP_NOT_FOUND:
          System.out.println(responseReader.lines().collect(Collectors.joining()));
          new Exception("Server not found.").printStackTrace();
          return null;
        case HttpURLConnection.HTTP_INTERNAL_ERROR: // 500
          System.out.println(responseReader.lines().collect(Collectors.joining()));
          ViewManagerUtils.exportToGUFOIssueDialog(
              "Oops! Something went wrong. \n"
                  + "Please check the model for any syntactical errors.\n"
              // + "If the problem persists, open a ticket with a case that simulates this problem."
              );
          new Exception("Server error").printStackTrace();
          return null;
        default:
          ViewManagerUtils.exportToGUFOIssueDialog("Unexpected error.");
          throw new Exception("Unknown error");
      }
    } catch (MalformedURLException e) {
      ViewManagerUtils.exportToGUFOIssueDialog("Server error.");
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static BufferedReader generateODBAFile(
      String model,
      String mappingStrategy,
      String targetDBMS,
      boolean isStandardizeNames,
      String baseIRI,
      boolean isGenerateSchema,
      boolean generateConnection,
      String hostName,
      String databaseName,
      String userConnection,
      String passwordConnection)
      throws Exception {

    final JsonObject optionsObj = new JsonObject();

    optionsObj.addProperty("mappingStrategy", mappingStrategy);
    optionsObj.addProperty("targetDBMS", targetDBMS);
    optionsObj.addProperty("isStandardizeNames", isStandardizeNames);
    optionsObj.addProperty("baseIri", baseIRI);
    optionsObj.addProperty("isGenerateSchema", isGenerateSchema);
    optionsObj.addProperty("generateConnection", generateConnection);
    optionsObj.addProperty("hostName", hostName);
    optionsObj.addProperty("databaseName", databaseName);
    optionsObj.addProperty("userConnection", userConnection);
    optionsObj.addProperty("passwordConnection", passwordConnection);

    final JsonObject bodyObj = new JsonObject();
    bodyObj.add("options", optionsObj);
    bodyObj.add("model", new JsonParser().parse(model).getAsJsonObject());

    final GsonBuilder builder = new GsonBuilder();
    final Gson gson = builder.serializeNulls().setPrettyPrinting().create();
    final String body = gson.toJson(bodyObj);

    final ProjectConfigurations configurations =
        Configurations.getInstance().getProjectConfigurations();
    final String url;

    if (configurations.isCustomServerEnabled()) {
      url = configurations.getServerURL() + TRANSFORM_OBDA_SERVICE_ENDPOINT;
    } else {
      url = ProjectConfigurations.DEFAULT_SERVER_URL + TRANSFORM_OBDA_SERVICE_ENDPOINT;
    }
    try {
      final HttpURLConnection request = request(url, body);
      final BufferedReader responseReader =
          request.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST
              ? new BufferedReader(new InputStreamReader(request.getInputStream()))
              : new BufferedReader(new InputStreamReader(request.getErrorStream()));

      switch (request.getResponseCode()) {
        case HttpURLConnection.HTTP_OK:
          if (!request.getContentType().equals("text/html")) {
            return responseReader;
          } else {
            System.out.println(responseReader.lines().collect(Collectors.joining()));
            new Exception("Server not found.").printStackTrace();
            return null;
          }
        case HttpURLConnection.HTTP_BAD_REQUEST:
          ViewManagerUtils.exportToGUFOIssueDialog(
              "Unable to transform the model due to an unexpected error.\n"
                  + "Please check the model for any syntactical errors.\n\n"
                  + "Warning: partially exporting models to gUFO may introduce syntactical"
                  + " errors.");
          System.out.println(responseReader.lines().collect(Collectors.joining()));
          new Exception(
                  "Unable to transform the model due to an unexpected error.\n"
                      + "Please check the model for any syntactical errors.\n\n"
                      + "Warning: partially exporting models to gUFO may introduce syntactical"
                      + " errors.")
              .printStackTrace();
          return null;
        case HttpURLConnection.HTTP_NOT_FOUND:
          System.out.println(responseReader.lines().collect(Collectors.joining()));
          new Exception("Server not found.").printStackTrace();
          return null;
        case HttpURLConnection.HTTP_INTERNAL_ERROR:
          System.out.println(responseReader.lines().collect(Collectors.joining()));
          new Exception("Server error.").printStackTrace();
          return null;
        default:
          ViewManagerUtils.exportToGUFOIssueDialog("Unexpected error.");
          throw new Exception("Unknown error");
      }
    } catch (MalformedURLException e) {
      ViewManagerUtils.exportToGUFOIssueDialog("Server error.");
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }
}
