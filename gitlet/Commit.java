package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
import java.text.SimpleDateFormat;

/** Commit class.
 * @author Sufyan Suliman
 * **/
public class Commit implements Serializable {

    /** All the files variable. **/
    private HashMap<String, String> files;

    /** Message variable. **/
    private String message;

    /** Timestamp variable. **/
    private String timeStamp;

    /** ID variable. **/
    private String hashID;

    /** The parent of current variable. **/
    private String itsParent;

    /** Date variable. **/
    private Date itsDate;

    /** Commit method.
     * @param deleteList of items to delete.
     * @param mes message of commit.
     * @param commitees things you want to commit.
     * @param parent of current commit
     * **/
    public Commit(ArrayList<String> deleteList, String mes,
                  HashMap<String, String> commitees, Commit parent) {
        message = mes;
        if (mes == null) {
            System.out.println("Please enter a commit message.");
            return;
        }
        if (parent == null) {
            files = new HashMap<String, String>();
            Date start = new Date(0);
            timeStamp = formatDate(start);
            hashID = Utils.sha1(mes + timeStamp);
        } else {
            files = new HashMap<String, String>(parent.files);
            itsDate = new Date();
            timeStamp = formatDate(itsDate);
            itsParent = parent.getId();
            for (String file : commitees.keySet()) {
                files.put(file, commitees.get(file));
                new File(".gitlet/stage/" + commitees.get(file));
            }
            String hashCode = Integer.toString(files.hashCode());
            hashID = Utils.sha1(mes + timeStamp + hashCode);
        }
    }

    /** Get method for id.
     * @return hashID
     **/
    public String getId() {
        return hashID;
    }

    /** Date formatting. **/
    public static final SimpleDateFormat DATEFORMAT =
            new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy");

    /** Date formatting.
     * @param inputDate date input.
     * @return formatted date.
     **/
    public String formatDate(Date inputDate) {
        return DATEFORMAT.format(inputDate) + " -0800";
    }

    /** Get method for timeStamp.
     * @return timestamp
     **/
    public String getTimeStamp() {
        return timeStamp;
    }

    /** Get method for message.
     * @return message
     **/
    public String getMessage() {
        return message;
    }

    /** Get method for parent.
     * @return itsParent.
     **/
    public String getItsParent() {
        return itsParent;
    }

    /** Get method for all files.
     * @return the files.
     **/
    public HashMap<String, String> getfiles() {
        return files;
    }

}
