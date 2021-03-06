/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.layouttpl.ui.cmd;

import com.liferay.ide.layouttpl.core.model.ModelElement;
import com.liferay.ide.layouttpl.core.model.PortletRowLayoutElement;
import com.liferay.ide.layouttpl.ui.model.LayoutConstraint;
import com.liferay.ide.layouttpl.ui.model.PortletColumn;
import com.liferay.ide.layouttpl.ui.model.PortletLayout;
import com.liferay.ide.layouttpl.ui.util.LayoutTplUIUtil;

import org.eclipse.gef.commands.Command;
import org.eclipse.osgi.util.NLS;

/**
 * @author Greg Amerson
 * @author Cindy Li
 */
public class PortletColumnCreateCommand extends Command
{
    protected PortletRowLayoutElement rowLayout;
    protected LayoutConstraint layoutConstraint;
    protected PortletColumn newColumn;
    protected int refColumnOldWeight = 0;

    public PortletColumnCreateCommand( PortletColumn newColumn, PortletRowLayoutElement rowLayout, LayoutConstraint constraint )
    {
        this.newColumn = newColumn;
        this.rowLayout = rowLayout;
        this.layoutConstraint = constraint;
        setLabel( Msgs.portletColumnAdded );
    }

    public boolean canExecute()
    {
        return newColumn != null && rowLayout != null && layoutConstraint != null;
    }

    public void execute()
    {
        redo();
    }

    public void redo()
    {
        if( layoutConstraint.equals( LayoutConstraint.EMPTY ) || layoutConstraint.newColumnIndex == -1 )
        {
            PortletLayout portletLayout = new PortletLayout();
            newColumn.setWeight( 100 );
            portletLayout.addColumn( newColumn );

            rowLayout.addRow( portletLayout, layoutConstraint.newRowIndex );
        }
        else if( layoutConstraint.rowIndex > -1 && layoutConstraint.newColumnIndex > -1 )
        {
            /* layoutConstraint.newRowIndex > -1 */

            if( layoutConstraint.refColumn != null )
            {
                refColumnOldWeight = layoutConstraint.refColumn.getWeight();

                //- 1 is for 33% to get 15% ref column not 20% after adjust weight
                int newRefWeight = refColumnOldWeight - layoutConstraint.weight - 1;
                layoutConstraint.refColumn.setWeight( LayoutTplUIUtil.adjustWeight( newRefWeight ) );
            }

            newColumn.setWeight( layoutConstraint.weight );

            // get the row that the column will be inserted into
            ModelElement row = rowLayout.getRows().get( layoutConstraint.rowIndex );
            PortletLayout portletLayout = (PortletLayout) row;

            if( row != null )
            {
                portletLayout.addColumn( newColumn, layoutConstraint.newColumnIndex );
            }
        }
    }

    public void undo()
    {
        if( layoutConstraint.equals( LayoutConstraint.EMPTY ) || layoutConstraint.newColumnIndex == -1  )
        {
            for( ModelElement row : rowLayout.getRows() )
            {
                PortletLayout portletLayout = (PortletLayout) row;

                if( portletLayout.getColumns().size() == 1 && portletLayout.getColumns().get( 0 ).equals( newColumn ) )
                {
                    rowLayout.removeRow( portletLayout );
                    break;
                }
            }
        }
        else if( layoutConstraint.rowIndex > -1 && layoutConstraint.newColumnIndex > -1 )
        {
            if( layoutConstraint.refColumn != null )
            {
                layoutConstraint.refColumn.setWeight( refColumnOldWeight );
            }

            ModelElement row = rowLayout.getRows().get( layoutConstraint.rowIndex );
            PortletLayout portletLayout = (PortletLayout) row;

            if( row != null )
            {
                portletLayout.removeColumn( newColumn );
            }
        }
    }

    private static class Msgs extends NLS
    {
        public static String portletColumnAdded;

        static
        {
            initializeMessages( PortletColumnCreateCommand.class.getName(), Msgs.class );
        }
    }
}
