/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buildpackage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author rdesouza
 */
public class BuildInstructions {

    private HashMap prop;
    private String[] taskTypes = {"SaveDTD", "SaveLOB", "MDX", "XMLFeed", "XSLT", "VendorDefinition", "BusinessFeed", "MessageType", "Workflow", "Event", "Task", "DDL", "PLSQL", "DML", "GSO", "GOC", "PublishingProfile"};
    private List completeFileList;
    private List changedFileList;
    private HashMap executionOrder;

    public BuildInstructions() {

    }

    public BuildInstructions(List complete, List changed) {
        completeFileList = complete;
        changedFileList = changed;
    }

    public void processBuild() {

        prop = getBuildProperties();

        PackageDescription pdGC = new PackageDescription(prop, "PackageDescription.xml", "GC");
        PackageDescription pdVD = new PackageDescription(prop, "PackageDescription_VD.xml", "VD");
        PackageDescription pdEngine = new PackageDescription(prop, "PackageDescription_Engines.xml", "ENGINE");

        BuildXML bld = new BuildXML(prop, "build.xml");
        ArrayList<GSBuildTask> buildTaskList = new ArrayList<GSBuildTask>();

        executionOrder = getExecutionOrder(completeFileList);
        buildTaskList = buildTaskList();
        Collections.sort(buildTaskList, GSBuildTask.orderCmp);
        writeTask(pdGC,bld,buildTaskList,"GC");
        pdGC.packageDescriptionEnd();
        bld.buildXMLGCEnd();
        bld.buildXMLVDStart();
        writeTask(pdVD,bld,buildTaskList,"VD");
        pdVD.packageDescriptionEnd();
        bld.buildXMLVDEnd();
        bld.buildXMLEngine();
    }

    public HashMap getBuildProperties() {
        ReadFile buildPropertiesFile = new ReadFile("build.properties");
        HashMap p = buildPropertiesFile.getProperties();
        String majorVersion = p.get("build.version").toString().substring(0, ordinalIndexOf(p.get("build.version").toString(), ".", 4));
        p.put("major.version", majorVersion);
        return p;
    }

    public HashMap getExecutionOrder(List completeFileList) {
        HashMap execOdr = new HashMap();

        for (int i = 0; i < completeFileList.size(); i++) {
            String fileAbsolutePath = completeFileList.get(i).toString();
            String filePath = fileAbsolutePath.substring(0, (fileAbsolutePath.lastIndexOf("/") + 1));
            String fileName = null;
            //System.out.println(fileAbsolutePath);
            if (fileAbsolutePath.contains(".")) {
                fileName = fileAbsolutePath.substring((fileAbsolutePath.lastIndexOf("/") + 1), fileAbsolutePath.lastIndexOf("."));
            } else {
                fileName = fileAbsolutePath.substring((fileAbsolutePath.lastIndexOf("/") + 1));
            }
            if (fileName.equals("ExecutionOrder")) {
                ReadFile rf = new ReadFile(prop.get("base.dir").toString() + fileAbsolutePath);
                execOdr.put(filePath, rf.getExecutionOrder());
                System.out.println("ExecOrder: " + filePath);
            }
        }
        return execOdr;
    }

    public ArrayList<GSBuildTask> buildTaskList() {
        ArrayList<GSBuildTask> bldTskLst = new ArrayList<GSBuildTask>();
        for (int i = 0; i < completeFileList.size(); i++) {
            String fileAbsolutePath = completeFileList.get(i).toString();
            String filePath = fileAbsolutePath.substring(0, (fileAbsolutePath.lastIndexOf("/") + 1));
            String fileName = null;
            //System.out.println(fileAbsolutePath);
            if (fileAbsolutePath.contains(".")) {
                fileName = fileAbsolutePath.substring((fileAbsolutePath.lastIndexOf("/") + 1), fileAbsolutePath.lastIndexOf("."));
            } else {
                fileName = fileAbsolutePath.substring((fileAbsolutePath.lastIndexOf("/") + 1));
            }
            String fileExtension = fileAbsolutePath.substring((fileAbsolutePath.lastIndexOf(".") + 1));
            //System.out.println(fileAbsolutePath);
            String srcPath = null;
            String destPath = null;
            if (filePath.contains("/configuration/resources/")) {
                srcPath = "tmp_out/" + fileAbsolutePath.substring((fileAbsolutePath.indexOf("configuration/resources/") + "configuration/resources/".length()));
                destPath = fileAbsolutePath.substring((fileAbsolutePath.indexOf("configuration/resources/")) + "configuration/resources/".length(), (fileAbsolutePath.lastIndexOf("/")));
            } else if (filePath.contains("/configuration/")) {
                srcPath = "tmp_out/" + fileAbsolutePath.substring((fileAbsolutePath.indexOf("configuration/") + "configuration/".length()));
            }
            String toDir = null;
            if (filePath.contains("/configuration/")) {
                toDir = fileAbsolutePath.substring((fileAbsolutePath.indexOf("configuration/") + "configuration/".length()), fileAbsolutePath.lastIndexOf("/"));
            }
            int order = getOrder((fileName + "." + fileExtension), filePath, executionOrder);
            if (order != 999999) {
                System.out.println(filePath + " " + fileName + " " + fileExtension + " " + srcPath + " " + destPath + " " + order);
            }
            if (filePath.equals("/configuration/workflows/") && isChangedFile(fileAbsolutePath, changedFileList)) {
                bldTskLst.add(new GSBuildTask("Workflow", true, "			<deployGSP name=\"Workflow: " + fileName + "\" destLoc=\"WFDataModel\" src=\"" + srcPath + "\" type=\"workflow\"/>", "		<copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/>", fileAbsolutePath, "GC", order));
            } else if (filePath.equals("/configuration/workflows/") && !isChangedFile(fileAbsolutePath, changedFileList)) {
                bldTskLst.add(new GSBuildTask("Workflow", false, "			<!--deployGSP name=\"Workflow: " + fileName + "\" destLoc=\"WFDataModel\" src=\"" + srcPath + "\" type=\"workflow\"/-->", "		<!--copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/-->", fileAbsolutePath, "GC", order));
            } else if (filePath.contains("/configuration/resources/mapping/") && isChangedFile(fileAbsolutePath, changedFileList)) {
                toDir = toDir.substring(toDir.indexOf("resources/") + "resources/".length());
                bldTskLst.add(new GSBuildTask("MDX", true, "			<deployResource name=\"MDX: " + fileName + "\" destLoc=\"CFDataModel\" src=\"" + srcPath + "\" dest=\"" + destPath + "\"  encoding=\"windows-1252\"/>", "		<copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/>", fileAbsolutePath, "GC", order));
            } else if (filePath.contains("/configuration/resources/mapping/") && !isChangedFile(fileAbsolutePath, changedFileList)) {
                toDir = toDir.substring(toDir.indexOf("resources/") + "resources/".length());
                bldTskLst.add(new GSBuildTask("MDX", false, "			<!--deployResource name=\"MDX: " + fileName + "\" destLoc=\"CFDataModel\" src=\"" + srcPath + "\" dest=\"" + destPath + "\"  encoding=\"windows-1252\"/-->", "		<!--copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/-->", fileAbsolutePath, "GC", order));
            } else if (filePath.endsWith("/configuration/vendordefinitions/") && isChangedFile(fileAbsolutePath, changedFileList)) {
                bldTskLst.add(new GSBuildTask("VendorDefinition", true, "			<deployGSP name=\"VendorDefinition: " + fileName + "\" destLoc=\"CFDataModel\" src=\"" + srcPath + "\"/>", "		<copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/>", fileAbsolutePath, "GC", order));
            } else if (filePath.endsWith("/configuration/vendordefinitions/") && !isChangedFile(fileAbsolutePath, changedFileList)) {
                bldTskLst.add(new GSBuildTask("VendorDefinition", false, "			<!--deployGSP name=\"VendorDefinition: " + fileName + "\" destLoc=\"CFDataModel\" src=\"" + srcPath + "\"/-->", "		<!--copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/-->", fileAbsolutePath, "GC", order));
            } else if (filePath.contains("xml/feeds/") && isChangedFile(fileAbsolutePath, changedFileList)) {
                toDir = toDir.substring(toDir.indexOf("resources/") + "resources/".length());
                bldTskLst.add(new GSBuildTask("XMLFeed", true, "			<deployResource name=\"XML Feed: " + fileName + "\" destLoc=\"CFDataModel\" src=\"" + srcPath + "\" dest=\"" + destPath + "\" encoding=\"windows-1252\"/>", "		<copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/>", fileAbsolutePath, "GC", order));
            } else if (filePath.contains("xml/feeds/") && !isChangedFile(fileAbsolutePath, changedFileList)) {
                toDir = toDir.substring(toDir.indexOf("resources/") + "resources/".length());
                bldTskLst.add(new GSBuildTask("XMLFeed", false, "			<!--deployResource name=\"XML Feed: " + fileName + "\" destLoc=\"CFDataModel\" src=\"" + srcPath + "\" dest=\"" + destPath + "\" encoding=\"windows-1252\"/-->", "		<!--copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/-->", fileAbsolutePath, "GC", order));
            } else if (filePath.contains("/xslt/") && isChangedFile(fileAbsolutePath, changedFileList)) {
                toDir = toDir.substring(toDir.indexOf("resources/") + "resources/".length());
                bldTskLst.add(new GSBuildTask("XSLT", true, "			<deployResource name=\"XSLT: " + fileName + "\" destLoc=\"CFDataModel\" src=\"" + srcPath + "\" dest=\"" + destPath + "\" encoding=\"windows-1252\"/>", "		<copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/>", fileAbsolutePath, "GC", order));
            } else if (filePath.contains("/xslt/") && !isChangedFile(fileAbsolutePath, changedFileList)) {
                toDir = toDir.substring(toDir.indexOf("resources/") + "resources/".length());
                bldTskLst.add(new GSBuildTask("XSLT", false, "			<!--deployResource name=\"XSLT: " + fileName + "\" destLoc=\"CFDataModel\" src=\"" + srcPath + "\" dest=\"" + destPath + "\" encoding=\"windows-1252\"/-->", "		<!--copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/-->", fileAbsolutePath, "GC", order));
            } else if (filePath.endsWith("/configuration/vendordefinitions/businessfeeds/") && isChangedFile(fileAbsolutePath, changedFileList)) {
                bldTskLst.add(new GSBuildTask("BusinessFeed", true, "			<deployGSP name=\"BusinessFeed: " + fileName + "\" destLoc=\"CFDataModel\" src=\"" + srcPath + "\"/>", "		<copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/>", fileAbsolutePath, "GC", order));
            } else if (filePath.endsWith("/configuration/vendordefinitions/businessfeeds/") && !isChangedFile(fileAbsolutePath, changedFileList)) {
                bldTskLst.add(new GSBuildTask("BusinessFeed", false, "			<!--deployGSP name=\"BusinessFeed: " + fileName + "\" destLoc=\"CFDataModel\" src=\"" + srcPath + "\"/-->", "		<!--copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/-->", fileAbsolutePath, "GC", order));
            } else if (filePath.endsWith("/configuration/vendordefinitions/messagetypes/") && isChangedFile(fileAbsolutePath, changedFileList)) {
                bldTskLst.add(new GSBuildTask("MessageType", true, "			<deployGSP name=\"MessageType: " + fileName + "\" destLoc=\"CFDataModel\" src=\"" + srcPath + "\"/>", "		<copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/>", fileAbsolutePath, "GC", order));
            } else if (filePath.endsWith("/configuration/vendordefinitions/messagetypes/") && !isChangedFile(fileAbsolutePath, changedFileList)) {
                bldTskLst.add(new GSBuildTask("MessageType", false, "			<!--deployGSP name=\"MessageType: " + fileName + "\" destLoc=\"CFDataModel\" src=\"" + srcPath + "\"/-->", "		<!--copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/-->", fileAbsolutePath, "GC", order));
            } else if (filePath.equals("/configuration/events/") && isChangedFile(fileAbsolutePath, changedFileList)) {
                bldTskLst.add(new GSBuildTask("Event", true, "			<deployGSP name=\"Event: " + fileName + "\" destLoc=\"WFDataModel\" src=\"" + srcPath + "\" type=\"workflow\"/>", "		<copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/>", fileAbsolutePath, "GC", order));
            } else if (filePath.equals("/configuration/events/") && !isChangedFile(fileAbsolutePath, changedFileList)) {
                bldTskLst.add(new GSBuildTask("Event", false, "			<!--deployGSP name=\"Event: " + fileName + "\" destLoc=\"WFDataModel\" src=\"" + srcPath + "\" type=\"workflow\"/-->", "		<!--copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/-->", fileAbsolutePath, "GC", order));
            } else if (filePath.equals("/configuration/tasks/") && isChangedFile(fileAbsolutePath, changedFileList)) {
                bldTskLst.add(new GSBuildTask("Task", true, "			<deployGSP name=\"Task: " + fileName + "\" destLoc=\"WFDataModel\" src=\"" + srcPath + "\"/>", "		<copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/>", fileAbsolutePath, "GC", order));
            } else if (filePath.equals("/configuration/tasks/") && !isChangedFile(fileAbsolutePath, changedFileList)) {
                bldTskLst.add(new GSBuildTask("Task", false, "			<!--deployGSP name=\"Task: " + fileName + "\" destLoc=\"WFDataModel\" src=\"" + srcPath + "\"/-->", "		<!--copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/-->", fileAbsolutePath, "GC", order));
            } else if (filePath.equals("/configuration/publishingProfiles/") && isChangedFile(fileAbsolutePath, changedFileList)) {
                bldTskLst.add(new GSBuildTask("PublishingProfile", true, "			<deployGSP name=\"PublishingProfile: " + fileName + "\" destLoc=\"WFDataModel\" src=\"" + srcPath + "\"/>", "		<copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/>", fileAbsolutePath, "GC", order));
            } else if (filePath.equals("/configuration/publishingProfiles/") && !isChangedFile(fileAbsolutePath, changedFileList)) {
                bldTskLst.add(new GSBuildTask("PublishingProfile", false, "			<!--deployGSP name=\"PublishingProfile: " + fileName + "\" destLoc=\"WFDataModel\" src=\"" + srcPath + "\"/-->", "		<!--copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/-->", fileAbsolutePath, "GC", order));
            } else if (filePath.equals("/configuration/sql/patch/" + prop.get("build.package.stream") + "/" + prop.get("major.version") + "/package/DDL/") && isChangedFile(fileAbsolutePath, changedFileList)) {
                if (fileName.endsWith("_GC")) {
                    bldTskLst.add(new GSBuildTask("DDL", true, "			<sql name=\"DDL: " + fileName + "\" src=\"" + srcPath + "\" destLoc=\"GSDMDataModel\" patchLevel=\"" + prop.get("build.version") + "\" dbDialect=\"ORACLE\"/>", "		<copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/>", fileAbsolutePath, "GC", order));
                } else if (fileName.endsWith("_VD")) {
                    bldTskLst.add(new GSBuildTask("DDL", true, "			<sql name=\"DDL: " + fileName + "\" src=\"" + srcPath + "\" destLoc=\"VDDBDataModel\" patchLevel=\"" + prop.get("build.version") + "\" dbDialect=\"ORACLE\"/>", "		<copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/>", fileAbsolutePath, "VD", order));
                } else {
                    bldTskLst.add(new GSBuildTask("DDL", true, "			<sql name=\"DDL: " + fileName + "\" src=\"" + srcPath + "\" destLoc=\"GSDMDataModel\" patchLevel=\"" + prop.get("build.version") + "\" dbDialect=\"ORACLE\"/>", "		<copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/>", fileAbsolutePath, "GC", order));
                    bldTskLst.add(new GSBuildTask("DDL", true, "			<sql name=\"DDL: " + fileName + "\" src=\"" + srcPath + "\" destLoc=\"VDDBDataModel\" patchLevel=\"" + prop.get("build.version") + "\" dbDialect=\"ORACLE\"/>", "		<copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/>", fileAbsolutePath, "VD", order));
                }
            } else if (filePath.equals("/configuration/sql/patch/" + prop.get("build.package.stream") + "/" + prop.get("major.version") + "/package/DDL/") && !isChangedFile(fileAbsolutePath, changedFileList)) {
                if (fileName.endsWith("_GC")) {
                    bldTskLst.add(new GSBuildTask("DDL", false, "			<!--sql name=\"DDL: " + fileName + "\" src=\"" + srcPath + "\" destLoc=\"GSDMDataModel\" patchLevel=\"" + prop.get("build.version") + "\" dbDialect=\"ORACLE\"/-->", "		<!--copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/-->", fileAbsolutePath, "GC", order));
                } else if (fileName.endsWith("_VD")) {
                    bldTskLst.add(new GSBuildTask("DDL", false, "			<!--sql name=\"DDL: " + fileName + "\" src=\"" + srcPath + "\" destLoc=\"VDDBDataModel\" patchLevel=\"" + prop.get("build.version") + "\" dbDialect=\"ORACLE\"/-->", "		<!--copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/-->", fileAbsolutePath, "VD", order));
                } else {
                    bldTskLst.add(new GSBuildTask("DDL", false, "			<!--sql name=\"DDL: " + fileName + "\" src=\"" + srcPath + "\" destLoc=\"GSDMDataModel\" patchLevel=\"" + prop.get("build.version") + "\" dbDialect=\"ORACLE\"/-->", "		<!--copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/-->", fileAbsolutePath, "GC", order));
                    bldTskLst.add(new GSBuildTask("DDL", false, "			<!--sql name=\"DDL: " + fileName + "\" src=\"" + srcPath + "\" destLoc=\"VDDBDataModel\" patchLevel=\"" + prop.get("build.version") + "\" dbDialect=\"ORACLE\"/-->", "		<!--copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/-->", fileAbsolutePath, "VD", order));
                }
            } else if (filePath.equals("/configuration/sql/patch/" + prop.get("build.package.stream") + "/" + prop.get("major.version") + "/package/DML/") && isChangedFile(fileAbsolutePath, changedFileList)) {
                if (fileName.endsWith("_GC")) {
                    bldTskLst.add(new GSBuildTask("DML", true, "			<sql name=\"DML: " + fileName + "\" src=\"" + srcPath + "\" destLoc=\"GSDMDataModel\" patchLevel=\"" + prop.get("build.version") + "\" dbDialect=\"ORACLE\"/>", "		<copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/>", fileAbsolutePath, "GC", order));
                } else if (fileName.endsWith("_VD")) {
                    bldTskLst.add(new GSBuildTask("DML", true, "			<sql name=\"DML: " + fileName + "\" src=\"" + srcPath + "\" destLoc=\"VDDBDataModel\" patchLevel=\"" + prop.get("build.version") + "\" dbDialect=\"ORACLE\"/>", "		<copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/>", fileAbsolutePath, "VD", order));
                } else {
                    bldTskLst.add(new GSBuildTask("DML", true, "			<sql name=\"DML: " + fileName + "\" src=\"" + srcPath + "\" destLoc=\"GSDMDataModel\" patchLevel=\"" + prop.get("build.version") + "\" dbDialect=\"ORACLE\"/>", "		<copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/>", fileAbsolutePath, "GC", order));
                    bldTskLst.add(new GSBuildTask("DML", true, "			<sql name=\"DML: " + fileName + "\" src=\"" + srcPath + "\" destLoc=\"VDDBDataModel\" patchLevel=\"" + prop.get("build.version") + "\" dbDialect=\"ORACLE\"/>", "		<copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/>", fileAbsolutePath, "VD", order));
                }
            } else if (filePath.equals("/configuration/sql/patch/" + prop.get("build.package.stream") + "/" + prop.get("major.version") + "/package/DML/") && !isChangedFile(fileAbsolutePath, changedFileList)) {
                if (fileName.endsWith("_GC")) {
                    bldTskLst.add(new GSBuildTask("DML", false, "			<!--sql name=\"DML: " + fileName + "\" src=\"" + srcPath + "\" destLoc=\"GSDMDataModel\" patchLevel=\"" + prop.get("build.version") + "\" dbDialect=\"ORACLE\"/-->", "		<!--copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/-->", fileAbsolutePath, "GC", order));
                } else if (fileName.endsWith("_VD")) {
                    bldTskLst.add(new GSBuildTask("DML", false, "			<!--sql name=\"DML: " + fileName + "\" src=\"" + srcPath + "\" destLoc=\"VDDBDataModel\" patchLevel=\"" + prop.get("build.version") + "\" dbDialect=\"ORACLE\"/-->", "		<!--copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/-->", fileAbsolutePath, "VD", order));
                } else {
                    bldTskLst.add(new GSBuildTask("DML", false, "			<!--sql name=\"DML: " + fileName + "\" src=\"" + srcPath + "\" destLoc=\"GSDMDataModel\" patchLevel=\"" + prop.get("build.version") + "\" dbDialect=\"ORACLE\"/-->", "		<!--copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/-->", fileAbsolutePath, "GC", order));
                    bldTskLst.add(new GSBuildTask("DML", false, "			<!--sql name=\"DML: " + fileName + "\" src=\"" + srcPath + "\" destLoc=\"VDDBDataModel\" patchLevel=\"" + prop.get("build.version") + "\" dbDialect=\"ORACLE\"/-->", "		<!--copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/-->", fileAbsolutePath, "VD", order));
                }
            } else if (filePath.equals("/configuration/sql/patch/" + prop.get("build.package.stream") + "/" + prop.get("major.version") + "/package/PLSQL/") && isChangedFile(fileAbsolutePath, changedFileList)) {
                if (fileName.endsWith("_GC")) {
                    bldTskLst.add(new GSBuildTask("PLSQL", true, "			<sqloperational name=\"PLSQL: " + fileName + "\" src=\"" + srcPath + "\" destLoc=\"GSDMDataModel\" patchLevel=\"" + prop.get("build.version") + "\"  dbDialect=\"ORACLE\"/>", "		<copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/>", fileAbsolutePath, "GC", order));
                } else if (fileName.endsWith("_VD")) {
                    bldTskLst.add(new GSBuildTask("PLSQL", true, "			<sqloperational name=\"PLSQL: " + fileName + "\" src=\"" + srcPath + "\" destLoc=\"VDDBDataModel\" patchLevel=\"" + prop.get("build.version") + "\"  dbDialect=\"ORACLE\"/>", "		<copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/>", fileAbsolutePath, "VD", order));
                } else {
                    bldTskLst.add(new GSBuildTask("PLSQL", true, "			<sqloperational name=\"PLSQL: " + fileName + "\" src=\"" + srcPath + "\" destLoc=\"GSDMDataModel\" patchLevel=\"" + prop.get("build.version") + "\"  dbDialect=\"ORACLE\"/>", "		<copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/>", fileAbsolutePath, "GC", order));
                    bldTskLst.add(new GSBuildTask("PLSQL", true, "			<sqloperational name=\"PLSQL: " + fileName + "\" src=\"" + srcPath + "\" destLoc=\"VDDBDataModel\" patchLevel=\"" + prop.get("build.version") + "\"  dbDialect=\"ORACLE\"/>", "		<copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/>", fileAbsolutePath, "VD", order));
                }
            } else if (filePath.equals("/configuration/sql/patch/" + prop.get("build.package.stream") + "/" + prop.get("major.version") + "/package/PLSQL/") && isChangedFile(fileAbsolutePath, changedFileList)) {
                if (fileName.endsWith("_GC")) {
                    bldTskLst.add(new GSBuildTask("PLSQL", true, "			<!--sqloperational name=\"PLSQL: " + fileName + "\" src=\"" + srcPath + "\" destLoc=\"GSDMDataModel\" patchLevel=\"" + prop.get("build.version") + "\"  dbDialect=\"ORACLE\"/-->", "		<!--copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/-->", fileAbsolutePath, "GC", order));
                } else if (fileName.endsWith("_VD")) {
                    bldTskLst.add(new GSBuildTask("PLSQL", true, "			<!--sqloperational name=\"PLSQL: " + fileName + "\" src=\"" + srcPath + "\" destLoc=\"VDDBDataModel\" patchLevel=\"" + prop.get("build.version") + "\"  dbDialect=\"ORACLE\"/-->", "		<!--copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/-->", fileAbsolutePath, "VD", order));
                } else {
                    bldTskLst.add(new GSBuildTask("PLSQL", true, "			<!--sqloperational name=\"PLSQL: " + fileName + "\" src=\"" + srcPath + "\" destLoc=\"GSDMDataModel\" patchLevel=\"" + prop.get("build.version") + "\"  dbDialect=\"ORACLE\"/-->", "		<!--copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/-->", fileAbsolutePath, "GC", order));
                    bldTskLst.add(new GSBuildTask("PLSQL", true, "			<!--sqloperational name=\"PLSQL: " + fileName + "\" src=\"" + srcPath + "\" destLoc=\"VDDBDataModel\" patchLevel=\"" + prop.get("build.version") + "\"  dbDialect=\"ORACLE\"/-->", "		<!--copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/-->", fileAbsolutePath, "VD", order));
                }
            } else if (filePath.equals("/configuration/gso/") && fileExtension.equals("gso") && isChangedFile(fileAbsolutePath, changedFileList)) {
                bldTskLst.add(new GSBuildTask("GSO", true, "			<deployGSE name=\"GSO: " + fileName + "\" destLoc=\"GSDMDataModel\" src=\"" + srcPath + "\" encoding=\"windows-1252\"/>", "		<copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/>", fileAbsolutePath, "GC", order));
            } else if (filePath.equals("/configuration/gso/") && fileExtension.equals("gso") && !isChangedFile(fileAbsolutePath, changedFileList)) {
                bldTskLst.add(new GSBuildTask("GSO", false, "			<!--deployGSE name=\"GSO: " + fileName + "\" destLoc=\"GSDMDataModel\" src=\"" + srcPath + "\" encoding=\"windows-1252\"/-->", "		<!--copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/-->", fileAbsolutePath, "GC", order));
            } else if (filePath.equals("/configuration/gso/") && fileExtension.equals("goc") && isChangedFile(fileAbsolutePath, changedFileList)) {
                bldTskLst.add(new GSBuildTask("GOC", true, "			<deployGSE name=\"GSO: " + fileName + "\" destLoc=\"GSDMDataModel\" src=\"" + srcPath + "\" encoding=\"windows-1252\"/>", "		<copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/>", fileAbsolutePath, "GC", order));
            } else if (filePath.equals("/configuration/gso/") && fileExtension.equals("goc") && !isChangedFile(fileAbsolutePath, changedFileList)) {
                bldTskLst.add(new GSBuildTask("GOC", false, "			<!--deployGSE name=\"GSO: " + fileName + "\" destLoc=\"GSDMDataModel\" src=\"" + srcPath + "\" encoding=\"windows-1252\"/-->", "		<!--copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/-->", fileAbsolutePath, "GC", order));
            } else if (filePath.endsWith("/reference/rulexml/") && fileExtension.equals("xml") && isChangedFile(fileAbsolutePath, changedFileList)) {
                ReadFile rf = new ReadFile(prop.get("base.dir").toString() + fileAbsolutePath);
                String dtd = rf.getDTDFile();
                String dtdPath = "tmp_out/" + fileAbsolutePath.substring((fileAbsolutePath.indexOf("configuration/") + "configuration/".length()), (fileAbsolutePath.lastIndexOf("/") + 1)) + dtd;
                String dtdSVNPath = fileAbsolutePath.substring((fileAbsolutePath.indexOf("/configuration/")), (fileAbsolutePath.lastIndexOf("/") + 1)) + dtd;
                if (fileName.endsWith("_GC")) {
                    bldTskLst.add(new GSBuildTask("SaveDTD", true, "			<savedtd name=\"Save DTD: " + fileName + "\" destLoc=\"CFDataModel\" src=\"" + srcPath + "\" columnName=\"MSG_SET_BLOB\" columnkeyname=\"XML_MSG_SET_ID\" columnkeyvalue=\"1\" dtdFile=\"" + dtdPath + "\" lastChgUsrId=\"RMBP:CSTM\" tableName=\"FT_T_XMGS\"/>", "		<copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/>", fileAbsolutePath, "GC", order));
                    bldTskLst.add(new GSBuildTask("SaveDTD", false, "", "		<copy file=\"${base.dir}" + dtdSVNPath + "\" todir=\"${temp.dir}/" + toDir + "\"/>", fileAbsolutePath, "GC", order));
                } else if (fileName.endsWith("_VD")) {
                    bldTskLst.add(new GSBuildTask("SaveDTD", true, "			<savedtd name=\"Save DTD: " + fileName + "\" destLoc=\"VDDBDataModel\" src=\"" + srcPath + "\" columnName=\"MSG_SET_BLOB\" columnkeyname=\"XML_MSG_SET_ID\" columnkeyvalue=\"1\" dtdFile=\"" + dtdPath + "\" lastChgUsrId=\"RMBP:CSTM\" tableName=\"FT_T_XMGS\"/>", "		<copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/>", "", "VD", order));
                    bldTskLst.add(new GSBuildTask("SaveDTD", false, "", "		<copy file=\"${base.dir}" + dtdSVNPath + "\" todir=\"${temp.dir}/" + toDir + "\"/>", fileAbsolutePath, "VD", order));
                } else {
                    bldTskLst.add(new GSBuildTask("SaveDTD", true, "			<savedtd name=\"Save DTD: " + fileName + "\" destLoc=\"CFDataModel\" src=\"" + srcPath + "\" columnName=\"MSG_SET_BLOB\" columnkeyname=\"XML_MSG_SET_ID\" columnkeyvalue=\"1\" dtdFile=\"" + dtdPath + "\" lastChgUsrId=\"RMBP:CSTM\" tableName=\"FT_T_XMGS\"/>", "		<copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/>", fileAbsolutePath, "GC", order));
                    bldTskLst.add(new GSBuildTask("SaveDTD", false, "", "		<copy file=\"${base.dir}" + dtdSVNPath + "\" todir=\"${temp.dir}/" + toDir + "\"/>", fileAbsolutePath, "GC", order));
                    bldTskLst.add(new GSBuildTask("SaveDTD", true, "			<savedtd name=\"Save DTD: " + fileName + "\" destLoc=\"VDDBDataModel\" src=\"" + srcPath + "\" columnName=\"MSG_SET_BLOB\" columnkeyname=\"XML_MSG_SET_ID\" columnkeyvalue=\"1\" dtdFile=\"" + dtdPath + "\" lastChgUsrId=\"RMBP:CSTM\" tableName=\"FT_T_XMGS\"/>", "		<copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/>", "", "VD", order));
                    bldTskLst.add(new GSBuildTask("SaveDTD", false, "", "		<copy file=\"${base.dir}" + dtdSVNPath + "\" todir=\"${temp.dir}/" + toDir + "\"/>", fileAbsolutePath, "VD", order));
                }
            } else if (filePath.endsWith("/reference/rulexml/") && fileExtension.equals("xml") && !isChangedFile(fileAbsolutePath, changedFileList)) {
                ReadFile rf = new ReadFile(prop.get("base.dir").toString() + fileAbsolutePath);
                String dtd = rf.getDTDFile();
                String dtdPath = "tmp_out/" + fileAbsolutePath.substring((fileAbsolutePath.indexOf("configuration/") + "configuration/".length()), (fileAbsolutePath.lastIndexOf("/") + 1)) + dtd;
                String dtdSVNPath = fileAbsolutePath.substring((fileAbsolutePath.indexOf("/configuration/")), (fileAbsolutePath.lastIndexOf("/") + 1)) + dtd;
                if (fileName.endsWith("_GC")) {
                    bldTskLst.add(new GSBuildTask("SaveDTD", true, "			<!--savedtd name=\"Save DTD: " + fileName + "\" destLoc=\"CFDataModel\" src=\"" + srcPath + "\" columnName=\"MSG_SET_BLOB\" columnkeyname=\"XML_MSG_SET_ID\" columnkeyvalue=\"1\" dtdFile=\"" + dtdPath + "\" lastChgUsrId=\"RMBP:CSTM\" tableName=\"FT_T_XMGS\"/-->", "		<!--copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/-->", fileAbsolutePath, "GC", order));
                    bldTskLst.add(new GSBuildTask("SaveDTD", false, "", "		<!--copy file=\"${base.dir}" + dtdSVNPath + "\" todir=\"${temp.dir}/" + toDir + "\"/-->", fileAbsolutePath, "GC", order));
                } else if (fileName.endsWith("_VD")) {
                    bldTskLst.add(new GSBuildTask("SaveDTD", true, "			<!--savedtd name=\"Save DTD: " + fileName + "\" destLoc=\"VDDBDataModel\" src=\"" + srcPath + "\" columnName=\"MSG_SET_BLOB\" columnkeyname=\"XML_MSG_SET_ID\" columnkeyvalue=\"1\" dtdFile=\"" + dtdPath + "\" lastChgUsrId=\"RMBP:CSTM\" tableName=\"FT_T_XMGS\"/-->", "", "", "VD", order));
                    bldTskLst.add(new GSBuildTask("SaveDTD", false, "", "		<!--copy file=\"${base.dir}" + dtdSVNPath + "\" todir=\"${temp.dir}/" + toDir + "\"/-->", fileAbsolutePath, "VD", order));
                } else {
                    bldTskLst.add(new GSBuildTask("SaveDTD", true, "			<!--savedtd name=\"Save DTD: " + fileName + "\" destLoc=\"CFDataModel\" src=\"" + srcPath + "\" columnName=\"MSG_SET_BLOB\" columnkeyname=\"XML_MSG_SET_ID\" columnkeyvalue=\"1\" dtdFile=\"" + dtdPath + "\" lastChgUsrId=\"RMBP:CSTM\" tableName=\"FT_T_XMGS\"/-->", "		<!--copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/-->", fileAbsolutePath, "GC", order));
                    bldTskLst.add(new GSBuildTask("SaveDTD", false, "", "		<!--copy file=\"${base.dir}" + dtdSVNPath + "\" todir=\"${temp.dir}/" + toDir + "\"/-->", fileAbsolutePath, "GC", order));
                    bldTskLst.add(new GSBuildTask("SaveDTD", true, "			<!--savedtd name=\"Save DTD: " + fileName + "\" destLoc=\"VDDBDataModel\" src=\"" + srcPath + "\" columnName=\"MSG_SET_BLOB\" columnkeyname=\"XML_MSG_SET_ID\" columnkeyvalue=\"1\" dtdFile=\"" + dtdPath + "\" lastChgUsrId=\"RMBP:CSTM\" tableName=\"FT_T_XMGS\"/-->", "", "", "VD", order));
                    bldTskLst.add(new GSBuildTask("SaveDTD", false, "", "		<!--copy file=\"${base.dir}" + dtdSVNPath + "\" todir=\"${temp.dir}/" + toDir + "\"/-->", fileAbsolutePath, "VD", order));
                }
            } else if (filePath.endsWith("/reference/rulexml/") && fileExtension.equals("xml") && !isChangedFile(fileAbsolutePath, changedFileList)) {
            } else if (filePath.endsWith("/streetreffiles/") && fileExtension.equals("xml") && isChangedFile(fileAbsolutePath, changedFileList)) {
                ReadFile rf = new ReadFile(prop.get("base.dir").toString() + fileAbsolutePath);
                String dtd = rf.getDTDFile();
                String dtdPath = "tmp_out/" + fileAbsolutePath.substring((fileAbsolutePath.indexOf("configuration/") + "configuration/".length()), (fileAbsolutePath.lastIndexOf("/") + 1)) + dtd;
                String dtdSVNPath = fileAbsolutePath.substring((fileAbsolutePath.indexOf("/configuration/")), (fileAbsolutePath.lastIndexOf("/") + 1)) + dtd;
                bldTskLst.add(new GSBuildTask("SaveLOB", true, "			<savelob name=\"Save LOB: " + fileName + "\" destLoc=\"CFDataModel\" src=\"" + srcPath + "\" columnName=\"XML_CONFIG_CLOB\" columnkeyname=\"XML_CONFIG_MNEM\" columnkeyvalue=\"" + fileName + "\" dtdFile=\"" + dtdPath + "\" lastChgUsrId=\"RMBP:CSTM\" tableName=\"FT_T_XCFG\"/>", "		<copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/>", fileAbsolutePath, "GC", order));
                bldTskLst.add(new GSBuildTask("SaveLOB", false, "", "		<copy file=\"${base.dir}" + dtdSVNPath + "\" todir=\"${temp.dir}/" + toDir + "\"/>", fileAbsolutePath, "GC", order));
            } else if (filePath.endsWith("/streetreffiles/") && fileExtension.equals("xml") && !isChangedFile(fileAbsolutePath, changedFileList)) {
                ReadFile rf = new ReadFile(prop.get("base.dir").toString() + fileAbsolutePath);
                String dtd = rf.getDTDFile();
                String dtdPath = "tmp_out/" + fileAbsolutePath.substring((fileAbsolutePath.indexOf("configuration/") + "configuration/".length()), (fileAbsolutePath.lastIndexOf("/") + 1)) + dtd;
                String dtdSVNPath = fileAbsolutePath.substring((fileAbsolutePath.indexOf("/configuration/")), (fileAbsolutePath.lastIndexOf("/") + 1)) + dtd;
                bldTskLst.add(new GSBuildTask("SaveLOB", false, "			<!--savelob name=\"Save LOB: " + fileName + "\" destLoc=\"CFDataModel\" src=\"" + srcPath + "\" columnName=\"XML_CONFIG_CLOB\" columnkeyname=\"XML_CONFIG_MNEM\" columnkeyvalue=\"" + fileName + "\" dtdFile=\"" + dtdPath + "\" lastChgUsrId=\"RMBP:CSTM\" tableName=\"FT_T_XCFG\"/-->", "		<!--copy file=\"${base.dir}" + fileAbsolutePath + "\" todir=\"${temp.dir}/" + toDir + "\"/-->", fileAbsolutePath, "GC", order));
                bldTskLst.add(new GSBuildTask("SaveLOB", false, "", "		<!--copy file=\"${base.dir}" + dtdSVNPath + "\" todir=\"${temp.dir}/" + toDir + "\"/-->", fileAbsolutePath, "GC", order));
            } else {
                System.out.println("Skipped: " + fileAbsolutePath);
            }
        }
        return bldTskLst;
    }

    public void writeTask(PackageDescription pd, BuildXML bld, ArrayList<GSBuildTask> buildTaskList, String type) {
        for (int i = 0; i < taskTypes.length; i++) {
            pd.append("			<!--START " + taskTypes[i] + "-->");
            bld.append("		<!--START " + taskTypes[i] + "-->");
            for (int j = 0; j < buildTaskList.size(); j++) {
                GSBuildTask bldtsk = (GSBuildTask) buildTaskList.get(j);
                //System.out.println(pdtsk.getType()+" "+PDTypes[i]+ " " +pdtsk.getTaskString());
                if (bldtsk.getType().equals(taskTypes[i])) {
                    if (bldtsk.getSchema().equals(type)) {
                        pd.append(bldtsk.getPDTaskString());
                        bld.append(bldtsk.getBuildTaskString());

                        if (bldtsk.isActive() && !bldtsk.getFilePath().equals("")) {
                            System.out.println("Changed: " + bldtsk.getFilePath());
                        }
                    }

                }
            }
            pd.append("			<!--END " + taskTypes[i] + "-->\n\n");
            bld.append("		<!--END " + taskTypes[i] + "-->\n\n");
        }
    }

    public static int ordinalIndexOf(String str, String substr, int n) {
        try {
            int pos = str.indexOf(substr);
            while (--n > 0 && pos != -1) {
                pos = str.indexOf(substr, pos + 1);
            }
            if (pos == -1) {
                return str.length();
            }
            return pos;
        } catch (Exception e) {
            return str.length();
        }
    }

    public static boolean isChangedFile(String fileAbsolutePath, List changedFileList) {
        Pattern p = Pattern.compile(".*" + fileAbsolutePath + ".*");
        for (int i = 0; i < changedFileList.size(); i++) {
            if (p.matcher(changedFileList.get(i).toString()).matches()) {
                return true;
            }
        }
        return false;
    }

    public static int getOrder(String fileName, String filePath, HashMap executionOrder) {
        int order = 999999;
        if (filePath.contains("DML") || filePath.contains("DDL") || filePath.contains("PLSQL")) {
            filePath = filePath.substring(0, filePath.lastIndexOf("/"));
            filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
            //System.out.println("getOrder: " + filePath + " | " + fileName);
        }
        try {
            List l = (List) executionOrder.get(filePath);
            for (int i = 0; i < l.size(); i++) {
                //System.out.println("Array: " + l.get(i));
                if (l.get(i).equals(fileName)) {
                    return i;
                }
            }
        } catch (Exception e) {
            //System.out.println(e);
        }
        return order;
    }

}
