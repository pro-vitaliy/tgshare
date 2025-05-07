lint:
	./mvnw checkstyle:check

test:
	./mvnw test

build:
	./mvnw clean package -DskipTests

clean:
	./mvnw clean

verify:
	./mvnw clean verify
