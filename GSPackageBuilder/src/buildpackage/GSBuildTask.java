/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buildpackage;

import java.util.Comparator;

/**
 *
 * @author rdesouza
 */
public class GSBuildTask{

    private String type;
    private String pdTaskString;
    private String buildTaskString;
    private boolean isActive;
    private String filePath;
    private String schema;
    private int orderNum;

    public GSBuildTask() {
    }

    public GSBuildTask(String typ, boolean isActv, String tskStr, String bldTsk, String path, String sc, int on) {
        type = typ;
        isActive = isActv;
        pdTaskString = tskStr;
        buildTaskString = bldTsk;
        filePath = path;
        schema = sc;
        orderNum = on;
    }

    public String getPDTaskString() {
        return pdTaskString;
    }

    public String getBuildTaskString() {
        return buildTaskString;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getType() {
        return type;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getSchema() {
        return schema;
    }

    public int getOrderNum() {
        return orderNum;
    }
    public static Comparator<GSBuildTask> orderCmp = new Comparator<GSBuildTask>() {

	public int compare(GSBuildTask s1, GSBuildTask s2) {

	   int rollno1 = s1.getOrderNum();
	   int rollno2 = s2.getOrderNum();

	   /*For ascending order*/
	   return rollno1-rollno2;

	   /*For descending order*/
	   //rollno2-rollno1;
   }};
}
