package q_2.nu_gatepass;

/**
 * Created by Pradumn K Mahanta on 01-05-2016.
 **/

public class UserListViewItem {

    public final String rUserName, rFullName, rBatch , rEnrollKey, rBranch, rContact, rPic, rStatus;

    public UserListViewItem(String rUserName, String rFullName, String rBatch , String rEnrollKey, String rBranch, String rContact,
                            String rPic, String rStatus){
        this.rFullName = rFullName;
        this.rUserName = rUserName;
        this.rBatch = rBatch;
        this.rEnrollKey = rEnrollKey;
        this.rBranch = rBranch;
        this.rContact = rContact;
        this.rPic = rPic;
        this.rStatus = rStatus;
    }
}
