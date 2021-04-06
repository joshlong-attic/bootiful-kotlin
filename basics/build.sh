mvn -DskipTests=true clean package spring-boot:build-image && \
  docker run -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal/orders docker.io/library/basics:0.0.1-SNAPSHOT
