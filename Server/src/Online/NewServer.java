package Online;

import IO.Console.Logger;
import IO.Console.OutputColor;
import IO.Files.FileLogger;
import IO.Files.LogFileType;
import IO.Files.PropertyReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.ServerSocket;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;


public class NewServer {
    private static ServerThreadExecutor exec;

    private static NewServer instance = null;
    private final Scanner input;
    private final List<Thread> clientThreads;
    private Logger logger;
    private FileLogger fileLogger;
    private final PropertyReader propsReader;
    private Set<Player> connectedPlayers;
    private Set<Integer> allIds;
    private Set<Integer> onlineIds;
    private Set<Integer> adminIds;
    private Set<Integer> clientIds;

    private NewServer() {
        propsReader = new PropertyReader("config.dat");
        int threadCount = Integer.parseInt(propsReader.getProperty(propsReader.updateAndGetConfigFile(), "server_max_threads"));

        if (threadCount == 0) {
            threadCount = Runtime.getRuntime().availableProcessors() * Integer.parseInt(propsReader.getProperty(propsReader.updateAndGetConfigFile(), "server_cores_multiplier"));
        }
        exec = new ServerThreadExecutor(threadCount, threadCount, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

        input = new Scanner(System.in);
        clientThreads = new CopyOnWriteArrayList<>();
        createSets();
        initLogger();
        logger.print("HELLO FROM NEW SERVER!", "Error");
        logger.print("SERVER WILL USE " + threadCount + " THREADS TO RUN!", "Error");
        initFileLogger();
        readPlayerData();
        startConsole();
        startServer();
    }


    public static synchronized NewServer getInstance() {
        if (instance == null) instance = new NewServer();
        return instance;
    }

    private static String formatDate(LocalDateTime date) {
        int year = date.getYear();
        String res;

        String month = date.getMonthValue() < 10 ? "0" + date.getMonthValue() : String.valueOf(date.getMonthValue());
        String day = date.getDayOfMonth() < 10 ? "0" + date.getDayOfMonth() : String.valueOf(date.getDayOfMonth());
        String hours = date.getHour() < 10 ? "0" + date.getHour() : String.valueOf(date.getHour());
        String min = date.getMinute() < 10 ? "0" + date.getMinute() : String.valueOf(date.getMinute());
        String sec = date.getSecond() < 10 ? "0" + date.getSecond() : String.valueOf(date.getSecond());

        res = day + "." + month + "." + year + "[" + hours + ":" + min + ":" + sec + "]";
        return res;
    }

    private boolean useColoredText() {
        System.out.println("Do you want to use colored console? (true/false)");
        boolean res = input.nextBoolean();
        input.nextLine();

        return res;
    }

    private void createSets() {
        connectedPlayers = ConcurrentHashMap.newKeySet();
        allIds = ConcurrentHashMap.newKeySet();
        onlineIds = ConcurrentHashMap.newKeySet();
        adminIds = ConcurrentHashMap.newKeySet();
        clientIds = ConcurrentHashMap.newKeySet();
    }

    private void initLogger() {
        boolean useColorText = Boolean.parseBoolean(propsReader.getProperty(propsReader.updateAndGetConfigFile(), "colored_output"));
        logger = Logger.getInstance();
        if (useColorText) logger.enableColoredText();
        else logger.disableColoredText();
        logger.addPrintColor("Connection", OutputColor.GREEN);
        logger.addPrintColor("Disconnection", OutputColor.CYAN);
        logger.addPrintColor("Registration", OutputColor.YELLOW);
        logger.addPrintColor("File creation", OutputColor.BLUE);
        logger.addPrintColor("Error", OutputColor.RED);
        logger.addPrintColor("Wrong data", OutputColor.PURPLE);
        logger.addPrintColor("Server state", OutputColor.GREEN);
    }


    private void initFileLogger() {
        logger.setOutputColor("File creation");
        logger.print("Attempting to create files:\n");
        fileLogger = new FileLogger("logFolder");
        logger.print("Log dir created in: " + fileLogger.getLogDirPath() + "\n");
        fileLogger.addLogFile("Connections file", "connectedPlayers.dat", LogFileType.CONNECTIONS);
        fileLogger.printFileInfo(logger::print, "Connections file");
        fileLogger.addLogFile("Turning on-off file", "on-off.dat", LogFileType.ON_OFF);
        fileLogger.printFileInfo(logger::print, "Turning on-off file");
        fileLogger.addLogFile("Id file", "ids.dat", LogFileType.SAVED_IDS);
        fileLogger.printFileInfo(logger::print, "Id file");
        logger.setDefaultOutputColor();
    }

    private void readPlayerData() {
        // TODO: 17.04.2023 Restore account data from disk
    }

    private void startConsole() {
        exec.execute(this::serverConsole, "Console");
    }

    private void startServer() {
        exec.execute(this::server, "Server");
    }

    private void stopServer() {
        logger.print("Shutting down...", "Disconnection");
        writeOnOff("Off");

        ArrayList<Player> players = new ArrayList<>(connectedPlayers);
        for (Player player : players) {
            try {
                player.writeLine(Prefixes.SYSTEM_SHUTDOWN);
                disconnectIfInactive(player, player.clientThread);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        connectedPlayers.clear();
        players.clear();
        logger.println("Press enter to stop the server", "Default");

        exec.shutdown();
        input.close();
        System.exit(0);
    }

    private void refreshActiveIDs() {
        onlineIds.clear();
        connectedPlayers.forEach(connection -> onlineIds.add(connection.id));
    }

    private void disconnectIfInactive(Player player, Thread current) {
        if (player == null || current == null)
            logger.print("Player to disconnect: " + player + ", current thread: " + current, "Wrong data");
        else try {
            if (player.isUnauthorized())
                logger.println("Unauthorized player from " + player.getIp() + " disconnected", "Disconnection");
            else if (player.isAdmin())
                logger.println("Admin with id " + player.id + " disconnected", "Disconnection");
            else
                logger.println("Player with id " + player.id + " disconnected", "Disconnection");

            writeConnection(player.id, false);

            connectedPlayers.remove(player);
            player.close();

            if (player.clientThread != null) player.clientThread.interrupt();
            current.interrupt();
            refreshActiveIDs();
        } catch (IOException e) {
            e.printStackTrace();
            logger.print("FAILED TO CLOSE CLIENT: " + player, "Error");
        }
    }


    private String getServerIp() {
        try {
            URL awsHost = new URL("https://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(awsHost.openStream()));
            return in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "UNABLE TO GET IP!!!";
        }
    }

    private void server() {
        final int SERVER_PORT = Integer.parseInt(propsReader.getProperty(propsReader.getConfigFile(), "server_port"));

        try (ServerSocket server = new ServerSocket(SERVER_PORT)) {
            logger.print("Server started with ip: " + getServerIp() + " On port: " + SERVER_PORT + "\n", "Yellow");
            writeOnOff("On");

            while (!exec.isShutdown()) {
                Connection connection = new Connection(server);
                Runnable clientThread;

                clientThread = () -> {
                    Player player = new Player(connection);
                    try {
                        player = login(connection);
                        connectedPlayers.add(player);
                        clientThreads.add(Thread.currentThread());
                        if (player == null || player.isUnauthorized() || player.id <= 0)
                            disconnectIfInactive(player, Thread.currentThread());
                        else {
                            onlineIds.add(player.id);

                            if (player.isAdmin()) {
                                adminIds.add(player.id);
                                logger.print("Admin connected: ip address is " + connection.getIp() + ", unique id is " + player.id, "Connection");
                            } else {
                                clientIds.add(player.id);
                                logger.print("Player connected: ip address is " + connection.getIp() + ", unique id is " + player.id, "Connection");
                            }
                            writeConnection(player.id, true);

                            communicationLoop(player);
                        }
                    } catch (IOException e) {
                        disconnectIfInactive(player, Thread.currentThread());
                    }
                };

                exec.execute(clientThread, "Player: " + connection.getIp());
            }
        } catch (RejectedExecutionException e) {
            if (!exec.isShutdown()) {
                logger.print("Failed to start new player thread task!", "Error");
                e.printStackTrace();
            }
        } catch (NullPointerException | IOException e) {
            logger.print("Failed to start a server:\n_________________________", "Error");
            e.printStackTrace();
        } finally {
            stopServer();
        }
    }

    private void messageInvalidData(Player to, String data) throws IOException {
        logger.print("Received invalid data from " + to.root + " with id " + to.id, "Wrong data");
        to.writeLine(Prefixes.INVALID_DATA_COMMON, data);
    }

    private boolean validateAdminReadData(Player admin, String data) throws IOException {
        String adminDataCommandRegex = propsReader.getProperty(propsReader.getConfigFile(), "admin_command_regex");
        if (!data.matches(adminDataCommandRegex)) {
            messageInvalidData(admin, data);
            return false;
        }
        return true;
    }

    private boolean validateClientReadData(Player player, String data) throws IOException {
        String clientDataCommandRegex = propsReader.getProperty(propsReader.getConfigFile(), "client_command_regex");
        if (!data.matches(clientDataCommandRegex)) {
            messageInvalidData(player, data);
            return false;
        }
        return true;
    }

    private boolean validateSelfSendId(Player player, int id) throws IOException {
        if (player.id == id) {
            logger.print("Attempt to send request on itself on id: " + player.id, "Wrong data");
            player.writeLine(Prefixes.REQUEST_SELF_SEND, String.valueOf(player.id));
            return false;
        }
        return true;
    }

    private boolean validateAnotherAdminSendId(Player player, int id) throws IOException {
        if (adminIds.contains(id)) {
            logger.print("Attempt to send request to admin with id: " + id, "Wrong data");
            player.writeLine(Prefixes.REQUEST_ADMIN_SEND, id);
            return false;
        }
        return true;
    }

    private void sendAdminRequest(Player admin, Player player, int clientToSendId, String command, String args) throws IOException {
        refreshActiveIDs();

        if (player == null) {
            logger.print("Sending error: system didn't find an online player with id " + clientToSendId, "Error");
            admin.writeLine(Prefixes.REQUEST_OFFLINE_CLIENT, clientToSendId);
        } else {
            logger.print("Invalid command: this id is free", "Wrong data");
            admin.writeLine(Prefixes.REQUEST_FREE_TARGET_ID, clientToSendId);
        }
    }

    private void sendRequestSuccess(Player to, Player by, int adminId, String response) throws IOException {
        refreshActiveIDs();
        if (to != null) {
            if (allIds.contains(adminId)) to.writeLine(response);
            else {
                logger.print("Invalid command: this id is free", "Wrong data");
                by.writeLine(Prefixes.REQUEST_FREE_TARGET_ID, adminId);
            }
        } else {
            logger.print("Sending error: system didn't find an online admin with id " + adminId, "Error");
            by.writeLine(Prefixes.REQUEST_OFFLINE_ADMIN, adminId);
        }
    }

    private void communicationLoop(Player player) throws IOException {
        while (!player.clientThread.isInterrupted()) {
            String readData = player.readLine();
            if (readData == null || !readData.contains("$")) {
                logger.print("Received invalid data from player with id " + player.id, "Wrong data");
                player.writeLine(Prefixes.INVALID_DATA_COMMON, readData);
                continue;
            }

            String[] readDataSplit = readData.split("\\$");

            if (player.isAdmin()) {
                adminIds.add(player.id);

                if (processInfoCommand(readData, player)) continue;
                if (!validateAdminReadData(player, readData)) continue;

                logger.print("Admin data read: " + readData, "Default");

                int clientToSendId = Integer.parseInt(readDataSplit[1]);
                String commandToSend = readDataSplit[2];
                String argsToSend = readDataSplit[3];

                if (!validateSelfSendId(player, clientToSendId) || !validateAnotherAdminSendId(player, clientToSendId))
                    continue;

                logger.print("Id to send: " + clientToSendId, "Default");
                logger.print("Id who sent: " + player.id, "Default");
                logger.print("Command to send: " + commandToSend, "Default");
                logger.print("Args to send: " + argsToSend, "Default");

                sendAdminRequest(player, getClientById(connectedPlayers, clientToSendId), clientToSendId, commandToSend, argsToSend);
            } else if (player.isClient()) {
                logger.print("Player data read: " + readData, "Default");
                if (!validateClientReadData(player, readData)) continue;

                int clientToSendId = Integer.parseInt(readDataSplit[1]);
                int commandId = Integer.parseInt(readDataSplit[2]);
                String success = readDataSplit[3];
            }
            refreshActiveIDs();
        }

    }

    private Player login(Connection connection) {
        Player res = new Player(connection);
        boolean loginFailed = true;
        ClientRoot root = null;
        int uniId = -1;

        do {
            try {
                refreshActiveIDs();
                String dataReceived = res.readLine();

                if (dataReceived == null) {
                    disconnectIfInactive(res, Thread.currentThread());
                    return res;
                }
                if (dataReceived.split("\\$").length != 2) {
                    logger.print("Received invalid data from: " + res + " data: " + dataReceived, "Wrong data");
                    res.writeLine(Prefixes.LOGIN_INVALID_SYNTAX, dataReceived);
                    disconnectIfInactive(res, Thread.currentThread());
                    return res;
                }

                root = dataReceived.split("\\$")[0].equals("A") ? ClientRoot.ADMIN : dataReceived.split("\\$")[0].equals("C") ? ClientRoot.CLIENT : ClientRoot.UNAUTHORIZED;
                uniId = Integer.parseInt(dataReceived.split("\\$")[1]);

                if (uniId <= 0) {
                    if (allIds.contains(-uniId)) {
                        logger.print("The user with id " + (-uniId) + " already exists", "Wrong data");
                        res.writeLine(Prefixes.LOGIN_EXISTING_ID, -uniId);
                        continue;
                    }

                    String register = "Successfully registered new user with root " + root + " and id: " + (-uniId);

                    fileLogger.logToAll("Id file", String.valueOf(-uniId));
                    logger.print(register, "Registration");

                    res.writeLine(Prefixes.LOGIN_CONNECT, root + "$" + Math.abs(uniId));
                    allIds.add(Math.abs(uniId));
                    break;
                } else {
                    if (allIds.contains(uniId)) {
                        if (!onlineIds.contains(uniId)) {
                            loginFailed = false;
                            res.writeLine(Prefixes.LOGIN_CONNECT, root + "$" + Math.abs(uniId));
                        } else {
                            logger.print("Failed to login a user with id " + uniId + ": user with this id has already logged in", "Wrong data");
                            res.writeLine(Prefixes.LOGIN_ONLINE_ID, uniId);
                        }
                    } else {
                        logger.print("Failed to login a user with id " + uniId + ": this id is free", "Wrong data");
                        res.writeLine(Prefixes.LOGIN_FREE_ID, uniId);
                    }
                }
            } catch (IOException e) {
                disconnectIfInactive(res, Thread.currentThread());
                break;
            }
        } while (loginFailed);

        if (uniId != -1)
            res = new Player(connection, Math.abs(uniId), root, Thread.currentThread());

        return res;
    }

    private void serverConsole() {
        String userInput;

        while (!exec.isShutdown()) {
            userInput = input.nextLine();
            if (userInput.trim().isEmpty()) continue;
            String finalAction = userInput;
            Runnable consoleThread = () -> {
                try {
                    switch (finalAction) {
                        case "$shutdown" -> stopServer();
                        case "$connections" -> {
                            if (connectedPlayers.size() > 0) {
                                logger.print("All active connections: ", "Connection");
                                connectedPlayers.forEach(player -> logger.print(player.toString(), "Registration"));
                                logger.print(connectedPlayers.size() + " connections in total\n", "Connection");
                            } else logger.print("No active connections", "Disconnection");
                        }
                        case "$idlist" -> {
                            if (allIds.size() == 0) logger.print("No registered IDs yet", "Disconnection");
                            else {
                                logger.print("All registered IDs: ", "Default");
                                allIds.forEach(id -> logger.print(String.valueOf(id), "Default"));
                            }
                        }
                        case "$help" -> {
                            logger.print("___________________________________", OutputColor.CYAN);
                            logger.print("Help: \n", OutputColor.CYAN);
                            logger.print("""
                                    $help to show this
                                    $shutdown to shut the server down
                                    $disconnect <int id> to disconnect a player from server
                                    $connectedPlayers to show all active connectedPlayers
                                    $idlest to show all registered ids
                                    $msg <int id> <String message> to send a message to the player
                                    ___________________________________\040
                                    """, OutputColor.CYAN);
                        }
                        default -> {
                            if (finalAction.matches("\\$disconnect *\\d* *")) {
                                if (finalAction.split("\\$disconnect").length > 0) {
                                    int idToDisconnect = Integer.parseInt(finalAction.split("\\$disconnect ")[1]);
                                    refreshActiveIDs();
                                    if (onlineIds.contains(idToDisconnect)) {
                                        getClientById(connectedPlayers, idToDisconnect).writeLine("SYS$DISCONNECT");
                                        getClientById(connectedPlayers, idToDisconnect).close();
                                        logger.print("Disconnected player with id " + idToDisconnect + "\n", "Disconnection");
                                        writeConnection(idToDisconnect, false);
                                        connectedPlayers.remove(getClientById(connectedPlayers, idToDisconnect));
                                    } else
                                        logger.print("Player with id " + idToDisconnect + " isn't connected", "Wrong data");
                                    if (connectedPlayers.size() > 0)
                                        logger.print(connectedPlayers.size() + " connectedPlayers in total\n", "Connection");
                                    else logger.print("No active connectedPlayers", "Disconnection");
                                } else {
                                    if (connectedPlayers.size() != 0) {
                                        ArrayList<Player> toDisconnect = new ArrayList<>(connectedPlayers);
                                        int disconnectedClientsCount = toDisconnect.size();
                                        logger.print("Disconnecting " + disconnectedClientsCount + " clients...", "Disconnection");
                                        for (Player player : toDisconnect) {
                                            player.writeLine("SYS$DISCONNECT");
                                            writeConnection(player.id, false);
                                            player.close();
                                        }
                                        clientThreads.forEach(Thread::interrupt);
                                        logger.print("Disconnected " + disconnectedClientsCount + " clients (all)", "Disconnection");
                                        connectedPlayers.clear();
                                    } else {
                                        logger.print("No active connectedPlayers", "Disconnection");
                                    }
                                }
                            } else if (finalAction.matches(propsReader.getProperty(propsReader.getConfigFile(), "message_command_regex"))) {
                                if (finalAction.split("\\$msg").length > 0) {
                                    int idToSend = Integer.parseInt(finalAction.split(" ")[1]);
                                    StringBuilder messageText = new StringBuilder();
                                    for (int i = 2; i < finalAction.split(" ").length; i++)
                                        messageText.append(finalAction.split(" ")[i]);

                                    refreshActiveIDs();
                                    if (onlineIds.contains(idToSend)) {
                                        getClientById(connectedPlayers, idToSend).writeLine("SYS$MSG$" + messageText);
                                        logger.print("Sent message " + messageText + " to player with id: " + idToSend, "Default");
                                    } else
                                        logger.print("Player with id: " + idToSend + " isn't connected", "Wrong data");
                                }
                            } else {
                                logger.print("Invalid command", "Wrong data");
                                logger.print("Type $help to show all available commands", OutputColor.CYAN);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
            exec.execute(consoleThread, "Console");

        }
    }


    private boolean processInfoCommand(String command, Player player) throws IOException {
        String[] split = command.split("\\$");
        if (!split[1].equals("INFO")) return false;
        if (split.length != 3) {
            player.writeLine(Prefixes.INFO_INVALID_SYNTAX, command);
            System.err.println(split.length);
            return false;
        }

        new Thread(() -> {
            String toSend;

            if (!adminIds.contains(player.id)) toSend = Prefixes.INFO_ACCESS_DENIED.str;
            else switch (split[2].toUpperCase(Locale.ROOT)) {
                case "ONLINE" -> {
                    StringBuffer sendBuffer = new StringBuffer(Prefixes.INFO_ONLINE_LIST.str + "$");
                    connectedPlayers.forEach(socket -> sendBuffer.append(socket.getIp()).append(", ").append(socket.id).append(", ").append("root: ").append(adminIds.contains(socket.id) ? "Admin" : "Player").append(";"));
                    if (sendBuffer.charAt(sendBuffer.length() - 1) == ';')
                        sendBuffer.deleteCharAt(sendBuffer.length() - 1);
                    toSend = sendBuffer.toString();
                }
                case "REG" -> {
                    toSend = Prefixes.INFO_REGISTERED_LIST.str + "$" + allIds;
                    logger.print("Admin with id: " + player.id + " requested registered id list:\n" + allIds, "Default");
                }
                case "ADMINS" -> {
                    toSend = Prefixes.INFO_ADMIN_LIST.str + "$" + adminIds;
                    logger.print("Admin with id: " + player.id + " requested admin id list:\n" + adminIds, "Default");
                }
                case "CLIENTS" -> {
                    toSend = Prefixes.INFO_CLIENT_LIST.str + "$" + clientIds;
                    logger.print("Admin with id: " + player.id + " requested player id list:\n" + clientIds, "Default");
                }
                case "HEALTH" -> {
                    StringBuilder res = new StringBuilder();
                    MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
                    res.append(String.format("Max heap memory: %.2f GB\n", (double) memoryMXBean.getHeapMemoryUsage().getMax() / 1073741824));
                    res.append(String.format("Used heap memory: %.2f GB\n\n", (double) memoryMXBean.getHeapMemoryUsage().getUsed() / 1073741824));
                    File cDrive = new File("K:/");
                    res.append(String.format("Total disk space: %.2f GB\n", (double) cDrive.getTotalSpace() / 1073741824));
                    res.append(String.format("Free disk space: %.2f GB\n", (double) cDrive.getFreeSpace() / 1073741824));
                    res.append(String.format("Usable disk space: %.2f GB\n\n", (double) cDrive.getUsableSpace() / 1073741824));
                    ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

                    for (Long threadID : threadMXBean.getAllThreadIds()) {
                        ThreadInfo info = threadMXBean.getThreadInfo(threadID);
                        res.append('\n').append("Thread name: ").append(info.getThreadName());
                        res.append("Thread State: ").append(info.getThreadState());
                        res.append(String.format("CPU time: %s ns", threadMXBean.getThreadCpuTime(threadID)));
                    }
                    toSend = res.toString();
                    logger.print("SERVER HEALTH: \n" + toSend, "Server state");
                }
                default -> {
                    if (split[2].matches("\\d+")) {
                        int idToSend = Integer.parseInt(split[2]);
                        Player cur = getClientById(connectedPlayers, idToSend);
                        if (cur != null) toSend = Prefixes.INFO_IP_BY_ID.str + "$" + cur.getIp();
                        else toSend = Prefixes.INFO_INVALID_TARGET_ID.str + "$" + idToSend;
                    } else toSend = Prefixes.INFO_INVALID_SYNTAX.str + "$" + split[2];
                }
            }
            try {
                player.writeLine(toSend);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        return true;
    }

    private Player getClientById(Set<Player> playerSet, long id) {
        Player[] res = new Player[1];
        playerSet.stream().filter(connection -> connection.id == id).findFirst().ifPresent(connection -> res[0] = connection);
        return res[0];
    }

    private void writeOnOff(String onOff) {
        LocalDateTime now = LocalDateTime.now();
        String normalDate = formatDate(now);
        String toAppend = normalDate + "$" + onOff;
        fileLogger.logToAll(LogFileType.ON_OFF, toAppend);
    }

    private void writeConnection(int clientID, boolean connected) {
        LocalDateTime now = LocalDateTime.now();
        String normalDate = formatDate(now);
        String toAppend = normalDate + "$" + clientID + "$" + (connected ? 'c' : 'd');
        fileLogger.logToAll("Connections file", toAppend);
    }
}

class ServerThreadExecutor extends ThreadPoolExecutor {
    public ServerThreadExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    @Override
    public void execute(Runnable command) {
        new Thread(command).start();
    }

    public void execute(Runnable command, String name) {
        new Thread(command, name).start();
    }
}

enum Prefixes {
    // TODO: 17.04.2023 Add docs for all of this
    INVALID_DATA_COMMON("INVALID$DATA"),

    LOGIN_INVALID_SYNTAX("LOGIN$INVALID_SYNTAX"),
    LOGIN_EXISTING_ID("LOGIN$INVALID_ID$EXISTS"),
    LOGIN_ONLINE_ID("LOGIN$INVALID_ID$ONLINE"),
    LOGIN_FREE_ID("LOGIN$INVALID_ID$FREE"),

    LOGIN_CONNECT("LOGIN$CONNECT"),

    REQUEST_SELF_SEND("INVALID$SELF_ID"),
    REQUEST_ADMIN_SEND("INVALID$ADMIN_ID"),
    REQUEST_FREE_TARGET_ID("INVALID$FREE"),
    REQUEST_OFFLINE_CLIENT("INVALID$OFFLINE_CLIENT"),
    REQUEST_OFFLINE_ADMIN("INVALID$OFFLINE_ADMIN"),

    INFO_COMMON("INFO$ERROR"),
    INFO_ACCESS_DENIED("INFO$ERROR$ACCESS_DENIED"),
    INFO_INVALID_TARGET_ID("INFO$ERROR$INVALID_ID"),
    INFO_INVALID_SYNTAX("INFO$ERROR$INVALID_SYNTAX"),

    INFO_ONLINE_LIST("INFO$ONLINE"),
    INFO_REGISTERED_LIST("INFO$REG"),
    INFO_ADMIN_LIST("INFO$ADMINS"),
    INFO_CLIENT_LIST("INFO$CLIENTS"),
    INFO_IP_BY_ID("INFO$IP"),

    SYSTEM_MESSAGE("SYS$MSG"),
    SYSTEM_DISCONNECT("SYS$DISCONNECT"),
    SYSTEM_SHUTDOWN("SYS$SHUTDOWN"),
    ;

    public final String str;

    Prefixes(String str) {
        this.str = str;
    }
}
