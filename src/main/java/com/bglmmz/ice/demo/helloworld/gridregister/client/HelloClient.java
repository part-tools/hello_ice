package com.bglmmz.ice.demo.helloworld.gridregister.client;

import com.bglmmz.ice.demo.helloworld.slice.HelloServicePrx;
import com.zeroc.Ice.Properties;

public class HelloClient {
    public static void main(String[] args) {
        int status = 0;

        com.zeroc.Ice.InitializationData initData = new com.zeroc.Ice.InitializationData();

        Properties properties = com.zeroc.Ice.Util.createProperties();
        //表明服务寻址器的地址
        properties.setProperty("Ice.Default.Locator", "DefaultIceGrid/Locator:tcp -p 4061");
        initData.properties = properties;


        // Communicator实例
        try (com.zeroc.Ice.Communicator ic = com.zeroc.Ice.Util.initialize(args, initData)) {
            status = run(ic);
        }
        System.exit(status);
    }

    private static int run(com.zeroc.Ice.Communicator ic) {

        //stringToProxy("HelloService:default -p 10001")是直接获取代理，这个"HelloService"是在服务端定义的服务的identityID
        //propertyToProxy("HelloService.Proxy")是用properties中的定义来获取代理。在properties文件的"HelloService.Proxy"这个配置项，配置了在服务端定义的服务的identityID。
        //在properties文件的"HelloService.Proxy"这个配置项的值，就是stringToProxy("HelloService:default -p 10001")这个的参数。

        //HelloServicePrx helloService = HelloServicePrx.checkedCast(ic.stringToProxy("HelloService:default -p 10001")).ice_twoway().ice_secure(false);


        String taskIceServantAdapterId = "icChannelAdapter1";
        String servantId = "task_1_party_id_1";
        //寻址地址模式：servantId@adapterId
        HelloServicePrx helloService = HelloServicePrx.checkedCast(ic.stringToProxy(servantId+"@"+taskIceServantAdapterId));
        if (helloService == null) {
            System.err.println("invalid proxy");
            return 1;
        }

        menu();

        java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));

        String line = null;
        do {
            try {
                System.out.print("==> ");
                System.out.flush();
                line = in.readLine();
                if (line == null) {
                    break;
                }
                if (line.startsWith("t ")) {
                    String[] args = line.split(" ");
                    if (args.length == 2) {
                        String echo = helloService.sayHello(args[1]);
                        System.out.println("echo from server:" + echo);
                    } else {
                        System.out.println("unknown command `" + line + "'");
                        menu();
                    }

                } else if (line.equals("s")) {
                    helloService.shutdown();
                } else if (line.equals("x")) {
                    // Nothing to do
                } else if (line.equals("?")) {
                    menu();
                } else {
                    System.out.println("unknown command `" + line + "'");
                    menu();
                }
            } catch (java.io.IOException ex) {
                ex.printStackTrace();
            } catch (com.zeroc.Ice.LocalException ex) {
                ex.printStackTrace();
            }
        }
        while (!line.equals("x"));

        return 0;
    }

    private static void connectRouter(com.zeroc.Ice.Communicator communicator) {
        com.zeroc.Glacier2.RouterPrx router = com.zeroc.Glacier2.RouterPrx.checkedCast(communicator.getDefaultRouter());
        com.zeroc.Glacier2.SessionPrx session;

        //
        // Loop until we have successfully create a session.
        //
        while (true) {
            //
            // Prompt the user for the credentials to create the session.
            //
            java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
            System.out.println("This demo accepts any user-id / password combination.");

            String id;
            String pw;
            try {
                System.out.print("user id: ");
                System.out.flush();
                id = in.readLine();

                System.out.print("password: ");
                System.out.flush();
                pw = in.readLine();
            } catch (java.io.IOException ex) {
                ex.printStackTrace();
                continue;
            }

            //
            // Try to create a session and break the loop if succeed,
            // otherwise try again after printing the error message.
            //
            try {
                session = router.createSession(id, pw);
                break;
            } catch (com.zeroc.Glacier2.PermissionDeniedException ex) {
                System.out.println("permission denied:\n" + ex.reason);
            } catch (com.zeroc.Glacier2.CannotCreateSessionException ex) {
                System.out.println("cannot create session:\n" + ex.reason);
            }
        }
        int acmTimeout = router.getACMTimeout();
        com.zeroc.Ice.Connection connection = router.ice_getCachedConnection();
        assert (connection != null);
        connection.setACM(java.util.OptionalInt.of(acmTimeout), null,
                java.util.Optional.of(com.zeroc.Ice.ACMHeartbeat.HeartbeatAlways));
        connection.setCloseCallback(con -> System.out.println("The Glacier2 session has been destroyed."));

    }

    private static void menu() {
        System.out.println(
                "usage:\n" +
                        "t xx: send xx to server\n" +
                        "s: shutdown server\n" +
                        "x: exit\n" +
                        "?: help\n");
    }
}
