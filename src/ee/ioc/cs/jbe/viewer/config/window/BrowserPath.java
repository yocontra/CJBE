/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package ee.ioc.cs.jbe.viewer.config.window;

import java.util.LinkedList;

/**
 * Description of the selected path in the tree of a <tt>BrowserComponent</tt>.
 *
 * @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
 * @version $Revision: 1.2 $ $Date: 2006/09/25 16:00:58 $
 */
public class BrowserPath {

    private LinkedList pathComponents = new LinkedList();

    /**
     * Get the list of viewer path components.
     *
     * @return the list.
     */
    public LinkedList getPathComponents() {
        return pathComponents;
    }

    /**
     * Set the list of viewer path components.
     *
     * @param pathComponents the list.
     */
    public void setPathComponents(LinkedList pathComponents) {
        this.pathComponents = pathComponents;
    }

    /**
     * Add a single viewer path component to this viewer path.
     *
     * @param pathComponent the new component.
     */
    public void addPathComponent(PathComponent pathComponent) {
        pathComponents.add(pathComponent);
    }

}
