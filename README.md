# Markdown Converter

Simple cli app to convert `md` to `html` converter.
Accepts two args:
1. `MD file` as source file
2. `HTML` where to store converted result (optional). Default store file  - same directory  `target.html`

# Build and execution
1. clone or download source code
2. There are two options - local and docker
## Run locally
- requires Java 17

`java -jar converter-1.0.jar TEST.md`

`TEST.md` included as an example.

## Build using Java and Maven
- requires Java 17
- Maven 3.3.2+ version

### Build
`mvn clean install -DskipTests`
### Run 
`java -jar target/converter-1.0.jar TEST.md`

## Docker
### Build
`docker build -t converter .`
### Execution
`docker run -v ./:/app/files converter /app/files/TEST.md /app/files/target.html`