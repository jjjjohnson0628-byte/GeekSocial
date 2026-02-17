FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy all Java source files and dependencies
COPY *.java ./
COPY lib/ ./lib/

# Compile all Java files
RUN javac -cp "lib/*" *.java

# Expose port (Render will set PORT env variable)
EXPOSE 8080

# Run the server
CMD java -cp ".:lib/*" SocialAppServer
