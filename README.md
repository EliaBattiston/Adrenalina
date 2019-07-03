# Prova Finale Ingegneria del Software 2019
## Group AM06

- ###   10522850    Aspesi Andrea ([@AndreaAspesiPoli](https://github.com/AndreaAspesiPoli))<br>andrea.aspesi@mail.polimi.it
- ###   10522687    Battiston Elia ([@EliaBattiston](https://github.com/EliaBattiston))<br>elia.battiston@mail.polimi.it
- ###   10518898    Carabelli Alessandro ([@AleCarabelli](https://github.com/AleCarabelli))<br>alessandro2.carabelli@mail.polimi.it

| Functionality | State |
|:-----------------------|:------------------------------------:|
| Basic rules | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Complete rules | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Socket | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| RMI | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| GUI | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| CLI | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Multiple games | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Persistence | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Domination or Towers modes | [![RED](https://placehold.it/15/f03c15/f03c15)](#) |
| Terminator | [![RED](https://placehold.it/15/f03c15/f03c15)](#) |

<!--
[![RED](https://placehold.it/15/f03c15/f03c15)](#)
[![YELLOW](https://placehold.it/15/ffdd00/ffdd00)](#)
[![GREEN](https://placehold.it/15/44bb44/44bb44)](#)
-->

# Execution instructions
## Running the Server
The server can be executed with `AM06_Server.jar`, built during Maven's `package` step. 

```
java -jar AM06_Server.jar
```

### Configuration
The configuration of the server can be changed by placing a `config.json` file in the same folder of the server jar.

The syntax of the configuration file is the following:

```
{
    "startMatchSeconds": 60,
    "playerTurnSeconds": 120,
    "minPlayers": 3
}
```
The values reported in this example are the defaults, set by the server if no `config.json` is found.

## Running the Client
The client can be executed with `AM06_Client.jar`, built during Maven's `package` step.

```
java -jar AM06_Client.jar
```

This command executes the CLI version of the client. Starting up the Client in its GUI version can be achieved adding startup flags:

|Flag|Function|
|-|-|
|-g|Start with a GUI|

### JavaFX external libraries
When executing the Client with a GUI (with the -g startup flag) JavaFX libraries for the correct OS have to be provided by placing the `javafx-sdk-11` folder in the same folder where `AM06_Client.jar` is placed.
In particular, these files need to be present in `./javafx-sdk-11/lib/`:

```
javafx.base.jar
javafx.controls.jar
javafx.graphics.jar
```