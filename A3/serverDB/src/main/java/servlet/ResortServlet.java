package servlet;

import com.google.gson.Gson;
import model.Resort;
import model.Resorts;
import model.Season;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "ResortServlet", value = "/ResortServlet")
public class ResortServlet extends HttpServlet {
    private Gson gson = new Gson();


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processGetRequest(request, response);

    }

    protected void processGetRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String urlPath = request.getPathInfo();
        System.out.println("urlPath:" + urlPath);
        System.out.println("RequestURI:" + request.getRequestURI());

        if (urlPath == null || urlPath.isEmpty()) {  //TODO: null?
            getResortsResponse(response);
            return;
        }


        String getNumSkiersPattern = "/(\\d+)/seasons/(\\d+)/day/(\\d+)/skiers";
        String getSeasonsPattern = "/(\\d+)/seasons";

        Matcher matchGetNumSkiers = Pattern.compile(getNumSkiersPattern).matcher(urlPath);
        Matcher matchGetSeasons = Pattern.compile(getSeasonsPattern).matcher(urlPath);

        if (matchGetSeasons.matches()) {
            getSeasonsResponse(response);

        }

        if (matchGetNumSkiers.matches()) {
            getNumSkiersResponse(response);

        }


    }

    protected void getResortsResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        // TODO: match output format
        Resort resort1 = new Resort("Steven Pass", 1);
        Resorts resorts = new Resorts(resort1);

        String resortsJsonString = this.gson.toJson(resorts);

        PrintWriter out = response.getWriter();
        out.print(resortsJsonString);
        out.flush();
    }

    protected void getNumSkiersResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("{\n" +
                "  \"time\": \"Mission Ridge\",\n" +
                "  \"numSkiers\": 78999\n" +
                "}");
    }

    protected void getSeasonsResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("{\n" +
                "  \"seasons\": [\n" +
                "    \"2013\",\"2019\"\n" +
                "  ]\n" +
                "}");
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("posturlPath:" + request.getPathInfo());
        System.out.println("postRequestURI:" + request.getRequestURI());
        processPostRequest(request, response);
    }

    protected void processPostRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String urlPath = request.getPathInfo();

        if (urlPath == null || urlPath.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("missing paramterers");
            return;
        }

        if (!isPostUrlPathValid(urlPath)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        BufferedReader reader = request.getReader();

        String requestBody = ReadBigStringIn(reader);

        if (!isPostParametersPathValid(requestBody)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            // TODO: process url params in `urlParts`
            response.getWriter().write("Posting resort data works!");
        }
    }

    private boolean isPostUrlPathValid(String urlPath) {
        // TODO: validate the request url path according to the API spec
        String pattern = "/(\\d+)/seasons";

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        Matcher m = r.matcher(urlPath);

        return m.matches();
    }


    private boolean isPostParametersPathValid(String parameters) {
        // TODO: validate the request url path according to the API spec
        Season season = (Season) gson.fromJson(parameters.toString(), Season.class);
        String year = season.getYear();
        System.out.println("year:" + year);
        return year != null;

    }

    public String ReadBigStringIn(BufferedReader buffIn) throws IOException {
        StringBuilder everything = new StringBuilder();
        String line;
        while ((line = buffIn.readLine()) != null) {
            everything.append(line);
        }
        return everything.toString();
    }

}




