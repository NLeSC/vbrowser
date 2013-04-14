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

package nl.esciencecenter.ptk.ui.icons;


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import nl.esciencecenter.ptk.util.logging.ClassLogger;

/** 
 * Simple (Icon) Image Renderer class. 
 * Performs scaling merging and grey out of (icon) images. 
 *  
 * @author P.T. de Boer
 */
public class ImageRenderer
{
    private static ClassLogger logger;
    
    static 
    {
        logger=ClassLogger.getLogger(ImageRenderer.class); 
    }
    
	// ==========================================================================
	// Object
	// ==========================================================================

    private Image miniLinkImage=null;  
     
	/** 
	 * Optional AWT object which can be used as  image 'source' to create peer compatible 
	 * image format.
	 * This can increase the rendering speed.
	 */
    
	@SuppressWarnings("unused")
	private Component imageSource=null;

    private Color greyoutColor=Color.blue; 
	
	// private IconProvider iconProvider=null;

	public ImageRenderer(Component source)
	{
		// use AWT component for image source (optional)
		this.imageSource=source;
	}
		
	/** Scale image, add optional link icon and perform optional 'greyout' */ 
	public Image renderIconImage(Image orgImage,boolean isLink,Dimension preferredSize,  boolean greyOut) 
	{
	    // === PRE === // 
	    
		if (orgImage==null) 
			throw new NullPointerException("Cannot render NULL image");   
		
		if  ((isLink==true) && (this.miniLinkImage==null)) 
		    throw new NullPointerException("Can't render link icon if LinkImage hasn't been set!. Use setLInkImage()"); 
		
		//ImageSynchronizer imageSyncer=new ImageSynchronizer(); 
	
		// extra check :
		if ((orgImage.getHeight(null)<=0) || (orgImage.getWidth(null)<=0))
		{
			logger.errorPrintf("*** Error: Illegal Image. Image not  (yet) loaded or broken:%s\n",orgImage); 
			return null; 
		}

		// === BODY === // 
		
		int orgWidth=orgImage.getWidth(null); 
		int orgHeight=orgImage.getHeight(null); 
		int prefWidth=orgWidth;
		int prefHeight=orgHeight;


		if (preferredSize!=null)
		{
			if (preferredSize.width>0)
				prefWidth=preferredSize.width;

			if (preferredSize.height>0)
				prefHeight=preferredSize.height;
		}

		// scaling 
		int scaleWidth=prefWidth;  
		int scaleHeight=prefHeight;
		boolean upScale=false; 
		boolean downScale=false;

		Image scaledImage=orgImage; // default to the same! 
		
		//
		// check resize:
		//
		if ((preferredSize!=null) && (prefWidth>0) && (prefHeight>0))
		{

			logger.debugPrintf("Rescaling image to:%s\n",preferredSize);

			if (orgWidth > prefWidth)
			{
				downScale=true;
			}
			else if (orgWidth < prefWidth)
			{
				upScale=true; 
				logger.warnPrintf("*** Warning, upscaling icon width:%d to %d\n",orgWidth,prefWidth);
			}

			if (orgHeight > prefHeight)
			{
				downScale=true; 
			}
			else if (orgHeight < prefHeight)
			{
				upScale=true;
				logger.warnPrintf("*** Warning, upscaling icon height:%d to %d\n",orgHeight,prefHeight);
			}

			//
			// limit upscaling to avoid big ugly icons 
			//

			if (upScale)
			{
				double xratio=scaleWidth/(double)orgWidth; 
				double yratio=scaleHeight/(double)orgHeight; 

				if ((xratio>=2) || (yratio>=2))
				{
					xratio=xratio/2.0; 
					yratio=yratio/2.0;

					scaleWidth=(int)(orgWidth*xratio);  
					scaleHeight=(int)(orgHeight*yratio); 
				}
			}

			if (upScale || downScale) 
			{
				// use swing's 'smooth' image scaler

			    // Method should already be 'synchronized'
				Image newImage = orgImage.getScaledInstance(scaleWidth,
						scaleHeight, Image.SCALE_SMOOTH);

                sync(newImage);
                
				scaledImage=newImage; 
			}
		}

		// done ?

		if ((scaleHeight==prefHeight) && (scaleWidth==prefWidth))
			if ((isLink==false) && (greyOut==false))
			{
				return scaledImage;
			}

		//
		// Create new Icon Canvas to draw & merge linkicon and greyout pattern. 
		// Must use full RGB+Alpha image. 
		// 
		
		BufferedImage newImage = new BufferedImage(prefWidth,prefHeight,BufferedImage.TYPE_INT_ARGB);
		Graphics2D imageGraphics = newImage.createGraphics();

		// 
		// I): new Image, optionally scaled and centered:  
		// 
		{
			// 
			// center  possible smaller icon image into bigger preffered size image 
			//

			int offx=0; 
			int offy=0; 

			if (prefWidth>scaleWidth)
				offx=(prefWidth-scaleWidth)/2;

			if (prefHeight>scaleHeight)
				offy=(prefHeight-scaleHeight)/2; 

			//synchronized(imageSyncer)
			{
    			// merge link icon with original icon 
    			boolean drawn=imageGraphics.drawImage(scaledImage, offx, offy, null, null); // imageSyncer);
    			
    			if (drawn==false)
    			{
    			   // System.err.println("***Warning:  image NOT yet drawn");
    			    // imageSyncer.waitForCompletion();
    			}
			}
			  
		}

		//sync(newImage); 
		//
		// II) Optional LinkImage (shortcut arrow): 
		//

		if (isLink)
		{
			// create merged icon image + shortcut image: 
			Image linkImage=getLinkImage();
			if (linkImage==null)
			    throw new NullPointerException("Cannot find linkImage");
			sync(linkImage); 

			int linkh=linkImage.getHeight(null);  
			int linkw=linkImage.getWidth(null); 

			//
			// Scale link icon a only little bit if the preferred size is very small.
			// this prevents that the shortcut icon are rendered to small  
			// 

			if (preferredSize!=null)
				if ((prefWidth<(2*linkw)) || (prefHeight<(2*linkh)))
				{
					//  
					linkw=(int)(linkw/1.5);
					linkh=(int)(linkh/1.5);

					linkImage = linkImage.getScaledInstance(linkw,linkh,
							Image.SCALE_SMOOTH);
					// synchronize!
					sync(linkImage);
				}

			//synchronized(imageSyncer)
            {
                // merge link icon with original icon 
                //imageGraphics.drawImage(linkImage, 0,prefHeight-linkh,linkw,linkh,null, imageSyncer);
                imageGraphics.drawImage(linkImage, 0,prefHeight-linkh,linkw,linkh,null); 
                
                
//                if (drawn==false)
//                {
//                    System.err.println("***Warning:  image NOT yet drawn");
//                    //imageSyncer.waitForCompletion();
//                }
            }
			
			
		}

		// === 
		// III) Perform greyOUt (or blue out)
		//      add mesh of pixels + make blueish
		// === 
		Color c=greyoutColor; 
		
		
		if (greyOut)
			doGreyout(newImage,c); 
		
		//
		// IV) new ImageIcon Object! 
		//
		imageGraphics.dispose(); 

		return newImage; 
	}
	
    private void sync(Image image)
    {
        // need better way to do this: 
        @SuppressWarnings("unused")
        javax.swing.ImageIcon ii=new javax.swing.ImageIcon(image);
    }
	
	/** Create mesh like pattern over the image */ 
	public void doGreyout(BufferedImage baseImage, Color greycolor)
	{
		int width=baseImage.getWidth(); 
		int height=baseImage.getHeight(); 
		
		//ColorModel model = newimage.getColorModel();
		//Raster raster=newimage.getRaster();
		
		// reduce in color strenght; 
		for (int x=0;x<width;x++) 
			for (int y=0;y<height;y++)
			{
				// TYPE INT ARGB
				int rgb=baseImage.getRGB(x,y);
				//colorModel.getRGB(raster.getDataElements(x, y, null));
				
				int a=(rgb>>24)%256; 
				int r=(rgb>>16)%256;
				int g=(rgb>>8)%256;
				int b=rgb%256;

				if ((x+y)%2==1)
				{
					// keep pixel; 
				}
				else
				{
					r=greycolor.getRed(); 
					g=greycolor.getGreen(); 
					b=greycolor.getBlue();
					
					// Keep alpha level! 
					//a=255;
					
					baseImage.setRGB(x,y,((int)a)*256*256*256+((int)r)*65536+((int)g)*256+((int)b));
				}
			}
	}
    
	public Image getLinkImage()  
    {
        return miniLinkImage;  
    }

    public void setLinkImage(Image image) 
    {
        this.miniLinkImage=image; 
    }

    public void setGreyOutColor(Color color)
    {
        this.greyoutColor=color; 
    }
    
    public Color getGreyOutColor()
    {
        return this.greyoutColor; 
    }

}
