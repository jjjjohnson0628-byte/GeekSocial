FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy only the server files (no JavaFX dependency)
COPY SocialAppServer.java NetworkClient.java ./
COPY lib/ ./lib/

# Compile only the server files (no JavaFX needed for server)
RUN javac -cp "lib/*" NetworkClient.java SocialAppServer.java

# Expose port
EXPOSE 8080

# Run the server
CMD java -cp ".:lib/*" SocialAppServer
