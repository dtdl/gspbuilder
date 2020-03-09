package buildpackage;

import buildpackage.WriteFile;
import java.util.HashMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author rdesouza
 */
public class PackageDescription {

    private WriteFile pdFile;
    private String type;

    public PackageDescription(HashMap prop, String fileName, String typ) {
        pdFile = new WriteFile(fileName);
        type = typ;
        packageDescriptionStart(prop);
    }

    public void packageDescriptionStart(HashMap prop) {
        if (type.equals("GC") || type.equals("VD")) {
            pdFile.setFileDataOverwrite("<?xml version=\"1.0\"?>\n"
                    + "<PackageDescription minInstallCenterVersion=\"" + prop.get("gs.version").toString() + "\" version=\"1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"PackageDescription.xsd\">\n"
                    + "	<Package name=\"" + prop.get("build.package.name").toString().replace("_", " ") + " "+type+"\" type=\"" + prop.get("build.package.type") + "\" version=\"" + prop.get("build.version") + "\">\n"
                    + "		<Description>" + prop.get("build.package.name").toString().replace("_", " ") + " "+type+"</Description>\n"
                    + "		<Component>" + prop.get("build.package.name").toString().replace("_", " ") + " "+type+"</Component>\n"
                    + "		<Content>\n"
                    + "			<File path=\"" + prop.get("build.package.name").toString() + ".tar.gz\" type=\"Package\"/>\n"
                    + "			<File path=\"ReleaseNotes.docx\" type=\"Package\"/>\n"
                    + "		</Content>\n"
                    + "	</Package>\n"
                    + "	<Deployment>\n"
                    + "		<Prerequisites>\n"
                    + "			<Product id=\"datamodel\" name=\"Datamodel GSDM\" type=\"GOLDENSOURCE\">\n"
                    + "				<Version min=\"" + prop.get("gs.version").toString() + "\"/>\n"
                    + "			</Product>\n"
                    + "			<Product id=\"dmgso\" name=\"Datamodel GoldenSource Objects\" type=\"GOLDENSOURCE\">\n"
                    + "				<Version min=\"" + prop.get("gs.version").toString() + "\"/>\n"
                    + "			</Product>\n"
                    + "			<Product id=\"stgso\" name=\"Starterset GoldenSource Objects\" type=\"GOLDENSOURCE\">\n"
                    + "				<Version min=\"" + prop.get("gs.version").toString() + "\"/>\n"
                    + "			</Product>\n"
                    + "			<Product id=\"configuration\" name=\"Datamodel Configuration\" type=\"GOLDENSOURCE\">\n"
                    + "				<Version min=\"" + prop.get("gs.version").toString() + "\"/>\n"
                    + "			</Product>\n"
                    + "			<Product id=\"workflow\" name=\"Datamodel Workflow\" type=\"GOLDENSOURCE\">\n"
                    + "				<Version min=\"" + prop.get("gs.version").toString() + "\"/>\n"
                    + "			</Product>\n"
                    + "		</Prerequisites>\n"
                    + "		<Locations>\n"
                    + "			<Location id=\"GSDMDataModel\" multiple=\"true\" type=\"Database\">\n"
                    + "				<Description>The destination where the DataModel GoldenSource Objects was installed.</Description>\n"
                    + "				<Prerequisites>\n"
                    + "					<Prerequisite id=\"datamodel\" required=\"true\"/>\n"
                    + "					<Prerequisite id=\"dmgso\" required=\"true\"/>\n"
                    + "					<Prerequisite id=\"stgso\" required=\"true\"/>\n"
                    + "				</Prerequisites>\n"
                    + "			</Location>\n"
                    + "			<Location id=\"VDDBDataModel\" type=\"Database\" multiple=\"true\">\n"
                    + "				<Description>A database where the Datamodel updates should be installed.</Description>\n"
                    + "			</Location>\n"
                    + "			<Location id=\"CFDataModel\" multiple=\"true\" type=\"Database\">\n"
                    + "				<Description>The destination where the Configuration Datamodel was installed.</Description>\n"
                    + "				<Prerequisites>\n"
                    + "					<Prerequisite id=\"configuration\"/>\n"
                    + "				</Prerequisites>\n"
                    + "			</Location>\n"
                    + "			<Location id=\"WFDataModel\" multiple=\"true\" type=\"Database\">\n"
                    + "				<Description>The destination where the Workflow Datamodel was installed.</Description>\n"
                    + "				<Prerequisites>\n"
                    + "					<Prerequisite id=\"workflow\"/>\n"
                    + "				</Prerequisites>\n"
                    + "			</Location>\n"
                    + "		</Locations>\n"
                    + "		<Tasks>\n"
                    + "			<!-- Unzip Installation Package -->\n"
                    + "			<tgz description=\"Unzip package archive\" dest=\"tmp_out/\" name=\"Unzip Package\" src=\"" + prop.get("build.package.name") + ".tar.gz\" srcLoc=\"PackageZip\"/>");
        } else if (type.equals("ENGINE")) {
            pdFile.setFileDataOverwrite("<?xml version=\"1.0\" ?>\n"
                    + "<PackageDescription minInstallCenterVersion=\"" + prop.get("gs.version").toString() + "\" version=\"1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"PackageDescription.xsd\">\n"
                    + "    <Package name=\"" + prop.get("build.package.name").toString().replace("_", " ") + " Engine Rules\" type=\"full\" version=\"" + prop.get("build.version") + "\">\n"
                    + "        <Description>" + prop.get("build.package.name").toString().replace("_", " ") + " Engine Rules</Description>\n"
                    + "        <Component>" + prop.get("build.package.name").toString().replace("_", " ") + " Engine Rules</Component>\n"
                    + "        <Content>\n"
                    + "            <File path=\"" + prop.get("build.package.name").toString() + "_Engines.tar.gz\" type=\"Package\"/>            \n"
                    + "        </Content>\n"
                    + "    </Package>\n"
                    + "    <Deployment>\n"
                    + "        <Prerequisites>\n"
                    + "            <Product id=\"BaseComp\" name=\"Base Components\" type=\"GOLDENSOURCE\">\n"
                    + "                <Version min=\"" + prop.get("gs.version").toString() + "\"/>\n"
                    + "            </Product>\n"
                    + "            <Product id=\"EngineConfig\" name=\"Reference Engine Default Configuration\" type=\"GOLDENSOURCE\">\n"
                    + "                <Version min=\"" + prop.get("gs.version").toString() + "\"/>\n"
                    + "            </Product>            \n"
                    + "            <Product id=\"datamodel\" name=\"Datamodel GSDM\" type=\"GOLDENSOURCE\">\n"
                    + "                <Version min=\"" + prop.get("gs.version").toString() + "\"/>\n"
                    + "            </Product>\n"
                    + "            <Product id=\"datamodelvddb\" name=\"Datamodel VDDB\" type=\"GOLDENSOURCE\">\n"
                    + "                <Version min=\"" + prop.get("gs.version").toString() + "\"/>\n"
                    + "            </Product>			\n"
                    + "            <Product id=\"starterset\" name=\"Starterset GSDM VDDB\" type=\"GOLDENSOURCE\">\n"
                    + "            	<Version min=\"" + prop.get("gs.version").toString() + "\"/>\n"
                    + "        	</Product>\n"
                    + "        	<Product id=\"startersetEXM\" name=\"Starterset Exception Management\" type=\"GOLDENSOURCE\">\n"
                    + "            	<Version min=\"" + prop.get("gs.version").toString() + "\"/>\n"
                    + "        	</Product>\n"
                    + "            <!-- DATABASE CLIENT/SERVER -->\n"
                    + "            <Product id=\"Ora11Server\" name=\"Oracle Server Version 11.2.0\" type=\"ORACLE_SERVER\">\n"
                    + "            	<Version max=\"12.2.x.x.x\" min=\"12.0.x.x.x\"/>\n"
                    + "            </Product>\n"
                    + "            <Product id=\"Oracle112\" name=\"Oracle Client Version 11.2.0\" type=\"ORACLE_CLIENT\">\n"
                    + "	            <Version max=\"12.2.x.x.x\" min=\"12.0.x.x.x\"/>\n"
                    + "            </Product>  \n"
                    + "            \n"
                    + "            <!-- OPERATING SYSTEM -->\n"
                    + "            <Product id=\"LINUX_2.6\" name=\"Linux with Kernel 3.10.x\" type=\"OS_LINUX\">\n"
                    + "				<Version max=\"3.10.x\" min=\"3.10.0\"/>\n"
                    + "			</Product>\n"
                    + "            \n"
                    + "        </Prerequisites>\n"
                    + "\n"
                    + "		\n"
                    + "        <Locations>\n"
                    + "            <Location id=\"ReferenceInstall\" type=\"EngineInstallation\">\n"
                    + "                <Description>Filesystem location where the engine should be installed.</Description>\n"
                    + "                <Prerequisites>\n"
                    + "                    <and name=\"ReferencePrerequisites\">\n"
                    + "                        <Prerequisite id=\"BaseComp\"/>\n"
                    + "                        <Prerequisite description=\"Certified Environment\" id=\"LINUX_2.6\"/>\n"
                    + "                        <Prerequisite id=\"Oracle112\"/>\n"
                    + "                    </and>\n"
                    + "                </Prerequisites>\n"
                    + "            </Location>\n"
                    + "            <Location id=\"DataModel\" multiple=\"true\" type=\"Database\">\n"
                    + "                <Description>The database where the Datamodel was installed.</Description>\n"
                    + "                <Prerequisites>\n"
                    + "                    <Prerequisite id=\"EngineConfig\"/>\n"
                    + "                    <Prerequisite id=\"Ora11Server\"/>                  \n"
                    + "                    <or name=\"GSDM Datamodel\" required=\"true\">\n"
                    + "                        <Prerequisite id=\"datamodel\"/>\n"
                    + "                        <Prerequisite id=\"datamodelvddb\"/>                        \n"
                    + "                    </or>                    \n"
                    + "					<or name=\"BaseStarterSet\" required=\"true\">\n"
                    + "						<Prerequisite id=\"starterset\"/>						\n"
                    + "					</or>\n"
                    + "                    <Prerequisite id=\"startersetEXM\"/>\n"
                    + "                </Prerequisites>\n"
                    + "            </Location>\n"
                    + "        </Locations>\n"
                    + "\n"
                    + "        <Tasks>\n"
                    + "            <tgz description=\"Unzip package archive\" destLoc=\"ReferenceInstall\" name=\"Unzip Package\" src=\"" + prop.get("build.package.name").toString() + "_Engines.tar.gz\" srcLoc=\"PackageZip\"/>\n"
                    + "        </Tasks>\n"
                    + "    </Deployment>\n"
                    + "</PackageDescription>");
        }
    }

    public void append(String line) {
        pdFile.setFileDataAppend(line);
    }

    public void packageDescriptionEnd() {
        if (type.equals("GC") || type.equals("VD")) {
            pdFile.setFileDataAppend("			</Tasks>\n"
                    + "	</Deployment>\n"
                    + "\n"
                    + "	<Undeployment>\n"
                    + "		<Prerequisites cloneFromDeploymentSection=\"true\"/>\n"
                    + "		<Locations cloneFromDeploymentSection=\"true\"/>\n"
                    + "		<Tasks cloneFromDeploymentSection=\"true\"/>\n"
                    + "	</Undeployment>\n"
                    + "</PackageDescription>");
        } else if (type.equals("ENGINE")) {
        }
    }
}
