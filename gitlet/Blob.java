package gitlet;

import java.io.File;
import java.io.Serializable;

/** Blob class.
 * @author Sufyan Suliman
 */
public class Blob implements Serializable {

    /** Id variable. **/
    private String id;

    /** Name variable. **/
    private File itFile;

    /** String variable. **/
    private String fileString;

    /** blob method.
     * @param name of file
     **/
    public Blob(String name) {
        itFile = new File(name);
        fileString = Utils.readContentsAsString(itFile);
        id = Utils.sha1(itFile.getName() + Utils.readContentsAsString(itFile));
    }

    /** Get function for id.
     * @return id.
     **/
    public String getId() {
        return id;
    }

    /** Get string function.
     * @return fileString.
     */
    public String getString() {
        return fileString;
    }
    /** Get function for inside contents.
     * @return contents.
     */
    public String getInside() {
        return fileString;
    }
}
