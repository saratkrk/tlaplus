package org.lamport.tla.toolbox.editor.basic;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.lamport.tla.toolbox.editor.basic.util.ElementStateAdapter;
import org.lamport.tla.toolbox.util.ResourceHelper;
import org.lamport.tla.toolbox.util.UIHelper;

/**
 * Basic editor without any additional features
 *
 * @author zambrovski
 */
public class TLAEditor extends TextEditor
{
    private IContextService contextService = null;
    private IContextActivation contextActivation = null;

    private Image rootImage = TLAEditorActivator.imageDescriptorFromPlugin(TLAEditorActivator.PLUGIN_ID,
            "/icons/root_file.gif").createImage();

    /**
     * Constructor
     */
    public TLAEditor()
    {
        super();
        setDocumentProvider(new FileDocumentProvider());
        setHelpContextId("org.lamport.tla.toolbox.editor.basic.main_editor_window");

        getDocumentProvider().addElementStateListener(new ElementStateAdapter() {
            public void elementDirtyStateChanged(Object element, boolean isDirty)
            {
                // System.out.println("elementDirtyStateChanged " + element);
                if (isDirty)
                {
                    contextService.deactivateContext(contextActivation);
                } else
                {
                    contextActivation = contextService.activateContext("toolbox.contexts.cleaneditor");
                }
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.texteditor.AbstractTextEditor#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     */
    public void init(IEditorSite site, IEditorInput input) throws PartInitException
    {
        super.init(site, input);
        if (input instanceof FileEditorInput)
        {
            FileEditorInput finput = (FileEditorInput) input;
            if (finput != null)
            {
                IPath path = finput.getPath();
                setContentDescription(path.toString());

                if (ResourceHelper.isRoot(finput.getFile()))
                {
                    setTitleImage(rootImage);
                }
            }
        }
        // grab context service and activate the context on editor load
        this.contextService = (IContextService) getSite().getService(IContextService.class);
        this.contextActivation = contextService.activateContext("toolbox.contexts.cleaneditor");
    }

    public void dispose()
    {
        super.dispose();
        rootImage.dispose();
    }

    protected void performSaveAs(IProgressMonitor progressMonitor)
    {
        IFile file = ((FileEditorInput) getEditorInput()).getFile();
        Shell shell = UIHelper.getShellProvider().getShell();
        SaveAsDialog saveAsDialog = null;
        while (true)
        {
            saveAsDialog = new SaveAsDialog(shell);
            saveAsDialog.setHelpAvailable(true);
            saveAsDialog.setBlockOnOpen(true);
            saveAsDialog.setOriginalFile(file);
            int result = saveAsDialog.open();
            if (result == Window.OK)
            {
                IPath newPath = saveAsDialog.getResult();
                if (newPath.toFile().exists())
                {
                    boolean confirmOverride = MessageDialog.openQuestion(shell, "Override file?",
                            "The provided filename already exists. The existing file will be overriden.\nDo you want to override the file "
                                    + newPath.toOSString() + " ?");
                    if (!confirmOverride)
                    {
                        continue;
                    }
                }
                
                System.out.println("TODO: Save " + file + " as " + newPath);
                // break out on save
                break;
            } else 
            {
                // break out on cancel of the dialog
                break;
            }
        }
    }

}
