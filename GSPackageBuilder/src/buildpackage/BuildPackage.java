/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buildpackage;

import java.util.List;

/**
 *
 * @author rdesouza
 */
public class BuildPackage {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String url = "http://svn/psg/rmb_cnc/branches/ClientMaster/8.7.1.39";
        String name = "psgdevopsuser";
        String password = "Gsource@1234";
//        String url = args[0];
//        String name = args[1];
//        String password = args[2];
        String type = "SVN";
        try {
            type = args[3];
        } catch (Exception e) {
            type = "SVN";
        }

        VersionControl vc = new VersionControl(type, url, name, password);
        vc.getConnection();
        List completeFileList = vc.getCompleteFileList();
        List changedFileList = vc.getChangedFileList();
        BuildInstructions bldInstr=new BuildInstructions(completeFileList, changedFileList);
        bldInstr.processBuild();
    }
}
