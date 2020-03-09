/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buildpackage;

import java.util.List;
import org.tmatesoft.svn.core.io.SVNRepository;

/**
 *
 * @author rdesouza
 */
public class VersionControl {

    private String url;
    private String name;
    private String password;
    private String type;
    SVNRepository repository;
    SVNVersionControl svn;

    public VersionControl(String typ, String u, String nme, String pass) {
        type = typ;
        url = u;
        name = nme;
        password = pass;
    }

    public void getConnection() {
        if (type.equals("SVN")) {
            try {
                svn = new SVNVersionControl(url, name, password);
                repository = svn.getSVNRepository();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        /*Write Code for Other Repo Types here
        else if (type.equals("GIT")) {
        }
        */ 
    }

    public List<String> getChangedFileList() {
        if (type.equals("SVN")) {
            return getSVNChangedFileList();
        }
        return null;
    }

    public List<String> getCompleteFileList() {
        if (type.equals("SVN")) {
            return getSVNCompleteFileList();
        }
        return null;
    }

    private List<String> getSVNChangedFileList() {
        try {
            List changedFileList = svn.getChangedFileList(repository);
            return changedFileList;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    private List<String> getSVNCompleteFileList() {
        try {
            List completeFileList = svn.getCompleteFileList(repository, "");
            return completeFileList;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

}
