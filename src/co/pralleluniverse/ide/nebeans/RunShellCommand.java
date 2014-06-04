/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.pralleluniverse.ide.nebeans;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Build",
        id = "co.pralleluniverse.ide.nebeans.RunShellCommand"
)
@ActionRegistration(
        iconBase = "co/pralleluniverse/ide/nebeans/Delete.png",
        displayName = "#CTL_RunShellCommand"
)
@ActionReferences({
    @ActionReference(path = "Menu/Tools", position = 0),
    @ActionReference(path = "Toolbars/Build", position = 500),
    @ActionReference(path = "Shortcuts", name = "S-F10")
})
@Messages("CTL_RunShellCommand=KillGradle")
public final class RunShellCommand implements ActionListener {
    @Override
        public void actionPerformed(ActionEvent e) {
        try {
            //pid -> parent pid
            HashMap<String,String> map = new HashMap<>();

            Process p = Runtime.getRuntime().exec("wmic process get processid,parentprocessid,executablepath");
            String line;
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                //gets all processes from running related to java and netbeans
                if(line.contains("java") || line.contains("netbeans"))
                {
                    String pattern = "( +)(\\d+)( +)(\\d+)( *)$";
                    Pattern r = Pattern.compile(pattern);
                    Matcher m = r.matcher(line);

                    if (m.find( )) {
                        map.put(m.group(4), m.group(2));
                    }
                }
            }
            
            input.close();

            String name = ManagementFactory.getRuntimeMXBean().getName();
            String ownPid = name.split("@")[0];
            String ownPPid = map.get(ownPid);
            
            //kills all processes and child processes of ownPid
            killCascade(map, ownPid);
           
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private static void killCascade(HashMap<String,String> map, String ownPid) throws IOException{
        for(Map.Entry<String,String> entry : map.entrySet())
        {
            if(entry.getValue().equals(ownPid)) {
                killCascade(map, entry.getKey());
                Runtime.getRuntime().exec("taskkill /f /pid " + entry.getKey());
            }
        }
    }
}
