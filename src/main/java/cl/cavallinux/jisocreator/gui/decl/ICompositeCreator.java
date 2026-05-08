package cl.cavallinux.jisocreator.gui.decl;

/**
 * Interface for creating and configuring composite GUI components.
 * <p>
 * Implementations of this interface are responsible for the complete lifecycle
 * of creating a composite component: creating the UI components, adding features
 * and functionality, and setting up event listeners.
 * </p>
 * <p>
 * Methods should be implemented in the following order:
 * <ol>
 *   <li>{@link #createComponents()} - Create all UI elements</li>
 *   <li>{@link #addFeatures()} - Configure component features</li>
 *   <li>{@link #addListeners()} - Attach event listeners</li>
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
    
    /**
     * Creates all UI components for this composite.
     * <p>
     * This method should initialize and configure all SWT/Swing UI widgets
     * such as buttons, text fields, labels, and other visual elements.
     * This should be the first method called in the lifecycle.
     * </p>
     */
    void createComponents();

    /**
     * Adds features and functionality to the composite.
     * <p>
     * This method should configure component behaviors, set initial states,
     * apply styling, and implement any non-event-driven business logic.
     * This should be called after {@link #createComponents()}.
     * </p>
     */
    void addFeatures();

    /**
     * Attaches event listeners to the composite and its components.
     * <p>
     * This method should register action listeners, mouse listeners, focus listeners,
     * and any other event handlers required for interactivity.
     * This should be called after {@link #addFeatures()}.
     * </p>
     */
    void addListeners();
}
