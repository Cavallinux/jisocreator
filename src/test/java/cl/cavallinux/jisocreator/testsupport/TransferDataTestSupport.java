package cl.cavallinux.jisocreator.testsupport;

import java.lang.reflect.Field;

import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * Ayuda compartida para construir {@link TransferData} validos en tests de
 * DnD de forma independiente de la plataforma.
 *
 * <p>{@code TransferData} es una estructura nativa cuyos campos relevantes
 * para {@code Transfer.isSupportedType(TransferData)} difieren por
 * plataforma: GTK solo compara el campo {@code type}, mientras que Win32
 * ademas exige un {@code formatetc} (OLE {@code FORMATETC}) no nulo cuyo
 * {@code cfFormat}/{@code dwAspect}/{@code tymed} coincidan. Construir un
 * {@code TransferData} "a mano" fijando solo {@code type} (valido en GTK)
 * produce un {@code NullPointerException} o un falso negativo en Win32,
 * porque {@code formatetc} queda {@code null}. Por eso estos helpers reusan
 * {@link Transfer#getSupportedTypes()}, que cada plataforma rellena
 * correctamente, en vez de construir el objeto manualmente.</p>
 */
public final class TransferDataTestSupport {

    private TransferDataTestSupport() {
    }

    /** Returns a {@link TransferData} that {@code transfer} genuinely supports, on any platform. */
    public static TransferData supportedTransferData(Transfer transfer) {
        return transfer.getSupportedTypes()[0];
    }

    /**
     * Returns a {@link TransferData} that looks like one of {@code transfer}'s supported types but
     * has been mutated so that {@code transfer.isSupportedType(...)} rejects it, on any platform.
     */
    public static TransferData unsupportedTransferData(Transfer transfer) throws ReflectiveOperationException {
        TransferData transferData = transfer.getSupportedTypes()[0];
        transferData.type = Integer.MAX_VALUE;
        Object formatetc = readFieldIfPresent(transferData, "formatetc");
        if (formatetc != null) {
            Field cfFormat = formatetc.getClass().getDeclaredField("cfFormat");
            cfFormat.setAccessible(true);
            cfFormat.setInt(formatetc, Integer.MAX_VALUE);
        }
        return transferData;
    }

    private static Object readFieldIfPresent(TransferData transferData, String fieldName)
            throws ReflectiveOperationException {
        try {
            Field field = TransferData.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(transferData);
        } catch (NoSuchFieldException notPresentOnThisPlatform) {
            return null;
        }
    }
}
