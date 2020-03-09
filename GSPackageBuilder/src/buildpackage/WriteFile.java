/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package buildpackage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 *
 * @author Ryan
 */
public class WriteFile {
private String fileName;
    public WriteFile()
    {}
    public WriteFile(String fname)
    {
        fileName=fname;
    }
    public WriteFile(File f)
    {
        fileName=f.getPath();
    }
    public void setFileDataAppend(String input)
    {
        FileOutputStream out; // declare a file output object
        PrintStream p;
        try
        {
            out = new FileOutputStream(fileName,true);
            p = new PrintStream( out );
            p.println(input);
            p.close();
        }
        catch (Exception e)
        {
                System.err.println("File input error"+e);
                //Prompt x=new Prompt("File input error");
                //x.setVisible(true);
        }
    }
    public void clearFile()
    {
        FileOutputStream out; // declare a file output object
        PrintStream p;
        File f=new File(fileName);
        f.delete();
        try
        {
            out = new FileOutputStream(fileName);
            p = new PrintStream( out );
            p.print("");
            p.close();
        }
        catch (Exception e)
        {
                System.err.println("File input error");
                //Prompt x=new Prompt("File input error");
                //x.setVisible(true);
        }
    }
    public void setFileDataOverwrite(String input)
    {
        FileOutputStream out; // declare a file output object
        PrintStream p;
        try
        {
            out = new FileOutputStream(fileName);
            p = new PrintStream( out );
            p.println(input);
            p.close();
        }
        catch (Exception e)
        {
                System.err.println("File input error");
                //Prompt x=new Prompt("File input error");
                //x.setVisible(true);
        }
    }
}
