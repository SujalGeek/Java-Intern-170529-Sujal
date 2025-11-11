package com.spring.ai.second.project.entity;

import java.time.LocalDate;

public class Tut {
    private String title;
    private String content;
    private String createdYear;

    public Tut(){

    }

    public Tut(String title, String createdYear, String content) {
        this.title = title;
        this.createdYear = createdYear;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedYear() {
        return createdYear;
    }

    public void setCreatedYear(String createdYear) {
        this.createdYear = createdYear;
    }

    @Override
    public String toString() {
        return "Tut{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", createdYear='" + createdYear + '\'' +
                '}';
    }
}
