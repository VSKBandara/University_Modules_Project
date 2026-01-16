package com.example.lms;

public class putPDF {

     public String name;
     public String url;


     public putPDF() {
     }


     public putPDF(String name, String url) {
          this.name = name;
          this.url = url;
     }


     public String getName() {
          return name;
     }

     public void setName(String name) {
          this.name = name;
     }

     public String getURL() {
          return url;
     }

     public void setURL(String url) {
          this.url = url;
     }
}
