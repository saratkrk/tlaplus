package org.lamport.tla.toolbox.tool.tlc.output.internal;

import java.util.Vector;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.lamport.tla.toolbox.tool.tlc.TLCActivator;
import org.lamport.tla.toolbox.tool.tlc.output.IProcessOutputSink;

/**
 * A listener broadcasting the stream appending to extensions 
 * @author Simon Zambrovski
 * @version $Id$
 */
public class BroadcastStreamListener implements IStreamListener
{
    private IProcessOutputSink[] listeners = null;

    /**
     * 
     * @param streamName
     * @param kind, see constants {@link IProcessOutputSink#TYPE_DEBUG}, {@link IProcessOutputSink#TYPE_ERROR}, {@link IProcessOutputSink#TYPE_OUT}
     */
    public BroadcastStreamListener(String streamName, int kind)
    {
        this.listeners = getRegisteredStreamManagers(streamName, kind);
    }

    /* (non-Javadoc)
     * @see org.eclipse.debug.core.IStreamListener#streamAppended(java.lang.String, org.eclipse.debug.core.model.IStreamMonitor)
     */
    public synchronized void streamAppended(String text, IStreamMonitor monitor)
    {
        // broadcast the message
        for (int i = 0; i < listeners.length; i++)
        {
            if (listeners[i] != null)
            {
                try
                {
                    listeners[i].appendText(text);
                } catch (Exception e)
                {
                    TLCActivator.getDefault().logError("Error broadcasting the message", e);
                }
            }
        }
    }

    /**
     * Called to inform us that it has been completed.
     */
    public synchronized void streamClosed()
    {
        // broadcast the message
        for (int i = 0; i < listeners.length; i++)
        {
            try
            {

                if (listeners[i] != null)
                {
                    listeners[i].processFinished();
                }
            } catch (Exception e)
            {
                TLCActivator.getDefault().logError("Error broadcasting the stream closed event", e);
            }
        }
    }

    /**
     * Retrieves all registered listener managers
     * @return 
     */
    private IProcessOutputSink[] getRegisteredStreamManagers(String name, int type)
    {
        IConfigurationElement[] decls = Platform.getExtensionRegistry().getConfigurationElementsFor(
                IProcessOutputSink.EXTENSION_ID);

        Vector validExtensions = new Vector();
        for (int i = 0; i < decls.length; i++)
        {
            try
            {
                IProcessOutputSink extension = (IProcessOutputSink) decls[i].createExecutableExtension("class");
                extension.initializeSink(name, type);
                validExtensions.add(extension);
            } catch (CoreException e)
            {
                TLCActivator.getDefault().logError("Error instatiating the IProcessSink extension", e);
            }
        }
        return (IProcessOutputSink[]) validExtensions.toArray(new IProcessOutputSink[validExtensions.size()]);
    }
}
