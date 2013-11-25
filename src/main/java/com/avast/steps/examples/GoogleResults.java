package com.avast.steps.examples;

import java.util.List;

/**
 * User: zslajchrt
 * Date: 11/24/13
 * Time: 1:50 PM
 */
public class GoogleResults {

    private ResponseData responseData;
    private String responseDetails;
    private int responseStatus;

    public ResponseData getResponseData() {
        return responseData;
    }

    public void setResponseData(ResponseData responseData) {
        this.responseData = responseData;
    }

    public String getResponseDetails() {
        return responseDetails;
    }

    public void setResponseDetails(String responseDetails) {
        this.responseDetails = responseDetails;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String toString() {
        return "ResponseData[" + responseData + "]";
    }

    public static class ResponseData {
        private List<Result> results;

        public List<Result> getResults() {
            return results;
        }

        public void setResults(List<Result> results) {
            this.results = results;
        }

        public String toString() {
            return "Results[" + results + "]";
        }
    }

    public static class Result {
        private String url;

//        "title": "Caterham shows off its first batch of motorcycles, and they look \u003cb\u003e...\u003c/b\u003e",
//                "titleNoFormatting": "Caterham shows off its first batch of motorcycles, and they look ...",
//                "postUrl": "http://www.stuff.tv/caterham-unveils-trio-gorgeous-motorbikes/news",
//                "content": "First up is the Brutus 750, a burly off-roader that the company describes as \u0026quot;the SUV of motorbikes\u0026quot; and as \u0026quot;surprisingly nimble to ride\u0026quot;; the world\u0026#39;s first motorcycle with an automatic transmission, it can be ridden on almost any \u003cb\u003e...\u003c/b\u003e",
//                "author": "unknown",
//                "blogUrl": "http://www.stuff.tv/",
//                "publishedDate": "Wed, 06 Nov 2013 07:15:28 -0800"

        private String title;
        private String postUrl;
        private String content;
        private String author;
        private String blogUrl;
        private String publishedDate;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPostUrl() {
            return postUrl;
        }

        public void setPostUrl(String postUrl) {
            this.postUrl = postUrl;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getBlogUrl() {
            return blogUrl;
        }

        public void setBlogUrl(String blogUrl) {
            this.blogUrl = blogUrl;
        }

        public String getPublishedDate() {
            return publishedDate;
        }

        public void setPublishedDate(String publishedDate) {
            this.publishedDate = publishedDate;
        }

        @Override
        public String toString() {
            return "title:" + title +
            ", postUrl" + postUrl +
            ", content" + content +
            ", author" + author +
            ", blogUrl" + blogUrl +
            ", publishedDate" + publishedDate;
        }
    }
}