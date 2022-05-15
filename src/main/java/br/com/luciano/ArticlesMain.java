package br.com.luciano;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ArticlesMain {

    public static void main(String[] args) throws Exception {

        var titles = getArticleTitles("coloneltcb");

        titles.forEach(System.out::println);

    }

    public static List<String> getArticleTitles(String author) throws IOException {
        String url = "https://jsonmock.hackerrank.com/api/articles?author=" + author;
        List<String> titles = new ArrayList<>();

        return getTitles(url, 1, titles);
    }

    private static List<String> getTitles(String url, int page, List<String> titles) throws IOException {

        String response = getResponsePage(url, page);

        Article art = new Gson().fromJson(response, Article.class);
        int totalPages = art.getTotal_pages();

        for (Data d : art.getData()) {

            if (d.getTitle() != null) {
                titles.add(d.getTitle());
            } else if (d.getTitle() == null && d.getStory_title() != null) {
                titles.add(d.getStory_title());
            }
        }

        return totalPages == page ? titles : getTitles(url, page + 1, titles);
    }

    private static String getResponsePage(String endpoint, int page) throws IOException {

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

class Article {
    private int page;
    private int total_pages;
    private List<Data> data;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }
}

class Data {
    private String title;
    private String url;
    private String author;

    private String story_title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getStory_title() {
        return story_title;
    }

    public void setStory_title(String story_title) {
        this.story_title = story_title;
    }
}
