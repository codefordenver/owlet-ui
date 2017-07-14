FROM java:8-alpine
MAINTAINER dviramontes | CODEFORDENVER.COM <dviramontes@gmail.com>

ADD target/uberjar/owlet.jar /owlet/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/owlet/app.jar"]
