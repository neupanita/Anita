/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.demo.filebrowser;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import org.demo.filebrowser.explorer.view.CustomListView;
import org.openide.util.LookupEvent;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
//import org.openide.util.ImageUtilities;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.filesystems.FileObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.LookupListener;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//org.demo.filebrowser//FileBrowser//EN",
autostore = false)
public final class FileBrowserTopComponent extends TopComponent implements LookupListener, ExplorerManager.Provider {

    private FileObject currentFolder = null;
    private ExplorerManager em = new ExplorerManager();
    private Lookup.Result result = null;
    private static FileBrowserTopComponent instance;
    /** path to the icon used by the component and its open action */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    private static final String PREFERRED_ID = "FileBrowserTopComponent";

    public FileBrowserTopComponent() {
        initComponents();
        initToolbar();
        setName(NbBundle.getMessage(FileBrowserTopComponent.class, "CTL_FileBrowserTopComponent"));
        setToolTipText(NbBundle.getMessage(FileBrowserTopComponent.class, "HINT_FileBrowserTopComponent"));
//        setIcon(ImageUtilities.loadImage(ICON_PATH, true));
        associateLookup(ExplorerUtils.createLookup(em, this.getActionMap())); 
        
        em.setRootContext(new AbstractNode(Children.LEAF));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new CustomListView();
        fileViewToolbar = new javax.swing.JToolBar();

        setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        fileViewToolbar.setFloatable(false);
        fileViewToolbar.setRollover(true);
        add(fileViewToolbar, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToolBar fileViewToolbar;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link #findInstance}.
     */
    public static synchronized FileBrowserTopComponent getDefault() {
        if (instance == null) {
            instance = new FileBrowserTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the FileBrowserTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized FileBrowserTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(FileBrowserTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof FileBrowserTopComponent) {
            return (FileBrowserTopComponent) win;
        }
        Logger.getLogger(FileBrowserTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID
                + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    @Override
    public void componentOpened() {
        SwingUtilities.invokeLater(new Runnable()  {

            @Override
            public void run() {
                TopComponent tc = WindowManager.getDefault().findTopComponent("FolderViewerTopComponent");
                if (tc == null) {
                    // XXX: message box?
                    return;
                }
                result = tc.getLookup().lookupResult(FileObject.class);
                result.addLookupListener(FileBrowserTopComponent.this);
                resultChanged(new LookupEvent(result));
            }
        });
    }

    @Override
    public void componentClosed() {
        result.removeLookupListener(this);
        result = null;
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    Object readProperties(java.util.Properties p) {
        if (instance == null) {
            instance = this;
        }
        instance.readPropertiesImpl(p);
        return instance;
    }

    private void readPropertiesImpl(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        Lookup.Result r = (Lookup.Result) ev.getSource();
        Collection<FileObject> coll = r.allInstances();
        if (!coll.isEmpty()) {
            currentFolder = coll.iterator().next();
        } else {
            currentFolder = null;
        }
        refreshFolder();
    }

    private void initToolbar() {
        List<? extends Action> alist = Utilities.actionsForPath("Toolbars/FileViewToolbar");
        for (Action action : alist) {
            this.fileViewToolbar.add(action);
        }
    }

    private void refreshFolder() {
        Runnable r=new Runnable()  {

            @Override
            public void run() {
                em.setRootContext(new FileNode(Children.create(new FileNode.FileNodeChildren(currentFolder), true)));
            }
        };
        RP.post(r);
        
    }
    private static RequestProcessor RP=new RequestProcessor(FileBrowserTopComponent.class.getCanonicalName(),1);

    @Override
    public ExplorerManager getExplorerManager() {
        return em;
    }
}
