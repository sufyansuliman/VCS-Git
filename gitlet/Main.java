package gitlet;

import java.io.File;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Sufyan Suliman
 */
public class Main {
    private static final long serialVersionUID = -2330735761681846151L;

    /** The working repo. **/
    private static Repo useRepo;

    /** The repo file type. **/
    private static File myrepo;

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        String[] input = args;
        String command = input[0];
        if (command.equals("init")) {
            myrepo = new File(".gitlet/repo");
            if (!myrepo.exists()) {
                useRepo = new Repo();
                Utils.writeObject(myrepo, useRepo);
            } else {
                String hold = "A Gitlet version-control system already";
                System.out.println(hold + " exists in the current directory.");
            }
        } else {
            useRepo = getRepo();
            if (command.equals("add")) {
                useRepo.add(input[1]);
                Utils.writeObject(myrepo, useRepo);
            } else if (command.equals("commit")) {
                if (input.length > 1 && !input[1].equals("")) {
                    useRepo.commit(input[1]);
                } else if ((input.length == 1) || (input[1].equals(""))) {
                    System.out.println("Please enter a commit message.");
                }
                Utils.writeObject(myrepo, useRepo);
            } else if (command.equals("status")) {
                useRepo.status();
            } else if (command.equals("log")) {
                useRepo.log();
            } else if (command.equals("global-log")) {
                useRepo.globalLog();
            } else if (command.equals("find")) {
                useRepo.find(input[1]);
            } else if (command.equals("checkout")) {
                if (input.length == 2) {
                    useRepo.checkout(input[1], null, true);
                } else if (input[1].equals("--")) {
                    useRepo.checkout(input[2], null, false);
                } else if (input[2].equals("--")) {
                    useRepo.checkout(input[3], input[1], false);
                }
                Utils.writeObject(myrepo, useRepo);
            } else if (command.equals("branch")) {
                useRepo.branch(input[1]);
                Utils.writeObject(myrepo, useRepo);
            } else if (command.equals("rm-branch")) {
                useRepo.rmBranch(input[1]);
                Utils.writeObject(myrepo, useRepo);
            } else if (command.equals("reset")) {
                useRepo.reset(input[1]);
                Utils.writeObject(myrepo, useRepo);
            } else if (command.equals("rm")) {
                useRepo.rm(input[1]);
                Utils.writeObject(myrepo, useRepo);
            } else {
                System.out.println("Incorrect operands.");
                return;
            }
        }
        System.exit(0);
    }

    /** Getting repo.
     * @return repo
     */
    public static Repo getRepo() {
        myrepo = new File(".gitlet/repo");
        return Utils.readObject(myrepo, Repo.class);
    }

}
