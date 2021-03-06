package buildpackage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.nyu.cs.javagit.api.*;
import edu.nyu.cs.javagit.api.commands.GitAddResponse;
import edu.nyu.cs.javagit.api.commands.GitLogResponse.Commit;
//import edu.nyu.cs.javagit.test.utilities.FileUtilities;
//import edu.nyu.cs.javagit.test.utilities.HelperGitCommands;

public class GITVersionControl {

	private static File repositoryDirectory;
	private static DotGit dotGit;

	public static void main(String []args) throws IOException, JavaGitException{
		
//		JavaGitConfiguration.setGitPath("C:\\Program Files\\Git\\bin");

		//Getting the currently installed Git version
//		System.out.println("Git version : "+JavaGitConfiguration.getGitVersion());
		
		//Create a new directory to be used as a git repository.
//		repositoryDirectory = FileUtilities.createTempDirectory("Demo_gitRepository");
		repositoryDirectory = new File("D:\\gitrepository\\gspbuilder\\GSPackageBuilder\\src\\buildpackage");
		System.out.println("Git Repository Location : " + repositoryDirectory.getAbsolutePath());
		//get the instance of the dotGit Object
		dotGit = DotGit.getInstance(repositoryDirectory);
		
		//Initialize the repository ,similar to git init
		dotGit.init();
		
		//Create a file in our repository
//		File file = FileUtilities.createFile(repositoryDirectory, "README", "First file in the git repository");
		
		//get the current working tree from the git repository
		WorkingTree wt = dotGit.getWorkingTree();
		System.out.println(wt.getCurrentBranch());
		System.out.println(wt.getTree().size());
		System.out.println(wt.getTree());
		for (int i=0; i< wt.getTree().size(); i++ ) {
			GitFileSystemObject obj = wt.getTree().get(i);
			System.out.println("........"+obj.getName());
//			System.out.println("........"+obj.getFile());
//			System.out.println("........"+obj.diff());
//			System.out.println("........"+obj.getWorkingTree().getTree().get(0));
		}
		/*
//		GitAddResponse ar = wt.add();
//		wt.commitAll("First commit to the git repository");
		
		//Print messages for the commits we just made.
		System.out.println("----- Print log to see our commit -----");
		for (Commit c : dotGit.getLog() ){
			System.out.println(c.getMessage());
		}
		
		//Print the branch names in the git repository
		Ref master = wt.getCurrentBranch();
		System.out.println("----- Print branches in the git repository -----");
		Iterator<Ref> refs = dotGit.getBranches();
		while(refs.hasNext()){
			System.out.println(refs.next().getName());
		}
		
		//Get the current branch
		System.out.println("Current branch is :"+ wt.getCurrentBranch().getName());
		
		/*
		//Create a new branch
		System.out.println("----- Creating a new branch -----");
		Ref experimental = dotGit.createBranch("experimental");
		refs = dotGit.getBranches();
		System.out.println("----- Print current branches in the git repository -----");
		while(refs.hasNext()){
			System.out.println("-> "+refs.next().getName());
		}
		
		//changing current branch to experimental
		wt.checkout(experimental);
		System.out.println("Current branch is :"+ wt.getCurrentBranch().getName());
		
		//Edit the file
		System.out.println("----- Editing the file -----");
//		FileUtilities.modifyFileContents(new File(repositoryDirectory.getPath() + File.separator + file.getPath()), "Appending text ...\n");
		
		//Commit the changes we just made to the file
		System.out.println("----- Commiting changes -----");
		wt.commitAll("Adding some content to README");
*/
		
		//Print messages for the commits we just made.
		System.out.println("----- Print logs about commit -----");
		for (Commit c : dotGit.getLog() ){
			System.out.println(c.getMessage());
//			System.out.println(c.getFilesChanged());
//			System.out.println(c.getFiles());
			System.out.println(c.getAuthor());
			System.out.println(c.getDateString());
//			System.out.println(c.getLinesInserted());
			System.out.println(c.getSha());
			System.out.println(c.getMergeDetails());
		}	

		/*
		wt.checkout(master);
		System.out.println("----- Deleting Branch -----");
		dotGit.deleteBranch(experimental, true);
		System.out.println("----- Print branches in the git repository again -----");
		refs = dotGit.getBranches();
		while(refs.hasNext()){
			System.out.println("-> "+refs.next().getName());
		}

		
			
		//Print messages for the commits after we delete the experimental branch.
		System.out.println("----- Print logs about commit -----");
		for (Commit c : dotGit.getLog() ){
			System.out.println(c.getMessage());
		}
		/*
//		FileUtilities.removeDirectoryRecursivelyAndForcefully(repositoryDirectory);
 * 	
 */
	}
}