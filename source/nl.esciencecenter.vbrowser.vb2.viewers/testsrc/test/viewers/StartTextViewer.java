package test.viewers;

import java.net.URI;

import nl.esciencecenter.vbrowser.vb2.ui.viewers.ImageViewer;
import nl.esciencecenter.vbrowser.vb2.ui.viewers.TextViewer;

public class StartTextViewer
{
    // === Main ===

    public static void main(String args[])
    {
        // Global.setDebug(true);
        
        try
        {
            ViewerTests.testViewer(TextViewer.class,new URI("file:///home/ptdeboer/tests/testText.txt"));

            // viewStandAlone(null);
        }
        catch (Exception e)
        {
            System.out.println("***Error: Exception:" + e);
            e.printStackTrace();
        }

    }

    
}
