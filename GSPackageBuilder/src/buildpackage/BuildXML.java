/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buildpackage;

import java.util.HashMap;

/**
 *
 * @author rdesouza
 */
public class BuildXML {

    private WriteFile buildFile;
    private HashMap prop;

    public BuildXML(HashMap prp, String fileName) {
        buildFile = new WriteFile(fileName);
        prop = prp;
        buildXMLStart();
    }

    public void buildXMLStart() {
        buildFile.setFileDataOverwrite("<project name=\"j4fe\" default=\"build-all\">\n"
                + "	<!-- Specify all paths here, also only specify relative paths -->\n"
                + "	<property name=\"build.dir\" value=\".\"/>\n"
                + "	<property location=\".\" file=\"${build.dir}/build.properties\"/>\n"
                + "\n"
                + "	<target name=\"makedir\" depends=\"prepare\">\n"
                + "		<echo message=\"Base Directory : ${base.dir}\"/>\n"
                + "		<echo message=\"Packages Directory : ${packages.dir}\"/>\n"
                + "		<mkdir dir=\"${temp.dir}\"/>\n"
                + "		<mkdir dir=\"${packages.dir}\"/>\n"
                + "		<mkdir dir=\"${packages.dir}/${today}\"/>\n"
                + "	</target>\n"
                + "\n"
                + "	<target name=\"clean\">\n"
                + "		<delete includeemptydirs=\"true\">\n"
                + "			<fileset dir=\"${temp.dir}\" includes=\"**/*\"/>\n"
                + "		</delete>\n"
                + "	</target>\n"
                + "\n"
                + "	<target name=\"prepare\">\n"
                + "		<tstamp>\n"
                + "			<format property=\"today\" pattern=\"yyyyMMdd\" locale=\"en,UK\"/>\n"
                + "		</tstamp>\n"
                + "	</target>\n"
                + "\n"
                + "	<!-- packaging GC components -->\n"
                + "	<target name=\"build.customgc\">");
    }

    public void append(String line) {
        buildFile.setFileDataAppend(line);
    }

    public void buildXMLGCEnd() {
    	System.out.println(prop.get("build.package.stream").toString());
    	System.out.println(prop.get("major.version").toString());
        buildFile.setFileDataAppend("		<copy todir=\"${temp.dir}/sql/patch/" + prop.get("build.package.stream").toString() + "/" + prop.get("major.version").toString() + "\">\n"
                + "		<fileset dir=\"${base.dir}/configuration/sql/patch/" + prop.get("build.package.stream").toString() + "/" + prop.get("major.version").toString() + "\"/>\n"
                + "		</copy>"
                + "		<!--Custom Workstation Code-->\n"
                + "		<copy todir=\"${temp.dir}/workstation\">\n"
                + "		<fileset dir=\"${base.dir}/workstation\"/>\n"
                + "		</copy>"
                + "		<!-- tar the copied files -->\n"
                + "		<tar destfile=\"${temp.dir}/${build.package.name}.tar.gz\" compression=\"gzip\">\n"
                + "			<tarfileset dir=\"${temp.dir}\">\n"
                + "				<include name=\"**\"/>\n"
                + "			</tarfileset>\n"
                + "		</tar>\n"
                + "		\n"
                + "		<!-- move the sfile out of the temporary directory so they do not get packaged in the tar.gz file -->\n"
                + "		<copy file=\"./PackageDescription.xml\" tofile=\"${temp.dir}/PackageDescription.xml\"/>\n"
                + "		<copy file=\"./ReleaseNotes.docx\" tofile=\"${temp.dir}/ReleaseNotes.docx\" failonerror=\"false\"/>\n"
                + "\n"
                + "		<!-- zip the files together with the release notes and the package description -->\n"
                + "		<zip destfile=\"${temp.dir}/${build.package.name}_GC_${build.version}.zip\">\n"
                + "			<fileset dir=\"${temp.dir}/\">\n"
                + "				<include name=\"${build.package.name}.tar.gz\"/>\n"
                + "				<include name=\"PackageDescription.xml\"/>\n"
                + "				<include name=\"ReleaseNotes.docx\"/>\n"
                + "			</fileset>\n"
                + "		</zip>\n"
                + "\n"
                + "		<move file=\"${temp.dir}/${build.package.name}_GC_${build.version}.zip\" todir=\"${packages.dir}/${today}\"/>\n"
                + "	</target>\n"
                + "\n"
                + "	<target name=\"removetempdir\">\n"
                + "		<delete dir=\"${temp.dir}\"/>\n"
                + "	</target>\n"
                + "	\n");
    }

    public void buildXMLVDStart() {
        buildFile.setFileDataAppend("	<target name=\"build.customvddb\">\n"
                + "		<delete dir=\"${temp.dir}\"/>\n"
                + "		<mkdir dir=\"${temp.dir}\"/>\n");
    }
    public void buildXMLVDEnd() {
        buildFile.setFileDataAppend("		<!-- tar the copied files -->\n"
                + "		<tar destfile=\"${temp.dir}/${build.package.name}.tar.gz\" compression=\"gzip\">\n"
                + "			<tarfileset dir=\"${temp.dir}\">\n"
                + "				<include name=\"**\"/>\n"
                + "			</tarfileset>\n"
                + "		</tar>\n"
                + "		\n"
                + "		<!-- move the sfile out of the temporary directory so they do not get packaged in the tar.gz file -->\n"
                + "		<copy file=\"./PackageDescription_VD.xml\" tofile=\"${temp.dir}/PackageDescription.xml\"/>\n"
                + "		<copy file=\"./ReleaseNotes.docx\" tofile=\"${temp.dir}/ReleaseNotes.docx\" failonerror=\"false\"/>\n"
                + "\n"
                + "		<!-- zip the files together with the release notes and the package description -->\n"
                + "		<zip destfile=\"${temp.dir}/${build.package.name}_VD_${build.version}.zip\">\n"
                + "			<fileset dir=\"${temp.dir}/\">\n"
                + "				<include name=\"${build.package.name}.tar.gz\"/>\n"
                + "				<include name=\"PackageDescription.xml\"/>\n"
                + "				<include name=\"ReleaseNotes.docx\"/>\n"
                + "			</fileset>\n"
                + "		</zip>\n"
                + "\n"
                + "		<move file=\"${temp.dir}/${build.package.name}_VD_${build.version}.zip\" todir=\"${packages.dir}/${today}\"/>\n"
                + "	</target>\n");
    }
    public void buildXMLEngine() {
        buildFile.setFileDataAppend("	<target name=\"build.engines\">\n"
                + "	<copy file=\"${base.dir}/engines/ReferenceEngine/lib/" + prop.get("custom.rule.jar").toString() + "\" todir=\"${temp.dir}/engines/ReferenceEngine/BRE/java\"/> \n"
                + "	<tar destfile=\"${temp.dir}/engines/${build.package.name}_Engines.tar.gz\" compression=\"gzip\">\n"
                + "			<tarfileset dir=\"${temp.dir}/engines\">\n"
                + "			</tarfileset>\n"
                + "		</tar>\n"
                + "		\n"
                + "		<copy file=\"./PackageDescription_Engines.xml\" tofile=\"${temp.dir}/engines/PackageDescription.xml\"/>\n"
                + "		<copy file=\"./ReleaseNotes_Engines.docx\" tofile=\"${temp.dir}/engines/ReleaseNotes.docx\" failonerror=\"false\"/>\n"
                + "		\n"
                + "		<!-- zip the files together with the release notes and the package description -->\n"
                + "		<zip destfile=\"${temp.dir}/engines/${build.package.name}_Engines_${build.version}.zip\">\n"
                + "			<fileset dir=\"${temp.dir}/engines/\">\n"
                + "				<include name=\"*${build.package.name}_Engines.tar.gz\"/>\n"
                + "				<include name=\"*PackageDescription.xml\"/>\n"
                + "				<include name=\"*ReleaseNotes.docx\"/>\n"
                + "			</fileset>\n"
                + "		</zip>		\n"
                + "		\n"
                + "		<move file=\"${temp.dir}/engines/${build.package.name}_Engines_${build.version}.zip\" todir=\"${packages.dir}/${today}\"/>\n"
                + "	</target>	\n"
                + "\n"
                + "	<target name=\"build-all\" depends=\"makedir,clean,build.customgc,build.customvddb,build.engines,removetempdir\">\n"
                + "	</target>\n"
                + "</project>");
    }
}
