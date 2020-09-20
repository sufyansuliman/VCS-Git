package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Collections;

/** Repo Class.
 * @author Sufyan Suliman
 */
public class Repo implements Serializable {

    private static final long serialVersionUID = -2330735761681846151L;

    /** Stage variable. **/
    private HashMap<String, String> stage;

    /** Current branch variable. **/
    private String currentBranch;

    /** List of files need to be deleted variable. **/
    private ArrayList<String> deleted;

    /** ID variable. **/
    private String itsID;

    /** Repo method. */
    public Repo() {
        Commit first = firstCommit();

        File start = new File(".gitlet");
        start.mkdir();
        File stageFiles = new File(".gitlet/stage");
        stageFiles.mkdir();
        File allCommits = new File(".gitlet/allCommits");
        allCommits.mkdir();
        File blobs = new File(".gitlet/blobs");
        blobs.mkdir();

        theParent = new HashMap<String, String>();
        theParent.put("master", first.getId());
        currentBranch = "master";
        deleted = new ArrayList<String>();
        itsID = theParent.get(currentBranch);
        File initial = new File(".gitlet/allCommits/" + first.getId());
        Utils.writeObject(initial, first);
        stage = new HashMap<String, String>();
    }

    /** The first commit.
     * @return The first commit.
     **/
    public static Commit firstCommit() {
        return new Commit(null, "initial commit", null, null);
    }

    /** Add method.
     * @param name of file.
     */
    public void add(String name) {
        File fileName = new File(name);

        if (!fileName.exists()) {
            System.out.println("File does not exist.");
            return;
        } else {
            Blob thisFile = new Blob(name);
            File fileBlob = new File(".gitlet/blobs/" + thisFile.getId());
            if ((!fileBlob.exists()) && (!stage.containsKey(name))) {
                stage.put(name, thisFile.getId());
                Utils.writeObject(fileBlob, thisFile);
                File fileStage = new File(".gitlet/stage/" + thisFile.getId());
                Utils.writeObject(fileStage, thisFile);
            } else if (deleted.contains(name)) {
                deleted.remove(name);
            }
        }
    }

    /** Inputting blobs.
     * @param folder where going.
     * @param itsblob what is.
     **/
    public void connectBlob(String folder, Blob itsblob) {
        File file = new File(".gitlet" + folder + "/" + itsblob.getId());
        Utils.writeObject(file, itsblob);
    }

    /** The Parent hashMap. **/
    private HashMap<String, String> theParent;

    /** The stage variable. **/
    private List<String> theStage = Utils.plainFilenamesIn(".gitlet/stage");

    /** Commit method.
     * @param message the message.
     */
    public void commit(String message) {
        List<String> commitees = Utils.plainFilenamesIn(".gitlet/stage");
        if (stage.size() > 0 || deleted.size() > 0) {
            String theBranch = theParent.get(currentBranch);
            File cur = new File(".gitlet/allCommits/" + theBranch);
            Commit itsParent = Utils.readObject(cur, Commit.class);
            Commit thisCommit = new Commit(deleted, message, stage, itsParent);
            connectCommit("allCommits", thisCommit);
            String newID = thisCommit.getId();
            theParent.put(currentBranch, newID);
            stage.clear();
            deleted.clear();
        } else {
            System.out.println("No changes added to the commit.");
        }
    }

    /** Commit connection method.
     * @param folder which folder.
     * @param itscommit which commit.
     */
    public void connectCommit(String folder, Commit itscommit) {
        File file = new File(".gitlet/" + folder + "/" + itscommit.getId());
        Utils.writeObject(file, itscommit);
    }

    /** log method. **/
    public void log() {
        String cur = theParent.get(currentBranch);
        while (cur != null) {
            File dir = new File(".gitlet/allCommits/" + cur);
            Commit latest = Utils.readObject(dir, Commit.class);
            System.out.println("===");
            System.out.println("commit" + " " + latest.getId());
            System.out.println("Date:" + " " + latest.getTimeStamp());
            System.out.println(latest.getMessage());
            cur = latest.getItsParent();
            if (cur != null) {
                System.out.println();
            }
        }
    }

    /** rm.
     * @param file its file.
     */
    public void rm(String file) {
        String curID = theParent.get(currentBranch);
        File cur = new File(".gitlet/allCommits/" + curID);
        Commit latest = Utils.readObject(cur, Commit.class);
        if (!stage.containsKey(file)
                && (!latest.getfiles().containsKey(file))) {
            System.out.println("No reason to remove the file.");
            return;
        }
        if (stage.containsKey(file)) {
            File removee = new File(".gitlet/stage/" + stage.get(file));
            removee.delete();
            stage.remove(file);
        }
        if (latest.getfiles().containsKey(file)) {
            deleted.add(file);
            File getRid = new File(file);
            if (getRid.exists()) {
                getRid.delete();
            }
        }
    }

    /** Checkout.
     * @param name the name.
     * @param theCommit the commit.
     * @param itsBranch the branch.
     */
    public void checkout(String name, String theCommit, boolean itsBranch) {
        if (itsBranch) {
            if (currentBranch.equals(name)) {
                System.out.println("No need to checkout the current branch.");
                return;
            }
            if (!theParent.containsKey(name)) {
                System.out.println("No such branch exists.");
                return;
            }
            String holder = theParent.get(currentBranch);
            reset(theParent.get(name));
            theParent.put(currentBranch, holder);
            currentBranch = name;
        } else {
            File swaparoo = new File(name);
            Commit switcharoo = getCommit(theParent.get(currentBranch));

            if (theCommit != null) {
                if (theCommit.length() == 6) {
                    String dir = ".gitlet/allCommits";
                    List<String> all = Utils.plainFilenamesIn(dir);
                    for (String id : all) {
                        if (id.substring(0, 6).equals(theCommit)) {
                            theCommit = id;
                            break;
                        }
                    }
                }
                switcharoo = getCommit(theCommit);
                if (switcharoo == null) {
                    System.out.println("No commit with that id exists.");
                    return;
                }
            }
            if (!switcharoo.getfiles().containsKey(name)) {
                System.out.println("File does not exist in that commit.");
                return;
            }
            Blob thisBlob = getBlob(switcharoo.getfiles().get(name));
            Utils.writeContents(swaparoo, thisBlob.getInside());
        }
    }


    /** Get commit.
     * @param iD its id
     * @return commit
     */
    public Commit getCommit(String iD) {
        File me = new File(".gitlet/allCommits/" + iD);
        if (me.exists()) {
            return Utils.readObject(me, Commit.class);
        }
        return null;
    }

    /** Get blob.
     * @param iD its ID
     * @return read
     */
    public Blob getBlob(String iD) {
        File me = new File(".gitlet/blobs/" + iD);
        return Utils.readObject(me, Blob.class);
    }


    /** reset.
     * @param thisID is the iD.
     * **/
    public void reset(String thisID) {
        if (thisID.length() == 6) {
            List<String> all = Utils.plainFilenamesIn(".gitlet/allCommits");
            for (String everyID : all) {
                String each = everyID.substring(0, 6);
                if (each.equals(thisID)) {
                    thisID = everyID;
                    break;
                }
            }
        }
        File cur = new File(System.getProperty("user.dir"));
        List<String> theCur = Utils.plainFilenamesIn(cur);
        ArrayList<String> inRes = new ArrayList<String>();
        if (theCur != null) {
            inRes = new ArrayList<String>(theCur);
        }
        Commit thisCur = getCommit(theParent.get(currentBranch));
        for (int x = 0; x < inRes.size(); x++) {
            String thiss = inRes.get(x);
            if (!thisCur.getfiles().containsKey(thiss)) {
                String hold = "There is an untracked file in the way";
                String hold2 = "; delete it or add it first.";
                System.out.println(hold + hold2);
                return;
            }
        }
        File swap = new File(".gitlet/allCommits/" + thisID);

        if (!swap.exists()) {
            System.out.println("No commit with that id exists.");
            return;
        }

        Commit swaparoo = getCommit(thisID);
        for (String curr : inRes) {
            Set<String> ourFiles = swaparoo.getfiles().keySet();
            if (!ourFiles.contains(curr)) {
                File bye = new File(curr);
                bye.delete();
            } else {
                checkout(curr, thisID, false);
            }
        }

        deleted.clear();
        stage.clear();
        theParent.put(currentBranch, thisID);

        File all = new File(".gitlet/stage");
        List<String> all2 = Utils.plainFilenamesIn(all);
        for (String each : all2) {
            File erase = new File(".gitlet/stage" + each);
            erase.delete();
        }
    }

    /** branch.
     * @param name the name.
     */
    public void branch(String name) {
        if (theParent.containsKey(name)) {
            System.out.println("A branch with that name already exists.");
            return;
        } else {
            theParent.put(name, theParent.get(currentBranch));
        }
    }

    /** rmBranch.
     * @param name the name.
     */
    public void rmBranch(String name) {
        if (!theParent.containsKey(name)) {
            System.out.println("A branch with that name does not exist.");
            return;
        } else if (name.equals(currentBranch)) {
            System.out.println("Cannot remove the current branch.");
            return;
        } else {
            theParent.remove(name);
        }
    }

    /** find.
     * @param mes the message.
     */
    public void find(String mes) {
        List<String> sift = Utils.plainFilenamesIn(".gitlet/allCommits");
        int tracker = 0;
        for (String id : sift) {
            File me = new File(".gitlet/allCommits/" + id);
            Commit thisCommit = Utils.readObject(me, Commit.class);
            if (thisCommit.getMessage().equals(mes)) {
                System.out.println(id);
            } else {
                tracker += 1;
            }
        }
        if (tracker == sift.size()) {
            System.out.println("Found no commit with that message.");
            return;
        }
    }

    /** Globallog. **/
    public void globalLog() {
        List<String> all = Utils.plainFilenamesIn(".gitlet/allCommits");
        for (String id : all) {
            File me = new File(".gitlet/allCommits/" + id);
            Commit cur = Utils.readObject(me, Commit.class);
            System.out.println("===");
            System.out.println("commit" + " " + cur.getId());
            System.out.println("Date:" + " " + cur.getTimeStamp());
            System.out.println(cur.getMessage());
            System.out.println();
        }
    }

    /** status. **/
    public void status() {
        System.out.println("=== Branches ===");
        Set<String> branches = theParent.keySet();
        List<String> orderedBranch = new ArrayList<String>(branches);
        Collections.sort(orderedBranch);
        for (String branch : orderedBranch) {
            if (branch == currentBranch) {
                System.out.println("*" + branch);
            } else {
                System.out.println(branch);
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        Set<String> newStage = stage.keySet();
        List<String> orderedStage = new ArrayList<String>(newStage);
        Collections.sort(orderedStage);
        for (String file : orderedStage) {
            System.out.println(file);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        Collections.sort(deleted);
        for (String delete : deleted) {
            System.out.println(delete);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

}

