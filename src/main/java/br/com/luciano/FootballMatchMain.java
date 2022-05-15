package br.com.luciano;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class FootballMatchMain {

    public static void main(String[] args) throws IOException {
        FootballMatchMain match = new FootballMatchMain();
        System.out.println(match.totalGoals(2011, "Barcelona"));
        System.out.println(match.totalGoals(2014, "Chelsea"));

        System.out.println(match.winnerGoals(2014, "English Premier League"));

        System.out.println(match.drawMatches(2011));
    }

    private static final String MATCH_URL = "https://jsonmock.hackerrank.com/api/football_matches";
    private static final String COMPETITION_URL = "https://jsonmock.hackerrank.com/api/football_competitions";

    public int drawMatches(int year) throws IOException {

        int maxGoal = 10;
        int totalMatch = 0;

        for (int goal = 0; goal <= maxGoal; goal++) {
            totalMatch += getDrawMatchByGoals(MATCH_URL + "?year=" + year, goal);
        }

        return totalMatch;
    }

    private int getDrawMatchByGoals(String url, int goal) throws IOException {

        String endpoint = String.format(url + "&team1goals=%d&team2goals=%d", goal, goal);
        String response = getResponsePerPage(endpoint, 1);

        JsonObject res = new Gson().fromJson(response, JsonObject.class);
        return res.get("total").getAsInt();
    }

    public int winnerGoals(int year, String name) throws IOException {
        String url = String.format(COMPETITION_URL + "?year=%d&name=%s", year, URLEncoder.encode(name, StandardCharsets.UTF_8));
        String teamName = getWinnerTeamName(url);
        System.out.println("Winner team :" + teamName);
        return totalGoals(year, teamName);
    }

    private String getWinnerTeamName(String url) throws IOException {

        String response = getResponsePerPage(url, 1);

        JsonObject jsonResponse = new Gson().fromJson(response, JsonObject.class);
        JsonElement e = jsonResponse.getAsJsonArray("data").get(0);

        return e.getAsJsonObject().get("winner").getAsString();
    }

    public int totalGoals(int year, String team) throws IOException {

        String team1Url = String.format(MATCH_URL + "?year=%d&team1=%s", year, URLEncoder.encode(team, StandardCharsets.UTF_8));
        String team2Url = String.format(MATCH_URL + "?year=%d&team2=%s", year, URLEncoder.encode(team, StandardCharsets.UTF_8));

        return getTeamGoals(team1Url, "team1", 1, 0) + getTeamGoals(team2Url, "team2", 1, 0);
    }

    private int getTeamGoals(String teamUrl, String teamtype, int page, int totalGoals) throws IOException {

        String response = getResponsePerPage(teamUrl, page);

        JsonObject jsonResponse = new Gson().fromJson(response, JsonObject.class);
        int totalPages = jsonResponse.get("total_pages").getAsInt();
        JsonArray data = jsonResponse.getAsJsonArray("data");
        for (JsonElement e : data) {
            totalGoals += e.getAsJsonObject().get(teamtype + "goals").getAsInt();
        }

        return totalPages == page ? totalGoals : getTeamGoals(teamUrl, teamtype, page + 1, totalGoals);
    }

    private String getResponsePerPage(String endpoint, int page) throws IOException {

        System.out.println(String.format(" URL: %s and page: %d", endpoint, page));

        URL url = new URL(endpoint + "&page=" + page);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.addRequestProperty("Content-Type", "application/json");

        int status = con.getResponseCode();
        if (status < 200 || status >= 300) {
            throw new IOException("Error in reading data with status:" + status);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String response;
        StringBuilder sb = new StringBuilder();
        while ((response = br.readLine()) != null) {
            sb.append(response);
        }

        br.close();
        con.disconnect();

        return sb.toString();
    }
}
