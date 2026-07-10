package cl.cavallinux.jisocreator.gui.decl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import cl.cavallinux.jisocreator.instances.JFaceResourcesManager;

/**
 * Interface for creating and configuring composite GUI components.
 * <p>
 * Implementations of this interface are responsible for the complete lifecycle
 * of creating a composite component: creating the UI components, adding
 * features and functionality, and setting up event listeners.
 * </p>
 * <p>
 * Methods should be implemented in the following order:
 * <ol>
 * <li>{@link #createComponents()} - Create all UI elements</li>
 * <li>{@link #addFeatures()} - Configure component features</li>
 * <li>{@link #addListeners()} - Attach event listeners</li>
 * </ol>
 * </p>
 * 
 * @see ICompositeCreator#createComponents()
 * @see ICompositeCreator#addFeatures()
 * @see ICompositeCreator#addListeners()
 * 
 * @author Paolo Mezzano Barahona (pmezzano@gmail.com)
 * @since 0.0.3
 * @version 0.0.3
 */
public interface ICompositeCreator {
    final int COMPOSITE_SWT_OPTIONS = SWT.VIRTUAL | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION
            | SWT.MULTI;
    final int COMPOSITE_DND_OPTIONS = DND.DROP_COPY | DND.DROP_MOVE;

    /**
     * Creates all UI components for this composite.
     * <p>
     * This method should initialize and configure all SWT/Swing UI widgets such as
     * buttons, text fields, labels, and other visual elements. This should be the
     * first method called in the lifecycle.
     * </p>
     */
    void createComponents();

    /**
     * Adds features and functionality to the composite.
     * <p>
     * This method should configure component behaviors, set initial states, apply
     * styling, and implement any non-event-driven business logic. This should be
     * called after {@link #createComponents()}.
     * </p>
     */
    void addFeatures();

    /**
     * Attaches event listeners to the composite and its components.
     * <p>
     * This method should register action listeners, mouse listeners, focus
     * listeners, and any other event handlers required for interactivity. This
     * should be called after {@link #addFeatures()}.
     * </p>
     */
    void addListeners();

    default void applyConstraints() {

    }

    /**
     * Fills the given table with predefined columns and tooltips.
     * <p>
     * This default method creates a set of standard columns for a file explorer
     * table, including "Name", "Type", "Size", and "Last Modified Date". Each
     * column is configured with a tooltip describing its purpose, and is set to be
     * moveable and resizable.
     * </p>
     * 
     * @param table the SWT Table to be filled with columns
     */
    default void fillTableColumnValues(Table table) {
        Map<String, String> tableColumnsTextAndTooltips = obtainTableColumnsTextAndTooltips();

        tableColumnsTextAndTooltips.forEach((columnName, tooltip) -> {
            TableColumn tvc = new TableColumn(table, SWT.LEFT);
            tvc.setText(columnName);
            tvc.setToolTipText(tooltip);
            tvc.setWidth(200);
            tvc.setMoveable(true);
            tvc.setResizable(true);
        });
        table.setHeaderVisible(true);
    }

    default Map<String, String> obtainTableColumnsTextAndTooltips() {
        return LinkedHashMap.newLinkedHashMap(4);
    }
    
    static Transfer[] obtainDragAndDropTransferTypes() {
        List<Transfer> transferTypes = new ArrayList<>();
        transferTypes.add(FileTransfer.getInstance());
        transferTypes.add(LocalSelectionTransfer.getTransfer());
        return transferTypes.toArray(Transfer[]::new);
    }
    
    static boolean isDragAndDropTransferTypeSupported(TransferData transferType) {
        return Arrays.stream(obtainDragAndDropTransferTypes())
                .anyMatch(transfer -> transfer.isSupportedType(transferType));
    }

    default void addPopMenuToTable(TableViewer table, IMenuListener menuListener) {
        MenuManager tableMenuManager = new MenuManager();
        tableMenuManager.setRemoveAllWhenShown(true);
        tableMenuManager.addMenuListener(menuListener);
        Control tableControl = table.getControl();
        tableControl.setMenu(tableMenuManager.createContextMenu(tableControl));
    }

    default void addJFaceResourcesToControls(JFaceResourcesManager resourceManager, TableViewer table,
            TreeViewer tree) {
        table.setContentProvider(resourceManager.getTableProviderAdapter());
        table.setLabelProvider(resourceManager.getTableProviderAdapter());
        table.addFilter(resourceManager.getToggleHiddenFilesFilter());
        table.setComparator(resourceManager.getDirectoriesComparator());

        tree.setContentProvider(resourceManager.getTreeContentProvider());
        tree.setLabelProvider(resourceManager.getTreeLabelProvider());
        tree.addFilter(resourceManager.getShowOnlyDirectoriesFilter());
        tree.addFilter(resourceManager.getToggleHiddenFilesFilter());
        tree.setComparator(resourceManager.getDirectoriesComparator());
    }
}
