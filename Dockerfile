# 1 Base images (OS) developer will give provide the details

FROM openjdk:17-slim

# 2 this is the working directory of the appliction

WORKDIR /app

# 3 Copy the code form our HOST to your Container

COPY src/Main.java /app/Main.java
COPY quotes.txt quotes.txt

# 4 Run the commnds to instll the librarys or to compile code

Run javac Main.java

# 5 Expose the port

EXPOSE 8000

#6 Server the app/ keep it running

CMD ["java","Main"]
