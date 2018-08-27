package com.maxtree.tb4license.client.example;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.event.*;
import sun.audio.*;
import de.schlichtherle.util.ObfuscatedString;

public class Hyde {
//implements LicenseableClass {

//	{
//	  private static final String DC_LICENSING_URL = "http://devdaily.com/hide-your-desktop";
//	  
//	  // "please license" dialog stuff
//	  private static final String LICENSE_DIALOG_TITLE = "devdaily.com - Hyde";
//	  private LicenseController licenseController;
//	  private LicenseReminderDialog licenseReminderDialog;
//	  private int numTimesUsed;
//	  private ApplyLicenseDialog applyLicenseDialog;
//	  
//	  private boolean licenseWasSuccessfullyApplied;
//	    
//	  // license stuff - uses the ObfuscatedString class from the True License library
//	  private static String keystoreFilename = new ObfuscatedString(new long[] {0x2531E65E362C302AL, 0xC6491960E7CAB920L, 0xC3BA520AAFF63939L}).toString(); /* => "dc.properties" */
//	  private static String alias = new ObfuscatedString(new long[] {0xDE2992A6F9C3C3A1L, 0xA08CA77A6F05791AL, 0x59E2378BA6F1BA96L}).toString(); /* => "dcpublickey" */
//	  private static String publicCertStorePassword = new ObfuscatedString(new long[] {0x43426A6C456239FAL, 0x98099DA1F000C879L, 0x4B8DCA266320210EL, 0xC13BE948052C1673L}).toString(); /* => "Z3N H4ND M00N 6590" */
//	  private static String cipherParamPassword = new ObfuscatedString(new long[] {0xCF899EE49F709ACAL, 0x954921EB531BF4BFL, 0x3D73B0811299826DL}).toString(); /* => "d0sxxb33rm3si" */
//
//	  // ftp license stuff - uses the ObfuscatedString class from the True License library
//	  private static String ftpAlias = new ObfuscatedString(new long[] {0xA4B0FBA523B3F63AL, 0x65D5A72B8657E00BL, 0x9E71F6D2395FF6FL}).toString(); /* => "dcftpkeyv1.x" */
//	  private static String ftpKeyPwd = new ObfuscatedString(new long[] {0xA2B75E0F17691BD4L, 0xEB9DB7C881078526L, 0x5ED8D7B254BC11BEL, 0x4422FAD5CD5DC734L}).toString(); /* => "J4V D3RB 88743 1411" */
//
//	  // prompt user for a license filename when they're ready to license
//	  private String licenseFilename;
//
//	  public static void main(String[] args)
//	  {
//	    new Hyde();
//	  }
//
//	  public Hyde()
//	  {
//	    // some 'init' work happened here, like hooking up to the java preferences ...
//	    
//	    // use my LicenseController class to verify and install a license
//	    licenseController = new LicenseController(this, logger);
//
//	    // do all the things to display the application's main frame here ...
//
//	    // the app main frame is displayed; now verify the license.
//	    // this makes a callback to handleVerifyLicenseFailedEvent() if the verification fails.
//	    // the handleVerifyLicenseFailedEvent() method in this class is called if the 
//	    // license verification process fails.
//	    licenseController.verifyLicense();
//	  }
//	  
//	  //----- implement the LicenseableClass interface -----//
//	  
//	  public String getApplicationName()
//	  {
//	    return APP_NAME;
//	  }
//
//	  public InputStream getPublicKeystoreAsInputStream() throws FileNotFoundException
//	  {
//	    final String resourceName = keystoreFilename;
//	    final InputStream in = getClass().getResourceAsStream(resourceName);
//	    if (in == null)
//	    {
//	      throw new FileNotFoundException(resourceName);
//	    }
//	    return in;
//	  }
//	  
//	  public String getAlias()
//	  {
//	    return alias;
//	  }
//
//	  public String getPublicKeystorePassword()
//	  {
//	    return publicCertStorePassword;
//	  }
//
//	  public String getCipherParamPassword()
//	  {
//	    return cipherParamPassword;
//	  }
//
//	  public Class getClassToLicense()
//	  {
//	    return this.getClass();
//	  }
//
//	  public String getFtpKeyPwd()
//	  {
//	    return ftpKeyPwd;
//	  }
//	  
//	  public String getFtpAlias()
//	  {
//	    return ftpAlias;
//	  }
//
//	  /**
//	   * This process is invoked by the system when the FTP license
//	   * period has expired. We need to show a slightly-modified version of
//	   * the ApplyLicenseDialog, and we need to shut down the application if
//	   * the user does not apply a valid license.
//	   */
//	  public void handleVerifyLicenseFailedEvent()
//	  {
//
//	    SwingUtilities.invokeLater(new Runnable()
//	    {
//	      public void run()
//	      {
//	        // create the dialog
//	        applyLicenseDialog = new ApplyLicenseDialog(desktopCurtainFrame);
//	        
//	        // need to add this to catch the event where the user presses the 
//	        // red close event on our dialog; in that case, we need to quit the app
//	        // if a license has not been successfully applied.
//	        // TODO this was another late patch, and this code can probably be consolidated
//	        //      with the Quit button code.
//	        applyLicenseDialog.addWindowListener(new WindowAdapter() 
//	        {
//	          public void windowClosed(WindowEvent e)
//	          {
//	            if (!licenseWasSuccessfullyApplied)
//	            {
//	              doQuitAction();
//	            }
//	          }
//	          public void windowClosing(WindowEvent e)
//	          {
//	            if (!licenseWasSuccessfullyApplied)
//	            {
//	              doQuitAction();
//	            }
//	          }
//	        });
//	        
//	        // change text to let user know the trial period is over
//	        applyLicenseDialog.getHeaderHelpText().setText("The free trial period has ended. Please install a license to continue.");
//	        
//	        // change the cancel button to say "quit"
//	        JButton forcedQuitButton = applyLicenseDialog.getCancelButton();
//	        forcedQuitButton.setText("Quit");
//	        forcedQuitButton.addActionListener(new ActionListener()
//	        {
//	          public void actionPerformed(ActionEvent e)
//	          {
//	            // shut down the app if the user selects this button at this time
//	            applyLicenseDialog.setVisible(false);
//	            doQuitAction();
//	          } 
//	        });
//
//	        // TODO this code is copied and pasted from the doInstallLicenseAction method
//	        DCJLinkLabel linkLabel = applyLicenseDialog.getNeedALicenseLabel();
//	        linkLabel.addMouseListener(new MouseAdapter()
//	        {
//	          public void mouseClicked(MouseEvent arg0)
//	          {
//	            // take the user to the proper url
//	            openUrlInBrowser(DC_LICENSING_URL);
//	          }
//	        });
//	        
//	        // add a listener to the Browser button
//	        JButton browseForLicenseFileButton = applyLicenseDialog.getBrowseButton();
//	        browseForLicenseFileButton.addActionListener(new BrowseForLicenseFileActionListener());
//	           
//	        // add a complicated listener to the Apply button
//	        JButton applyLicenseButton = applyLicenseDialog.getApplyLicenseButton();
//	        applyLicenseButton.addActionListener(new ApplyLicenseActionListener());
//	        
//	        // set the dialog visible, and leave it to the listeners to handle the action
//	        applyLicenseDialog.setResizable(false);
//	        applyLicenseDialog.pack();
//	        applyLicenseDialog.setModal(true);
//	        applyLicenseDialog.setLocationRelativeTo(desktopCurtainFrame);
//	        applyLicenseDialog.setVisible(true);  
//	        
//	      }
//	    });
//	  }
//	  
//	  private class ApplyLicenseActionListener implements ActionListener
//	  {
//	    /**
//	     * "Apply" button was clicked, so get the filename from the textfield and 
//	     * try to apply it as a license.
//	     */
//	    public void actionPerformed(ActionEvent e)
//	    {
//	      // get the filename
//	      licenseFilename = applyLicenseDialog.getFileTextField().getText();
//	      if (licenseFilename == null || licenseFilename.trim().equals(""))
//	      {
//	        JOptionPane.showMessageDialog(desktopCurtainFrame, 
//	            "Sorry, it does not look like a file was selected.",
//	            "No Filename",
//	            JOptionPane.INFORMATION_MESSAGE);
//	        return;
//	      }
//
//	      // TODO should probably do this in a thread and indeterminate dialog.
//	      // got a filename. try to install it with the license controller.
//	      boolean result = licenseController.installLicense(licenseFilename);
//	      
//	      // let the user know it went good or bad
//	      if (result)
//	      {
//
//	        // the license was applied successfully; show the user their information
//	        licenseWasSuccessfullyApplied = true;
//	        
//	        String message = "The devdaily.com Hyde license was installed -- thank you!\n\n"
//	          + "License holder information:\n\n  (" + licenseController.getLicenseContent().getHolder().getName() + ")\n"; 
//	        
//	        JTextArea textArea = new JTextArea(8, 31);
//	        textArea.setText(message);
//	        textArea.setEditable(false);
//	        textArea.setLineWrap(true);
//	        textArea.setWrapStyleWord(true);
//	        JScrollPane scrollPane = new JScrollPane(textArea);
//	        
//	        JOptionPane.showMessageDialog(desktopCurtainFrame,
//	            scrollPane,
//	            "License Installed",
//	            JOptionPane.INFORMATION_MESSAGE);
//	        // also close the apply dialog
//	        applyLicenseDialog.setVisible(false);
//	        applyLicenseDialog.dispose();
//	      }
//	      else
//	      {
//	        String message = "I'm sorry, there was a problem applying the license. "
//	          + "Please contact devdaily@gmail.com for assistance.\n\n"
//	          + "A system error message should be shown below here:\n\n"
//	          + licenseController.getErrorMessage();
//	        
//	        JTextArea textArea = new JTextArea(10, 35);
//	        textArea.setText(message);
//	        textArea.setEditable(false);
//	        textArea.setLineWrap(true);
//	        textArea.setWrapStyleWord(true);
//	        JScrollPane scrollPane = new JScrollPane(textArea);
//	        
//	        // display them in a message dialog
//	        JOptionPane.showMessageDialog(desktopCurtainFrame, 
//	            scrollPane,
//	            "Problem Installing License",
//	            JOptionPane.WARNING_MESSAGE);
//	      }
//
//	      // always need to do this after dialog calls until i find out how to do this right
//	      giveFocusBackToCurtain();
//
//	    } 
//	  }
//	  
//	  private class BrowseForLicenseFileActionListener implements ActionListener
//	  {
//	    public void actionPerformed(ActionEvent e)
//	    {
//	      // browser button was clicked, show a FileDialog to prompt for a filename
//	      String canonicalFilename = promptForFilenameWithFileDialog(desktopCurtainFrame, "Select Your License File", null, "*.lic");
//	      
//	      // if the name isn't null, put it in the textfield
//	      if (canonicalFilename != null)
//	      {
//	        applyLicenseDialog.getFileTextField().setText(canonicalFilename);
//	      }
//	    }
//	  }
//
//
//	  /**
//	   * Come here when the user selects the "Apply License" menu item.
//	   */
//	  public void doInstallLicenseAction()
//	  {
//	    // prompt the user for the license file
//	    applyLicenseDialog = new ApplyLicenseDialog(desktopCurtainFrame);
//
//	    // TODO do this much better; just added as a quick go-live fix
//	    // TODO modified the ApplyLicenseDialog manually to add a JLinkLabel ***
//	    DCJLinkLabel linkLabel = applyLicenseDialog.getNeedALicenseLabel();
//	    linkLabel.addMouseListener(new MouseAdapter()
//	    {
//	      public void mouseClicked(MouseEvent arg0)
//	      {
//	        // take the user to the proper url
//	        openUrlInBrowser(DC_LICENSING_URL);
//	      }
//	    });
//	    
//	    // add a listener to the Browser button
//	    JButton browseForLicenseFileButton = applyLicenseDialog.getBrowseButton();
//	    browseForLicenseFileButton.addActionListener(new BrowseForLicenseFileActionListener());
//	       
//	    // add a complicated listener to the Apply button
//	    JButton applyLicenseButton = applyLicenseDialog.getApplyLicenseButton();
//	    applyLicenseButton.addActionListener(new ApplyLicenseActionListener());
//
//	    // add a simple listener to the Cancel button
//	    JButton cancelButton = applyLicenseDialog.getCancelButton();
//	    cancelButton.addActionListener(new ActionListener()
//	    {
//	      public void actionPerformed(ActionEvent e)
//	      {
//	        applyLicenseDialog.setVisible(false);
//	      } 
//	    });
//	    
//	    // set the dialog visible, and leave it to the listeners to handle the action
//	    applyLicenseDialog.pack();
//	    applyLicenseDialog.setModal(true);
//	    applyLicenseDialog.setLocationRelativeTo(desktopCurtainFrame);
//	    applyLicenseDialog.setVisible(true);
//	  }
//
//	  private boolean productIsLicensed()
//	  {
//	    return licenseController.verifyLicense();
//	  }
//
//	  public boolean licenseWasSuccessfullyApplied()
//	  {
//	    return licenseWasSuccessfullyApplied;
//	  }
//
//	  public void setLicenseWasSuccessfullyApplied(boolean licenseWasSuccessfullyApplied)
//	  {
//	    this.licenseWasSuccessfullyApplied = licenseWasSuccessfullyApplied;
//	  }
	  
}
