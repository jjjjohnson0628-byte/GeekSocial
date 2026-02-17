FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy only the main Java source files and dependencies
COPY SocialApp.java SocialAppServer.java NetworkClient.java ./
COPY lib/ ./lib/

# Compile only the main Java files
RUN javac -cp "lib/*" SocialApp.java SocialAppServer.java NetworkClient.java

# Expose port (Render will set PORT env variable)
EXPOSE 8080

# Run the server
CMD java -cp ".:lib/*" SocialAppServer
