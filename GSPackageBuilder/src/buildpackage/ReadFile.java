/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package buildpackage;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
//Still no solution for read parts of the file

/**
 *
 * @author Ryan
 */
public class ReadFile {

    private String fileName;

    public ReadFile() {
    }

    public ReadFile(String fname) {
        fileName = fname;
    }

    public HashMap getProperties() {
        try {
            HashMap output = new HashMap();
            FileInputStream fstream = new FileInputStream(fileName);
            DataInputStream in = new DataInputStream(fstream);
            while (in.available() != 0) {
                String temp = in.readLine();
                if (temp.contains("=")) {
                    output.put(temp.split("=")[0], temp.split("=")[1]);
                }
            }
            System.out.println(output);
            in.close();
            return (output);
        } catch (Exception e) {
            System.out.println("File input error" + e);
            return (null);
        }
    }

    public String getDTDFile() {
        try {
            String output = "";
            FileInputStream fstream = new FileInputStream(fileName);
            DataInputStream in = new DataInputStream(fstream);
            while (in.available() != 0) {
                String temp = in.readLine();
                if (temp.contains("<!DOCTYPE")) {
                    output = temp.substring(temp.indexOf("\"")+1, temp.lastIndexOf("\""));
                    break;
                }
            }
            System.out.println(output);
            in.close();
            return (output);
        } catch (Exception e) {
            System.out.println("File input error" + e);
            return (null);
        }
    }
    public List getExecutionOrder()
    {
        try {
            List output = new ArrayList<String>();
            FileInputStream fstream = new FileInputStream(fileName);
            DataInputStream in = new DataInputStream(fstream);
            while (in.available() != 0) {
                String temp = in.readLine();
                output.add(temp);
            }
            in.close();
            return (output);
        } catch (Exception e) {
            System.out.println("File input error" + e);
            return (null);
        }
    }

}
