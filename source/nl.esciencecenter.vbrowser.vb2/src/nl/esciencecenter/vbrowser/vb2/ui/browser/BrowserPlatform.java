/*
 * Copyrighted 2012-2013 Netherlands eScience Center.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache License at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * For the full license, see: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 */
// source: 

package nl.esciencecenter.vbrowser.vb2.ui.browser;

import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JFrame;
import javax.swing.TransferHandler;

import nl.esciencecenter.ptk.GlobalProperties;
import nl.esciencecenter.ptk.net.URIFactory;
import nl.esciencecenter.ptk.ui.icons.IconProvider;
import nl.esciencecenter.ptk.util.ResourceLoader;
import nl.esciencecenter.vbrowser.vb2.ui.dnd.DnDUtil;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyFactory;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyFactoryRegistry;
import nl.esciencecenter.vbrowser.vb2.ui.viewerplugin.ViewerRegistry;
import nl.esciencecenter.vbrowser.vb2.ui.viewerplugin.ViewerResourceHandler;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;

/**
 * Browser Platform.
 * 
 * Typically one Platform instance per application environment is created.
 */
public class BrowserPlatform
{
    private static BrowserPlatform instance = null;

    public static synchronized BrowserPlatform getInstance()
    {
        if (instance == null)
            instance = new BrowserPlatform();

        return instance;
    }

    // ========================================================================
    // Instance
    // ========================================================================

    private ProxyFactoryRegistry proxyRegistry = null;

    private ViewerRegistry viewerRegistry;

    private ResourceLoader resourceLoader;

    private JFrame rootFrame;

    private IconProvider iconProvider;

    protected BrowserPlatform()
    {
        try
        {
            init();
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }
    }

    private void init() throws URISyntaxException
    {
        // init defaults:
        this.proxyRegistry = ProxyFactoryRegistry.getInstance();

        ResourceLoader resourceLoader = new ResourceLoader();
        ViewerResourceHandler resourceHandler = new ViewerResourceHandler(resourceLoader);
        resourceHandler.setViewerConfigDir(getPlatformConfigDir());

        this.viewerRegistry = new ViewerRegistry(resourceHandler);

        rootFrame = new JFrame();
        iconProvider = new IconProvider(rootFrame, resourceLoader);
    }

    public void setResourceLoader(ResourceLoader resourceLoader)
    {
        viewerRegistry.getResourceHandler().setResourceLoader(resourceLoader);
    }

    public String getPlatformID()
    {
        return "VBTK2";
    }

    public ProxyFactory getProxyFactoryFor(VRL locator)
    {
        return this.proxyRegistry.getProxyFactoryFor(locator);
    }

    public BrowserInterface createBrowser()
    {
        return createBrowser(true);
    }

    public BrowserInterface createBrowser(boolean show)
    {
        return new ProxyBrowser(this, show);
    }

    public void registerProxyFactory(ProxyFactory factory)
    {
        this.proxyRegistry.registerProxyFactory(factory);
    }

    /**
     * Returns Internal Browser DnD TransferHandler for DnDs between browser
     * frames and ViewNodeComponents.
     */
    public TransferHandler getTransferHandler()
    {
        // default;
        return DnDUtil.getDefaultTransferHandler();
    }

    public ViewerRegistry getViewerRegistry()
    {
        return viewerRegistry;
    }

    public URI getPlatformConfigDir()
    {
        try
        {
            return new URIFactory("file:///" + GlobalProperties.getGlobalUserHome()).appendPath(
                    "." + getPlatformID().toLowerCase()).toURI();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public IconProvider getIconProvider()
    {
        return iconProvider;
    }

}
