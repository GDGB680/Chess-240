# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```


Phase 2: Chess server Design
https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTJ5vH5AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIei0azF5vDgHYsgwr5ks9K+KDvvorxLAC5wFrKaooOUCAHjysL7oeqLorE2IJoYLphm6ZIUgatLlqOJpEuGFoctyvIGoKwowEBoakW6naYeURraI6zoEjhLLlCUSAAGaWFUoGfoRI5vjAH5LCRTJMUml4weUOgQBA3DsBxiZcYxPEwJ63pBrO6BibcxEMbJ0qRhyMDRtZQbxnKWHJpBJbITy2a5pg-4gtBJRXAMRGjAuk59NOhnzuOrZ9N+V7eYU2Q9jA-aDr0-niWOE5fKFjbNhFi5mAgBgwAAkmgVDAMgtpATA0CSSJSwaY5f7OWmACiUDeFAnnNcxMXlAALE4ACMQ7fO10ArD4wRBtASAAF4oMs0XUB28ldjkYD9UNI2UB1E1TX6M3zYtZicKuXi+P4AReCg6B7gevjMMe6SZJg8UXkUy1phU0gtbuLX1C1zQtA+qhPiNWVzrsUXtr+ZxAgBIUGdl87-F18OwyxMDwfYD1IfdvqoRiGEORq3GkjA5JgLG-oQ+gMlmhGhSWpRMZ2doQphLTaBmQzPWKTA072TBpPaeTHAoNwmTU2FAZI3O9NkZZpTSBLFKGNTQtOomTno+UyEPe5CB5t1q0-n2A4w52b0bYlFsS4VJVlRVAty+gDWdl55RtR1aOpnzvkwANw0pTt40TJN06HQt0M-lb5420H21jVAe2R1Ac3R0up2mOdG4BJCtq7tCMAAOKjqyT2nq98dsmblQl-9QP2KO4OuyjeWxzrqZ+VzUMQejtcOXB0Jl6MqhISP5eE+h7vYaL7oUxS1Oy3WyMK3JTNWSztlxuztFczzis+eCLu7-Is9aeZ5PILEo9qLC68WZv5Tb+VCCl6ODquhZx-yhT5ezy7sCPWk8x6G2NgPU2vVbbJX6M3Me4xIpLWOKta25tYF9HgWoRBeV7aGEduVJAtoD6YQ9s1L2ydfZQR8iWROIdk6p2munI6McYpx3WptYO-RQ4p3DvtVIUdjrLjOuuS62AfBQGwNweAupMgf1GCkZ6Z51qD0+uUG8DQm4txSr3T4TwsEADlRx90tkAhGujqxTEMcY1GXlVFa1grpWRKA76wjgM4u+08sQXxkGTBelNl5c0fuaJW28NZ705m3Q+clf6OMFuxUhc8r4Lz0pkVx1jRjBMZuycoNk76a2dGYtM7ivRpNHOAqhK0aFpiSiNLBRVpAmM7qcNBMC6mjgaSYvBxVSqEOIVExJRSKE+zsVAtRgctr0N2nwtOGdjrNLivHThSdpkDH4YI6Gwic6iMCJYCW8FkgwAAFIQB5PIwwAQdAIFAA2auKjqnqKqJSO8LQsGt1XpDPRcxrl7KgHAVSYdLGYI6Y01Gncmq61LIjD5zYvnPB+ZQf58FeFAvqaCvKoyFIOPKAAK1OWgVxJyeSeJQGiImPjv7kwCUGFeM417RKfjksJbN5Ac1PnSucDKQmxPKPE8+iTL4MzwmAdJIKsnkS5DyW0+SIn-1GA0rlEYeVypQAUzSvj56sWwFoMpoxYRovfAiv5AKoCzCAuKpWlJtV6nOWqxqcNu7HPxSSnMRtKmxQ+gHJKpiWlLJgflB2vTnZc2qrANFgCIWOu9tAd1-taGTO4QwmZTC5msM+uwhKdDE2rIjimlhWcVzbIuoELwwAwiIC9LAYA2ApGEHiIkRRVdrb2OvD9P6AMgbGFMZG4BsblUgDUnCYmwsklCt0oOh+iqJUVrwALZUMB5BXLUBGh1vbRkPP9QstaCVvXLiAA