# 1 Base Images (OS) Develepor will give all information

FROM openjdk:17-jdk-slim

# 2 working directory for the app

WORKDIR /app

# 3 copy the code form your HOST to your container working directory

COPY src/Main.java  /app/Main.java

COPY quotes.txt quotes.txt

# 4 Run the commands to install libs or to compile code 

RUN javac Main.java

# 5  Expose the port

EXPOSE 8000

#6 Server the app/ keep it running

CMD ["java","Main"]
