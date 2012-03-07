/*
 * SCUBA smart card framework.
 *
 * Copyright (C) 2009 - 2012  The SCUBA team.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * $Id: $
 */

package net.sourceforge.scuba.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;

import net.sourceforge.scuba.data.Country;

/**
 * Utility class with methods for reading icons and other images from file.
 * Assumes file structure (relative to class path) something like this:
 * <ul>
 *    <li>images/</li>
 *    <ul>
 *       <li>flags/</li>
 *       <ul>
 *          <li><i>alpha2countrycode</i>.png</li>
 *       </ul>
 *       <li>icons/</li>
 *       <ul>
 *          <li><i>action</i>.png</li>
 *       </ul>
 *       <li><i>image</i>.png</li>
 *    </ul>
 * </ul>
 * 
 * @author The JMRTD team (info@jmrtd.org)
 * 
 * @version $Id: $
 */
public class IconUtil
{	
	private static final Image DEFAULT_16X16_IMAGE =  new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB),
	DEFAULT_16X11_IMAGE =  new BufferedImage(16, 11, BufferedImage.TYPE_INT_ARGB);

	private static Map<String, Image> famFamFamSilkCache = new HashMap<String, Image>();
	private static Map<Country, Image> flagCache = new HashMap<Country, Image>();

	private static URL getImagesDir() {
		try {
			URL basePathURL = Files.getBaseDir();
			URL imagesDirURL = new URL(basePathURL + "/images");
			return imagesDirURL;
		} catch (Exception e) {
			return null;
		}
	}
	
	private static URL getImagesDir(Class<?> c) {
		try {
			URL basePathURL = Files.getBaseDir(c);
			URL imagesDirURL = new URL(basePathURL + "/images");
			return imagesDirURL;
		} catch (Exception e) {
			return null;
		}
	}
	
	private static URL getIconsDir() {
		return getImagesDir(IconUtil.class);
	}

	public static Image getImage(String imageName, Class<?> c) {
		try {
			URL imagesDir = getImagesDir(c);
			String fileName = imageName.toLowerCase() + ".png";
			Image image = ImageIO.read(new URL(imagesDir + "/" + fileName));
			return image;
		} catch (IOException e) {
			return null;
		}
	}
	
	public static Image getImage(String imageName) {
		try {
			URL imagesDir = getImagesDir();
			String fileName = imageName.toLowerCase() + ".png";
			Image image = ImageIO.read(new URL(imagesDir + "/" + fileName));
			return image;
		} catch (IOException e) {
			return null;
		}
	}

	public static Image getFlagImage(Country country) {
		Image image = flagCache.get(country);
		if (image != null) { return image; }
		image = getImageFromCollection("flags", country.toString().toLowerCase(), DEFAULT_16X11_IMAGE);
		flagCache.put(country, image);
		return image;
	}

	/**
	 * Gets icon from file.
	 * 
	 * @param iconName name without the .png or .gif
	 * @return an image
	 */
	public static Image getFamFamFamSilkIcon(String iconName) {
		Image image = famFamFamSilkCache.get(iconName);
		if (image != null) { return image; }
		image = getImageFromCollection("famfamfam_silk", iconName.toLowerCase(), DEFAULT_16X16_IMAGE);
		famFamFamSilkCache.put(iconName, image);
		return image;
	}
	
	private static Image getImageFromCollection(String collectionName, String imageName, Image defaultImage) {
		/* TODO: check if directory with name collectionName or zip file with name collectionName.zip exists */
		return getImageFromZippedCollection(collectionName, imageName, defaultImage);
	}

	private static Image getImageFromZippedCollection(String collectionName, String imageName, Image defaultImage) {
		try {
			URL collectionURL = new URL(getIconsDir() + "/" + collectionName + ".zip");
			ZipInputStream zipIn = new ZipInputStream(collectionURL.openStream());
			ZipEntry entry;
			while ((entry = zipIn.getNextEntry()) != null) {
				String fileName = imageName + ".png";
				String entryName = entry.getName();
				if (entryName != null && entryName.equals(fileName)) {
					Image flagImage = ImageIO.read(zipIn);
					return flagImage;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return defaultImage;
		}
		return defaultImage;
	}
}